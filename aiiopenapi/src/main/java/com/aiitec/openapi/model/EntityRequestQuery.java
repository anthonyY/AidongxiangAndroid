package com.aiitec.openapi.model;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;


public class EntityRequestQuery extends RequestQuery {
	
	
	/**
	 * 用户对象
	 */
	@JSONField(name="entity")
	private Entity entity;

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeParcelable(this.entity, flags);
	}

	public EntityRequestQuery() {
	}

	protected EntityRequestQuery(Parcel in) {
		this.entity = in.readParcelable(Entity.class.getClassLoader());
	}

	public static final Creator<EntityRequestQuery> CREATOR = new Creator<EntityRequestQuery>() {
		@Override
		public EntityRequestQuery createFromParcel(Parcel source) {
			return new EntityRequestQuery(source);
		}

		@Override
		public EntityRequestQuery[] newArray(int size) {
			return new EntityRequestQuery[size];
		}
	};
}
