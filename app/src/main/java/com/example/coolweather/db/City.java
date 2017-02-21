package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 蚍蜉 on 2017/2/21.
 */

public class City extends DataSupport {
    private int id;//每个类都有的id

    private String cityName;//城市名

    private int cityCode;//城市代码

    private int provinceId;//城市所属省id

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getCityName(){
        return cityName;
    }

    public void setCityName(String cityName){
        this.cityName = cityName;
    }

    public int getCityCode(){
        return cityCode;
    }

    public void setCityCode(int cityCode){
        this.cityCode = cityCode;
    }

    public int getProvinceId(){
        return provinceId;
    }

    public void setProvinceId(int provinceId){
        this.provinceId = provinceId;
    }
}
