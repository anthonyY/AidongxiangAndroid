package com.aidongxiang.business.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/12/10.
 * @version 1.0
 */
class Microblog : Entity(){

    var id = 0
    var content : String ?= null
    var timestamp : String ?= null
    var address : String ?= null
    var praiseNum = 0
    var commentNum = 0
    var repeatNum = 0
    var videoPath : String ?= null
    var images  : ArrayList<String> ?= null
    var isFocus = 0
    var user : User ?= null

}