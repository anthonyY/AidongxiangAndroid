package com.aiitec.openapi.model;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import com.aiitec.openapi.json.annotation.JSONField;


public class BaseWhere extends Entity {

    /** 地区id */
    private int regionId = -1;
    /** 搜索关键字 */
    @JSONField(name = "sk")
    private String searchKey;
    private String mobile;
    private List<Integer> ids;
    private List<String> mobiles;

    public List<String> getMobiles() {
        return mobiles;
    }

    public void setMobiles(List<String> mobiles) {
        this.mobiles = mobiles;
    }

    /**
     * 获取地区id
     * 
     * @return 地区id
     */
    public int getRegionId() {
        return regionId;
    }

    /**
     * 设置地区id
     * 
     * @param regionId
     *            地区id
     */
    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    /**
     * 获取搜索关键字
     * 
     * @return 搜索关键字
     */
    public String getSearchKey() {
        return searchKey;
    }

    /**
     * 设置搜索关键字
     * 
     * @param searchKey
     *            搜索关键字
     */
    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.regionId);
        dest.writeString(this.searchKey);
        dest.writeString(this.mobile);
        dest.writeList(this.ids);
        dest.writeStringList(this.mobiles);
    }

    public BaseWhere() {
    }

    protected BaseWhere(Parcel in) {
        super(in);
        this.regionId = in.readInt();
        this.searchKey = in.readString();
        this.mobile = in.readString();
        this.ids = new ArrayList<Integer>();
        in.readList(this.ids, Integer.class.getClassLoader());
        this.mobiles = in.createStringArrayList();
    }

    public static final Creator<BaseWhere> CREATOR = new Creator<BaseWhere>() {
        @Override
        public BaseWhere createFromParcel(Parcel source) {
            return new BaseWhere(source);
        }

        @Override
        public BaseWhere[] newArray(int size) {
            return new BaseWhere[size];
        }
    };
}
