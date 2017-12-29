package zte.com.downloader.AddDownloadTask;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import zte.com.downloader.Activities.MainActivity;
import zte.com.downloader.Activities.MainActivityPresenter;
import zte.com.downloader.Item.DownloadItem;
import zte.com.downloader.R;

/**
 * Created by chyl411 on 2017/12/22.
 */

public class AddTaskFragment extends Fragment implements AddTaskPresent{
    private FloatingActionButton downloadBtn;
    private EditText nameView;
    private EditText urlView;
    private AddTaskPresenter addPresenter;
    private MainActivity ma;

    public MainActivityPresenter getMainactivityPresenter()
    {
        return ma.getPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        ma = (MainActivity)getActivity();
        addPresenter = new AddTaskPresenter(this);

        downloadBtn = getActivity().findViewById(R.id.detailed_action_update_notice);
        nameView = getActivity().findViewById(R.id.nameView);
        urlView = getActivity().findViewById(R.id.urlView);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPresenter.addTask(nameView.getText().toString(), urlView.getText().toString());
            }
        });
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.add_task_layout, container, false);
    }

    public Context getContext()
    {
        return getActivity();
    }

    public void restart(DownloadItem di){
        addPresenter.restart(di);
    }


}
