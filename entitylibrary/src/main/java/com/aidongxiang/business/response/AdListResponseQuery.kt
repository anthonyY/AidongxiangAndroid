package com.aidongxiang.business.response

import com.aidongxiang.business.model.Ad
import com.aiitec.openapi.model.ListResponseQuery

/**
 *
 * @author Anthony
 * createTime 2018/1/14.
 * @version 1.0
 */
class AdListResponseQuery : ListResponseQuery(){

    var ads : ArrayList<Ad> ?= null
}