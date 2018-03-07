package com.example.xiewujie.dailyzhihu;
import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.xiewujie.dailyzhihu.adapter.MyAdapter;
import com.example.xiewujie.dailyzhihu.download.Download;
import com.example.xiewujie.dailyzhihu.download.DownloadListener;
import com.example.xiewujie.dailyzhihu.myJson.AllStories;
import com.example.xiewujie.dailyzhihu.myJson.Editors;
import com.example.xiewujie.dailyzhihu.myJson.Others;
import com.example.xiewujie.dailyzhihu.myJson.Stories;
import com.example.xiewujie.dailyzhihu.myJson.Theme;
import com.example.xiewujie.dailyzhihu.myJson.TopStories;
import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.example.xiewujie.dailyzhihu.mytool.MyApplication;
import com.google.gson.Gson;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by xiewujie on 2018/1/29.
 */

public class MainActivity extends BaseActivity implements DownloadListener{
    public static final int PROGRESS = 0;
    private static final int FRESH = 1;
    private SQLiteDatabase db;
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    MyAdapter adapter;
    RecyclerView drawerRecycler;
    MyAdapter drawerAdapter;
    SharedPreferences.Editor editor;
    List<Editors> elist = new ArrayList<>();//用于加载主编头像
    List<Stories> mlist = new ArrayList<>();//加载stories的list
    List<Others> olist = new ArrayList<>(); //editor的信息的list
    List<TopStories> tlist = new ArrayList<>(); //加载top_stories
    ContentValues values = new ContentValues(); //数据库用于insert
    SwipeRefreshLayout swipeRefreshLayout;
    Myhandle hander;
    private int m = 0;  //用于判断获取的日期类型
    static int mode = 1; //用于判断日间/夜间模式
    MyDatabaseHelper databaseHelper;
    TextView downloadText;
    Message message ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerRecycler = (RecyclerView) findViewById(R.id.drawer_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.home_recycle_view);
        hander = new Myhandle(MainActivity.this);
        databaseHelper = new MyDatabaseHelper(MyApplication.getContext(), "Stories.db", null, 1);
        db = databaseHelper.getWritableDatabase();
        checkpermission(); //获取权限
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);//设置toolbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        drawerAdapter = new MyAdapter(null, null, olist, null);
        adapter = new MyAdapter(mlist, tlist, null, elist);
        initData();
        initDrawerData();
        initDrawerView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        /*
        *用于下拉加载
         */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (!recyclerView.canScrollVertically(1)) {
                        initNext();
                    }
                }
                if (dy < 0) {
                    if (!recyclerView.canScrollVertically(-1)) {
                        if (tlist.size() > 1)
                            setTitle("首页");
                    }
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        drawerAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item = menu.findItem(R.id.menu_night); //得到是否为夜间模式的item，设置此item的title
        nightModeText(item);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.menu_setting:
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_night:
                editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                if (mode%2==1)
                editor.putBoolean("nightMode",true);
                editor.apply();
                isNight = true;
                if (mode%2==0){
                    editor.putBoolean("nightMode",false);
                    editor.apply();
                    isNight = false;
                }
                nightModeText(item);
                mode++;
               recreate();
                break;
                default:
                    break;
        }
        return true;
    }

    @Override
    public void setDayMode() {
        super.setDayMode();
    }
    private void nightModeText(MenuItem modeItem){
        if (modeItem!=null)
        if (isNight){
            modeItem.setTitle("日间模式");
        }else {
            modeItem.setTitle("夜间模式");
        }
    }

    private void initData() {
        final String latestAddress = "http://news-at.zhihu.com/api/4/news/latest";
        Cursor cursor = db.query("Allstory",null,null,null,null,null,null);
        if (cursor.getCount()>0){
            if (cursor.moveToFirst()){//从数据库获取首页资源
                    String content = cursor.getString(cursor.getColumnIndex("allStory"));
                    Gson gson = new Gson();
                    AllStories allStories = gson.fromJson(content,AllStories.class);
                    NetworkInfo networkInfo= MyApplication.getNetworkInfo();
                if (networkInfo!=null&&networkInfo.isAvailable()) {
                        initAllList(latestAddress);
                }else {
                    initList(allStories);
                }
            }
            cursor.close();
        }else if (cursor.getCount()==0) {
        initAllList(latestAddress);//从网络获取首页资源
         }
    }
/*
*获取过去的新闻
 */
    private void initNext() {
        String datebefore = getDayBefore(m, 0);
        final String beforeOneDayAddress = "https://news-at.zhihu.com/api/4/news/before/" + datebefore;
        initAllList(beforeOneDayAddress);
        m++;
    }
/*
*得到stories
 */
    private synchronized void initAllList(String address) {
        HttpUtil.sendHttpRequest(address, new CallbackListener() {
            @Override
            public void onFinish(String response) {
                String content = response;
                Gson gson = new Gson();
                AllStories allStories = null;
                allStories = gson.fromJson(content, AllStories.class);
                initList(allStories);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
/*
*根据stories添加到list
 */
    private synchronized void initList(final AllStories allStories) {
        if (allStories.stories != null) {
            for (int i = 0; i < allStories.stories.size(); i++) {
                Stories stories = new Stories();
                if (i == 0 && allStories.stories.size() > 5) {
                    if (m == 0) {
                        stories.date = "今日新闻";
                    } else {
                        final String date = getDayBefore(m, 1);
                        stories.date = date;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tlist.size() > 1)
                                    setTitle(date);
                            }
                        });
                    }
                }
                stories.images = allStories.stories.get(i).images;
                stories.title = allStories.stories.get(i).title;
                stories.id = allStories.stories.get(i).id;
                mlist.add(stories);
            }
        }
        if (allStories.top_stories != null) {
            for (int j = 0; j < allStories.top_stories.size(); j++) {
                tlist.add(allStories.top_stories.get(j));
            }
        } else if (allStories.top_stories == null) {
            if (allStories.description != null && allStories.image != null) {
                TopStories topStories = new TopStories();
                topStories.title = allStories.description;
                topStories.image = allStories.image;
                tlist.add(topStories);
                if (allStories.editors != null && allStories.editors.size() > 0) {
                    for (int k = 0; k < allStories.editors.size(); k++) {
                        elist.add(allStories.editors.get(k));
                    }
                }
            }
        }
        refresh();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                drawerAdapter.notifyDataSetChanged();
            }
        });
    }
/*
*获取Drawerview的数据，并优先从数据库获取
 */
    private void initDrawerData() {
        Cursor cursor = db.query("Theme", null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            final String address = "https://news-at.zhihu.com/api/4/themes";
            HttpUtil.sendHttpRequest(address, new CallbackListener() {
                @Override
                public void onFinish(String response) {
                    String jsonContent = response;
                    values.put("theme", jsonContent);
                    db.insert("Theme", null, values);
                    values.clear();
                    setOlist(jsonContent);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        } else if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                String jsonContent = cursor.getString(cursor.getColumnIndex("theme"));
                setOlist(jsonContent);
            }
        }
        addListener();//给每个theme添加listener
        drawerAdapter.notifyDataSetChanged();
    }
/*
*editor的信息list的添加
 */
    private void setOlist(String jsonContent) {
        Gson gson = new Gson();
        Theme theme = gson.fromJson(jsonContent, Theme.class);
        for (int i = 0; i < theme.others.size(); i++) {
            olist.add(theme.others.get(i));
        }
    }
/*
*获取过往日期，并转换格式
 */
    private String getDayBefore(int dayBefore, int type) {
        dayBefore = -dayBefore;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayBefore);
        Date time = calendar.getTime();
        String date = "";
        if (type == 0) {
            date = new SimpleDateFormat("yyyyMMdd").format(time);
            return date;
        } else if (type == 1) {
            StringBuilder builder = new StringBuilder();
            builder.append(new SimpleDateFormat("yyyy年MM月dd日").format(time));
            builder.append("  ");
            builder.append(new SimpleDateFormat("EEEE", Locale.CHINA).format(time));
            date = builder.toString();
        }
        return date;
    }

    private void initDrawerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        drawerRecycler.setLayoutManager(layoutManager);
        drawerRecycler.setAdapter(drawerAdapter);
        drawerAdapter.notifyDataSetChanged();
    }
/*
*drawerview的listener
 */
    private void addListener() {
        drawerAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void itemClick(View view, int position) {

                if (position != -2) {
                    mlist.clear();
                    tlist.clear();
                    elist.clear();
                    adapter.notifyDataSetChanged();
                    if (position == -1) {
                        initData();
                    } else if (position >= 0) {
                        Others others = olist.get(position);
                        setTitle(others.name);
                        String id = others.id;
                        changeList(id);
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (position == -2) {
                    final String latestAddress = "http://news-at.zhihu.com/api/4/news/latest";
                    Download download = new Download(MainActivity.this);
                    download.execute(latestAddress);
                    downloadText = (TextView) view.findViewById(R.id.download);
                    downloadText.setText("准备下载");
                }
                adapter.notifyDataSetChanged();
            }
        });

    }
    /*
    *根据id加载主题日报
     */
    private void changeList(String id) {
        String newaddress = "https://news-at.zhihu.com/api/4/theme/" + id;
        initAllList(newaddress);
    }
/*
*刷新
 */
    private void refresh() {
     hander.postDelayed(new Runnable() {
         @Override
         public void run() {
             adapter.notifyDataSetChanged();
             drawerAdapter.notifyDataSetChanged();
         }
     },2000);
    }
static class Myhandle extends Handler{
        WeakReference weakReference;
        MainActivity activity;

    public Myhandle(MainActivity activity) {
        weakReference = new WeakReference(activity);
        this.activity = activity;
    }
/*
*主要用于更新downloadText的text
 */
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case PROGRESS:
                if (msg.arg1!=-1) {
                    if (activity.downloadText != null) {
                        activity.downloadText.setText(msg.arg1 + "");
                        if (msg.arg1 == 100)
                            activity.downloadText.setText("下载完成");
                    }
                }else if (msg.arg1==-1){
                    activity.downloadText.setText("离线下载");
                    Toast.makeText(activity,"下载失败",Toast.LENGTH_SHORT).show();
                }
        }
    }
}
/*
*DownloadListener onProgress，onSucced，onFailed方法的复写，用于更新下载进度
 */
    @Override
    public void onProgress(final int progress) {
        message = new Message();
        message.arg1 = progress;
        hander.sendMessage(message);
    }

    @Override
    public void onSucced() {
        message = new Message();
        message.what = PROGRESS;
            message.arg1 = 100;
            if (hander!=null)
            hander.sendMessage(message);
    }
   public void onFailed(){
        message = new Message();
        message.what = PROGRESS;
        message.arg1 = -1;
        if (hander==null){
            hander = new Myhandle(MainActivity.this);
        }
        hander.sendMessage(message);
   }
    private void checkpermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case 1:
               if (!(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                   Toast.makeText(this,"拒绝权限将不能存储照片",Toast.LENGTH_SHORT).show();
               }
               break;
       }
    }
}