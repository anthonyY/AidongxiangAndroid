package com.aiitec.openapi.packet;


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

    public UploadFilesRequest() {
    }


}
