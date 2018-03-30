package zte.com.ipc.nludomain.playerdomain;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;


import zte.com.ipc.R;


public class DaemonService extends Service {
    private static String TAG = "IPC";
    private static final int NOTIF_ID = 1234;

    private Notification notification = null;
    private RemoteViews remoteViews = null;
    private PlayBinder binder = new PlayBinder();
    private NotificationManager mNotificationManager;

    private List<Music> playList = new ArrayList<>();
    private int currentPlaying = 0;
    private Player mp = new Player();

    public class PlayerBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra("action", Constants.ACTION_PAU);
            Log.e(TAG, "action==>" + action);
            processAction(action);
        }
    }

    void processAction(int action)
    {
        if(notification == null)
            return;

        try
        {
            switch (action)
            {
                case Constants.ACTION_PRV:
                    binder.actionPrevious();
                    break;
                case Constants.ACTION_STR:
                    remoteViews.setViewVisibility(R.id.plyBtnId, View.GONE);
                    remoteViews.setViewVisibility(R.id.pauBtnId, View.VISIBLE);
                    binder.actionStart();
                    break;
                case Constants.ACTION_PAU:
                    remoteViews.setViewVisibility(R.id.pauBtnId, View.GONE);
                    remoteViews.setViewVisibility(R.id.plyBtnId, View.VISIBLE);
                    binder.actionPause();
                    break;
                case Constants.ACTION_NXT:
                    binder.actionNext();
                    break;
            }
        }catch (RemoteException e) {
            e.printStackTrace();
        }

        int api = Build.VERSION.SDK_INT;

        if (api < Build.VERSION_CODES.HONEYCOMB) {
            mNotificationManager.notify(NOTIF_ID, notification);
        }else if (api >= Build.VERSION_CODES.HONEYCOMB) {
            mNotificationManager.notify(NOTIF_ID, notification);
        }
    }

    public DaemonService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");
        // 在API11之后构建Notification的方式
        remoteViews = new RemoteViews(this.getPackageName(),
                R.layout.notification_layout);// 获取remoteViews（参数一：包名；参数二：布局资源）

        Notification.Builder builder = new Notification.Builder(this.getApplicationContext()).setContent(remoteViews);// 设置自定义的Notification内容
        builder.setWhen(System.currentTimeMillis()).setSmallIcon(R.mipmap.ic_launcher);

        notification = builder.build();// 获取构建好的通知--.build()最低要求在API16及以上版本上使用，低版本上可以使用.getNotification()。

        setupClickEvents(remoteViews);
        startForeground(NOTIF_ID, notification);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
//        String pidName = intent.getStringExtra("PID");
//        Log.e("IPC","======>" + pidName);
//        Log.e("IPC","======>PID:" + android.os.Process.myPid());
//        innerList.add(pidName);
        return binder;
    }

    class PlayBinder extends IPlayerManager.Stub {


        @Override
        public void updatePlayList(List<Music> list) throws RemoteException {
            playList = list;
        }

        @Override
        public void actionPlay(int idx) throws RemoteException {
            if(playList != null && idx < playList.size() && idx >=0)
            {
                Music m = playList.get(idx);
                mp.playUrl(m.getUrl());
            }
        }

        @Override
        public void actionStart() throws RemoteException {
            mp.start();

        }

        @Override
        public void actionPause() throws RemoteException {
            mp.pause();

        }

        @Override
        public void actionPrevious() throws RemoteException {
            if(playList == null)
                return;
            currentPlaying--;
            if(currentPlaying < 0)
            {
                currentPlaying = playList.size() - 1;
            }
            if(currentPlaying < playList.size())
            {
                Music m = playList.get(currentPlaying);
                mp.playUrl(m.getUrl());
            }
        }

        @Override
        public void actionNext() throws RemoteException {
            if(playList == null)
                return;
            currentPlaying++;
            if(currentPlaying >= playList.size())
            {
                currentPlaying = 0;
            }
            if(currentPlaying >= 0)
            {
                Music m = playList.get(currentPlaying);
                mp.playUrl(m.getUrl());
            }
        }

        @Override
        public int actionGetProgress() throws RemoteException {
            return mp.getProgress();
        }

        @Override
        public void actionSeekTo(int progress) throws RemoteException {
            mp.seekToProgress(progress);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PlayerBroadcastReceiver receiver = new PlayerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.PLAYER_ACTION);
        registerReceiver(receiver, intentFilter);

        Log.e(TAG, "SERVICE STARTED");
        new Thread(new Runnable() {
            @Override
            public void run() {


            }
        }).start();
    }

    void setupClickEvents(RemoteViews remoteViews)
    {
        //上一曲
        Intent prv = new Intent();
        prv.setAction(Constants.PLAYER_ACTION);
        prv.putExtra("action", Constants.ACTION_PRV);
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 0, prv,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.prevBtnId, intent_prev);

        //播放
        Intent ply = new Intent();
        ply.setAction(Constants.PLAYER_ACTION);
        ply.putExtra("action", Constants.ACTION_STR);
        PendingIntent intent_ply = PendingIntent.getBroadcast(this, 1, ply,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.plyBtnId, intent_ply);

        //暂停
        Intent pau = new Intent();
        pau.setAction(Constants.PLAYER_ACTION);
        pau.putExtra("action", Constants.ACTION_PAU);
        PendingIntent intent_pau = PendingIntent.getBroadcast(this, 2, pau,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.pauBtnId, intent_pau);

        //下一曲
        Intent nxt = new Intent();
        nxt.setAction(Constants.PLAYER_ACTION);
        nxt.putExtra("action", Constants.ACTION_NXT);
        PendingIntent intent_nxt = PendingIntent.getBroadcast(this, 3, nxt,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.nxtBtnId, intent_nxt);
    }
}
