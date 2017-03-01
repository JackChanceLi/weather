package com.example.coolweather;

/**
 * Created by 蚍蜉 on 2017/2/21.
 *碎片实体部分代码的实现
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {//继承Fragment类

    //常量
    private static final String TAG = "ChooseAreaFragment";

    public static final int LEVEL_PROVINCE = 0;//省级别

    public static final int LEVEL_CITY = 1;//市级别

    public static final int LEVEL_COUNTY = 2;//县级别

    /**
     *控件实例设置为全局，便于操作
     */
    private ProgressDialog progressDialog;//进度对话框

    private TextView titleText;//状态栏标题

    private Button backButton;//返回按钮

    private ListView listView;//列表展示

    private ArrayAdapter<String> adapter;//ListView的适配器，适配来自dataList的数据

    private List<String> dataList = new ArrayList<>();//通过add方法，存储需要listView显示的数据

    /**
     * 省列表
     */
    private List<Province> provinceList;

    /**
     * 市列表
     */
    private List<City> cityList;

    /**
     * 县列表
     */
    private List<County> countyList;

    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;

    /**
     * 当前选中的级别
     */
    private int currentLevel;



    /**
     * onCreatView方法，
     * 完成为fragment加载布局
     * @param inflater 填充，碎片布局
     * @param container
     * @param savedInstanceState //活动不可见的时候，是否保存获得记录
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载碎片使用的布局
        View view = inflater.inflate(R.layout.choose_area, container, false);
        //获得布局中的各个实例
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        //通过dataList获得适配器Adapter
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        //将适配器Adapter传入listview
        listView.setAdapter(adapter);
        //别忘了返回view
        return view;
    }


    /**
     * 重写onActivityCreated方法，
     * 它在Activity的onCreated方法调用之后调用，
     * 完成碎片的构建
     * @param savedInstanceState 碎片不可见时候，是否保存获得记录
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //只有在view上才可以设置click监听
        //ListView上对应的是ItemListener
        //编写listview上的按键监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //当前级别是省
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);//记录选择的省
                    //获取所选择省的城市数据
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if(currentLevel == LEVEL_COUNTY){
                    //通过Intent跳转到天气界面
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    //将选中的县的信息传递过去
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    //不杀死选择列表
                    //getActivity().finish();
                    Log.d("chooseactivity","intent启动");
                }
            }
        });

        //编写标题栏back按钮的逻辑
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY) {
                    //如果当前层次为县，那么back之后，查询city数据
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    //如果当前层次为市，back之后，查询province数据
                    queryProvinces();
                }
            }
        });
        //默认查询获取省级的数据
        queryProvinces();
    }


    /**
     * 查询全国所有的省，优先从本地数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryProvinces() {
        //标题栏信息，中国
        titleText.setText("中国");
        //标题栏back按钮不显示
        backButton.setVisibility(View.GONE);

        //从本地数据库获取所有Province类型数据，存储在provinceList中
        provinceList = DataSupport.findAll(Province.class);//参数传入需要的类模板

        if (provinceList.size() > 0) {//如果本地有数据
            dataList.clear();//清空datalist列表

            //遍历provinceList列表，将读取到的
            // provinceList中的province字段add到datalist中去
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            //通知listView刷新更改
            adapter.notifyDataSetChanged();
            //保持listView的item位置不变
            listView.setSelection(0);
            //更新当前位置
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");//从服务器上获取字段
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());//设置标题栏内容
        backButton.setVisibility(View.VISIBLE);//标题栏back按钮可见

        //查询本地数据库，获取限制选中省的所属city，使用where方法
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);

        //同省级操作
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询。
     */
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }



    /**
     * 根据传入的地址和类型从服务器上查询省市县数据。
     * 参数为（目的服务器地址， 数据类型标签）
     */
    private void queryFromServer(String address, final String type) {
        //开启显示进度条
        showProgressDialog();

        //通过刚才编写的HttpUtil中的sendOkhttpRequest方法发起一个请求
        HttpUtil.sendOkHttpRequest(address,
                new Callback() {//注册一个callback回调事件，处理服务器返回的信息

            //重写onResponse
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //从参数response获取请求回复信息
                String responseText = response.body().string();
                //标记是否成功获得，并且成功解析
                boolean result = false;

                //根据传入的数据类型type，对需要的数据进行解析，并存储到数据库
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }

                //如果数据库获取数据成功，那么再次调用queryProvince方法，从数据库获取数据
                //queryProvince涉及UI操作，所以通过runOnUiThread从子线程回到主线程
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();//关闭进度条

                            //根据传入的类型，将数据传入
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }


            //获取失败，Toast通知失败
            @Override
            public void onFailure(Call call, IOException e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
