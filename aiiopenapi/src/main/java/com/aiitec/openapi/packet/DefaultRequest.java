package com.aiitec.openapi.packet;

import android.os.Parcel;

public class DefaultRequest extends Request{
	public DefaultRequest() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
	}

	protected DefaultRequest(Parcel in) {
	}

	public static final Creator<DefaultRequest> CREATOR = new Creator<DefaultRequest>() {
		@Override
		public DefaultRequest createFromParcel(Parcel source) {
			return new DefaultRequest(source);
		}

		@Override
		public DefaultRequest[] newArray(int size) {
			return new DefaultRequest[size];
		}
	};
}
