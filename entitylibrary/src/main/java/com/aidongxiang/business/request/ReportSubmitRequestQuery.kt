package com.aidongxiang.business.request

import com.aiitec.openapi.model.RequestQuery

/**
 *
 * @author Anthony
 * createTime 2018/4/15.
 * @version 1.0
 */
class ReportSubmitRequestQuery : RequestQuery(){
    var categoryId : Long = -1
    var content : String ?= null
}