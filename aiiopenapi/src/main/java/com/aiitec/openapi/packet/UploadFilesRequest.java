package com.aiitec.openapi.packet;


import com.aiitec.openapi.json.annotation.JSONField;
import com.aiitec.openapi.model.UploadImageRequestQuery;


/**
 * 上传图片请求类
 * 
 * @author Anthony
 *
 */
public class UploadFilesRequest extends Request {

    @JSONField(name="q")
    private UploadImageRequestQuery query = new UploadImageRequestQuery();

    public UploadImageRequestQuery getQuery() {
        return query;
    }

    public void setQuery(UploadImageRequestQuery query) {
        this.query = query;
    }

    public UploadFilesRequest() {
    }


}
