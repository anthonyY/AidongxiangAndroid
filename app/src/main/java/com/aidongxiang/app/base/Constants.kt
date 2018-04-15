package com.aidongxiang.app.base

import android.os.Environment
import com.aidongxiang.app.event.LocationEvent
import com.aidongxiang.business.model.User

/**
 *
 * @author Anthony
 * createTime 2017/11/20.
 * @version 1.0
 */
object Constants {
    var VIDEOS_DIR: String? = null
    var CACHEDIR = Environment.getExternalStorageDirectory().toString() + "/file/com.aidongxiang.app/uploadfiles/"

    val ARG_ID = "id"
    val ARG_NAME = "name"
    val ARG_URL = "url"
    val ARG_TITLE = "title"
    val ARG_TYPE = "type"
    val ARG_FIRST_LAUNCHER = "firstLauncher"
    val ARG_MOBILE = "mobile"
    val ARG_SMSCODE_ID = "smscodeId"
    val ARG_IS_THIRTY_PART_LOGIN = "isThirtyPartLogin"
    val ARG_ACTION = "action"
    val ARG_MICROBLOG = "action"

    var location : LocationEvent ?= null
    var user : User? = null
}