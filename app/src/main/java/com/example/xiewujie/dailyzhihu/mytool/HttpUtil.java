package com.example.xiewujie.dailyzhihu.mytool;

import com.example.xiewujie.dailyzhihu.CallbackListener;
import com.example.xiewujie.dailyzhihu.GetInputListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiewujie on 2018/2/13.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address,final CallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(12000);
                    connection.setConnectTimeout(10000);
                    connection.setDoInput(true);
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    if (listener!=null){
                        listener.onFinish(response.toString());
                    }
                }catch (Exception e){
                    if (listener!=null){
                        listener.onError(e);
                    }
                }finally {
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    public static void getInputStram(final String address, final GetInputListener listener){
       new Thread(new Runnable() {
           @Override
           public void run() {
               InputStream inputStream = null;
               HttpURLConnection connection = null;
        try {
            URL url = new URL(address);
            connection=(HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setReadTimeout(8000);
            connection.setConnectTimeout(8000);
            inputStream = connection.getInputStream();
            if (listener!=null){
                listener.onInputStream(inputStream);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (connection!=null)
                connection.disconnect();
            if (inputStream!=null)
                try {
                    inputStream.close();
                }catch (IOException i){
                i.printStackTrace();
                }
        }
           }
       }).start();
    }
}
