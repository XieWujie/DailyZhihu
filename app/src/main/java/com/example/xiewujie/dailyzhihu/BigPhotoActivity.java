package com.example.xiewujie.dailyzhihu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.example.xiewujie.dailyzhihu.mytool.MyLruCache;
import com.example.xiewujie.dailyzhihu.mytool.SavePhoto;

import java.io.InputStream;

public class BigPhotoActivity extends AppCompatActivity {
ImageView bigPhoto;
private MyLruCache lruCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_photo);
        bigPhoto = (ImageView)findViewById(R.id.big_photo);
        lruCache = MyLruCache.getInstanse();
       String url = getIntent().getStringExtra("url");
        setView(url);
        savePhoto(url);
    }
    private void setView(String url){
        if (lruCache.getCache(url)!=null){
            bigPhoto.setImageBitmap(lruCache.getCache(url));
        }else {
            HttpUtil.getInputStram(url, new GetInputListener() {
                @Override
                public void onInputStream(InputStream inputStream) {
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bigPhoto.setImageBitmap(bitmap);
                        }
                    });
                }
            });
        }
    }
    private void savePhoto(final String url){
        bigPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BigPhotoActivity.this);
                builder.setItems(new String[]{ "保存图片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                SavePhoto.save(url,BigPhotoActivity.this);
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }
}
