package com.example.xiewujie.dailyzhihu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xiewujie on 2018/2/5.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CRREATE_STORIERS = "create table Story("
            +"id integer,"
            +"body text,"
            +"title text,"
            +"image text,"
            +"share_url text,"
            +"image_source text)";
    public static final String CREATE_ALLSTORIES = "create table Allstory("
            +"id integer  primary key autoincrement,"
            +"allStory text)";
    public static final String CREATE_DEAWERLIST = "create table Theme("
            +"id integer  primary key autoincrement,"
            +"theme text)";
    public static final String CREATE_COMMENTS = "create table Comment("
            +"id integer,"
            +"comments text)";
    private Context context;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CRREATE_STORIERS);
        sqLiteDatabase.execSQL(CREATE_ALLSTORIES);
        sqLiteDatabase.execSQL(CREATE_DEAWERLIST);
        sqLiteDatabase.execSQL(CREATE_COMMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists Story");
        sqLiteDatabase.execSQL("drop table if exists Allstory");
        sqLiteDatabase.execSQL("drop table if exists Theme");
        sqLiteDatabase.execSQL("drop table if exists Comment");
        onCreate(sqLiteDatabase);
    }
}
