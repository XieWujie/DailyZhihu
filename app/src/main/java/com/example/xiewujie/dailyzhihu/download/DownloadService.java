package com.example.xiewujie.dailyzhihu.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.Toast;

import com.example.xiewujie.dailyzhihu.mytool.MyApplication;

public class DownloadService extends Service {
    Context context = MyApplication.getContext();
    public Download downloadTask;
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onSucced() {

        }

        @Override
        public void onProgress(int progress) {

        }

        @Override
        public void onFailed() {
            Toast.makeText(context,"自动缓存失败",Toast.LENGTH_SHORT ).show();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startdownload();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startdownload() {
        String url = "http://news-at.zhihu.com/api/4/news/latest";
        NetworkInfo info = MyApplication.getNetworkInfo();
        int netType = info.getType();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            if (downloadTask == null) {
                downloadTask = new Download(listener);
                downloadTask.execute(url);
            }
        }
    }
    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
