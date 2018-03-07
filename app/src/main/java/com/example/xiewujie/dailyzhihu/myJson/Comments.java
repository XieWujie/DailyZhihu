package com.example.xiewujie.dailyzhihu.myJson;

import android.support.annotation.VisibleForTesting;

/**
 * Created by xiewujie on 2018/2/7.
 */

public class Comments {
    public String author;
    public String content;
    public String avatar;
    public String time;
    public ReplyTo reply_to;
    public String likes;
    public String id;
   public class ReplyTo{
      public String content;
      public String status;
      public String id;
      public String author;
    }
}
