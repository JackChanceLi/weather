package com.example.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;//天气滚动布局

    private TextView titleCity;//顶部当前城市Text

    private TextView titleUpdateTime;//顶部更新时间Text

    private TextView degreeText;//温度显示Text

    private TextView weatherInfoText;//天气详情显示

    private LinearLayout forecastLayout;//未来天气预测布局

    private TextView aqiText;//空气指数

    private TextView pm25Text;//pm2.5

    private TextView comfortText;

    private TextView carWashText;

    private TextView sportText;

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //合并标题栏
        if(Build.VERSION.SDK_INT >= 21){//21表示的是安卓5.1之上的系统

            //获得当前活动的DecorView
            View decorView = getWindow().getDecorView();
            //变更UI显示，使活动能够显示在状态狼上
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //再将状态栏透明
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        // 初始化控件,得到控件实例
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        imageView = (ImageView) findViewById(R.id.bing_pic_img);


        //调用本地存储，
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        //判断本地是否有数据
        if (weatherString == null) {
            // 有缓存时直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            // 无缓存时去服务器查询天气
            //从Intent调取天气id
            String mWeatherId = getIntent().getStringExtra("weather_id");
            //加载过程中设置天气详情界面不可见
            weatherLayout.setVisibility(View.INVISIBLE);
            //根据天气id向服务器请求数据
            requestWeather(mWeatherId);
        }

        //初始化显示必应图片，如果本地有，这里设置的是本地的图片。
        //刷新图片，在获取天气的过程中进行。
        String bingPic = prefs.getString("bing_pic",null);
        Log.d("Weatheractivity",bingPic);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(imageView);
            Log.d("Weatheractivity","本地存在图片");
        }else{
            loadBingPic();
            Log.d("Weatheractivity","本地无图片，load");
        }

    }

    /**
     * 根据天气id请求城市天气信息。
     */
    public void requestWeather(final String weatherId) {
        //由API接口和天气id以及key组成的接口地址
        String weatherUrl = "http://guolin.tech/api/weather?cityid="
                + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
        //调用HttpUtil的sendOkHttpRequest发起请求
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            //重写方法，对callback回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //从response获得序列
                final String responseText = response.body().string();
                //得到序列，直接解析，得到Weather对象
                final Weather weather = Utility.handleWeatherResponse(responseText);
                //回到主线程，显示UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //天气非空，数据可用
                        if (weather != null && "ok".equals(weather.status)) {
                            //将数据缓存在本地
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            //展示数据
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                //刷新天气。
                loadBingPic();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    /**
     * 处理并展示Weather实体类中的数据。
     * 就是将解析得到的数据，和界面中的控件对应
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        //设置标题栏等控件
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);

        //布局天气预报界面
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max+"℃");
            minText.setText(forecast.temperature.min+"℃");

            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        //完成之后，使天气详情可见
        weatherLayout.setVisibility(View.VISIBLE);
    }


    /**
     * 加载必应一图
     *
     */
    private void loadBingPic(){
        //请求图片地址
        String requestBingPic = "http://guolin.tech/api/bing_pic";

        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                Log.d("weatheractivity","从API获取了必应了");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(imageView);
                    }
                });
            }
        });
    }
}