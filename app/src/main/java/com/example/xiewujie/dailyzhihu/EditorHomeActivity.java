package com.example.xiewujie.dailyzhihu;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toolbar;

public class EditorHomeActivity extends BaseActivity {
    WebView webView;
    android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_home);
        webView = (WebView)findViewById(R.id.editor_home);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.editor_home_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        String id = getIntent().getStringExtra("id");
        String url = "https://news-at.zhihu.com/api/4/editor/"+id+"/profile-page/android";
        if(isNight){
            webView.setBackgroundColor(Color.DKGRAY);
        }else {
            webView.setBackgroundColor(Color.WHITE);
        }
        webView.loadUrl(url);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case android.R.id.home:
               finish();
               break;
       }
       return true;
    }
}
