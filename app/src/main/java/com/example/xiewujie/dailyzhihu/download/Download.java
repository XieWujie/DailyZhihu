package com.example.xiewujie.dailyzhihu.download;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import com.example.xiewujie.dailyzhihu.GetInputListener;
import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.example.xiewujie.dailyzhihu.mytool.MyApplication;
import com.example.xiewujie.dailyzhihu.MyDatabaseHelper;
import com.example.xiewujie.dailyzhihu.mytool.MyLruCache;
import com.example.xiewujie.dailyzhihu.myJson.AllStories;
import com.example.xiewujie.dailyzhihu.myJson.WebContent;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiewujie on 2018/2/5.
 */
public class Download extends AsyncTask<String,Integer,Integer> {
    public static final int SUCCEED = 0;
    public static final int FAILED = 1;
    HttpURLConnection connection;
    AllStories allStories;
    List<String> allId;DownloadListener listener;
    MyDatabaseHelper dbhelp;
    SQLiteDatabase db;
    Context context = MyApplication.getContext();
    private MyLruCache lruCache = MyLruCache.getInstanse();
    public Download(DownloadListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected Integer doInBackground(String... values) {
        int length = 0;
        int progress = 0;
        String downloadUrl = values[0];
        try{
            int allLenghth = getId(downloadUrl);
            for (int k = 0; k < allId.size(); k++) {
                String address = "https://news-at.zhihu.com/api/4/news/" + allId.get(k);
                String content = getContent(address);
                Gson gson = new Gson();
                WebContent webAll = gson.fromJson(content, WebContent.class);
                length += toDatabase(webAll);
                progress = (int) (length * 100 / allLenghth);
                NetworkInfo networkInfo = MyApplication.getNetworkInfo();
                if (networkInfo != null && (!networkInfo.isAvailable())) {
                    return FAILED;
                }
                publishProgress(progress);
            }
        }catch (IOException i){
            i.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            if (connection!=null){
                connection.disconnect();
            }
        }
        return SUCCEED;
    }
    private String getContent(String address)throws Exception{
        InputStream inputStream = getInputStream(address);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        if (inputStream!=null){
            inputStream.close();
        }
        if (reader!=null){
            reader.close();
        }
        return response.toString();
    }
    private InputStream getInputStream(String address)throws Exception{
        URL url = new URL(address);
        connection =(HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(8000);
        connection.setDoInput(true);
        connection.setReadTimeout(8000);
        connection.setRequestMethod("GET");
        InputStream inputStream =connection.getInputStream();
        return inputStream;
    }
    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case SUCCEED:
                listener.onSucced();
                break;
            case FAILED:
                listener.onFailed();
                break;
                default:
                    break;
        }
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress>0){
        }
        listener.onProgress(progress);
    }
    private int getId(String url)throws Exception{
        ContentValues values = new ContentValues();
        dbhelp = new MyDatabaseHelper(context,"Stories.db",null,1);
        db = dbhelp.getWritableDatabase();
            String content = getContent(url);
            values.put("allStory",content);
            db.insert("Allstory",null,values);
            values.clear();
            Gson gson = new Gson();
            allStories = gson.fromJson(content,AllStories.class);
        allId= new ArrayList<>();
        for (int i = 0;i<allStories.stories.size();i++){
            String id = allStories.stories.get(i).id;
            cacheView(allStories.stories.get(i).images.get(0));
            allId.add(id);
        }
        for (int j = 0;j<allStories.top_stories.size();j++){
            cacheView(allStories.top_stories.get(j).image);
            allId.add(allStories.top_stories.get(j).id);
        }
        return allId.size();
    }
    private int toDatabase(WebContent content)throws Exception {
        ContentValues values = new ContentValues();
        if (content.id != null) {
           Cursor cursor = db.query("Story", null, "id=?", new String[]{content.id}, null, null, null);
            if (cursor.getCount() == 0) {
                values.put("id", content.id);
                values.put("body", content.body);
                values.put("share_url", content.share_url);
                values.put("image", content.image);
                cacheView(content.image);
                values.put("image_source", content.image_source);
                values.put("title", content.title);
                db.insert("Story",null,values);
                values.clear();
            }
        }
        return 1;
    }

    private void cacheView(final String url) {
        HttpUtil.getInputStram(url, new GetInputListener() {
            @Override
            public void onInputStream(InputStream inputStream) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                lruCache.addCache(url,bitmap);
            }
        });
    }
}