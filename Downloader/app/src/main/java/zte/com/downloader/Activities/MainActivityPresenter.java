package zte.com.downloader.Activities;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zte.com.downloader.Item.DownloadItem;

/**
 * Created by chyl411 on 2017/12/27.
 */

public class MainActivityPresenter {
    MainActivity mainActivity;
    private List<DownloadItem> datalist = new ArrayList<>();

    public MainActivityPresenter(MainActivity m)
    {
        mainActivity = m;
    }

    //新增下载任务
    public void addNewItem(DownloadItem di){

        //检测文件名存在否，已经存在则后缀添加(1)，  例如 filename.apk 会变成 filename(1).apk。然后变成 filename(2).apk
        while (true){
            Boolean isExists = false;
            for(DownloadItem tmp : datalist){
                if(tmp.getName().equals(di.getName())){
                    isExists = true;
                }
            }
            if(isExists == false){
                break;
            }
            else{
                String name = di.getName();
                String extName = "";
                if(name.indexOf(".") != -1)
                {
                    extName = name.substring(name.indexOf("."));//后缀
                    name = name.replace(extName, "");//去掉后缀后的名字
                }

                Pattern pattern = Pattern.compile("\\([0-9]+\\)");
                Matcher m = pattern.matcher(name);
                if(m.find())
                {
                    String paStr = m.group();
                    paStr = paStr.replace("(", "");
                    paStr = paStr.replace(")", "");

                    int count = Integer.parseInt(paStr);
                    count++;
                    name = name.substring(0, name.indexOf("("));
                    di.setName(name + "(" + count + ")" + extName);
                }
                else{
                    di.setName(name + "(" + 1 + ")" + extName);
                }

            }
        }
        datalist.add(di);
        mainActivity.fragmentList.notifyChange(datalist);
    }

    public void restart(DownloadItem di){
        mainActivity.fragmentAdd.restart(di);
    }
    //删除下载任务
    public void delete(DownloadItem di)
    {
        int indx = datalist.indexOf(di);
        if(indx > -1 && indx < datalist.size())
        {
            datalist.remove(indx);
            mainActivity.fragmentList.notifyChange(datalist);
        }
    }

    public List<DownloadItem> getDatalist(){
        return datalist;
    }
}
