package com.example.xiewujie.dailyzhihu.myJson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by xiewujie on 2018/1/29.
 */

public class Stories {
    public String date;
    public String title;
    @SerializedName("id")
    public String id;
    public List<String> images;
}
