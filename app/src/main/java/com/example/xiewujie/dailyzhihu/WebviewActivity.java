package com.example.xiewujie.dailyzhihu;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xiewujie.dailyzhihu.myJson.ContentExtra;
import com.example.xiewujie.dailyzhihu.myJson.WebContent;
import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.example.xiewujie.dailyzhihu.mytool.MyApplication;
import com.example.xiewujie.dailyzhihu.mytool.MyLruCache;
import com.example.xiewujie.dailyzhihu.mytool.SavePhoto;
import com.google.gson.Gson;

import java.io.InputStream;

/**
 * Created by xiewujie on 2018/2/4.
 */
public class WebviewActivity extends BaseActivity implements View.OnClickListener{
    Context context = MyApplication.getContext();
    WebView webView ;
    WebContent webAll;
    TextView collTitle;
    ImageView collImage;
    TextView collResource;
    WebSettings settings;
    Toolbar toolbar;
    TextView supportText;
    TextView commentText;
    RelativeLayout commentLayout;
    String id;
    SharedPreferences preferences;
    public static boolean isNoPic ;
    public static int MOBILE_TYPE = 0;
    private ContentExtra extra;
    private MyDatabaseHelper dbHelp;
    private Handler mhandler = new Handler();
    private MyLruCache lruCache = MyLruCache.getInstanse();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        collTitle = (TextView)findViewById(R.id.coll_title);
        collImage = (ImageView)findViewById(R.id.coll_image);
        collResource = (TextView)findViewById(R.id.coll_source);
        supportText = (TextView)findViewById(R.id.support_text);
        commentText = (TextView)findViewById(R.id.comment_text);
        commentLayout = (RelativeLayout)findViewById(R.id.menu_comment);
        getPref();
        setActionBar();
        webviewSet();
        commentLayout.setOnClickListener(this);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
         initExtra(id);
        if (id!=null){
            initWebview(id);
        }else {
            initWebview("3892357");
        }
    }
    private void getPref(){
        preferences = getSharedPreferences("data",MODE_PRIVATE);
        isNoPic = preferences.getBoolean("isNoPic",false);
    }
    private void webviewSet(){
        webView = (WebView)findViewById(R.id.web_view);
        settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        boolean isNight = preferences.getBoolean("nightMode",false);
        if (isNight) {
            webView.setBackgroundColor(Color.DKGRAY);
        }else {
            webView.setBackgroundColor(Color.WHITE);
        }
    }
    private void setActionBar(){
        toolbar = (Toolbar)findViewById(R.id.web_toolBar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    /*
    *优先从数据库加载，再从网络获取
     */
    private void initWebview(String id){
        final String CSS_STYPE = "<head><style>img{max-width:100%!important;}</style></head>";//使图片最大为100%
        dbHelp = new MyDatabaseHelper(context,"Stories.db",null,1);
       final SQLiteDatabase db = dbHelp.getWritableDatabase();
        Cursor cursor = db.query("Story",null,"id=?",new String[]{id},null,null,null);
        if ( cursor.getCount()>0) {
            if (cursor.moveToFirst()) {
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String image = cursor.getString(cursor.getColumnIndex("image"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String image_source = cursor.getString(cursor.getColumnIndex("image_source"));
                setView(image,collImage);
                collResource.setText(image_source);
                collTitle.setText(title);
                try {
                    webView.loadDataWithBaseURL(null,CSS_STYPE+body, "text/html", "utf-8",null);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else if (cursor.getCount()==0) {
            String address = "https://news-at.zhihu.com/api/4/news/" + id;
            HttpUtil.sendHttpRequest(address, new CallbackListener() {
                @Override
                public void onFinish(String response) {
                    final String webContent = response.toString();
                    Gson gson = new Gson();
                    webAll = gson.fromJson(webContent, WebContent.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String body =webAll.body;
                            String title  = webAll.title;
                            String image_source = webAll.image_source;
                            String image = webAll.image;
                            setView(image,collImage);
                            collResource.setText(image_source);
                            collTitle.setText(title);
                            try {
                                webView.loadDataWithBaseURL(null,CSS_STYPE+ body, "text/html", "utf-8",null);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            ContentValues values = new ContentValues();
                            values.put("id",webAll.id);
                            values.put("body",body);
                            values.put("share_url",webAll.share_url);
                            values.put("image",image);
                            values.put("image_source",image_source);
                            values.put("title",title);
                            db.insert("Story",null,values);
                            values.clear();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        settings.setBlockNetworkImage(false);
    }
    private  void initExtra(String id){
        if (id!=null){
            String url = "https://news-at.zhihu.com/api/4/story-extra/"+id;
            HttpUtil.sendHttpRequest(url, new CallbackListener() {
                @Override
                public void onFinish(String response) {
                    String content = response.toString();
                    final Gson gson = new Gson();
                    extra = gson.fromJson(content,ContentExtra.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initMene(extra);
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }
    private void initMene(ContentExtra extra){
        if (commentText!=null){
            commentText.setText(extra.comments+"");
        }
        if (supportText!=null){
            supportText.setText(extra.popularity+"");
        }
    }
    public void onClick(View view){
        switch (view.getId()){
            case R.id.menu_comment:
                Intent intent = new Intent(WebviewActivity.this,CommentsActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);
                break;
        }
    }
    /*
    *优先从内存中加载图片，然后根据是否为无图模式和网络类型加载图片
     */
    private void setView(final String url,final ImageView image_icon){
        SavePhoto.savePhoto(WebviewActivity.this,url,image_icon);
        if (lruCache.getCache(url)==null) {
            if (isNoPic) {
                if (MyApplication.MOBILE_NET_2G != MOBILE_TYPE && MyApplication.MOBILE_NET_3G != MOBILE_TYPE) {
                    getView(url,image_icon);
                }
            } else {
                getView(url,image_icon);
            }
        }else {
            image_icon.setImageBitmap(lruCache.getCache(url));
        }
    }
    private void getView(final String url,final ImageView image_icon){
        HttpUtil.getInputStram(url, new GetInputListener() {
            @Override
            public void onInputStream(InputStream inputStream) {
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        image_icon.setImageBitmap(bitmap);
                lruCache.addCache(url,bitmap);
            }
        });
    }
    });
}
}