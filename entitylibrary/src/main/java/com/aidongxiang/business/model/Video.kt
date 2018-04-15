package com.aidongxiang.business.model

import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/12/3.
 * @version 1.0
 */
class Video : Entity(){
    var id : Long = -1
    var imagePath : String ?= null
    var playNum = 0
    var audioLength : String ?= null
    var audioPath: String ?= null
    var name: String ?= null
    var timestamp: String ?= null
    var isSelected = false




}