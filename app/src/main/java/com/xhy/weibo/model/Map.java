package com.xhy.weibo.model;

/**
 * Created by xuhaoyang on 2017/7/12.
 */

public class Map {

    /**
     * id : 3
     * name : 深圳职业技术学院
     * address : 留仙大道7098号
     * longitude : 113.938846
     * latitude : 22.582190
     * cityname : 深圳市
     * citycode : 0755
     */

    private int id;
    private String name;
    private String address;
    private double longitude;
    private double latitude;
    private String cityname;
    private String citycode;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }
}
