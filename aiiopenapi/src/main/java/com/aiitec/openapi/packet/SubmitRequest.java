package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.SubmitRequestQuery;

public class SubmitRequest extends Request {


	/**请求对象*/
	@JSONField(name="q") 
	protected SubmitRequestQuery query;

	public SubmitRequestQuery getQuery() {
		return query;
	}
	public void setQuery(SubmitRequestQuery query) {
		this.query = query;
	}


	public SubmitRequest() {
		query = new SubmitRequestQuery();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeParcelable(this.query, flags);
	}

	protected SubmitRequest(Parcel in) {
		super(in);
		this.query = in.readParcelable(SubmitRequestQuery.class.getClassLoader());
	}

	public static final Creator<SubmitRequest> CREATOR = new Creator<SubmitRequest>() {
		@Override
		public SubmitRequest createFromParcel(Parcel source) {
			return new SubmitRequest(source);
		}

		@Override
		public SubmitRequest[] newArray(int size) {
			return new SubmitRequest[size];
		}
	};
}
