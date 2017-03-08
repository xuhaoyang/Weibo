package com.xhy.weibo.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.xhy.weibo.R;
import com.xhy.weibo.model.Setting;
import com.xhy.weibo.ui.base.ListActivity;
import com.xhy.weibo.ui.vh.SettingHeadViewHolder;
import com.xhy.weibo.ui.vh.SettingItemViewHolder;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;
import hk.xhy.android.commom.utils.ViewUtils;
import hk.xhy.android.commom.widget.PullToRefreshMode;

public class SettingActivity extends ListActivity<ViewHolder, Setting, List<Setting>> implements OnListItemClickListener {

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setSupportActionBar(toolbar);

        //设置item间间隔样式
        getRecyclerView().addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.VERTICAL));
        //设置下拉刷新颜色
        getPullToRefreshLayout().setColorSchemeResources(R.color.colorPrimary);
        /* 解决刷新动画出不来的问题 */
        getPullToRefreshLayout().setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        setMode(PullToRefreshMode.PULL_FROM_START);
        setFooterShowEnable(false);

        initLoader();


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        switch (viewType) {
            case Setting.ITEM_HEAD:
                holder = new SettingHeadViewHolder(ViewUtils.inflate(parent, R.layout.item_head_title));
                break;
            case Setting.ITEM_SINGLE:
                holder = new SettingItemViewHolder(ViewUtils.inflate(parent, R.layout.item_single_config));
                break;
            case Setting.ITEM_TWICE:
                holder = new SettingItemViewHolder(ViewUtils.inflate(parent, R.layout.item_twice_config));

                break;
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof SettingItemViewHolder) {
            boolean isHide = false;
            if (position + 1 <= getItemCount() - 1) {
                if (getItemsSource().get(position + 1).getConfig() == Setting.ITEM_HEAD) {
                    isHide = true;
                }
            } else {
                isHide = true;
            }
            ((SettingItemViewHolder) holder).bind(getItemsSource().get(position), this, isHide);
        } else if (holder instanceof SettingHeadViewHolder) {
            ((SettingHeadViewHolder) holder).bind(this, getItemsSource().get(position));
        }

    }

    @Override
    public void onLoadStart() {

    }

    @Override
    public List<Setting> onLoadInBackground() throws Exception {
        List<Setting> settings = new ArrayList<>();

        Setting head = new Setting();
        head.setId(0);
        head.setWeight(0);
        head.setConfig(Setting.ITEM_HEAD);
        head.setMainHead("常规设置");
        settings.add(head);

        Setting notify = new Setting();
        notify.setId(1);
        notify.setWeight(1);
        notify.setConfig(Setting.ITEM_SINGLE);
        notify.setFunctionConfig(Setting.ITEM_OPTIONS);
        notify.setMainHead("通知设置");
        settings.add(notify);

        Setting userinfo = new Setting();
        userinfo.setId(1);
        userinfo.setWeight(1);
        userinfo.setConfig(Setting.ITEM_SINGLE);
        userinfo.setFunctionConfig(Setting.ITEM_OPTIONS);
        userinfo.setMainHead("用户设置");
        settings.add(userinfo);

        Setting feedback = new Setting();
        feedback.setId(2);
        feedback.setWeight(2);
        feedback.setConfig(Setting.ITEM_SINGLE);
        feedback.setFunctionConfig(Setting.ITEM_OPTIONS);
        feedback.setMainHead("意见反馈");
        settings.add(feedback);

        Setting about = new Setting();
        about.setId(3);
        about.setWeight(3);
        about.setConfig(Setting.ITEM_SINGLE);
        about.setFunctionConfig(Setting.ITEM_OPTIONS);
        about.setMainHead("关于");
        settings.add(about);

        return settings;
    }

    @Override
    public void onLoadComplete(List<Setting> data) {
        getItemsSource().clear();
        getItemsSource().addAll(data);
        getAdapter().notifyDataSetChanged();
        onRefreshComplete();
    }

    @Override
    public int getItemViewType(int position) {
        Setting setting = getItemsSource().get(position);
        return setting.getConfig();
    }

    @Override
    public void OnListItemClick(int postion) {

    }

    @Override
    public void OnItemOtherClick(int postion, int type) {

    }
}
