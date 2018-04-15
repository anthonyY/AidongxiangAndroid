package com.aidongxiang.app.utils;

import com.aidongxiang.app.base.App;
import com.aidongxiang.app.base.Constants;
import com.aidongxiang.app.event.LocationEvent;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

import org.greenrobot.eventbus.EventBus;

/**
 * @author Anthony
 *         createTime 2018/3/26.
 * @version 1.0
 */

public class LocationUtils {

    private static int failCount = 0;

    /**
     * 开始定位
     */
    public static void startLocation(){

//初始化定位
        final AMapLocationClient mLocationClient = new AMapLocationClient(App.app);
        AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                    //解析定位结果
                    mLocationClient.stopLocation();
                    failCount = 0;
                    LocationEvent locationEvent = new LocationEvent();
                    locationEvent.setAdCode(amapLocation.getAdCode());
                    locationEvent.setAddress(amapLocation.getAddress());
                    locationEvent.setCity(amapLocation.getCity());
                    locationEvent.setCityCode(amapLocation.getCityCode());
                    locationEvent.setCountry(amapLocation.getCountry());
                    locationEvent.setDescription(amapLocation.getDescription());
                    locationEvent.setDistrict(amapLocation.getDistrict());
                    locationEvent.setLatitude(amapLocation.getLatitude());
                    locationEvent.setLongitude(amapLocation.getLongitude());
                    locationEvent.setLocationDetail(amapLocation.getLocationDetail());
                    locationEvent.setProvince(amapLocation.getProvince());
                    locationEvent.setRoad(amapLocation.getRoad());
                    locationEvent.setStreetNum(amapLocation.getStreetNum());
                    locationEvent.setStreet(amapLocation.getStreet());
                    Constants.INSTANCE.setLocation(locationEvent);
                    EventBus.getDefault().post(locationEvent);
                } else {
                    failCount++;
                    //失败超过10次，也就不定位了
                    if(failCount > 10){
                        mLocationClient.stopLocation();
                        failCount = 0;
                    }
                }
            }
        };
//设置定位回调监听
        mLocationClient.setLocationListener(mAMapLocationListener);
//启动定位
        mLocationClient.startLocation();

    }
}
