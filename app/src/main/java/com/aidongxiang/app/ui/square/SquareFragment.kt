package com.aidongxiang.app.ui.square

import android.support.v4.view.ViewPager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_square.*

/**
 * @author Anthony
 * 主页 - 广场
 */
@ContentView(R.layout.fragment_square)
class SquareFragment : BaseKtFragment() {


    var categorys = arrayListOf("关注","热门")

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    override fun init(view: View) {
        setToolBar(toolbar)
        StatusBarUtil.addStatusBarView(titlebar, android.R.color.transparent)
        mPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, activity)

        for((index, title) in categorys.withIndex()){
            mPagerAdapter?.addFragment(PostListFragment.newInstance(index), title)
        }

        viewpager.adapter = mPagerAdapter
//        tablayout.setupWithViewPager(viewpager)
//        tablayout.tabMode = TabLayout.MODE_SCROLLABLE
        radioGroupSquare.setOnCheckedChangeListener { _, i ->
            when(i){
                R.id.rbAttention->viewpager.currentItem = 0
                R.id.rbHot->viewpager.currentItem = 1
            }
        }
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when(position){
                    0->radioGroupSquare.check(R.id.rbAttention)
                    1->radioGroupSquare.check(R.id.rbHot)
                }
            }
        })
        ibtn_title_search.setImageResource(R.drawable.common_btn_photograph)
        ibtn_title_search.setOnClickListener{
            switchToActivity(PublishPostActivity::class.java)
        }
    }


}