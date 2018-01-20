package com.aidongxiang.business.response

import com.aidongxiang.business.model.Category
import com.aiitec.openapi.model.ListResponseQuery

/**
 *
 * @author Anthony
 * createTime 2018/1/14.
 * @version 1.0
 */
class CategoryListResponseQuery : ListResponseQuery(){

    var categorys : ArrayList<Category> ?= null
}