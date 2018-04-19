package com.aidongxiang.business.response

import com.aidongxiang.business.model.Navigation
import com.aiitec.openapi.model.ListResponseQuery

/**
 *
 * @author Anthony
 * createTime 2018/4/19.
 * @version 1.0
 */
class NavigationListListResponseQuery : ListResponseQuery(){
    var navigations : ArrayList<Navigation> ?= null
}