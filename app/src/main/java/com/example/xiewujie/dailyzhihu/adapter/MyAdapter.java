package com.example.xiewujie.dailyzhihu.adapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xiewujie.dailyzhihu.EditorActivity;
import com.example.xiewujie.dailyzhihu.GetInputListener;
import com.example.xiewujie.dailyzhihu.R;
import com.example.xiewujie.dailyzhihu.WebviewActivity;
import com.example.xiewujie.dailyzhihu.myJson.AllStories;
import com.example.xiewujie.dailyzhihu.myJson.Editors;
import com.example.xiewujie.dailyzhihu.myJson.Others;
import com.example.xiewujie.dailyzhihu.myJson.Stories;
import com.example.xiewujie.dailyzhihu.myJson.TopStories;
import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.example.xiewujie.dailyzhihu.mytool.MyApplication;
import com.example.xiewujie.dailyzhihu.mytool.MyLruCache;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xiewujie on 2018/1/29.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>implements View.OnClickListener{
    private List<Stories> mlist ; ////今日新闻和过往新闻的list
    private ArrayList<View>alist; //pagerview的list
    private List<TopStories> tlist;//顶部新闻的集合
    private List<Editors> elist;  //主题新闻editor的list
    private List<Others> olist;   //editor的信息的list
    private MyLruCache lruCache = MyLruCache.getInstanse(); //缓存对象的创建
    public static boolean is_no_pic = false;
    PagerAdapter pagerAdaper;  //pagerview的adapter
    Context context = MyApplication.getContext();
    private OnItemClickListener onItemClickListener; //从内部创建一个listener给外部使用
    Handler mhandler = new Handler();
    ViewHolder pagerHolder;
    private int pagerPosition = 0;
    private static final int MOBILE_TYPE = MyApplication.getMobileType();  //获取网路类型
    private static final int TIMEDelay = 9000;   //顶部新闻轮播的间隔时间
    private static final int HEAD_TYPE = 0;     //顶部新闻的类型
    private static final int NEAT_TYPE = 1;     //今日新闻和以前新闻
    private static final int DRAWRE_TYPE = 2;   //主题新闻类型
    private static final int DRAWER_HOME_TYPE = 3; //主题新闻position==1时的类型
    class ViewHolder extends RecyclerView.ViewHolder{
        TextView text_date; //日期
        TextView text_title; //新闻标题
        ImageView image_icon; //新闻图片
        ViewPager viewPager;
        RelativeLayout homeLayouut;
        LinearLayout indView;
        TextView drawerListTheme; //主题名称
        LinearLayout list_linearlayout;//text_date的父布局，用于动态添加editor头像
        LinearLayout newsLayout;
        TextView downloadText;
        TextView diver;
        public ViewHolder(View view){
            super(view);
            downloadText = (TextView)view.findViewById(R.id.download);
            text_date = (TextView)view.findViewById(R.id.recycler_view_date);
            image_icon = (ImageView)view.findViewById(R.id.recycler_view_icon);
            text_title = (TextView)view.findViewById(R.id.title_view);
            viewPager = (ViewPager)view.findViewById(R.id.view_pager);
            indView = (LinearLayout)view.findViewById(R.id.ind_view);
            drawerListTheme = (TextView)view.findViewById(R.id.drawer_list_theme);
            list_linearlayout = (LinearLayout)view.findViewById(R.id.list_item_linearlayout);
            homeLayouut = (RelativeLayout)view.findViewById(R.id.home_layout);
            newsLayout = (LinearLayout)view.findViewById(R.id.news_layout);
            diver = (TextView)view.findViewById(R.id.diver);
        }
    }
    public MyAdapter(List<Stories> mist, List<TopStories> tlist, List<Others> olist,List<Editors> elist){
        this.tlist = tlist;
        this.mlist = mist;
        this.olist = olist;
        this.elist = elist;
    }
    /*
    *从本地获取是否为无图模式
     */
    private boolean getIsNoPic(){
        SharedPreferences preferences = context.getSharedPreferences("data",Context.MODE_PRIVATE);
        boolean b = preferences.getBoolean("isNoPic",false);
        return b;
    }
    /*
    *内部创建一个listener
     */
    public static interface OnItemClickListener{
        void itemClick(View view,int position);
    }
    /*
    *判断adapter的加载类型
     */
    @Override
    public int getItemViewType(int position) {
        if (mlist!=null) {
            if (position == 0)
                return HEAD_TYPE;
            return NEAT_TYPE;
        }else if (mlist==null){
            if (position==0)
                return DRAWER_HOME_TYPE;
        }
        return DRAWRE_TYPE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        if (viewType==HEAD_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.head_pager,parent,false);
            holder = new ViewHolder(view);
        }else if (viewType==NEAT_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_next_item, parent, false);
            holder = new ViewHolder(view);
        }else if (viewType ==DRAWRE_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list_item,parent,false);
            holder = new ViewHolder(view);
            holder.drawerListTheme.setOnClickListener(this);
        }else if (viewType==DRAWER_HOME_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_home_item,parent,false);
            holder = new ViewHolder(view);
        }
        return holder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        is_no_pic = getIsNoPic();
        holder.setIsRecyclable(false);
            try {
                if (getItemViewType(position)==NEAT_TYPE) {
                    final Stories stories = mlist.get(position - 1);
                    if (elist.isEmpty()) {          //如果没有editor则加载日期
                        if (stories.date != null && stories.date.length() > 2) {
                            holder.text_date.setMaxHeight(110);
                            holder.text_date.setMinHeight(100);
                            holder.text_date.setText(stories.date);
                            holder.diver.setVisibility(View.GONE);
                            holder.list_linearlayout.setVisibility(View.VISIBLE);
                        }else {
                            holder.list_linearlayout.setVisibility(View.GONE);
                            holder.diver.setVisibility(View.VISIBLE);
                        }
                    } else if (!elist.isEmpty()) {//如果elist不为空则动态加载editor头像
                        if (holder.getAdapterPosition() == 1) {
                            holder.text_date.setText("主编 ");
                            for (int j = 0; j < elist.size(); j++) {
                                ImageView circleImageView = new CircleImageView(context);
                                circleImageView.setMaxHeight(110);
                                circleImageView.setMinimumHeight(100);
                                circleImageView.setMaxWidth(110);
                                setView(elist.get(j).avatar,circleImageView);
                                FrameLayout frameLayout = new FrameLayout(context);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(90, 90);
                                holder.list_linearlayout.addView(circleImageView, layoutParams);
                            }
                            holder.list_linearlayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {  //设置主编头像的点击事件
                                    Intent intent = new Intent(context, EditorActivity.class);
                                    intent.putExtra("elist",(Serializable)elist);
                                    context.startActivity(intent);
                                }
                            });
                            holder.list_linearlayout.setVisibility(View.VISIBLE);
                            holder.diver.setVisibility(View.GONE);
                        }else {
                            holder.diver.setVisibility(View.VISIBLE);
                            holder.list_linearlayout.setVisibility(View.GONE);
                        }
                    }
                    holder.text_title.setText(stories.title);
                    if (stories.images!=null&&holder.image_icon!=null)
                    setView(stories.images.get(0),holder.image_icon);
                    holder.newsLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {  //设置点击事件
                            toIntent(stories.id);
                        }
                    });
                }
                else if (getItemViewType(position)==HEAD_TYPE){
                    initView(holder);//加载top_stoties
                }else if (getItemViewType(position)==DRAWRE_TYPE){
                    holder.drawerListTheme.setText(olist.get(position-1).name);
                    holder.drawerListTheme.setTag(position-1);
                }else if (getItemViewType(position)==DRAWER_HOME_TYPE){
                    holder.downloadText.setText("离线下载");
                    holder.homeLayouut.setOnClickListener(this);
                    holder.downloadText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) { //离线下载的点击事件
                            if (onItemClickListener!=null){
                                onItemClickListener.itemClick(view,-2);
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        public void onClick(View v) {
            if (onItemClickListener != null) {
                switch (v.getId()) {
                    case R.id.drawer_list_theme:
                        onItemClickListener.itemClick(v, (int) v.getTag());
                        break;
                    case R.id.home_layout:
                        onItemClickListener.itemClick(v,-1);
                        break;
                    case R.id.list_item_linearlayout:
                        Intent intent = new Intent(context, EditorActivity.class);
                        intent.putExtra("elist",(Serializable)elist);
                        context.startActivity(intent);
                        break;
                }
            }
        }
        private void toIntent(String id){
            Intent intent = new Intent(context,WebviewActivity.class);
            intent.putExtra("id",id);
            context.startActivity(intent);
        }
        public void setOnItemClickListener(OnItemClickListener listener){
            onItemClickListener = listener;
        }
    @Override
    public int getItemCount() {
        if (mlist==null){
            return olist.size()+1;
        }
        return mlist.size()+1;
    }
    /*
    *加载top_stories的方法
     */
    public void initView(final ViewHolder holder) {
        alist = new ArrayList<>();
        for (int i = 0; i < tlist.size(); i++) {
            //动态添加图片和新闻标题
            TextView textView = new TextView(context);
            textView.setText(tlist.get(i).title);
            textView.setTextSize(20);
            textView.setTextColor(Color.WHITE);
            textView.setGravity(Gravity.BOTTOM);
            textView.setPadding(20, 0, 20, 60);
            textView.setBackgroundColor(Color.TRANSPARENT);
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            setView(tlist.get(i).image, imageView);
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.addView(imageView);
            frameLayout.addView(textView);
            alist.add(frameLayout);
            final String id = tlist.get(i).id;
            if (id != null)
                frameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toIntent(id);
                    }
                });
            if (tlist.size()>1) {
                //viewpager圆形提示点的动态添加
                Button view = new Button(context);
                view.setBackgroundResource(R.drawable.diabled);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
                params.leftMargin = 10;
                holder.indView.addView(view, params);
                if (pagerPosition==0||pagerPosition%alist.size()==0){
                    holder.indView.getChildAt(0).setBackgroundResource(R.drawable.enable);
                }
            }
        }
        pagerHolder = holder;
        holder.viewPager.setAdapter(pagerAdaper);
        pagerAdaper = new MyPagerAdaper(alist);
        if (tlist.size() > 1) {
            if (pagerPosition == 0) {
                if (holder.indView != null)
                    holder.indView.getChildAt(0).setBackgroundResource(R.drawable.enable);
            }
            mhandler.postDelayed(runnable, TIMEDelay);//自动轮播
            holder.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    for (int i = 0; i < alist.size(); i++) {
                        if (i == position) {
                            holder.indView.getChildAt(i).setBackgroundResource(R.drawable.enable);
                        } else {
                            holder.indView.getChildAt(i).setBackgroundResource(R.drawable.diabled);
                        }
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }
    //自动轮播的定时线程
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                pagerPosition++;
                mhandler.postDelayed(this,TIMEDelay);
                if (pagerHolder!=null) {
                    if (alist.size()!=0)
                    pagerHolder.viewPager.setCurrentItem(pagerPosition % alist.size());
                }
            }
    };
    /*
    *优先加载内存缓存的图片，如果为无图模式且网络类型不是2/3G类型则从网络加载图片，
    * 为2/3G则不加载图片
     */
    private void setView(final String url,final ImageView image_icon){
        if (lruCache.getCache(url)==null) {
            if (is_no_pic) {
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
    /*
    *从网络获取图片
     */
    private void getView(final String url,final ImageView image_icon){
        HttpUtil.getInputStram(url, new GetInputListener() {
            @Override
            public void onInputStream(InputStream inputStream) {
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                mhandler.post(new Runnable() {
                    @Override
                    public void run() {
                        image_icon.setImageBitmap(bitmap);
                    }
                });
                lruCache.addCache(url,bitmap);
            }
        });
    }
}













