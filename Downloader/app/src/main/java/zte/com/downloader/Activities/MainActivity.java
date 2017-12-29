package zte.com.downloader.Activities;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.List;


import zte.com.downloader.AddDownloadTask.AddTaskFragment;
import zte.com.downloader.Item.DownloadItem;
import zte.com.downloader.ListTasks.ListTaskFragment;
import zte.com.downloader.R;

public class MainActivity extends AppCompatActivity {
    public AddTaskFragment fragmentAdd = null;
    public ListTaskFragment fragmentList = null;

    int selected = 0;
    MainActivityPresenter mp = new MainActivityPresenter(this);

    public MainActivityPresenter getPresenter()
    {
        return mp;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    showFragment(fragmentAdd);
                    selected = 0;
                    return true;
                case R.id.navigation_dashboard:
                    showFragment(fragmentList);
                    selected = 1;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initFragments(savedInstanceState);
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    void initFragments(Bundle savedInstanceState)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(savedInstanceState != null)//横竖屏切换
        {
            int idx = savedInstanceState.getInt("selected");

            //准备再次保存时的值
            selected = idx;
            fragmentAdd = (AddTaskFragment) getFragmentManager().findFragmentByTag("add");
            ft.hide(fragmentAdd);

            fragmentList = (ListTaskFragment) getFragmentManager().findFragmentByTag("list");
            ft.hide(fragmentList);

            if(idx == 0)
            {
                ft.show(fragmentAdd);
            }
            else
            {
                ft.show(fragmentList);
            }
        }
        else{//正常进入
            fragmentAdd = new AddTaskFragment();
            ft.add(R.id.inner_frame, fragmentAdd, "add");
            ft.show(fragmentAdd);

            fragmentList = new ListTaskFragment();
            ft.add(R.id.inner_frame, fragmentList, "list");
            ft.hide(fragmentList);
        }
        ft.commit();
    }

    void showFragment(Fragment beShowed)
    {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if(fragmentAdd != null)
        {
            ft.hide(fragmentAdd);
        }

        if(fragmentList != null)
        {
            ft.hide(fragmentList);
        }

        ft.show(beShowed);
        ft.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle)
    {
        super.onSaveInstanceState(bundle);

        bundle.putInt("selected", selected);
    }
}
