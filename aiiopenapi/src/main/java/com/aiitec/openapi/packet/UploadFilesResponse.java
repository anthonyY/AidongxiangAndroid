package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.FileListResponseQuery;

/**
 * 上传图片返回类
 * 
 * @author Anthony
 * 
 */
public class UploadFilesResponse extends ListResponse {

	@JSONField(name = "q")
	FileListResponseQuery query;

	public FileListResponseQuery getQuery() {
		return query;
	}

	public void setQuery(FileListResponseQuery query) {
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

	public UploadFilesResponse() {
	}

	protected UploadFilesResponse(Parcel in) {
		super(in);
		this.query = in.readParcelable(FileListResponseQuery.class.getClassLoader());
	}

	public static final Creator<UploadFilesResponse> CREATOR = new Creator<UploadFilesResponse>() {
		@Override
		public UploadFilesResponse createFromParcel(Parcel source) {
			return new UploadFilesResponse(source);
		}

		@Override
		public UploadFilesResponse[] newArray(int size) {
			return new UploadFilesResponse[size];
		}
	};
}
