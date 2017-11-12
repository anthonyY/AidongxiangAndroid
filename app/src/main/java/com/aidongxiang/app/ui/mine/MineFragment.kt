package com.aidongxiang.app.ui.mine

import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.utils.StatusBarUtil

/**
 * 主页 - 我的
 */
@ContentView(R.layout.fragment_mine)
class MineFragment : BaseKtFragment() {



    override fun init(view: View) {
        StatusBarUtil.addStatusBarView(view)

    }


}