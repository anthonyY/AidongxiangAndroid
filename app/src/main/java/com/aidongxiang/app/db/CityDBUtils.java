package com.aidongxiang.app.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.aidongxiang.app.model.City;
import com.aidongxiang.app.model.Region;

import java.util.ArrayList;


/**
 * 城市列表选择工具类
 *
 * @author Anthony
 */
public class CityDBUtils {

    private CityDatebbase aiiOpenDb;
    public static final int PROVICE = 0x01;
    public static final int CITY = 0x02;
    public static final int DISTRICTS = 0x03;
    public static final int BUSINESS_DISTRICT = 0x04;
    public static final int REGIONID_INIT = 0x05;
    private OnCityInitListener onCityInitListener;
    private OnCityInitListener2 onCityInitListener2;

    public CityDBUtils(Context context) {
        aiiOpenDb = CityDatebbase.getInstance(context);
    }


    /**
     * 初始化省
     */
    public void initProvince() {
        new InitCitysThread(1, PROVICE).start();
    }

    /**
     * 初始化城市
     *
     * @param pcode 省份id
     */
    public void initCities(final int pcode) {
        new InitCitysThread(pcode, CITY).start();

    }

    public void initCities(final int pcode, int index) {
        new InitCitysThread(pcode, index).start();
    }

    public void initSearchCities(final String city, int index) {
        new SearchCitysThread(city, index).start();
    }

    public void initAllCities(int index) {
        new InitAllCitysThread(index).start();
    }

    public void initRegionId(final int regionID, int index) {
        new InitCityThread(regionID, index).start();
    }

    /**
     * 把regionId 转换成city对象
     *
     * @param regionID
     */
    public void initRegionId(final int regionID) {
        new InitCityThread(regionID, REGIONID_INIT).start();
    }

    /**
     * 初始化城市的的线程
     */
    class InitCitysThread extends Thread {
        private int regionId;
        private int index;

        public InitCitysThread(int regionId, int index) {
            this.regionId = regionId;
            this.index = index;
        }

        @Override
        public void run() {
            super.run();
            try {
                ArrayList<Region> list = aiiOpenDb.findCityRegions(regionId);
                Message msg = new Message();
                msg.what = index;
                msg.obj = list;
                handler2.sendMessage(msg);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 初始化所有城市的的线程
     */
    class InitAllCitysThread extends Thread {
        private int index;

        public InitAllCitysThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            super.run();
            try {
                ArrayList<Region> list = aiiOpenDb.findAllRegions();
                Message msg = new Message();
                msg.what = index;
                msg.obj = list;
                handler2.sendMessage(msg);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 模糊查询城市的的线程
     */
    class SearchCitysThread extends Thread {
        private String city;
        private int index;

        public SearchCitysThread(String city, int index) {
            this.city = city;
            this.index = index;
        }

        @Override
        public void run() {
            super.run();
            try {
                ArrayList<Region> list = aiiOpenDb.findRegionsFromSearch(city);
                Message msg = new Message();
                msg.what = index;
                msg.obj = list;
                handler2.sendMessage(msg);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 初始化城市的的线程
     */
    class InitCityThread extends Thread {
        private int regionId;
        private int index;

        public InitCityThread(int regionId, int index) {
            this.regionId = regionId;
            this.index = index;
        }

        @Override
        public void run() {
            super.run();
            if (regionId <= 0) {
                return;
            }
            try {
                int provinceRegion = 0, cityRegion = 0, districtCode = 0;
                City city = new City();
                city.setRegionId(regionId);
                if (regionId < 1000000) {// 如果regionId是6位数的格式
                    cityRegion = regionId / 100 * 100;
                    provinceRegion = regionId / 10000 * 10000;
                } else {// 如果是大于6位数的格式也就是包含商区的
                    Region region = aiiOpenDb.findRegionsFromId(regionId);
                    if (region != null) {// 如果有数据
                        // String bussinessName =
                        // bussinessCursor.getString(bussinessCursor.getColumnIndexOrThrow(RegionField.NAME));
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
                Message msg = new Message();
                msg.what = index;
                msg.obj = city;
                msg.arg1 = CITY;
                handler2.sendMessage(msg);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    public void setOnCityInitListener(OnCityInitListener onCityInitListener) {
        this.onCityInitListener = onCityInitListener;
    }

    public interface OnCityInitListener {
        public void getCitys(ArrayList<Region> citys, int index);

        public void getCity(City city, int index);
    }

    public void setOnCityInitListener2(OnCityInitListener2 onCityInitListener2) {
        this.onCityInitListener2 = onCityInitListener2;
    }

    public interface OnCityInitListener2 {
        public void getCity(City city, int index);
    }

    @SuppressLint("HandlerLeak")
    Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.arg1 == CITY) {
                City city = (City) msg.obj;
                if (onCityInitListener != null) {
                    onCityInitListener.getCity(city, msg.what);
                }
                if (onCityInitListener2 != null) {
                    onCityInitListener2.getCity(city, msg.what);
                }
            } else {
                @SuppressWarnings("unchecked")
                ArrayList<Region> regions = (ArrayList<Region>) msg.obj;
                if (onCityInitListener != null) {
                    onCityInitListener.getCitys(regions, msg.what);
                }

            }
        }

        ;
    };
}
