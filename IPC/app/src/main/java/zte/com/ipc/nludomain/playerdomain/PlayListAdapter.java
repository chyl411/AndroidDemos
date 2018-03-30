package zte.com.ipc.nludomain.playerdomain;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import zte.com.ipc.R;

/**
 * Created by chyl411 on 2017/12/15.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder>  {
    List<Music> list;

    public PlayListAdapter(List<Music> li)
    {
        this.list = li;
    }

    @Override
    public PlayListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //加载布局文件
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PlayListAdapter.ViewHolder holder, final int position) {

        if(position < list.size())
        {
            final Music m = list.get(position);
            holder.titleView.setText(m.getName());
            holder.titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView titleView;
        public ViewHolder(View itemView) {
            super(itemView);
            //由于itemView是item的布局文件，我们需要的是里面的textView，因此利用itemView.findViewById获
            //取里面的textView实例，后面通过onBindViewHolder方法能直接填充数据到每一个textView了
            titleView = (TextView) itemView.findViewById(R.id.titleViewId);
        }
    }

    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int m);
    };
}
