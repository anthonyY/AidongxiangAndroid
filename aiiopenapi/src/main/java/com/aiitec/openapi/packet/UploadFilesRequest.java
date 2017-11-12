package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.UploadFileRequestQuery;


/**
 * 上传图片请求类
 * 
 * @author Anthony
 *
 */
public class UploadFilesRequest extends Request {

    @JSONField(name="q")
    private UploadFileRequestQuery query = new UploadFileRequestQuery();

    public UploadFileRequestQuery getQuery() {
        return query;
    }

    public void setQuery(UploadFileRequestQuery query) {
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

    public UploadFilesRequest() {
    }

    protected UploadFilesRequest(Parcel in) {
        super(in);
        this.query = in.readParcelable(UploadFileRequestQuery.class.getClassLoader());
    }

    public static final Creator<UploadFilesRequest> CREATOR = new Creator<UploadFilesRequest>() {
        @Override
        public UploadFilesRequest createFromParcel(Parcel source) {
            return new UploadFilesRequest(source);
        }

        @Override
        public UploadFilesRequest[] newArray(int size) {
            return new UploadFilesRequest[size];
        }
    };
}
