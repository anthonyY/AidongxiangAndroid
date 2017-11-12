package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.ListResponseQuery;





/**
 * 列表返回基类
 * 所以列表返回的都继承该类
 * @author Anthony
 *
 */
public class ListResponse extends Response{
	
	@JSONField(name="q")
	ListResponseQuery query;

	public ListResponseQuery getQuery() {
		return query;
	}

	public void setQuery(ListResponseQuery query) {
		this.query = query;
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

	public ListResponse() {
	}

	protected ListResponse(Parcel in) {
		this.query = in.readParcelable(ListResponseQuery.class.getClassLoader());
	}

}
