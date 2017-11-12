package com.aidongxiang.app.ui.audio

import android.support.design.widget.TabLayout
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.ui.video.VideoListFragment
import com.aidongxiang.app.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_video_tablelayout.*
import kotlinx.android.synthetic.main.layout_title_bar_home.*

/**
 * 主页 - 音频
 */
@ContentView(R.layout.fragment_video_tablelayout)
class AudioFragment : BaseKtFragment() {

    var categorys = arrayListOf<String>("动听侗歌","动听苗歌","山歌","尙重琵琶歌")

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    override fun init(view: View) {
        setToolBar(toolbar)
        StatusBarUtil.addStatusBarView(view)
        mPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, activity)

        for(title in categorys){
            mPagerAdapter?.addFragment(VideoListFragment(), title)
        }

        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)
        tablayout.tabMode = TabLayout.MODE_FIXED
    }


}