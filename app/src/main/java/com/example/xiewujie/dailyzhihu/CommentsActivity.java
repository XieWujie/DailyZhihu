package com.example.xiewujie.dailyzhihu;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.xiewujie.dailyzhihu.adapter.CommentAdapter;
import com.example.xiewujie.dailyzhihu.myJson.AllComment;
import com.example.xiewujie.dailyzhihu.myJson.Comments;
import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
public class CommentsActivity extends BaseActivity {
    MyDatabaseHelper dbheop;
    SQLiteDatabase db;
    List<Comments> mlist = new ArrayList<>();
    RecyclerView recyclerView;
    CommentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        recyclerView = (RecyclerView)findViewById(R.id.comment_recycler_view);
        adapter = new CommentAdapter(mlist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        dbheop = new MyDatabaseHelper(CommentsActivity.this,"Stories.db",null,1);
        db = dbheop.getWritableDatabase();
        initData(id);
    }
    private void initData(String id){
        if (id!=null){
            Cursor cursor = db.query("Comment",null,"id=?",new String[]{id},null,null,null);
            if (cursor.getCount()==0) {
                String longUrl = "https://news-at.zhihu.com/api/4/story/" + id + "/long-comments";
                String shortUtl = "https://news-at.zhihu.com/api/4/story/" + id + "/short-comments";
                getContent(longUrl);
                getContent(shortUtl);
            }else if (cursor.getCount()>0){
                if (cursor.moveToFirst()){
                    String content = cursor.getString(cursor.getColumnIndex("comments"));
                    initList(content);
                }
            }
        }
    }
    private void getContent(String url){
        HttpUtil.sendHttpRequest(url, new CallbackListener() {
            @Override
            public void onFinish(String response) {
                String content = response.toString();
                ContentValues values = new ContentValues();
                values.put("comments",content);
                db.insert("Comment",null,values);
                values.clear();
                initList(content);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void initList(String content){
        Gson gson = new Gson();
        AllComment allComment = gson.fromJson(content, AllComment.class);
        for (Comments comment:allComment.comments){
            mlist.add(comment);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
