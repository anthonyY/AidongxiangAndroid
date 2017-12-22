package com.aidongxiang.business.response

import com.aidongxiang.business.model.Comment
import com.aiitec.openapi.model.ListResponseQuery

/**
 *
 * @author Anthony
 * createTime 2017/12/9.
 * @version 1.0
 */
class CommentListResponseQuery : ListResponseQuery(){
    var comments : ArrayList<Comment> ?= null
}