package com.aidongxiang.business.response

import com.aidongxiang.business.model.Fans
import com.aiitec.openapi.model.ListResponseQuery

/**
 *
 * @author Anthony
 * createTime 2018/4/17.
 * @version 1.0
 */
class FansListResponseQuery : ListResponseQuery(){
    var users : ArrayList<Fans> ?= null
}