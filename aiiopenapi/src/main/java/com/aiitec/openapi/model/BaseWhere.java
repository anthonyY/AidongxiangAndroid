package com.aiitec.openapi.model;

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



}
