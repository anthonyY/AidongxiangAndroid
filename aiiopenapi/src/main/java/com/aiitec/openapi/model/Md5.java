package com.aiitec.openapi.model;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;

public class Md5 extends Entity {

    private String item;
    
    @JSONField(notCombination=true)
    private String key;

    
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.item);
        dest.writeString(this.key);
    }

    public Md5() {
    }

    protected Md5(Parcel in) {
        super(in);
        this.item = in.readString();
        this.key = in.readString();
    }

    public static final Creator<Md5> CREATOR = new Creator<Md5>() {
        @Override
        public Md5 createFromParcel(Parcel source) {
            return new Md5(source);
        }

        @Override
        public Md5[] newArray(int size) {
            return new Md5[size];
        }
    };
}
