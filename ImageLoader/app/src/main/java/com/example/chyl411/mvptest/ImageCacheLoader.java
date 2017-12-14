package com.example.chyl411.mvptest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by chyl411 on 2017/12/12.
 */

public class ImageCacheLoader {
    private static ImageCacheLoader PUBLIC_INSTANCE = null;
    private ThreadPoolExecutor executor = null;

    public static ImageCacheLoader getInstance()
    {
        if(PUBLIC_INSTANCE != null)
        {
            return PUBLIC_INSTANCE;
        }
        else
        {
            PUBLIC_INSTANCE = new ImageCacheLoader();
            return PUBLIC_INSTANCE;
        }
    }

    public ImageCacheLoader(){
        executor = new ThreadPoolExecutor(
                4,
                4,
                0,
                TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(15),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    void addNeedReadImgAndPosition(final String path, final int position, final Handler callback)
    {
        Log.e("cache", "------ffffffff " + position + "----pid:" + Thread.currentThread().getId());
        executor.execute(new Runnable() {
            private ThreadLocal<Integer> localPosition = new ThreadLocal<Integer>(){
                @Override
                protected Integer initialValue() {
                    return position;
                }
            };

            private ThreadLocal<Handler> callbak = new ThreadLocal<Handler>(){
                @Override
                protected Handler initialValue() {
                    return callback;
                }
            };

            @Override
            public void run() {
                try{
                    File avaterFile = new File(path);
                    if(avaterFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(avaterFile.getPath());
                        Handler hdl = callbak.get();
                        Message msg = Message.obtain();
                        msg.arg1 = localPosition.get();
                        msg.obj = bitmap;

                        hdl.sendMessage(msg);
                        Log.e("cache", "+++++read file " + localPosition.get() + "----pid:" + Thread.currentThread().getId());
                    }
                } catch (Exception e) {}
            }
        });
    }
}
