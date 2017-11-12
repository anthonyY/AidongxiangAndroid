package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.Entity;
import com.aiitec.openapi.model.ResponseQuery;

/**
 * 协议返回基类
 * 所以协议返回类都继承该类
 * @author Anthony
 *
 */
public class Response extends Entity {

	/**
	 * 协议名称
	 */
	@JSONField(name="n") 
	protected String namespace;
	/**
	 * 缓存时间戳
	 */
	@JSONField(name="t") 
	protected String timestampLatest;
	/**
	 * SessionId
	 */
	@JSONField(name="s") 
	protected String session;
	
	/**
	 * 时间戳
	 */
	@JSONField(name="t") 
	protected String timestamp;

	/**
	 * 获取缓存时间戳
	 * @return 缓存时间戳
	 */ 
	public String getTimestampLatest() {
		return timestampLatest;
	}
	/**
	 * 设置缓存时间戳
	 * @param timestampLatest
	 */
	public void setTimestampLatest(String timestampLatest) {
		this.timestampLatest = timestampLatest;
	}


	/**请求参数对象*/
	@JSONField(name="q") 
	protected ResponseQuery query = new ResponseQuery();
	/**
	 * 获取时间戳
	 * @return 时间戳
	 */
	public String getTimestamp() {
		return timestamp;
	}
	/**
	 * 设置时间戳
	 * @param timestamp
	 */
	@JSONField(name="t") 
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * 获取协议命名空间
	 * @return 协议命名空间
	 */
	public String getNamespace() {
		return namespace;
	}
	/**
	 * 设置协议命名空间
	 * @param namespace 协议命名空间
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	/**
	 * 获取sessionId
	 * @return sessionId
	 */
	public String getSession() {
		return session;
	}
	/**
	 * 设置sessionId
	 * @param sessionId
	 */
	public void setSession(String sessionId) {
		this.session = sessionId;
	}
	
	
	/**
	 * 获取请求对象
	 * @return 请求对象
	 */
	public ResponseQuery getQuery() {
		return query;
	}
	/**
	 * 设置请求对象
	 * @param query 请求对象
	 */
	public void setQuery(ResponseQuery query) {
		this.query = query;
	}


	public Response() {
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.namespace);
		dest.writeString(this.timestampLatest);
		dest.writeString(this.session);
		dest.writeString(this.timestamp);
		dest.writeParcelable(this.query, flags);
	}

	protected Response(Parcel in) {
		super(in);
		this.namespace = in.readString();
		this.timestampLatest = in.readString();
		this.session = in.readString();
		this.timestamp = in.readString();
		this.query = in.readParcelable(ResponseQuery.class.getClassLoader());
	}

	public static final Creator<Response> CREATOR = new Creator<Response>() {
		@Override
		public Response createFromParcel(Parcel source) {
			return new Response(source);
		}

		@Override
		public Response[] newArray(int size) {
			return new Response[size];
		}
	};
}
