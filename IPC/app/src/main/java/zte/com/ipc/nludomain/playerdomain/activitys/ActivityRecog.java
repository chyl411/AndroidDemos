package zte.com.ipc.nludomain.playerdomain.activitys;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.util.HashMap;
import java.util.Map;


import android.widget.Button;


import com.google.gson.Gson;


import zte.com.ipc.CommonActivity;
import zte.com.ipc.IStatus;
import zte.com.ipc.MyRecognizer;
import zte.com.ipc.R;
import zte.com.ipc.recognization.CommonRecogParams;
import zte.com.ipc.recognization.MessageStatusRecogListener;
import zte.com.ipc.recognization.PidBuilder;
import zte.com.ipc.recognization.StatusRecogListener;
import zte.com.ipc.recognization.offline.OfflineRecogParams;

/**
 * 识别的基类Activity。封装了识别的大部分逻辑，包括MyRecognizer的初始化，资源释放、
 * <p>
 * 大致流程为
 * 1. 实例化MyRecognizer ,调用release方法前不可以实例化第二个。参数中需要开发者自行填写语音识别事件的回调类，实现开发者自身的业务逻辑
 * 2. 如果使用离线命令词功能，需要调用loadOfflineEngine。在线功能不需要。
 * 3. 根据识别的参数文档，或者demo中测试出的参数，组成json格式的字符串。调用 start 方法
 * 4. 在合适的时候，调用release释放资源。
 * <p>
 * Created by fujiayi on 2017/6/20.
 */

public abstract class ActivityRecog extends CommonActivity implements IStatus {

    /**
     * 识别控制器，使用MyRecognizer控制识别的流程
     */
    protected MyRecognizer myRecognizer;

    /*
     * Api的参数类，仅仅用于生成调用START的json字符串，本身与SDK的调用无关
     */
    protected CommonRecogParams apiParams;

    /*
     * 本Activity中是否需要调用离线命令词功能。根据此参数，判断是否需要调用SDK的ASR_KWS_LOAD_ENGINE事件
     */
    protected boolean enableOffline = false;

    Button btn;

    /**
     * 控制UI按钮的状态
     */
    protected int status;

    /**
     * 日志使用
     */
    private static final String TAG = "ActivityRecog";

    /**
     * 在onCreate中调用。初始化识别控制类MyRecognizer
     */
    protected void initRecog() {
        StatusRecogListener listener = new MessageStatusRecogListener(handler);
        myRecognizer = new MyRecognizer(this, listener);
        apiParams = getApiParams();
        status = STATUS_NONE;
        if (enableOffline) {
            myRecognizer.loadOfflineEngine(OfflineRecogParams.fetchOfflineParams());
        }
    }


    /**
     * 销毁时需要释放识别资源。
     */
    @Override
    protected void onDestroy() {
        myRecognizer.release();
        Log.i(TAG, "onDestory");
        super.onDestroy();
    }

    /**
     * 开始录音，点击“开始”按钮后调用。
     */
    protected void start() {
        String config = "{\"accept-audio-volume\":false,\"_nlu_online\":true,\"accept-audio-data\":false,\"_model\":\"search\"}";

        Gson gson = new Gson();
        Map<String, Object> map = new HashMap<String, Object>();

        Map jMap = gson.fromJson(config, map.getClass());
        PidBuilder builder = new PidBuilder();
        jMap = builder.addPidInfo(jMap);
        myRecognizer.start(jMap);
    }


    /**
     * 开始录音后，手动停止录音。SDK会识别在此过程中的录音。点击“停止”按钮后调用。
     */
    private void stop() {
        myRecognizer.stop();
    }

    /**
     * 开始录音后，取消这次录音。SDK会取消本次识别，回到原始状态。点击“取消”按钮后调用。
     */
    private void cancel() {
        myRecognizer.cancel();
    }


    /**
     * @return
     */
    protected abstract CommonRecogParams getApiParams();

    // 以上为 语音SDK调用，以下为UI部分
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initView() {
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (status) {
                    case STATUS_NONE: // 初始状态
                        start();
                        status = STATUS_WAITING_READY;
                        updateBtnTextByStatus();
                        break;
                    case STATUS_WAITING_READY: // 调用本类的start方法后，即输入START事件后，等待引擎准备完毕。
                    case STATUS_READY: // 引擎准备完毕。
                    case STATUS_SPEAKING:
                    case STATUS_FINISHED: // 长语音情况
                    case STATUS_RECOGNITION:
                        stop();
                        status = STATUS_STOPPED; // 引擎识别中
                        updateBtnTextByStatus();
                        break;
                    case STATUS_STOPPED: // 引擎识别中
                        cancel();
                        status = STATUS_NONE; // 识别结束，回到初始状态
                        updateBtnTextByStatus();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    protected void handleMsg(Message msg) {
        super.handleMsg(msg);

        switch (msg.what) { // 处理MessageStatusRecogListener中的状态回调
            case STATUS_FINISHED:
                if (msg.arg2 == 1) {
                    Log.i("IPC", msg.obj.toString());
                }
                status = msg.what;
                updateBtnTextByStatus();

                //开始语音控制
                if(msg.obj instanceof String)
                {
                    execNluAction((String)msg.obj);
                }

                break;
            case STATUS_NONE:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                status = msg.what;
                updateBtnTextByStatus();
                break;
            default:
                break;

        }
    }

    private void updateBtnTextByStatus() {
        switch (status) {
            case STATUS_NONE:
                btn.setText("录音");
                btn.setEnabled(true);
                break;
            case STATUS_WAITING_READY:
            case STATUS_READY:
            case STATUS_SPEAKING:
            case STATUS_RECOGNITION:
                btn.setText("停止录音");
                btn.setEnabled(true);
                break;

            case STATUS_STOPPED:
                btn.setText("取消整个识别过程");
                btn.setEnabled(true);
                break;
            default:
                break;
        }
    }

    public void execNluAction(String json){

    }
}
