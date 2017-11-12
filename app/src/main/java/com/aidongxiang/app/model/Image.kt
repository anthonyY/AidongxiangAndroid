package com.aidongxiang.app.model

/**
 *
 * @author Anthony
 * createTime 2017/11/11.
 * @version 1.0
 */
class Image {
    constructor()
    constructor(path : String){
        this.path = path
    }
    var path : String ?= null
    var isSelect = false
}