package com.xhy.weibo.model;

import java.util.ArrayList;

/**
 * Created by xuhaoyang on 2017/3/8.
 */

public class DialogData<V> extends Model {

    public static final int TEXT = 2001;
    public static final int RAIDO = 2002;
    public static final int SPINNER = 2003;
    public static final int CHECKBOX = 2004;


    private int id;
    private int config;
    private ArrayList<Item<V>> items;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConfig() {
        return config;
    }

    public void setConfig(int config) {
        this.config = config;
    }

    public ArrayList<Item<V>> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item<V>> items) {
        this.items = items;
    }
}
