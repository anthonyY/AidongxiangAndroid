package com.aidongxiang.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.aiitec.openapi.model.Entity;


/**
 * regionInfo 的 region对象
 *
 * @author Anthony
 */
@SuppressWarnings("serial")
public class Region extends Entity implements Parcelable {

    /**
     * 父id
     */
    protected long parentId = -1;
    protected int id = -1;
    /**
     * 拼音
     */
    protected String pinyin;

    protected int deep = -1;

    protected int status = -1;

    protected String timestamp;
    protected String name;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.parentId);
        dest.writeInt(this.id);
        dest.writeString(this.pinyin);
        dest.writeInt(this.deep);
        dest.writeInt(this.status);
        dest.writeString(this.timestamp);
        dest.writeString(this.name);
    }

    public Region() {
    }

    protected Region(Parcel in) {
        this.parentId = in.readLong();
        this.id = in.readInt();
        this.pinyin = in.readString();
        this.deep = in.readInt();
        this.status = in.readInt();
        this.timestamp = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Region> CREATOR = new Parcelable.Creator<Region>() {
        @Override
        public Region createFromParcel(Parcel source) {
            return new Region(source);
        }

        @Override
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };
}
