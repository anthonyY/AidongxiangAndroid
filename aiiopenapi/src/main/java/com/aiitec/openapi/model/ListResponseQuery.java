package com.aiitec.openapi.model;

import android.os.Parcel;


import com.aiitec.openapi.model.ResponseQuery;


public class ListResponseQuery extends ResponseQuery{

	protected int total = -1;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(this.total);
	}

	public ListResponseQuery() {
	}

	protected ListResponseQuery(Parcel in) {
		this.total = in.readInt();
	}

	public static final Creator<ListResponseQuery> CREATOR = new Creator<ListResponseQuery>() {
		@Override
		public ListResponseQuery createFromParcel(Parcel source) {
			return new ListResponseQuery(source);
		}

		@Override
		public ListResponseQuery[] newArray(int size) {
			return new ListResponseQuery[size];
		}
	};
}
