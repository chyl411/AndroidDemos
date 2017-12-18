package zte.com.ipc;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, PlayListAdapter.OnItemClickListener {
    private Button btnPause, btnPlay, btnPrev, btnNext;
    private RecyclerView playListView;
    private SeekBar skbProgress;
    private IPlayerManager pm;
    private PlayListAdapter mAdapter;
    private static List<Music> li = new ArrayList<>();
    private Timer mTimer = new Timer();

    static {
        li.add(new Music("钢琴曲 - 轻音乐集 - 19岁的纯情", "http://sc1.111ttt.com:8282/2015/1/03m/14/96141925588.m4a?#.mp3"));
        li.add(new Music("石进 - 夜的钢琴曲二十七", "http://sc1.111ttt.com:8282/2015/1/04m/05/97051152159.m4a?#.mp3"));
        li.add(new Music("纯音乐 夜空中最亮的星", "http://sc1.111ttt.com:8282/2015/1/06m/16/99162014044.m4a?#.mp3"));
        li.add(new Music("钢琴曲 - 菊次郎的夏天", "http://sc1.111ttt.com:8282/2015/1/04m/25/97250104332.m4a?#.mp3"));
        li.add(new Music("Remember", "http://sc1.111ttt.com:8282/2015/1/04m/20/97202128349.m4a?#.mp3"));
        li.add(new Music("钢琴曲 - 轻音乐集", "http://sc1.111ttt.com:8282/2014/1/12m/26/5262046509.m4a?#.mp3"));
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
}
