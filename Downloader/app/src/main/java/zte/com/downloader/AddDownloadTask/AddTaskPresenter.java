package zte.com.downloader.AddDownloadTask;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import zte.com.downloader.Activities.MainActivityPresenter;
import zte.com.downloader.Item.DownloadItem;

/**
 * Created by chyl411 on 2017/12/25.
 */

public class AddTaskPresenter {
    private AddTaskFragment atp;
    ExecutorService fixedThreadPool;
    MainActivityPresenter mainActivityPresenter;

    public AddTaskPresenter(AddTaskFragment interf) {
        atp = interf;
        mainActivityPresenter = atp.getMainactivityPresenter();
        fixedThreadPool = Executors.newFixedThreadPool(5);
    }

    public void addTask(final String name, final String url) {
        final DownloadItem di = new DownloadItem(name, url);
        mainActivityPresenter.addNewItem(di);

        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(di.getUrl())
                        .build();

                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    InputStream bytes = response.body().byteStream();

                    saveToFile(di, bytes, response.body().contentLength());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void saveToFile(DownloadItem di, InputStream bytes, long total) {
        di.totalBytes = total;

        try{
            File outputfile = getOpFile(di.getName());
            FileOutputStream fos = new FileOutputStream(outputfile);

            long readed = 0;

            byte[] inputarray = new byte[1000];
            int length;
            while((length = bytes.read(inputarray)) != -1) {

                if(di.getStatus() == DownloadItem.Status.DOWNLOAD_PLAY)
                {
                    fos.write(inputarray, 0, length);
                    readed += length;

                    int p = (int)(readed * 100.0f / total);

                    di.setProgress(p, readed);
                }
                else
                {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restart(final DownloadItem di){
        fixedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(di.getUrl())
                        .addHeader("RANGE","bytes="+ di.downloadedBytes +"-")  //断点续传要用到的，指示下载的区间
                        .build();

                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    InputStream bytes = response.body().byteStream();

                    try{
                        File outputfile = getOpFile(di.getName());
                        RandomAccessFile savedFile = new RandomAccessFile(outputfile,"rw");
                        savedFile.seek(di.downloadedBytes);

                        long readed = di.downloadedBytes;

                        byte[] inputarray = new byte[1000];
                        int length;
                        while((length = bytes.read(inputarray)) != -1) {

                            if(di.getStatus() == DownloadItem.Status.DOWNLOAD_PLAY)
                            {
                                savedFile.write(inputarray, 0, length);
                                readed += length;

                                int p = (int)(readed * 100.0f / di.totalBytes);

                                di.setProgress(p, readed);
                            }
                            else
                            {
                                Thread.currentThread().interrupt();
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    File getOpFile(String name){
        File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/download");//设置保存路径

            if (!PHOTO_DIR.exists()) {
                PHOTO_DIR.mkdirs();
            }

            File outputfile = new File(PHOTO_DIR, name);//设置文件名称
            if (outputfile.exists()) {
                outputfile.delete();
            }
        return outputfile;
    }
}

//HandlerThread 使用
//public class AddTaskPresenter {
//    private AddTaskPresent atp;
//    private HandlerThread handlerthread;
//    private Handler handler;
//
//    public AddTaskPresenter(AddTaskPresent interf) {
//        atp = interf;
//        handlerthread = new HandlerThread("download");
//        handlerthread.start();
//        handler = new Handler(handlerthread.getLooper());
//    }
//
//    public void addTask(final String name, final String url) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                OkHttpClient okHttpClient = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url(url)
//                        .build();
//
//                Call call = okHttpClient.newCall(request);
//                try {
//                    Response response = call.execute();
//                    InputStream bytes = response.body().byteStream();
//
//                    saveToFile(name, bytes);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    public void saveToFile(String name, InputStream bytes) {
//        File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/download");//设置保存路径
//        try {
//            if (!PHOTO_DIR.exists()) {
//                PHOTO_DIR.mkdirs();
//            }
//
//            File outputfile = new File(PHOTO_DIR, name);//设置文件名称
//            FileOutputStream fos = new FileOutputStream(outputfile);
//
//            byte[] inputarray = new byte[1000];
//            int length;
//            while((length = bytes.read(inputarray)) != -1) {
//                fos.write(inputarray, 0, length);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
