package com.hbck.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * 百度定位
 *
 * @Date 2018-11-06.
 */
public class LocationActivity extends AppCompatActivity {
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = new MyLocationListener();
    private EditText et_key;
    private ListView listView;
    private String city;
    private LatLng latLng;
    private List<PoiInfo> allPoi = new ArrayList<>();
    private String address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initView();

        initLocation();
    }


    private void initView() {
        et_key = findViewById(R.id.et_key);
        et_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() != 0 && !"".equals(editable.toString())) {
//                    citySearch(editable.toString());
                    nearbyPoiSearch(editable.toString(), latLng);
                }else {
                    nearbyPoiSearch(address, latLng);
                }
            }
        });
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String result = "";
                if (i != 0) {
                    result = allPoi.get(i).getName();
                }
                Intent intent = getIntent();
                intent.putExtra("address", result);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initLocation() {
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数

        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();

        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setIsNeedAddress(true);

        //可选，是否需要周边POI信息，默认为不需要，即参数为false
        //如果开发者需要获得周边POI信息，此处必须为true
        option.setIsNeedLocationPoiList(true);


        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    public void finish(View view) {
        finish();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            address = location.getBuildingName();
            if (address == null)
                address = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            nearbyPoiSearch(address, latLng);
        }
    }

    /**
     * 周边poi检索示例
     */
    public void nearbyPoiSearch(String key, LatLng latLng) {
        //创建poi检索实例
        PoiSearch poiSearch = PoiSearch.newInstance();

        //设置poi监听者该方法要先于检索方法searchNearby(PoiNearbySearchOption)前调用，否则会在某些场景出现拿不到回调结果的情况
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        //设置请求参数
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption()
                .keyword(key)//检索关键字
                .location(latLng)//检索位置
                .pageNum(0)//分页编号，默认是0页
                .pageCapacity(30)//设置每页容量，默认10条
                .radius(2000);//附近检索半径
        //发起请求
        poiSearch.searchNearby(nearbySearchOption);
        //释放检索对象
//        poiSearch.destroy();
    }

    /* 城市内搜索
     */
    private void citySearch(String key) {
        // 设置检索参数
        PoiSearch poiSearch = PoiSearch.newInstance();
        // 设置检索监听器
        poiSearch.setOnGetPoiSearchResultListener(poiListener);
        PoiCitySearchOption citySearchOption = new PoiCitySearchOption();
        citySearchOption.city(city);// 城市
        citySearchOption.keyword(key);// 关键字
        citySearchOption.pageCapacity(15);// 默认每页10条
        citySearchOption.pageNum(1);// 分页编号
        // 发起检索请求
        poiSearch.searchInCity(citySearchOption);
    }


    //创建poi监听者
    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {// 没有找到检索结果
                Toast.makeText(LocationActivity.this, "未找到结果", Toast.LENGTH_LONG).show();
                allPoi.clear();
                PoiInfo info = new PoiInfo();
                info.setName("不显示");
                info.setAddress("");
                allPoi.add(info);
                listView.setAdapter(new MyAdapter(LocationActivity.this, allPoi));
                return;
            }
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
                allPoi.clear();
                PoiInfo info = new PoiInfo();
                info.setName("不显示");
                info.setAddress("");
                allPoi.add(info);
                allPoi.addAll(poiResult.getAllPoi());
                listView.setAdapter(new MyAdapter(LocationActivity.this, allPoi));
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            Log.d("LocationActivity", "poiDetailResult:" + poiDetailResult);
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            Log.d("LocationActivity", "poiDetailSearchResult:" + poiDetailSearchResult);
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            Log.d("LocationActivity", "poiIndoorResult:" + poiIndoorResult);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
