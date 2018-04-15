package com.aidongxiang.business.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2018/4/8.
 * @version 1.0
 */
class Audio : Entity(){
    var id : Long = -1
    var audioType = 1
    var name : String ?= null
    var price = 0f
    var payType = 0
    var praiseNum = 0
    var playNum = 0
    var commentNum = 0
    var isFavorite = 0
    var status = 0
    var imagePath : String ?= null
    var isDownload = 0
    var isPraise = 0
    var isBuy = 0
    var description : String ?= null
    var audioPath : String ?= null
    var audioLength : String ?= null
}