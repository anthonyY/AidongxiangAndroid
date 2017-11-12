package com.aidongxiang.app.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;


import com.aidongxiang.app.model.City;
import com.aidongxiang.app.model.Region;

import java.util.ArrayList;
import java.util.List;


/**
 * 城市列表选择工具类
 *
 * @author Anthony
 */
public class CityDBUtils2 {

    private CityDatebbase aiiOpenDb;

    public CityDBUtils2(Context context) {
        aiiOpenDb = CityDatebbase.getInstance(context);
    }


    /**
     * 初始化省
     */
    public void initProvince(final OnCitysListener onCitysListener) {
        initCities(1, onCitysListener);
    }

    /**
     * 初始化城市
     *
     * @param regionId id
     */
    public void initRegion(final int regionId, final OnCityListener onCityListener) {
        new AsyncTask<Integer, Void, City>(){
            @Override
            protected City doInBackground(Integer... params) {
                try {
                    int regionId = params[0];
                    int provinceRegion = 0, cityRegion = 0, districtCode = 0;
                    City city = new City();
                    city.setRegionId(regionId);
                    if (regionId < 1000000) {// 如果regionId是6位数的格式
                        cityRegion = regionId / 100 * 100;
                        provinceRegion = regionId / 10000 * 10000;
                    } else {// 如果是大于6位数的格式也就是包含商区的
                        Region region = aiiOpenDb.findRegionsFromId(regionId);
                        if (region != null) {// 如果有数据
                            districtCode = (int) region.getId();
                            city.setBusinessDistrict(region.getName());
                            city.setBusinessDistrictCode(regionId);
                            city.setDistrictCode(districtCode);
                            cityRegion = districtCode / 100 * 100;
                            provinceRegion = districtCode / 10000 * 10000;
                        }

                    }
                    Region pRegion = aiiOpenDb.findRegionsFromId(provinceRegion);
                    if (pRegion != null) {
                        String provinceName = pRegion.getName();
                        city.setProvince(provinceName);
                        city.setProvinceCode(provinceRegion);
                    }

                    Region cRegion = aiiOpenDb.findRegionsFromId(cityRegion);
                    if (cRegion != null) {
                        String cityName = cRegion.getName();
                        city.setCity(cityName);
                        city.setCityCode(cityRegion);
                    }
                    if (regionId == cityRegion) {
                        city.setDistrict("全区");
                        city.setDistrictCode(regionId);
                    } else {

                        Region countyRegion = aiiOpenDb.findRegionsFromId(regionId);
                        if (countyRegion != null) {
                            String countyName = countyRegion.getName();
                            city.setDistrict(countyName);
                            city.setDistrictCode(regionId);
                        }
                    }
                    return city;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(City city) {
                super.onPostExecute(city);
                if(onCityListener != null){
                    onCityListener.getCity(city);
                }

            }
        }.execute(regionId);
    }

    public void initCities(int parentId, final OnCitysListener onCitysListener) {
        new AsyncTask<Integer, Void, List<Region>>(){
            @Override
            protected List<Region> doInBackground(Integer... params) {
                try {
                    int parentId = params[0];
                    ArrayList<Region> list = aiiOpenDb.findCityRegions(parentId);
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Region> regions) {
                super.onPostExecute(regions);
                if(onCitysListener != null){
                    onCitysListener.getCitys(regions);
                }
            }
        }.execute(parentId);

    }

    public void initAllCities(final OnCitysListener onCitysListener) {
        new AsyncTask<Void, Void, List<Region>>(){
            @Override
            protected List<Region> doInBackground(Void... params) {
                try {
                    ArrayList<Region> list = aiiOpenDb.findAllRegions();
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ArrayList<Region>();
            }

            @Override
            protected void onPostExecute(List<Region> regions) {
                super.onPostExecute(regions);
                if(onCitysListener != null){
                    onCitysListener.getCitys(regions);
                }
            }
        }.execute();

    }

    public void initSearchCities(String searchKey, final OnCitysListener onCitysListener) {
        new AsyncTask<String, Void, List<Region>>(){
            @Override
            protected List<Region> doInBackground(String... params) {
                try {
                    String searchKey = params[0];
                    ArrayList<Region> list = aiiOpenDb.findRegionsFromSearch(searchKey);
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new ArrayList<Region>();
            }

            @Override
            protected void onPostExecute(List<Region> regions) {
                super.onPostExecute(regions);
                if(onCitysListener != null){
                    onCitysListener.getCitys(regions);
                }
            }
        }.execute(searchKey);

    }

    public interface OnCitysListener {
        void getCitys(List<Region> citys);
    }

    public interface OnCityListener {
        void getCity(City city);
    }

}
