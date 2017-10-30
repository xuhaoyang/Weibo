package com.xhy.weibo.ui.activity.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.model.DialogData;
import com.xhy.weibo.model.Item;
import com.xhy.weibo.model.Setting;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.interfaces.SaveDatas;
import com.xhy.weibo.ui.vh.SettingHeadViewHolder;
import com.xhy.weibo.ui.vh.SettingItemViewHolder;
import com.xhy.weibo.utils.RecycleViewDivider;
import com.xhy.weibo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import hk.xhy.android.common.ui.vh.OnListItemClickListener;
import hk.xhy.android.common.ui.vh.ViewHolder;
import hk.xhy.android.common.utils.ConstUtils;
import hk.xhy.android.common.utils.ConvertUtils;
import hk.xhy.android.common.utils.FragmentUtils;
import hk.xhy.android.common.utils.LogUtils;
import hk.xhy.android.common.utils.ObjectUtils;
import hk.xhy.android.common.utils.ViewUtils;
import hk.xhy.android.common.widget.PullToRefreshMode;

/**
 * Created by xuhaoyang on 2017/3/9.
 */

public class SettingNotificationFragment extends ListFragment<ViewHolder, Setting, List<Setting>, FrameLayout>
        implements OnListItemClickListener, FragmentUtils.OnBackClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public static SettingNotificationFragment newInstance() {
        return new SettingNotificationFragment();
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
        setMode(PullToRefreshMode.DISABLED);

        setFooterShowEnable(false);
        initLoader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        head.setMainHead(getString(R.string.title_item_regular_settings));
        settings.add(head);

        /**
         * 开启通知
         */
        Setting notify = new Setting();
        notify.setId(1);
        notify.setWeight(1);
        notify.setConfig(Setting.ITEM_SINGLE);
        notify.setCheckBoxIs(AppConfig.isNotify());
        notify.setMainHead(getString(R.string.title_item_notifications));
        settings.add(notify);

        /**
         * 免打扰
         */
        Setting notify2 = new Setting();
        notify2.setId(2);
        notify2.setWeight(2);
        notify2.setConfig(Setting.ITEM_TWICE);
        notify2.setCheckBoxIs(AppConfig.getDoNotDisturb());
        notify2.setMainHead(getString(R.string.title_item_donotdisturb_mode));
        notify2.setSubHead(getString(R.string.title_item_donotdisturb_mode_content));
        settings.add(notify2);


        /**
         * 推送时间
         */
        final Setting interval = new Setting();
        DialogData<Integer> intervalData = new DialogData();
        intervalData.setConfig(DialogData.RAIDO);
        intervalData.setId(0);
        final String[] intervalItem = getResources().getStringArray(R.array.dialog_content_item_notification_interval);
        final int[] intervalItemValue = getResources().getIntArray(R.array.dialog_content_item_notification_interval_values);
        intervalData.setItems(new ArrayList<Item<Integer>>() {{
            for (int i = 0; i < intervalItem.length; i++) {
                add(new Item(i, intervalItem[i], intervalItemValue[i]));
            }
        }});
        interval.setId(3);
        interval.setWeight(3);
        interval.setConfig(Setting.ITEM_TWICE);
        interval.setFunctionConfig(Setting.FUNCTION_ITEM_DIALOG);
        interval.setMainHead(getString(R.string.title_item_notification_interval));
        interval.setSubHead(AppConfig.getNotificaitonInterval() / ConstUtils.MIN + " " + getString(R.string.content_minute));
        interval.setDialogData(intervalData);
        settings.add(interval);


        /**
         * 推送方式
         * 自建 极光
         */


        final Setting mode = new Setting();
        DialogData<Integer> modeDialogData = new DialogData<>();
        modeDialogData.setItems(new ArrayList<Item<Integer>>() {{
            add(new Item<Integer>(0, getString(R.string.dialog_content_name_ourpush), 0, 0));
            add(new Item<Integer>(1, getString(R.string.dialog_content_name_jpush), 1, 1));
        }});
        modeDialogData.setId(0);

        modeDialogData.setConfig(DialogData.RAIDO);
        mode.setWeight(4);
        mode.setId(4);
        mode.setConfig(Setting.ITEM_TWICE);
        mode.setFunctionConfig(Setting.FUNCTION_ITEM_DIALOG);
        mode.setMainHead(getString(R.string.title_item_notification_mode));
        switch (AppConfig.getNotifyMode()) {
            case 0:
                mode.setSubHead(getString(R.string.dialog_content_name_ourpush));
                break;
            case 1:
                mode.setSubHead(getString(R.string.dialog_content_name_jpush));
                break;
        }
        mode.setDialogData(modeDialogData);
        settings.add(mode);
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
    public void onLoadError(Exception e) {
        super.onLoadError(e);
        LogUtils.e(e);
    }

    @Override
    public int getItemViewType(int position) {
        final Setting setting = getItemsSource().get(position);
        return setting.getConfig();
    }

    @Override
    public void OnListItemClick(int postion) {
        final Setting setting = getItemsSource().get(postion);
        final int id = setting.getId();
        switch (id) {
            case 1:
                //TODO 开启关闭推送 要做
                if (AppConfig.isNotify()) {
                    AppConfig.setNotify(false);
                } else {
                    AppConfig.setNotify(true);
                }
                break;
            case 2:
                //TODO 极光推送和自建推送做维护
                if (AppConfig.getDoNotDisturb()) {
                    AppConfig.setDoNotDisturb(false);
                } else {
                    AppConfig.setDoNotDisturb(true);
                }

                break;
            case 3:
                showDialog(getmActivity(), setting, (Integer.parseInt(AppConfig.getNotificaitonInterval() + "") / ConstUtils.MIN), new SaveDatas<Integer>() {
                    @Override
                    public void save(Integer value) {
                        AppConfig.setNotificationInterval(value);
                        restartLoader();//刷新界面
                    }
                });
                break;

            case 4:
                showDialog(getmActivity(), setting, AppConfig.getNotifyMode(), new SaveDatas<Integer>() {
                    @Override
                    public void save(Integer value) {
                        AppConfig.setNotifyMode(value);

                        //判断是否开启推送
                        if (AppConfig.isNotify()) {
                            switch (value) {
                                case 0:
                                    //自带

                                    //关闭极光推送
                                    if (!JPushInterface.isPushStopped(getmActivity().getApplicationContext())) {
                                        JPushInterface.stopPush(getmActivity().getApplicationContext());
                                    }
                                    //开启自带推送
                                    //TODO 应该不用TOKEN传入了
                                    getmActivity().startService(Utils.getPushServiceIntent());

                                    break;
                                case 1:
                                    //极光
                                    //关闭另一个推送
                                    getmActivity().stopService(Utils.getPushServiceIntent());
                                    //开启极光推送
                                    if (JPushInterface.isPushStopped(getmActivity().getApplicationContext())) {
                                        JPushInterface.resumePush(getmActivity().getApplicationContext());
                                    }
                                    break;
                            }
                        }

                        restartLoader();
                    }
                });
                break;
        }
        switch (setting.getFunctionConfig()) {
            case Setting.FUNCTION_ITEM_OPTIONS:
                break;
            case Setting.FUNCTION_ITEM_DIALOG:

                break;
        }

        restartLoader();
    }

    public void showDialog(final Context context, final Setting setting, final int settingValue, final SaveDatas callBack) {

        switch (setting.getDialogData().getConfig()) {
            case DialogData.RAIDO:
                LinearLayout linearLayout = new LinearLayout(context);
                final int layout_px_16 = ConvertUtils.dp2px(16);
                final RadioGroup radioGroup = new RadioGroup(context);

                linearLayout.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(layout_px_16, layout_px_16, layout_px_16, layout_px_16);

                radioGroup.setOrientation(RadioGroup.VERTICAL);
                radioGroup.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                final ArrayList<Item> items = setting.getDialogData().getItems();
                for (int i = 0; i < items.size(); i++) {
                    final Item item = items.get(i);
                    final RadioButton rb = new RadioButton(context);
                    rb.setText(item.getName());


                    final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(layout_px_16, 0, 0, layout_px_16);
                    rb.setLayoutParams(layoutParams);
                    rb.setTextSize(16f);
                    radioGroup.addView(rb);
                }

                //判断当前是不是设置选项
                for (int i = 0; i < items.size(); i++) {
                    final Item item = items.get(i);
                    try {
                        LogUtils.v(item.getValue());
                        LogUtils.v(AppConfig.getNotificaitonInterval());
                        /**
                         * Integer.parseInt(AppConfig.getNotificaitonInterval() + "") / ConstUtils.MIN
                         */
                        if (ObjectUtils.compare(item.getValue(), settingValue) == 0) {
                            final RadioButton childAt = (RadioButton) radioGroup.getChildAt(i);
                            childAt.setChecked(true);

                        }
                    } catch (Exception e) {
                        LogUtils.e(e);
                    }

                }

                linearLayout.addView(radioGroup);


                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(setting.getMainHead());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < radioGroup.getChildCount(); i++) {
                            RadioButton rd = (RadioButton) radioGroup.getChildAt(i);
                            if (rd.isChecked()) {
                                for (int j = 0; j < items.size(); j++) {
                                    final Item item = items.get(j);
                                    final String name = item.getName();
                                    if (name != null && name.equals(rd.getText().toString())) {
                                        callBack.save(item.getValue());
                                    }
                                }
                            }
                        }

                        getAdapter().notifyDataSetChanged();


                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setView(linearLayout);
                builder.show();
                break;
        }

    }

    @Override
    public void OnItemOtherClick(int postion, int type) {

    }

    @Override
    public boolean onBackClick() {
        return false;
    }

}
