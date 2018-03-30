package zte.com.ipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import zte.com.ipc.Model.NLUPojo.Domain;
import zte.com.ipc.Model.NLUPojo.NLUPojo;
import zte.com.ipc.nludomain.playerdomain.DaemonService;
import zte.com.ipc.nludomain.playerdomain.IPlayerManager;
import zte.com.ipc.nludomain.playerdomain.Music;
import zte.com.ipc.nludomain.playerdomain.PlayListAdapter;
import zte.com.ipc.nludomain.playerdomain.activitys.ActivityRecog;
import zte.com.ipc.recognization.CommonRecogParams;
import zte.com.ipc.recognization.nlu.NluRecogParams;

public class MainActivity extends ActivityRecog implements View.OnClickListener, PlayListAdapter.OnItemClickListener {
    private Button btnPause, btnPlay, btnPrev, btnNext;
    private RecyclerView playListView;
    private SeekBar skbProgress;
    private IPlayerManager pm;
    private PlayListAdapter mAdapter;
    private static List<Music> li = new ArrayList<>();
    private Timer mTimer = new Timer();

    static {
        li.add(new Music("说散就散", "http://music.163.com/song/media/outer/url?id=523251118.mp3"));
        li.add(new Music("空空如也", "http://music.163.com/song/media/outer/url?id=526464293.mp3"));
        li.add(new Music("Panama", "http://music.163.com/song/media/outer/url?id=34229976.mp3"));
        li.add(new Music("BINGBIAN病变", "http://music.163.com/song/media/outer/url?id=543607345.mp3"));
        li.add(new Music("烟火里的尘埃", "http://music.163.com/song/media/outer/url?id=29004400.mp3"));
        li.add(new Music("Beautiful Now", "http://music.163.com/song/media/outer/url?id=32019002.mp3"));
        li.add(new Music("Dream It Possible", "http://music.163.com/song/media/outer/url?id=38592976.mp3"));
    }

    @Override
    protected CommonRecogParams getApiParams() {
        return new NluRecogParams(this);
    }

    private ServiceConnectionImpl mConnection = null;

    @Override
    public void onItemClick(int m) {
        if(pm != null)
        {
            try {
                pm.actionPlay(m);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    class ServiceConnectionImpl implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pm = IPlayerManager.Stub.asInterface(service);
            try {
                pm.updatePlayList(li);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("IPC", "EXIT SUCCESS!");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnection = new ServiceConnectionImpl();

        setContentView(R.layout.activity_main);

        btnPause = findViewById(R.id.btnPause);
        btnPlay = findViewById(R.id.btnPlay);
        btnPrev = findViewById(R.id.btnPreview);
        btnNext = findViewById(R.id.btnNext);



        btnPause.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());

        initRecyclerView();
        setupTimer();
        initView();
    }

    /*******************************************************
     * 通过定时器和Handler来更新进度条
     ******************************************************/
    TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            if(pm != null)
            {
                try {
                    int progress = pm.actionGetProgress();
                    skbProgress.setProgress(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void setupTimer()
    {
        mTimer.schedule(mTimerTask, 0, 500);
    }

    private void initRecyclerView() {
        //1 实例化RecyclerView
        playListView = (RecyclerView) findViewById(R.id.playListViewId);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //2 为RecyclerView创建布局管理器，这里使用的是LinearLayoutManager，表示里面的Item排列是线性排列
        playListView.setLayoutManager(mLayoutManager);
        mAdapter = new PlayListAdapter(li);
        mAdapter.setListener(this);
        //3 设置数据适配器
        playListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {

        if(pm == null)
            return;

        try {
            switch (v.getId()) {
                case R.id.btnPause:
                    pm.actionPause();
                    break;
                case R.id.btnPlay:
                    pm.actionStart();
                    break;
                case R.id.btnPreview:
                    pm.actionPrevious();
                    break;
                case R.id.btnNext:
                    pm.actionNext();
                    break;
                default:
                    break;
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent its = new Intent(this, DaemonService.class);
        its.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(its);

        Intent itb = new Intent(this, DaemonService.class);
        bindService(itb, mConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if(fromUser)
            {
                if(pm != null)
                {
                    try {
                        pm.actionSeekTo(progress);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    @Override
    public void execNluAction(String json){
        if(json.startsWith("nlujson:")){
            String str = json.replace("nlujson:", "");

            Gson g = new Gson();
            NLUPojo np = g.fromJson(str, NLUPojo.class);

            ArrayList<Domain> domainList = np.merged_res.semantic_form.results;

            for(int i = 0 ; i < domainList.size(); i++)
            {
                Domain dm = domainList.get(i);
                if(dm.domain.equals("player")){
                    if(pm == null)
                        return;

                    if(dm.intent.equals("set")){
                        try {
                        switch (dm.object.get("action_type")){
                            case "play":
                                pm.actionStart();
                                break;
                            case "pause":
                                pm.actionPause();
                                break;
                            case "previous":
                                pm.actionPrevious();
                                break;
                            case "next":
                                pm.actionNext();
                                break;
                            case "exitplayer":
                                break;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(dm.intent.equals("play")){

                    }
                }
            }
        }
    }
}
