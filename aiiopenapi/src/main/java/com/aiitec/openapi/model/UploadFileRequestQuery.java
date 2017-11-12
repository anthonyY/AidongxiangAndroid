package com.aiitec.openapi.model;

import android.os.Parcel;

import java.util.List;

import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.json.enums.AIIAction;

public class UploadFileRequestQuery extends RequestQuery {

    /**
     * action 组包使用a， 默认空
     */
    @JSONField(name = "a")
    protected AIIAction action = AIIAction.ONE;

    public AIIAction getAction() {
        return action;
    }

    public void setAction(AIIAction action) {
        this.action = action;
    }

    private List<Md5> md5s;

    public List<Md5> getMd5s() {
        return md5s;
    }

    public void setMd5s(List<Md5> md5s) {
        this.md5s = md5s;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.action == null ? -1 : this.action.ordinal());
        dest.writeTypedList(this.md5s);
    }

    public UploadFileRequestQuery() {
    }

    protected UploadFileRequestQuery(Parcel in) {
        super(in);
        int tmpAction = in.readInt();
        this.action = tmpAction == -1 ? null : AIIAction.values()[tmpAction];
        this.md5s = in.createTypedArrayList(Md5.CREATOR);
    }

    public static final Creator<UploadFileRequestQuery> CREATOR = new Creator<UploadFileRequestQuery>() {
        @Override
        public UploadFileRequestQuery createFromParcel(Parcel source) {
            return new UploadFileRequestQuery(source);
        }

        @Override
        public UploadFileRequestQuery[] newArray(int size) {
            return new UploadFileRequestQuery[size];
        }
    };
}
