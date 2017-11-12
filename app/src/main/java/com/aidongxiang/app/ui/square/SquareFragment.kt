package com.aidongxiang.app.ui.square

import android.support.design.widget.TabLayout
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.ui.video.VideoListFragment
import com.aidongxiang.app.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_square.*

/**
 * 主页 - 广场
 */
@ContentView(R.layout.fragment_square)
class SquareFragment : BaseKtFragment() {


    var categorys = arrayListOf("关注","热门")

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    override fun init(view: View) {
        setToolBar(toolbar)
        StatusBarUtil.addStatusBarView(view)
        mPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, activity)

        for(title in categorys){
            mPagerAdapter?.addFragment(PostListFragment(), title)
        }

        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)
        tablayout.tabMode = TabLayout.MODE_SCROLLABLE

        ibtn_title_search.setOnClickListener{
            switchToActivity(PublishPostActivity::class.java)
        }
    }


}