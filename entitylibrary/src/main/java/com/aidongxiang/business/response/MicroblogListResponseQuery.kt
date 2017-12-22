package com.aidongxiang.business.response

import com.aidongxiang.business.model.Microblog
import com.aiitec.openapi.model.ListResponseQuery

/**
 *
 * @author Anthony
 * createTime 2017/12/10.
 * @version 1.0
 */
class MicroblogListResponseQuery : ListResponseQuery(){

    var microblogs : ArrayList<Microblog> ?= null
}