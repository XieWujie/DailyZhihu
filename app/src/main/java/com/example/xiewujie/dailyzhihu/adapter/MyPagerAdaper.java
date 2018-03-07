package com.example.xiewujie.dailyzhihu.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by xiewujie on 2018/1/29.
 */

public class MyPagerAdaper extends PagerAdapter {
    private ArrayList<View> list;
    public MyPagerAdaper() {
    }
    public MyPagerAdaper(ArrayList<View> list){
        super();
        this.list = list;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position));
        return list.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       container.removeView(list.get(position));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
