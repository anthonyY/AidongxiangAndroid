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

    var categorys = arrayListOf("动听侗歌","动听苗歌","山歌","尙重琵琶歌")

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    override fun init(view: View) {
        setToolBar(toolbar)
        setTitle("音频")
        StatusBarUtil.addStatusBarView(titlebar, android.R.color.transparent)
        mPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, activity)

        for(title in categorys){
            mPagerAdapter?.addFragment(AudioListFragment(), title)
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