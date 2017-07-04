package com.xhy.weibo.ui.vh;

import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xhy.weibo.R;
import com.xhy.weibo.model.Setting;

import hk.xhy.android.commom.bind.ViewById;
import hk.xhy.android.commom.ui.vh.OnListItemClickListener;
import hk.xhy.android.commom.ui.vh.ViewHolder;

/**
 * Created by xuhaoyang on 2017/3/8.
 */

public class SettingItemViewHolder extends ViewHolder {


    @ViewById(R.id.item)
    LinearLayout item_layout;

    @ViewById(R.id.item_main_title)
    TextView item_main_title;

    @ViewById(R.id.item_sub_title)
    TextView item_sub_title;

    //    @ViewById(R.id.item_checkbox)
//    CheckBox item_checkbox;
    @ViewById(R.id.item_switch)
    SwitchCompat item_switch;

    //item 底部分割线
    @ViewById(R.id.item_decoration_end)
    View item_Decoration_end;

    public SettingItemViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(final Setting item, final OnListItemClickListener listener, boolean isDecorationHide) {


        switch (item.getFunctionConfig()) {
            case Setting.FUNCTION_ITEM_DIALOG://弹出框 隐藏checkbox
            case Setting.FUNCTION_ITEM_OPTIONS:
                item_switch.setVisibility(View.GONE);
                break;
            default:
                item_switch.setChecked(item.getCheckBoxIs());
                break;
        }


        if (item != null) {
            item_main_title.setText(item.getMainHead());
            switch (item.getConfig()){
                case Setting.ITEM_TWICE:
                    item_sub_title.setText(item.getSubHead());
                    break;
            }
        }

        item_layout.setClickable(true);
        item_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.OnListItemClick(getAdapterPosition());
                }
            }
        });

        item_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (listener != null) {
                    /**
                     * 略有疑问 不知道怎么写
                     */
                    listener.OnListItemClick(getAdapterPosition());
                }
            }
        });
//        item_checkbox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener != null) {
//                    listener.OnListItemClick(getAdapterPosition());
//                }
//            }
//        });


        /**
         * 下一个如果是HEAD就隐藏间隔
         */
        if (isDecorationHide) {
            item_Decoration_end.setVisibility(View.GONE);
        }

    }

}
