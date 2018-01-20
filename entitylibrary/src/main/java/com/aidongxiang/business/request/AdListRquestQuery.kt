package com.aidongxiang.business.request

import com.aiitec.openapi.model.RequestQuery

/**
 *
 * @author Anthony
 * createTime 2018/1/14.
 * @version 1.0
 */
class AdListRquestQuery : RequestQuery (){

    var positionId = -1
    init {
        namespace = "AdList"
    }
}