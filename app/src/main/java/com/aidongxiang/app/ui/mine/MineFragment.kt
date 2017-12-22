package com.aidongxiang.app.ui.mine

import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.base.Constants.ARG_TYPE
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * 主页 - 我的
 */
@ContentView(R.layout.fragment_mine)
class MineFragment : BaseKtFragment() {


    override fun init(view: View) {
        llSetting.setOnClickListener{
            switchToActivity(SettingActivity::class.java)
        }
        llMyDownload.setOnClickListener {
            switchToActivity(MyDownloadActivity::class.java)
        }
        llMyCollection.setOnClickListener {
            switchToActivity(MyCollectionActivity::class.java)
        }
        llWatchHistory.setOnClickListener {
            switchToActivity(WatchHistoryActivity::class.java)
        }
        llMineUserInfo.setOnClickListener {
            switchToActivity(PersonCenterActivity::class.java)
        }

        ll_mine_fans.setOnClickListener { switchToActivity(FansListActivity::class.java, ARG_TYPE to 2) }
        ll_mine_follow.setOnClickListener { switchToActivity(FansListActivity::class.java, ARG_TYPE to 1) }
        ll_mine_microblog.setOnClickListener { switchToActivity(MyMicroblogActivity::class.java) }

    }



}