package com.example.chyl411.mvptest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.chyl411.mvptest.com.example.chl411.beans.ImageResultStruct;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by chyl411 on 2017/12/11.
 */

public class LoadImages extends AsyncTask<String, Bitmap, List<String>> {
    Handler hd;

    public LoadImages(Handler hd)
    {
        this.hd = hd;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected List<String> doInBackground(String... url) {
        List<String> list = new ArrayList<>();

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url[0])
                .build();
            Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            ImageResultStruct rest;
            Gson gs = new Gson();
            rest = gs.fromJson(response.body().string(), ImageResultStruct.class);
            startDownloadImgs(rest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    void startDownloadImgs(final ImageResultStruct rest)
    {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < rest.results.size(); i++) {
            final int index = i;
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {

                        ImageResultStruct.ImgItem item = rest.results.get(index);
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(item.url)
                                .build();
                        Call call = okHttpClient.newCall(request);
                        try {
                            Response response = call.execute();
                            byte[] bytes = response.body().bytes();//得到图片的流
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inJustDecodeBounds = true;
                            opts.inPreferredConfig = Bitmap.Config.RGB_565;
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);

                            opts.inSampleSize = calculateInSampleSize(opts);
                            opts.inJustDecodeBounds = false ;//inJustDecodeBounds 需要设置为false，如果设置为true，那么将返回null
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);


                            Message msg = Message.obtain();
                            msg.obj = bmp;
                            msg.arg1 = opts.outHeight;
                            hd.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    protected void onProgressUpdate(Bitmap... values)
    {

    }

    public int calculateInSampleSize(BitmapFactory.Options options) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        DisplayMetrics dm = MyApplication.getAppContext().getResources().getDisplayMetrics();
        int reqWidth = dm.widthPixels / 3;

        inSampleSize = Math.round((float) width / (float) reqWidth);

        return inSampleSize;
    }
}
