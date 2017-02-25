package com.example.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 蚍蜉 on 2017/2/21.
 */

public class County extends DataSupport {
    private int id;//每个类都有的id

    private String countyName;//县名称

    private String weatherId;//天气id

    private int cityId;//所属的Cityid

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getCountyName(){
        return countyName;
    }

    public void setCountryName(String countryName){
        this.countyName = countryName;
    }

    public String getWeatherId(){
        return weatherId;
    }

    public void setWeatherId(String weatherId){
        this.weatherId = weatherId;
    }

    public int getCityId(){
        return cityId;
    }

    public void setCityId(int cityId){
        this.cityId = cityId;
    }
}
