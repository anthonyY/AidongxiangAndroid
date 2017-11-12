package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.Entity;
import com.aiitec.openapi.model.RequestQuery;

/**
 * 总请求类 所以请求的类都继承该类
 * 
 * @author Anthony
 * 
 */

public class Request extends Entity {
    /**
     * 协议名称
     */
    @JSONField(name = "n")
    protected String namespace;
    /**
     * 缓存时间戳
     */
    @JSONField(name = "t")
    protected String timestampLatest;
    /**
     * SessionId
     */
    @JSONField(name = "s")
    protected String session;

    /** 加密信息 */
    @JSONField(name = "m")
    protected String md5;

    /**
     * 是否开启加密 默认开启
     */
    @JSONField(name = "isOpenMd5", notCombination = true)
    protected static boolean isOpenMd5 = true;


    /** 请求对象 */
    @JSONField(name = "q")
    protected RequestQuery query = new RequestQuery();



    /**
     * 获取加密信息
     * 
     * @return 加密信息
     */
    public String getMd5() {
        return md5;
    }

    /**
     * 设置加密信息
     * 
     * @param md5
     *            加密信息
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * 获取缓存时间戳
     * 
     * @return 缓存时间戳
     */
    public String getTimestampLatest() {
        return timestampLatest;
    }

    /**
     * 设置缓存时间戳
     * 
     * @param timestampLatest
     */
    public void setTimestampLatest(String timestampLatest) {
        this.timestampLatest = timestampLatest;
    }

    /**
     * 获取协议命名空间
     * 
     * @return 协议命名空间
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * 设置协议命名空间
     * 
     * @param namespace
     *            协议命名空间
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * 获取sessionId
     * 
     * @return sessionId
     */
    public String getSession() {
        return session;
    }

    /**
     * 设置sessionId
     * 
     * @param sessionId
     */
    public void setSession(String sessionId) {
        this.session = sessionId;
    }

    /**
     * 获取请求对象
     * 
     * @return 请求对象
     */
    public RequestQuery getQuery() {
        return query;
    }

    /**
     * 设置请求对象
     * 
     * @param query
     *            请求对象
     */
    public void setQuery(RequestQuery query) {
        this.query = query;
    }

    public static boolean isOpenMd5() {
        return isOpenMd5;
    }

    public static void setOpenMd5(boolean isOpenMd5) {
        Request.isOpenMd5 = isOpenMd5;
    }

    public Request() {
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
        dest.writeString(this.md5);
        dest.writeParcelable(this.query, flags);
    }

    protected Request(Parcel in) {
        super(in);
        this.namespace = in.readString();
        this.timestampLatest = in.readString();
        this.session = in.readString();
        this.md5 = in.readString();
        this.query = in.readParcelable(RequestQuery.class.getClassLoader());
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel source) {
            return new Request(source);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };
}
