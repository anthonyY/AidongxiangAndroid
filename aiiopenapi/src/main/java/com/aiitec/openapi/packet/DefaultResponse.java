package com.aiitec.openapi.packet;

import android.os.Parcel;


public class DefaultResponse extends Response {
    public DefaultResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected DefaultResponse(Parcel in) {
    }

    public static final Creator<DefaultResponse> CREATOR = new Creator<DefaultResponse>() {
        @Override
        public DefaultResponse createFromParcel(Parcel source) {
            return new DefaultResponse(source);
        }

        @Override
        public DefaultResponse[] newArray(int size) {
            return new DefaultResponse[size];
        }
    };
}
