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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.api.ApiClient;
import com.xhy.weibo.logic.UserLoginLogic;
import com.xhy.weibo.model.DialogData;
import com.xhy.weibo.model.Item;
import com.xhy.weibo.model.Result;
import com.xhy.weibo.model.Setting;
import com.xhy.weibo.model.User;
import com.xhy.weibo.ui.base.ListFragment;
import com.xhy.weibo.ui.interfaces.SaveDatas;
import com.xhy.weibo.ui.vh.SettingHeadViewHolder;
import com.xhy.weibo.ui.vh.SettingItemViewHolder;
import com.xhy.weibo.utils.RecycleViewDivider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import hk.xhy.android.common.ui.vh.OnListItemClickListener;
import hk.xhy.android.common.ui.vh.ViewHolder;
import hk.xhy.android.common.utils.ConstUtils;
import hk.xhy.android.common.utils.ConvertUtils;
import hk.xhy.android.common.utils.LogUtils;
import hk.xhy.android.common.utils.ObjectUtils;
import hk.xhy.android.common.utils.ToastUtils;
import hk.xhy.android.common.utils.ViewUtils;
import hk.xhy.android.common.widget.PullToRefreshMode;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;

import static android.R.attr.value;

/**
 * Created by xuhaoyang on 2017/7/10.
 */

public class SettingUserFragment extends ListFragment<ViewHolder, Setting, List<Setting>, FrameLayout> implements OnListItemClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static SettingUserFragment newInstance() {
        return new SettingUserFragment();
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

        //临时获得数据
        final Call<Result<User>> resultCall = ApiClient.getApi().getUserinfo(AppConfig.getUserId(), null, 0, AppConfig.getAccessToken().getToken());
        Result<User> result = null;
        try {
            result = resultCall.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final User info = result.getInfo();

        List<Setting> settings = new ArrayList<>();

        Setting head = new Setting();
        head.setId(0);
        head.setWeight(0);
        head.setConfig(Setting.ITEM_HEAD);
        head.setMainHead(getString(R.string.title_item_user_settings));
        settings.add(head);

        // 昵称
        Setting username = new Setting();
        username.setId(1);
        username.setWeight(1);
        username.setConfig(Setting.ITEM_TWICE);
        username.setMainHead(getString(R.string.title_item_nickname));
        username.setSubHead(info.getUsername());
        username.setFunctionConfig(Setting.FUNCTION_ITEM_DIALOG);
        DialogData usernameData = new DialogData();
        usernameData.setConfig(DialogData.TEXT);
        username.setDialogData(usernameData);
        settings.add(username);

        // 真实姓名
        Setting truename = new Setting();
        truename.setId(2);
        truename.setWeight(2);
        truename.setConfig(Setting.ITEM_TWICE);
        truename.setMainHead(getString(R.string.title_item_truename));
        truename.setSubHead(info.getTruename());
        truename.setFunctionConfig(Setting.FUNCTION_ITEM_DIALOG);
        DialogData truenameData = new DialogData();
        truenameData.setConfig(DialogData.TEXT);
        truename.setDialogData(truenameData);
        settings.add(truename);

        // 性别
        Setting sex = new Setting();
        sex.setId(3);
        sex.setWeight(3);
        sex.setConfig(Setting.ITEM_TWICE);
        sex.setMainHead(getString(R.string.title_item_sex));
        sex.setSubHead(info.getSex());
        sex.setFunctionConfig(Setting.FUNCTION_ITEM_DIALOG);
        DialogData sexData = new DialogData();
        final String[] sexarr = getResources().getStringArray(R.array.dialog_content_item_sex);
        sexData.setItems(new ArrayList<Item>() {{
            for (int i = 0; i < sexarr.length; i++) {
                add(new Item(i, sexarr[i], sexarr[i]));
            }
        }});
        sexData.setConfig(DialogData.RAIDO);
        sex.setDialogData(sexData);
        settings.add(sex);

        // 介绍
        Setting intro = new Setting();
        intro.setId(4);
        intro.setWeight(4);
        intro.setConfig(Setting.ITEM_TWICE);
        intro.setMainHead(getString(R.string.title_item_intro));
        intro.setSubHead(info.getIntro());
        intro.setFunctionConfig(Setting.FUNCTION_ITEM_DIALOG);
        DialogData introData = new DialogData();
        introData.setConfig(DialogData.TEXT);
        intro.setDialogData(introData);
        settings.add(intro);


        // 地点

        return settings;

    }

    @Override
    public void onLoadError(Exception e) {
        super.onLoadError(e);
        LogUtils.e(e);
    }

    @Override
    public int getItemViewType(int position) {
        Setting setting = getItemsSource().get(position);
        return setting.getConfig();
    }

    @Override
    public void onLoadComplete(List<Setting> data) {
        getItemsSource().clear();
        getItemsSource().addAll(data);
        getAdapter().notifyDataSetChanged();
        onRefreshComplete();
    }

    @Override
    public void OnListItemClick(int postion) {
        final Setting setting = getItemsSource().get(postion);
        final int id = setting.getId();
        showDialog(getmActivity(), setting, new SaveDatas<String>() {
            @Override
            public void save(final String value) {

                ApiClient.getApi().setUserinfoRx(AppConfig.getUserId(), new HashMap<String, String>() {{
                    String name = null;
                    switch (id) {
                        case 1:
                            name = "username";
                            break;
                        case 2:
                            name = "truename";
                            break;
                        case 3:
                            name = "sex";
                            break;
                        case 4:
                            name = "intro";
                            break;
                    }
                    put(name, value);

                }}, AppConfig.getAccessToken().getToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Result>() {
                            @Override
                            public void accept(@NonNull Result result) throws Exception {
                                ToastUtils.showShort(result.getMsg());
                                if (result.isSuccess()) {
                                    onRefresh();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                ToastUtils.showShort(throwable.getLocalizedMessage());
                                LogUtils.e(throwable);
                            }
                        });
            }
        });


    }

    @Override
    public void OnItemOtherClick(int postion, int type) {

    }

    public void showDialog(final Context context, final Setting setting, final SaveDatas callBack) {
        final int layout_px_16 = ConvertUtils.dp2px(16);
        final int layout_px_8 = ConvertUtils.dp2px(8);

        final LinearLayout linearLayout = new LinearLayout(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        switch (setting.getDialogData().getConfig()) {

            case DialogData.TEXT:

                linearLayout.setLayoutParams(
                        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(layout_px_16, layout_px_16, layout_px_16, layout_px_16);

                final LinearLayout lin2 = new LinearLayout(context);
                lin2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                lin2.setOrientation(LinearLayout.HORIZONTAL);


                final TextView textView = new TextView(context);
                final EditText editText = new EditText(context);

                final LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                final LinearLayout.LayoutParams edParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                tvParams.setMargins(layout_px_8, layout_px_8, layout_px_8, layout_px_8);
                edParams.setMargins(layout_px_8, layout_px_8, layout_px_8, layout_px_8);

                textView.setLayoutParams(tvParams);
                editText.setLayoutParams(edParams);

                textView.setText(getString(R.string.content_please_input) + setting.getMainHead());
                lin2.addView(textView);
                lin2.addView(editText);

                linearLayout.addView(lin2);

                builder.setTitle(setting.getMainHead());
                builder.setPositiveButton(R.string.content_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callBack.save(editText.getText().toString());
                    }
                });
                builder.setCancelable(true);
                builder.setNegativeButton(R.string.content_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setView(linearLayout);
                builder.show();

                break;
            case DialogData.RAIDO:
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
                        if (ObjectUtils.compare(item.getValue(), Integer.parseInt(AppConfig.getNotificaitonInterval() + "") / ConstUtils.MIN) == 0) {
                            final RadioButton childAt = (RadioButton) radioGroup.getChildAt(i);
                            childAt.setChecked(true);

                        }
                    } catch (Exception e) {
                        LogUtils.e(e);
                    }

                }

                linearLayout.addView(radioGroup);


                builder.setTitle(setting.getMainHead());
                builder.setPositiveButton(R.string.content_ok, new DialogInterface.OnClickListener() {
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
                builder.setNegativeButton(R.string.content_cancel, new DialogInterface.OnClickListener() {
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
}
