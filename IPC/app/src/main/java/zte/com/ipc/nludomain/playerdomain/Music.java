package zte.com.ipc.nludomain.playerdomain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chyl411 on 2017/12/15.
 */

public class Music implements Parcelable {
    private String name;
    private String url;


    public Music(String n, String u) {
        name = n;
        url = u;
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

    protected Music(Parcel in) {
        name = in.readString();
        url = in.readString();
    }


    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
    }
}
