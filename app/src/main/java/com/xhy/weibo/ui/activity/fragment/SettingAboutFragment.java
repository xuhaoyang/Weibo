package com.xhy.weibo.ui.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xhy.weibo.BuildConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.model.Setting;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.vh.SettingHeadViewHolder;
import com.xhy.weibo.ui.vh.SettingItemViewHolder;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import hk.xhy.android.common.ui.vh.OnListItemClickListener;
import hk.xhy.android.common.ui.vh.ViewHolder;
import hk.xhy.android.common.utils.FragmentUtils;
import hk.xhy.android.common.utils.ViewUtils;
import hk.xhy.android.common.widget.PullToRefreshMode;

public class SettingAboutFragment extends ListFragment<ViewHolder, Setting, List<Setting>, FrameLayout>
        implements OnListItemClickListener, FragmentUtils.OnBackClickListener {

    public static SettingAboutFragment newInstance() {
        SettingAboutFragment fragment = new SettingAboutFragment();
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置item间间隔样式
        getRecyclerView().addItemDecoration(new RecycleViewDivider(getmActivity(),
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

    @Nullable
    @Override
    public View onCreateView(@android.support.annotation.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_list, container, false);
        return view;
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
            //判断是否隐藏item分隔线
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
            ((SettingHeadViewHolder) holder).bind(getmActivity(), getItemsSource().get(position));
        }
    }

    @Override
    public List<Setting> onLoadInBackground() throws Exception {

        List<Setting> settings = new ArrayList<>();

        Setting head = new Setting();
        head.setId(0);
        head.setWeight(0);
        head.setConfig(Setting.ITEM_HEAD);
        head.setMainHead(getString(R.string.title_item_about));
        settings.add(head);

        Setting version = new Setting();
        version.setId(1);
        version.setWeight(1);
        version.setConfig(Setting.ITEM_TWICE);
        version.setMainHead(getString(R.string.title_item_version));
        version.setSubHead(BuildConfig.VERSION_NAME);
        version.setFunctionConfig(Setting.FUNCTION_ITEM_NONE);
        settings.add(version);

        Setting github = new Setting();
        github.setId(2);
        github.setWeight(2);
        github.setConfig(Setting.ITEM_TWICE);
        github.setFunctionConfig(Setting.FUNCTION_ITEM_BROWSER);
        github.setMainHead("Github");
        github.setSubHead("https://github.com/xuhaoyang/Weibo-Android");
        settings.add(github);


        Setting author = new Setting();
        author.setId(3);
        author.setWeight(3);
        author.setConfig(Setting.ITEM_TWICE);
        author.setFunctionConfig(Setting.FUNCTION_ITEM_NONE);
        author.setMainHead("作者");
        author.setSubHead("xuhaoyang");
        settings.add(author);

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
        final Setting setting = getItemsSource().get(position);
        return setting.getConfig();
    }


    @Override
    public void OnListItemClick(int postion) {

    }

    @Override
    public void OnItemOtherClick(int postion, int type) {

    }

    @Override
    public boolean onBackClick() {
        return false;
    }
}
