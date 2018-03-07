package com.example.xiewujie.dailyzhihu.download;

/**
 * Created by xiewujie on 2018/2/5.
 */

public interface DownloadListener {
    void onSucced();
    void onProgress(int progress);
    void onFailed();
}
