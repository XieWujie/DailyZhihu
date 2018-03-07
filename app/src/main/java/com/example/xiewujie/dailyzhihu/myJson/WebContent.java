package com.example.xiewujie.dailyzhihu.myJson;

import java.util.List;

/**
 * Created by xiewujie on 2018/2/4.
 */

public class WebContent {
    public String body;
    public String image_source;
    public String title;
    public String image;
    public String share_url;
    public String type;
    public String id;
    public Section section;
    public List<Recommenders> recommenders;
    class Section{
        public String thumbnail;
        public String id;
        public String name;
    }
}
