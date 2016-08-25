package com.xhy.weibo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xhy.weibo.R;
import com.xhy.weibo.activity.SearchActivity;
import com.xhy.weibo.model.Hot;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/5/20.
 */
public class HotAdpater extends RecyclerView.Adapter {

    private List<Hot> hots;
    private Context context;

    public HotAdpater(Context context, List<Hot> hots) {
        this.context = context;
        this.hots = hots;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder root = new ItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nav, parent, false));
        return root;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder viewHolder = (ItemViewHolder) holder;
        final Hot hot = hots.get(position);
        viewHolder.tv_nav.setText(hot.getKeyword());

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent(context, SearchActivity.class);
                data.putExtra(SearchActivity.SEARCH_CONTENT, hot.getKeyword());
                context.startActivity(data);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hots.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_nav)
        public TextView tv_nav;
        @BindView(R.id.cv_item)
        CardView cardView;


        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
