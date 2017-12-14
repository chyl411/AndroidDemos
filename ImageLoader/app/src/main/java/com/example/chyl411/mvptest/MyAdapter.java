package com.example.chyl411.mvptest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chyl411 on 2017/12/11.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<SoftReference<Bitmap>> mDataSet;
    private List<Integer> mHeight;

    private Handler refreshHandler = new Handler(){
        @Override
        public synchronized void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bitmap bmp = (Bitmap) msg.obj;
            Integer pos = (Integer) msg.arg1;

            SoftReference<Bitmap> weakBmp = new SoftReference<Bitmap>(bmp);

            mDataSet.set(pos, weakBmp);
            notifyItemChanged(pos);
        }
    };


    //构造器，接受数据集
    public MyAdapter(List<SoftReference<Bitmap>> data){
        mDataSet = data;
        mHeight = new ArrayList<>();
    }

    public void addImgWithHeight(Bitmap b, int h){
        if(b == null)
        {
            Log.e("error : ", "null bitmap pointer:" + h);
            return;
        }
        SoftReference<Bitmap> weakBmp = new SoftReference<Bitmap>(b);

        mDataSet.add(weakBmp);
        mHeight.add(h);
        Log.e("count", "==>" + mDataSet.size());

        cacheToFile(b, mDataSet.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //加载布局文件
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlayout,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //将数据填充到具体的view中
        SoftReference<Bitmap> srb = mDataSet.get(position);
        if(srb.get() != null)
        {
            holder.mImageView.setImageBitmap(srb.get());
        }
        else
        {
            Log.e("read cache", "==> cache index:" + position);

            readCache(position, refreshHandler);

        }


        //绑定数据的同时，修改每个ItemView的高度
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        lp.height = mHeight.get(position);
        holder.itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            //由于itemView是item的布局文件，我们需要的是里面的textView，因此利用itemView.findViewById获
            //取里面的textView实例，后面通过onBindViewHolder方法能直接填充数据到每一个textView了
            mImageView = (ImageView) itemView.findViewById(R.id.img);
        }
    }


    void cacheToFile(Bitmap b, int indx)
    {
        if(b == null)
        {
            Log.e("error : ", "null bitmap pointer:" + indx);
        }
        //将bitmap保存为本地文件

        File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/meimei");//设置保存路径
        try {
            if(!PHOTO_DIR.exists())
            {
                PHOTO_DIR.mkdirs();
            }

            File avaterFile = new File(PHOTO_DIR, indx + ".jpg");//设置文件名称

            if(avaterFile.exists()){
                avaterFile.delete();
            }

            avaterFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(avaterFile);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void readCache(int idx, Handler hd)
    {
        //如果需要读文件cache的话，把需要读的图片全部扔到一个队列里面，只读最后的几个图片

        Bitmap bitmap = null;
        File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/meimei");//设置保存路径
        try{
            File avaterFile = new File(PHOTO_DIR, idx + ".jpg");

            String path = avaterFile.getPath();
            ImageCacheLoader.getInstance().addNeedReadImgAndPosition(path, idx, hd);
        } catch (Exception e) {}
    }
}
