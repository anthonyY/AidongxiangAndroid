package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.enums.CacheMode;
import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.RequestQuery;

public class DetailsRequest extends RequestQuery {
	
	
	@JSONField(notCombination=true)
	protected CacheMode cacheMode = CacheMode.PRIORITY_OFTEN;
	@Override
	public CacheMode getCacheMode() {
	    return cacheMode;
	}
	@Override
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
		dest.writeInt(this.cacheMode == null ? -1 : this.cacheMode.ordinal());
	}

	public DetailsRequest() {
	}

	protected DetailsRequest(Parcel in) {
		super(in);
		int tmpCacheMode = in.readInt();
		this.cacheMode = tmpCacheMode == -1 ? null : CacheMode.values()[tmpCacheMode];
	}

	public static final Creator<DetailsRequest> CREATOR = new Creator<DetailsRequest>() {
		@Override
		public DetailsRequest createFromParcel(Parcel source) {
			return new DetailsRequest(source);
		}

		@Override
		public DetailsRequest[] newArray(int size) {
			return new DetailsRequest[size];
		}
	};
}
