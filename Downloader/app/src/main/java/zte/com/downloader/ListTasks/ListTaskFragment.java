package zte.com.downloader.ListTasks;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Vector;

import zte.com.downloader.Item.DownloadItem;
import zte.com.downloader.R;

/**
 * Created by chyl411 on 2017/12/25.
 */

public class ListTaskFragment extends Fragment{
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ListAdapter mAdapter;

    public void notifyChange(List<DownloadItem> dl){

        mAdapter.setDlList(dl);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.list_task_layout, container, false);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        //1 实例化RecyclerView
        mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.recycleListId);
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ListAdapter();
        //3 设置数据适配器
        mRecyclerView.setAdapter(mAdapter);
    }
}
