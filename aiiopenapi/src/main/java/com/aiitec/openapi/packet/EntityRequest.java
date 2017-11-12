package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.EntityRequestQuery;

/**
 * 实体请求类
 * 所有带实体请求的类都继承该类
 * @author Anthony
 *
 */
public class EntityRequest extends Request {
	
	@JSONField(name="q") 
	protected EntityRequestQuery query;
	
	public EntityRequestQuery getQuery() {
		return query;
	}

	public void setQuery(EntityRequestQuery query) {
		this.query = query;
	}

	public EntityRequest() {
		query = new EntityRequestQuery();
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

	protected EntityRequest(Parcel in) {
		this.query = in.readParcelable(EntityRequestQuery.class.getClassLoader());
	}

	public static final Creator<EntityRequest> CREATOR = new Creator<EntityRequest>() {
		@Override
		public EntityRequest createFromParcel(Parcel source) {
			return new EntityRequest(source);
		}

		@Override
		public EntityRequest[] newArray(int size) {
			return new EntityRequest[size];
		}
	};
}
