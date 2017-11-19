package com.aidongxiang.business.response

import com.aidongxiang.business.model.Result
import com.aiitec.openapi.packet.Response
import java.util.*

/**
 *
 * @author Anthony
 * createTime 2017/11/19.
 * @version 1.0
 */
class VersionCheckResponse : Response (){

    var results: ArrayList<Result> ?= null
}