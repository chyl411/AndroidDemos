package zte.com.downloader.ListTasks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import zte.com.downloader.Activities.MainActivity;
import zte.com.downloader.Item.DownloadItem;
import zte.com.downloader.R;

/**
 * Created by chyl411 on 2017/12/25.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {
    public List<DownloadItem> dlList = new ArrayList<DownloadItem>();

    @Override
    public int getItemCount() {
        return dlList.size();
    }

    public void setDlList(List<DownloadItem> dl){
        dlList = dl;
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar pb;
        public ImageView playBtn;
        public ImageView cancelBtn;
        public TextView nameView;
        public TextView progressTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            //由于itemView是item的布局文件，我们需要的是里面的textView，因此利用itemView.findViewById获
            //取里面的textView实例，后面通过onBindViewHolder方法能直接填充数据到每一个textView了
            pb = (ProgressBar) itemView.findViewById(R.id.progress_id);
            playBtn = itemView.findViewById(R.id.play_btn);
            cancelBtn = itemView.findViewById(R.id.cancel_btn);
            nameView = itemView.findViewById(R.id.nameViewId);
            progressTextView = itemView.findViewById(R.id.progressTextViewId);
        }
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
        holder.pb.setProgress(dlList.get(position).getProgress());
        holder.nameView.setText(dlList.get(position).getName());


        if(dlList.get(position).getStatus() == DownloadItem.Status.DOWNLOAD_PLAY)
        {
            holder.playBtn.setImageResource(R.drawable.download_pause);
            holder.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    dlList.get(position).setStatus(DownloadItem.Status.DOWNLOAD_PAUSE);
                    ListAdapter.this.notifyDataSetChanged();
                }
            });
        }
        else if(dlList.get(position).getStatus() == DownloadItem.Status.DOWNLOAD_PAUSE)
        {
            holder.playBtn.setImageResource(R.drawable.download_start);

            holder.playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    dlList.get(position).setStatus(DownloadItem.Status.DOWNLOAD_PLAY);
                    Context context = v.getContext();
                    if (context instanceof MainActivity)
                    {
                        MainActivity activity = (MainActivity)context;
                        activity.getPresenter().restart(dlList.get(position));
                    }

                    ListAdapter.this.notifyDataSetChanged();
                }
            });
        }

        //设置观察者的进度条
        dlList.get(position).innerObserver.setPb(holder.pb, holder.progressTextView);

        holder.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                dlList.get(position).setStatus(DownloadItem.Status.DOWNLOAD_CANCELED);
                Context context = v.getContext();
                if (context instanceof MainActivity)
                {
                    MainActivity activity = (MainActivity)context;
                    activity.getPresenter().delete(dlList.get(position));
                }

                ListAdapter.this.notifyDataSetChanged();
            }
        });
    }


}
