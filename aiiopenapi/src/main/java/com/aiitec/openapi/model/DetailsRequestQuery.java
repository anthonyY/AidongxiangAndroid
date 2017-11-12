package com.aiitec.openapi.model;

import android.os.Parcel;

import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.json.enums.AIIAction;



public class DetailsRequestQuery extends Entity {

    /**
     * action 组包使用a， 默认空
     */
    @JSONField(name = "a")
    protected AIIAction action = AIIAction.NULL;

    @JSONField(name = "cacheMode", notCombination = true)
    protected CacheMode cacheMode = CacheMode.PRIORITY_OFTEN;

    @JSONField(notCombination = true)
    public String namespace;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public AIIAction getAction() {
        return action;
    }

    public void setAction(AIIAction action) {
        this.action = action;
    }

    public CacheMode getCacheMode() {
        return cacheMode;
    }

    public void setCacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.action == null ? -1 : this.action.ordinal());
        dest.writeInt(this.cacheMode == null ? -1 : this.cacheMode.ordinal());
        dest.writeString(this.namespace);
    }

    public DetailsRequestQuery() {
    }

    protected DetailsRequestQuery(Parcel in) {
        super(in);
        int tmpAction = in.readInt();
        this.action = tmpAction == -1 ? null : AIIAction.values()[tmpAction];
        int tmpCacheMode = in.readInt();
        this.cacheMode = tmpCacheMode == -1 ? null : CacheMode.values()[tmpCacheMode];
        this.namespace = in.readString();
    }

    public static final Creator<DetailsRequestQuery> CREATOR = new Creator<DetailsRequestQuery>() {
        @Override
        public DetailsRequestQuery createFromParcel(Parcel source) {
            return new DetailsRequestQuery(source);
        }

        @Override
        public DetailsRequestQuery[] newArray(int size) {
            return new DetailsRequestQuery[size];
        }
    };
}
