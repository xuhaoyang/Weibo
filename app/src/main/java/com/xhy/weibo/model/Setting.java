package com.xhy.weibo.model;

import com.xhy.weibo.AppConfig;

/**
 * Created by xuhaoyang on 2017/3/7.
 */


public class Setting extends Model {

    public static final int ITEM_HEAD = 1001;
    public static final int ITEM_SINGLE = 1002;//单层菜单
    public static final int ITEM_TWICE = 1003;//双层菜单

    public static final int FUNCTION_ITEM_DIALOG = 1101;//弹出窗口菜单
    public static final int FUNCTION_ITEM_OPTIONS = 1102;//跳转新的页面

    private static final String CHECKBOX = "CB";
    private int id;
    private String mainHead;//主标题
    private String subHead;//副标题
    private int weight;//权重 用于排序
    private int config;//菜单种类
    private int functionConfig;
    private DialogData dialogData;

    public int getConfig() {
        return config;
    }

    public void setConfig(int config) {
        this.config = config;
    }

    public int getFunctionConfig() {
        return functionConfig;
    }

    public void setFunctionConfig(int functionConfig) {
        this.functionConfig = functionConfig;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMainHead() {
        return mainHead;
    }

    public void setMainHead(String mainHead) {
        this.mainHead = mainHead;
    }

    public String getSubHead() {
        return subHead;
    }

    public void setSubHead(String subHead) {
        this.subHead = subHead;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setCheckBoxIs(boolean isOn) {
        AppConfig.putBoolean(id + CHECKBOX, isOn);
    }

    public boolean getCheckBoxIs() {
        return AppConfig.getBoolean(id + CHECKBOX, false);
    }

    public DialogData getDialogData() {
        return dialogData;
    }

    public void setDialogData(DialogData dialogData) {
        this.dialogData = dialogData;
    }

    /**
     * 转换json为Model
     *
     * @param json
     * @return
     */
    public static Setting parseObject(final String json){
        return Model.parseObject(json,Setting.class);
    }
}
