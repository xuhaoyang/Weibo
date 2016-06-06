package com.xhy.weibo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.xhy.weibo.R;

import java.util.ArrayList;

/**
 * Created by xuhaoyang on 16/5/31.
 */
public class WriteStatusGridImgsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Uri> datas;
    private GridView gv;

    public WriteStatusGridImgsAdapter(Context context, ArrayList<Uri> datas, GridView gv) {
        this.context = context;
        this.datas = datas;
        this.gv = gv;
    }

    @Override
    public int getCount() {
//        return datas.size() > 0 ? datas.size() + 1 : 0;
        return datas.size();
    }

    @Override
    public Uri getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_grid_image, null);
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.iv_delete_image = (ImageView) convertView.findViewById(R.id.iv_delete_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int horizontalSpacing = gv.getHorizontalSpacing();
        int width = (gv.getWidth() - horizontalSpacing * 2
                - gv.getPaddingLeft() - gv.getPaddingRight()) / 3;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        holder.iv_image.setLayoutParams(params);

        // set data
        Uri item = getItem(position);
        //优化加载
        setImage(holder.iv_image,item);

        holder.iv_delete_image.setVisibility(View.VISIBLE);
        holder.iv_delete_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        public ImageView iv_image;
        public ImageView iv_delete_image;
    }

    public void setImage(ImageView view, Uri imageuri) {
        Glide.with(view.getContext()).load(imageuri).thumbnail(0.2f).fitCenter().into(view);
    }

}
