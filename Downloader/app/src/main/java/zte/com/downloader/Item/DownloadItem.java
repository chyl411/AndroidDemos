package zte.com.downloader.Item;

import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by chyl411 on 2017/12/25.
 */

public class DownloadItem extends Observable {
    private int progress;
    private String name;
    private String url;
    private Status status;
    public long totalBytes;
    public long downloadedBytes;
    public DownloadItemObserver innerObserver = null;

    public enum Status {
        DOWNLOAD_PLAY,//正在下载
        DOWNLOAD_PAUSE,//暂停下载
        DOWNLOAD_CANCELED,//取消下载
    }

    public static class DownloadItemObserver implements Observer {
        private ProgressBar pb = null;
        private TextView progressTextView = null;

        public void setPb(ProgressBar p, TextView ptv){
            pb = p;
            progressTextView = ptv;
        }

        @Override
        public void update(Observable o, Object arg) {
            final DownloadItem di = (DownloadItem)o;
            final long downloaded = (long)arg;
            if(pb != null && di != null)
            {
                pb.post(new Runnable() {
                    @Override
                    public void run() {
                        pb.setProgress(di.getProgress());
                        progressTextView.setText(downloaded / 1000 + "K/" + di.totalBytes / 1000 + "K");
                    }
                });
            }
        }
    }
    public DownloadItem(String name, String url){
        this(0, name, url, Status.DOWNLOAD_PLAY);
    }

    public DownloadItem(int progress, String name, String url, Status status) {
        this.setProgress(progress, 0);
        this.setName(name);
        this.setUrl(url);
        this.setStatus(status);

        addObserver(innerObserver);
    }

    @Override
    public void addObserver(Observer o)
    {
        if(innerObserver == null)
        {
            innerObserver = new DownloadItemObserver();
            super.addObserver(innerObserver);
        }
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress, long downloaded) {
        this.downloadedBytes = downloaded;
        this.progress = progress;

        setChanged();
        notifyObservers(downloadedBytes);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
