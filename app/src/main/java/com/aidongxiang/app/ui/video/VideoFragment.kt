package com.aidongxiang.app.ui.video

import android.support.design.widget.TabLayout
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_video_tablelayout.*
import kotlinx.android.synthetic.main.layout_title_bar_home.*

/**
 * 主页 - 视频
 */
@ContentView(R.layout.fragment_video_tablelayout)
class VideoFragment : BaseKtFragment() {

    var categorys = arrayListOf<String>("精彩推荐","精彩斗牛","牯藏节","侗歌")

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    override fun init(view: View) {
        setToolBar(toolbar)
        setTitle("视频")
        StatusBarUtil.addStatusBarView(titlebar, android.R.color.transparent)
        mPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, activity)

        for(title in categorys){
            mPagerAdapter?.addFragment(VideoListFragment(), title)
        }

        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)
        if(categorys.size > 4){
            tablayout.tabMode = TabLayout.MODE_SCROLLABLE
        } else {
            tablayout.tabMode = TabLayout.MODE_FIXED
        }

    }


}