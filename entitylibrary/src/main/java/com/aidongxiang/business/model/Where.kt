package com.aidongxiang.business.model

import com.aiitec.openapi.model.BaseWhere

/**
 *
 * @author Anthony
 * createTime 2017/11/19.
 * @version 1.0
 */
class Where : BaseWhere(){
    var type = -1
    var status = -1
    var code = -1
    var categoryId = -1
    var timeType = -1
    var audioType = -1
    var longitude = -1.0
    var latitude = -1.0
    var userId: Long = -1

}