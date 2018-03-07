package com.example.xiewujie.dailyzhihu.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xiewujie.dailyzhihu.EditorHomeActivity;
import com.example.xiewujie.dailyzhihu.GetInputListener;
import com.example.xiewujie.dailyzhihu.R;
import com.example.xiewujie.dailyzhihu.myJson.Editors;
import com.example.xiewujie.dailyzhihu.mytool.HttpUtil;
import com.example.xiewujie.dailyzhihu.mytool.MyApplication;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by xiewujie on 2018/2/14.
 */

public class EditorAdapter extends RecyclerView.Adapter<EditorAdapter.ViewHolder> {
    List<Editors> mlist;
    Handler handler = new Handler();
    Context context = MyApplication.getContext();
    class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView circleImageView ;
        TextView nameText;
        TextView editorText;
        LinearLayout editorLayout;

        public ViewHolder(View view) {
            super(view);
            this.circleImageView = (CircleImageView) view.findViewById(R.id.editor_circle_view);
            this.nameText = (TextView)view.findViewById(R.id.ni_name);
            editorLayout = (LinearLayout)view.findViewById(R.id.editor_layout);
            editorText = (TextView)view.findViewById(R.id.editor_text);
        }
    }

    public EditorAdapter(List<Editors> list){
        mlist = list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.editor_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Editors editors = mlist.get(position);
        setView(editors.avatar,holder.circleImageView);
        holder.editorText.setText(editors.bio);
        holder.nameText.setText(editors.name);
        holder.editorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditorHomeActivity.class);
                intent.putExtra("id",editors.id);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
    private void setView(final String url, final ImageView imageView){
        HttpUtil.getInputStram(url, new GetInputListener() {
            @Override
            public void onInputStream(InputStream inputStream) {
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }
}
