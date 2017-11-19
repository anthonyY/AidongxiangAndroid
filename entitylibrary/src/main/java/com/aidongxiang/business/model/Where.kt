package com.aidongxiang.business.model

import com.aiitec.openapi.model.BaseWhere

/**
 *
 * @author Anthony
 * createTime 2017/11/19.
 * @version 1.0
 */
class Where : BaseWhere(){
    var type: Int = 0
    var status: Int = 0
    var code: Int = 0
    var categoryId: Int = 0
    var timeType: Int = 0
    var longitude = -1.0
    var latitude = -1.0
    var userId: Long = 0

}