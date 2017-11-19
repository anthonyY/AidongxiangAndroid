package com.aidongxiang.business.model

import com.aiitec.openapi.json.annotation.JSONField
import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/11/19.
 * @version 1.0
 */
class Wxpay : Entity(){
    var retcode = 0
    var payType = 0
    var retmsg: String? = null
    var appid: String? = null
    var noncestr: String? = null
    @JSONField(name = "package")
    var packager: String? = null
    var partnerid: Long = 0
    var prepayid: String? = null
    var sign: String? = null
    var transferNo: String? = null

    /**
     * 订单支付类别：
     * 1—— 商城支付
     * 2—— 我的订单支付
     * 3—— 积分支付
     */
    var payCategory = 0
}