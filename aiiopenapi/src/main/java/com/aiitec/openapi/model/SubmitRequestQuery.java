package com.aiitec.openapi.model;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;


public class SubmitRequestQuery extends RequestQuery {
	
	@JSONField(isPassword=true)
	private String password;
	@JSONField(isPassword=true)
	private String passwordNew;
	private int type ;
    private int smscodeId ;
    private int commentId ;
	private int open ;
	private String mobile;
	private String message;
	private String content;
	@JSONField(name="w")
	private BaseWhere where;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public BaseWhere getWhere() {
		return where;
	}

	public void setWhere(BaseWhere where) {
		this.where = where;
	}

	public int getSmscodeId() {
		return smscodeId;
	}

	public void setSmscodeId(int smscodeId) {
		this.smscodeId = smscodeId;
	}

	public String getPasswordNew() {
		return passwordNew;
	}

	public void setPasswordNew(String passwordNew) {
		this.passwordNew = passwordNew;
	}

	public int getOpen() {
		return open;
	}

	public void setOpen(int open) {
		this.open = open;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.password);
		dest.writeString(this.passwordNew);
		dest.writeInt(this.type);
		dest.writeInt(this.smscodeId);
		dest.writeInt(this.commentId);
		dest.writeInt(this.open);
		dest.writeString(this.mobile);
		dest.writeString(this.message);
		dest.writeString(this.content);
		dest.writeParcelable(this.where, flags);
	}

	public SubmitRequestQuery() {
	}

	protected SubmitRequestQuery(Parcel in) {
		super(in);
		this.password = in.readString();
		this.passwordNew = in.readString();
		this.type = in.readInt();
		this.smscodeId = in.readInt();
		this.commentId = in.readInt();
		this.open = in.readInt();
		this.mobile = in.readString();
		this.message = in.readString();
		this.content = in.readString();
		this.where = in.readParcelable(BaseWhere.class.getClassLoader());
	}

	public static final Creator<SubmitRequestQuery> CREATOR = new Creator<SubmitRequestQuery>() {
		@Override
		public SubmitRequestQuery createFromParcel(Parcel source) {
			return new SubmitRequestQuery(source);
		}

		@Override
		public SubmitRequestQuery[] newArray(int size) {
			return new SubmitRequestQuery[size];
		}
	};
}
