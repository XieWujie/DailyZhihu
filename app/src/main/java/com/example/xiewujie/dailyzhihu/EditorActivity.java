package com.example.xiewujie.dailyzhihu;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.xiewujie.dailyzhihu.R;
import com.example.xiewujie.dailyzhihu.adapter.EditorAdapter;
import com.example.xiewujie.dailyzhihu.myJson.Editors;

import java.util.ArrayList;
import java.util.List;

public class EditorActivity extends BaseActivity {
    RecyclerView recyclerView;
    EditorAdapter adapter;
    List<Editors> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar)findViewById(R.id.editor_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = (RecyclerView)findViewById(R.id.editor_recycler_view);
        list = (List<Editors>)getIntent().getSerializableExtra("elist");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new EditorAdapter(list);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
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
