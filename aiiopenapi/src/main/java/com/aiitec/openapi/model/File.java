package com.aiitec.openapi.model;

import android.os.Parcel;

public class File extends Entity {

	/**文件路径*/
	private String path;
	private String filename;
	private String md5;
	
	

	public String getMd5() {
        return md5;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * 获取文件路径
	 * @return 文件路径
	 */
	public String getPath() {
		return path;
	}
	/**
	 * 设置文件路径
	 * @param path 文件路径
	 */
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.path);
		dest.writeString(this.filename);
		dest.writeString(this.md5);
	}

	public File() {
	}

	protected File(Parcel in) {
		super(in);
		this.path = in.readString();
		this.filename = in.readString();
		this.md5 = in.readString();
	}

	public static final Creator<File> CREATOR = new Creator<File>() {
		@Override
		public File createFromParcel(Parcel source) {
			return new File(source);
		}

		@Override
		public File[] newArray(int size) {
			return new File[size];
		}
	};
}
