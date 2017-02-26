package com.example.coolweather.util;

import android.text.TextUtils;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.w3c.dom.Text;

/**
 * Created by 蚍蜉 on 2017/2/17.
 * Utility工具类，用于服务器返回JSON的解析以及处理
 */

public class Utility {

    /**
     * 用于解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {//TextUtil的方法判断返回的请求是否为空
            try {
                //根据JSON数据格式解析
                //1.将response转化为JSON数组，
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    //2.遍历JSON数组，从数组中读取记录到JSON对象
                    JSONObject provinceObject = allProvinces.getJSONObject(i);//获得第i个JSON对象
                    //声明类对象
                    Province province = new Province();
                    //3.将JSON对象中的内容存储到Province中
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //调用save存储到database
                    province.save();
                }
                return true;//ok
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的市级的数据
     */
    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);

                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 解析服务器返回的县级的数据
     */
    public static boolean handleCountyResponse(String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountries = new JSONArray(response);
                for(int i=0; i < allCountries.length(); i++){
                    JSONObject countryObject = allCountries.getJSONObject(i);
                    County county = new County();
                    county.setCountryName(countryObject.getString("name"));
                    county.setWeatherId(countryObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 解析天气JSON数据的方法
     * 将服务器返回的JSON代码，转换为Weather实体类
     */
    public static Weather handleWeatherResponse(String response){

        try{
            //要根据返回的GSON代码的格式来解析
            //1.首先返回的GSON是个对象{}
            JSONObject jsonObject = new JSONObject(response);
            //2.解析获取HeWeather部分的数据，而这部分是一个数组
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            //3.数组的内部又是一个对象，所以最终将内容解析到String
            String weatherContent = jsonArray.getJSONObject(0).toString();
            //4.之前的操作将天气数据的主体解析出来，由于之前按照JSON数据格式定义了类
            //所以只要调用fromJson就可以直接转换为Weather对象。
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}


