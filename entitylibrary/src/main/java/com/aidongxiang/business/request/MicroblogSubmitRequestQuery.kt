package com.aidongxiang.business.request

import com.aidongxiang.business.model.Microblog
import com.aiitec.openapi.model.RequestQuery

/**
 *
 * @author Anthony
 * createTime 2018/3/26.
 * @version 1.0
 */
class MicroblogSubmitRequestQuery : RequestQuery(){
    var microblog : Microblog?= null
}