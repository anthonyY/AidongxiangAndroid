package com.aiitec.openapi.model;

import android.os.Parcel;

import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.json.enums.AIIAction;

public class RequestQuery extends Entity {

	public RequestQuery(String namespace){
		this.namespace = namespace;
	}
    /**
     * action 组包使用a， 默认空
     */
    @JSONField(name = "a")
    protected AIIAction action = AIIAction.NULL;

    @JSONField(name = "cacheMode", notCombination = true)
    protected CacheMode cacheMode = CacheMode.NONE;

    @JSONField(notCombination = true)
    public String namespace;
    /**
     * 是否需要Gzip压缩 默认为true
     */
    @JSONField(notCombination = true)
    protected boolean isGzip = true;
    
    /**
     * 是否需要session 默认为true
     */
    @JSONField(notCombination = true)
    protected boolean isNeedSession = true;
    
    
    public boolean isNeedSession() {
		return isNeedSession;
	}

	public void setNeedSession(boolean isNeedSession) {
		this.isNeedSession = isNeedSession;
	}

	
	public boolean isGzip() {
		return isGzip;
	}

	public void setGzip(boolean isGzip) {
		this.isGzip = isGzip;
	}

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

    public RequestQuery() {
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
        dest.writeByte(this.isGzip ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNeedSession ? (byte) 1 : (byte) 0);
    }

    protected RequestQuery(Parcel in) {
        super(in);
        int tmpAction = in.readInt();
        this.action = tmpAction == -1 ? null : AIIAction.values()[tmpAction];
        int tmpCacheMode = in.readInt();
        this.cacheMode = tmpCacheMode == -1 ? null : CacheMode.values()[tmpCacheMode];
        this.namespace = in.readString();
        this.isGzip = in.readByte() != 0;
        this.isNeedSession = in.readByte() != 0;
    }

}
