package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.text.TextUtils
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.base.Constants.ARG_NAME
import com.aidongxiang.app.ui.square.PostListFragment

/**
 * 我的侗言
 * @author Anthony
 * createTime 2017-12-13
 */
@ContentView(R.layout.activity_my_microblog)
class MyMicroblogActivity : BaseKtActivity() {


    override fun init(savedInstanceState: Bundle?) {
        val name = bundle.getString(ARG_NAME)
        val id = bundle.getLong(ARG_ID, -1)
        title = if(!TextUtils.isEmpty(name)){
            if(Constants.user != null && Constants.user!!.id == id){
                //是我自己
                "我的侗言"
            } else {
                "${name}的侗言"
            }

        } else {
            "我的侗言"
        }

        val fragment = PostListFragment.newInstance(3, id)
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()

    }

}
