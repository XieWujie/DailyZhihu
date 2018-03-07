package com.example.xiewujie.dailyzhihu.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xiewujie.dailyzhihu.GetInputListener;
import com.example.xiewujie.dailyzhihu.R;
import com.example.xiewujie.dailyzhihu.myJson.Comments;
import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.example.xiewujie.dailyzhihu.mytool.MyApplication;
import com.example.xiewujie.dailyzhihu.mytool.MyLruCache;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xiewujie on 2018/2/7.
 */
/*
*评论的adapter
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    List<Comments> mlist;
    MyLruCache lruCache = MyLruCache.getInstanse();
    android.os.Handler handler = new android.os.Handler();
    Context context = MyApplication.getContext();
    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView ;
        TextView nameText;
        TextView supportText;
        TextView contentText;
        TextView dateText;
        TextView replyAuthorText;
        TextView replyContentText;

        public ViewHolder(View view) {
            super(view);
            this.circleImageView = (CircleImageView) view.findViewById(R.id.comment_circle_view);
            this.nameText = (TextView)view.findViewById(R.id.comment_name);
            this.supportText = (TextView)view.findViewById(R.id.comment_support);
            this.contentText = (TextView)view.findViewById(R.id.comment_content);
            this.dateText = (TextView)view.findViewById(R.id.comment_date);
            replyAuthorText = (TextView)view.findViewById(R.id.reply_comment_author);
            replyContentText = (TextView)view.findViewById(R.id.reply_content);
        }
    }

    public CommentAdapter(List<Comments> list){
        mlist = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comments comment = mlist.get(position);
        setView(comment.avatar,holder.circleImageView);
        holder.nameText.setText(comment.author);
        holder.supportText.setText(comment.likes);
        holder.dateText.setText(comment.time);
        holder.contentText.setText(comment.content);
        if (comment.reply_to!=null){
           holder.replyContentText.setText("");
            String author = comment.reply_to.author;
            String authorHtml = "<font color='black'>"+"//"+author+":"+"</font>";
            holder.replyContentText.append(Html.fromHtml(authorHtml));
            holder.replyContentText.append(comment.reply_to.content);
        }
    }
    private void setView(final String url, final ImageView imageView){
       HttpUtil.getInputStram(url, new GetInputListener() {
           @Override
           public void onInputStream(InputStream inputStream) {
               final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
               lruCache.addCache(url,bitmap);
               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       imageView.setImageBitmap(bitmap);
                   }
               });
           }
       });
    }
    @Override
    public int getItemCount() {
        return mlist.size();
    }
}

