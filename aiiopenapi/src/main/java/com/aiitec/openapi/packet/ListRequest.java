package com.aiitec.openapi.packet;

import android.os.Parcel;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.ListRequestQuery;
import com.aiitec.openapi.model.Table;

/**
 * 列表请求类 所以返回列表的请求类都继承该类
 * 
 * @author Anthony
 * 
 */
public class ListRequest extends Request {

    @JSONField(name = "q")
    protected ListRequestQuery query = new ListRequestQuery();

    public ListRequestQuery getQuery() {
        return query;
    }

    public void setQuery(ListRequestQuery query) {
        this.query = query;
    }

    public ListRequest() {
        super();
        Table ta = new Table();
        query.setTable(ta);
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

    protected ListRequest(Parcel in) {
        this.query = in.readParcelable(ListRequestQuery.class.getClassLoader());
    }

    public static final Creator<ListRequest> CREATOR = new Creator<ListRequest>() {
        @Override
        public ListRequest createFromParcel(Parcel source) {
            return new ListRequest(source);
        }

        @Override
        public ListRequest[] newArray(int size) {
            return new ListRequest[size];
        }
    };
}
