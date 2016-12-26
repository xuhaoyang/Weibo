package com.xhy.weibo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xhy.weibo.R;
import com.xhy.weibo.ui.activity.UserInfoActivity;
import com.xhy.weibo.ui.base.BaseActivity;
import com.xhy.weibo.model.User;
import com.xhy.weibo.network.URLs;
import com.xhy.weibo.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xuhaoyang on 16/5/16.
 */
public class UserAdpater extends RecyclerView.Adapter {



    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context mContext;
    private List<User> users;
    private int type;
    private View.OnClickListener listener;

    public UserAdpater(Context mContext, List<User> user) {
        this.mContext = mContext;
        users = user;
    }

    public UserAdpater(Context mContext, List<User> users, int type) {
        this.mContext = mContext;
        this.users = users;
        this.type = type;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_avatar)
        public ImageView iv_avatar;
        @BindView(R.id.tv_subhead)
        public TextView tv_subhead;
        @BindView(R.id.cv_item)
        public CardView cv_item;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            ItemViewHolder root = new ItemViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_user, parent, false));
            return root;
        } else if (viewType == TYPE_FOOTER) {
            FootViewHolder root = new FootViewHolder(
                    LayoutInflater.from(parent.getContext()).
                            inflate(R.layout.item_comment_foot, parent, false));
            return root;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            final User user = users.get(position);


            //设置头像
            if (TextUtils.isEmpty(user.getFace())) {
                viewHolder.iv_avatar.setImageResource(R.mipmap.ic_launcher);
            } else {
                String url = URLs.AVATAR_IMG_URL + user.getFace();
                Glide.with(viewHolder.iv_avatar.getContext()).load(url).
                        fitCenter().into(viewHolder.iv_avatar);
            }

            switch (type) {
                case Constants.TYPE_WRITE_FRIEND_LISTENER:
                    listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent data = new Intent();
                            data.putExtra(Constants.RESULT_DATA_USERNAME_AT, "@" + user.getUsername()+" ");
                            ((BaseActivity) mContext).setResult(Activity.RESULT_OK, data);
                            ((BaseActivity) mContext).finish();
                        }
                    };
                    break;
                default:
                    listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent data = new Intent(mContext, UserInfoActivity.class);
                            data.putExtra(Constants.USER_ID, user.getUid());
                            mContext.startActivity(data);
                        }
                    };
                    break;
            }


            viewHolder.iv_avatar.setOnClickListener(listener);

            //设置用户名
            viewHolder.tv_subhead.setText(user.getUsername());


            viewHolder.cv_item.setOnClickListener(listener);
        }

    }


    @Override
    public int getItemCount() {
        return users.size() + 1;
        //users.size() == 0 ? 0 :
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

}
