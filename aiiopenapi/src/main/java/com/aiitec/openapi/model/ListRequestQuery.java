package com.aiitec.openapi.model;

import android.os.Parcel;

import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.annotation.JSONField;

public class ListRequestQuery extends RequestQuery {

    @JSONField(notCombination = true)
    protected CacheMode cacheMode = CacheMode.PRIORITY_COMMON;
    @JSONField(name = "ta")
    private Table table;
    private int category = -1;
    protected int positionId = -1;

    @Override
    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    public ListRequestQuery() {
        table = new Table();
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getPositionId() {
        return positionId;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.cacheMode == null ? -1 : this.cacheMode.ordinal());
        dest.writeParcelable(this.table, flags);
        dest.writeInt(this.category);
        dest.writeInt(this.positionId);
    }

    protected ListRequestQuery(Parcel in) {
        super(in);
        int tmpCacheMode = in.readInt();
        this.cacheMode = tmpCacheMode == -1 ? null : CacheMode.values()[tmpCacheMode];
        this.table = in.readParcelable(Table.class.getClassLoader());
        this.category = in.readInt();
        this.positionId = in.readInt();
    }

    public static final Creator<ListRequestQuery> CREATOR = new Creator<ListRequestQuery>() {
        @Override
        public ListRequestQuery createFromParcel(Parcel source) {
            return new ListRequestQuery(source);
        }

        @Override
        public ListRequestQuery[] newArray(int size) {
            return new ListRequestQuery[size];
        }
    };
}
