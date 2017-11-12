package com.aiitec.openapi.packet;


import android.os.Parcel;

/**
 * session返回类
 * @author Anthony
 *
 */
public final class SessionResponse extends Response{

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public SessionResponse() {
    }

    protected SessionResponse(Parcel in) {
        super(in);
    }

    public static final Creator<SessionResponse> CREATOR = new Creator<SessionResponse>() {
        @Override
        public SessionResponse createFromParcel(Parcel source) {
            return new SessionResponse(source);
        }

        @Override
        public SessionResponse[] newArray(int size) {
            return new SessionResponse[size];
        }
    };
}
