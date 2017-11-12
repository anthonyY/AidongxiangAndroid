package com.aidongxiang.app.model;

import com.aiitec.openapi.model.Entity;


/**
 * regionInfo 的 region对象
 *
 * @author Anthony
 */
@SuppressWarnings("serial")
public class Region extends Entity {

    /**
     * 父id
     */
    protected long parentId = -1;
    /**
     * 拼音
     */
    protected String pinyin;

    protected int deep = -1;

    protected int status = -1;

    protected String timestamp;

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取父Id
     *
     * @return 父id
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * 设置父id
     *
     * @param parentId 父id
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取拼音
     *
     * @return 拼音
     */
    public String getPinyin() {
        return pinyin;
    }

    /**
     * 设置拼音
     *
     * @param pinyin 拼音
     */
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
