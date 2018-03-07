package com.example.xiewujie.dailyzhihu;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;

public class BaseActivity extends AppCompatActivity {
    SharedPreferences preferences;
    public static boolean isNight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences("data",MODE_PRIVATE);
        isNight = preferences.getBoolean("nightMode",false);
        setDayMode();
    }
    public  void setDayMode(){
        if (isNight){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        if (!isNight){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
