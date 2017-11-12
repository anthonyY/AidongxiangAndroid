package com.aiitec.openapi.model;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;


public class ResponseQuery extends Entity{

	@JSONField(name="t")
	protected String timestamp;
	
	@JSONField(name="s")
	protected int status;
	
	@JSONField(name="d")
	protected String desc;
	
	protected String expire;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.timestamp);
		dest.writeInt(this.status);
		dest.writeString(this.desc);
		dest.writeString(this.expire);
	}

	public ResponseQuery() {
	}

	protected ResponseQuery(Parcel in) {
		super(in);
		this.timestamp = in.readString();
		this.status = in.readInt();
		this.desc = in.readString();
		this.expire = in.readString();
	}

	public static final Creator<ResponseQuery> CREATOR = new Creator<ResponseQuery>() {
		@Override
		public ResponseQuery createFromParcel(Parcel source) {
			return new ResponseQuery(source);
		}

		@Override
		public ResponseQuery[] newArray(int size) {
			return new ResponseQuery[size];
		}
	};
}
