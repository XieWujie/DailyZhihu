package com.example.xiewujie.dailyzhihu.mytool;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by xiewujie on 2018/2/13.
 */

public class MyLruCache {
    private LruCache<String,Bitmap> lruCache;
    int maxMemory = (int)Runtime.getRuntime().maxMemory();
    private static MyLruCache myLruCache;
    private MyLruCache(){
        lruCache = new LruCache<String,Bitmap>(maxMemory/16){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    public static MyLruCache getInstanse(){
        if (myLruCache==null){
            myLruCache = new MyLruCache();
        }
        return myLruCache;
    }
    public void addCache(String key,Bitmap bitmap){
        if (lruCache.get(key)!=bitmap){
            lruCache.put(key,bitmap);
        }
    }
    public Bitmap getCache(String key){
        if (key!=null)
        return lruCache.get(key);
        return null;
    }
    public void removeAll(){
        lruCache.evictAll();
    }
}
