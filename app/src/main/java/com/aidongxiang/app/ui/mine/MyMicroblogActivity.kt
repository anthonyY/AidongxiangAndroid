package com.aidongxiang.app.ui.mine

import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.square.PostListFragment

/**
 * 我的微博
 * @author Anthony
 * createTime 2017-12-13
 */
@ContentView(R.layout.activity_my_microblog)
class MyMicroblogActivity : BaseKtActivity() {


    override fun init(savedInstanceState: Bundle?) {
        title = "我的微博"
        val fragment = PostListFragment.newInstance(2)
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()

    }

}
