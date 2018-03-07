package com.example.xiewujie.dailyzhihu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiewujie.dailyzhihu.download.DownloadService;
import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.example.xiewujie.dailyzhihu.mytool.MyLruCache;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
/*
*开始界面的activity
 */
public class StartActivity extends BaseActivity {
    ImageView startView;
    TextView startText;
    MyLruCache lruCache = MyLruCache.getInstanse();
    Handler handler = new Handler();
    public static boolean isAutodownload =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startView = (ImageView)findViewById(R.id.start_image);
        startText = (TextView)findViewById(R.id.start_text);
        initView();
        isAutodownload = getpref();

        autoDownload();
    }
    private boolean getpref(){
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        boolean b = preferences.getBoolean("isAutodownload",false);
        return b;
    }
    private void initView(){
        final String address = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(address, new CallbackListener() {
            @Override
            public void onFinish(String response) {
                String content = response.toString();
                final String imageUrl  = content;
                    setView(imageUrl, startView);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void setView(final String url,final ImageView imageView){
        if (lruCache.getCache(url)==null) {
            HttpUtil.getInputStram(url, new GetInputListener() {
                @Override
                public void onInputStream(InputStream inputStream) {
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    lruCache.addCache(url, bitmap);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);

                        }
                    });
                    handler.postDelayed(runnable, 2300);
                }
            });
        }else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imageView.setImageBitmap(lruCache.getCache(url));
                }
            });
            handler.postDelayed(runnable, 2300);
        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(StartActivity.this,MainActivity.class);
            startActivity(intent);
        }
    };
    private void autoDownload(){
        Intent intent = new Intent(StartActivity.this,DownloadService.class);
        if (isAutodownload){
            startService(intent);
        }else {
           stopService(intent);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
