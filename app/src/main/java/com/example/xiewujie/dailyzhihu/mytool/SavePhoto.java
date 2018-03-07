package com.example.xiewujie.dailyzhihu.mytool;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.xiewujie.dailyzhihu.BigPhotoActivity;
import com.example.xiewujie.dailyzhihu.WebviewActivity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xiewujie on 2018/2/24.
 */

public class SavePhoto {

    public static void save(String url, Context context){
        MyLruCache lruCache = MyLruCache.getInstanse();
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/zhihu/";
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHss", Locale.getDefault());
        String time = format.format(new Date());
        File file = new File(dir+time+".jpg");
        try{
            if (!file.exists()){
                file.getParentFile().mkdir();
                file.createNewFile();
            }
           FileOutputStream outputStream = new FileOutputStream(file);
            if (lruCache.getCache(url)!=null){
                Bitmap bitmap = lruCache.getCache(url);
                bitmap.compress(Bitmap.CompressFormat.JPEG,90,outputStream);
                outputStream.flush();
                outputStream.close();
                Uri uri = Uri.fromFile(file);
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void savePhoto(final Context context,final String url, ImageView imageView){
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"查看图片", "保存图片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(context, BigPhotoActivity.class);
                                intent.putExtra("url", url);
                                context.startActivity(intent);
                                break;
                            case 1:
                                SavePhoto.save(url,context);
                                Toast.makeText(context,"保存成功",Toast.LENGTH_SHORT).show();
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
