package com.example.xiewujie.dailyzhihu;

/**
 * Created by xiewujie on 2018/2/13.
 */

public interface CallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
