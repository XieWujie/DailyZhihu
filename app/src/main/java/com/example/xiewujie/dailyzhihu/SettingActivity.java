package com.example.xiewujie.dailyzhihu;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiewujie.dailyzhihu.adapter.MyAdapter;
import com.example.xiewujie.dailyzhihu.mytool.MyLruCache;

public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{
    CheckBox autoDownload;
    CheckBox noPic;
    TextView clear;
    android.support.v7.widget.Toolbar toolbar;
    SharedPreferences.Editor editor;
    SharedPreferences preferences;
    MyDatabaseHelper dbhelp;
    MyLruCache lruCache;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        autoDownload = (CheckBox)findViewById(R.id.menu_auto_download_check_box);
        noPic = (CheckBox)findViewById(R.id.menu_no_pic_check_box);
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.setting_toolbar);
        clear = (TextView)findViewById(R.id.menu_clear_text);
        setSupportActionBar(toolbar);
        editor = getSharedPreferences("data",MODE_PRIVATE).edit();
        preferences = getSharedPreferences("data",MODE_PRIVATE);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        autoDownload.setChecked(preferences.getBoolean("isAutodownload",false));
        noPic.setChecked(preferences.getBoolean("isNoPic",false));
        autoDownload.setOnCheckedChangeListener(this);
        noPic.setOnCheckedChangeListener(this);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCache();
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
            finish();
        }
        return true;
    }
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.menu_auto_download_check_box:
                StartActivity.isAutodownload = b;
                editor.putBoolean("isAutodownload",b);
                editor.apply();
                break;
            case R.id.menu_no_pic_check_box:
                MyAdapter.is_no_pic = b;
                WebviewActivity.isNoPic = b;
                editor.putBoolean("isNoPic",b);
                editor.apply();
                break;

        }
    }
    private void clearCache(){
        dbhelp = new MyDatabaseHelper(SettingActivity.this,"Stories.db",null,1);
        SQLiteDatabase db = dbhelp.getWritableDatabase();
        db.delete("Theme",null,null);
        db.delete("Story",null,null);
        db.delete("Allstory",null,null);
        db.delete("Comment",null,null);
        lruCache = MyLruCache.getInstanse();
        lruCache.removeAll();
        Toast.makeText(SettingActivity.this,"清理成功",Toast.LENGTH_SHORT).show();
    }

}


