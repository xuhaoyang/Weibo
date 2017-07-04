package com.xhy.weibo.model;

/**
 * Created by xuhaoyang on 2017/3/8.
 */

public class Item<V> {

    private int id;
    private String name;
    private V value;
    private int weight;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Item(int id, String name, V value, int weight) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.weight = weight;
    }

    public Item(int id, String name, V value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }
}

