package com.aidongxiang.app.model;


import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable, Cloneable {

    private int regionId = -1;
    private int provinceCode = -1;
    private int cityCode = -1;
    private int districtCode = -1;
    private int businessDistrictCode = -1;
    private int deep = -1;
    private String province;
    private String city;
    private String district;
    private String businessDistrict;


    public int getBusinessDistrictCode() {
        return businessDistrictCode;
    }

    public void setBusinessDistrictCode(int businessDistrictCode) {
        this.businessDistrictCode = businessDistrictCode;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public String getBusinessDistrict() {
        return businessDistrict;
    }

    public void setBusinessDistrict(String businessDistrict) {
        this.businessDistrict = businessDistrict;
    }

    public static Creator<City> getCreator() {
        return CREATOR;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String priovince) {
        this.province = priovince;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(int districtCode) {
        this.districtCode = districtCode;
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        public City createFromParcel(Parcel source) {
            City mCity = new City();
            mCity.regionId = source.readInt();
            mCity.province = source.readString();
            mCity.city = source.readString();
            mCity.district = source.readString();
            mCity.provinceCode = source.readInt();
            mCity.cityCode = source.readInt();
            mCity.districtCode = source.readInt();
            mCity.businessDistrictCode = source.readInt();
            mCity.businessDistrict = source.readString();

            return mCity;
        }

        public City[] newArray(int size) {
            return new City[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(regionId);
        parcel.writeString(province);
        parcel.writeString(city);
        parcel.writeString(district);
        parcel.writeString(businessDistrict);
        parcel.writeInt(provinceCode);
        parcel.writeInt(cityCode);
        parcel.writeInt(districtCode);
        parcel.writeInt(businessDistrictCode);
    }

    @Override
    public String toString() {
        return "province:" + province + ", provinceCode:" + provinceCode + ", city" + city + ", cityCode:" + cityCode +
                "\n,district:" + district + ", districtCode:" + districtCode + ",businessDistrict:" + businessDistrict + ", businessDistrictCode:" + businessDistrictCode;
    }

    @Override
    public City clone() {
        City city = null;
        try {
            city = (City) super.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return city;
    }
}
