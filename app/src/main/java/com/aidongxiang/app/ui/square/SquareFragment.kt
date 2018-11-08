package com.aidongxiang.app.ui.square

import android.support.v4.view.ViewPager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.ui.login.LoginActivity
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
        setTitle("热门微博")

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
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            switchToActivity(PublishPostActivity::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        if(Constants.user != null){
            if(mPagerAdapter?.count != 2){
                mPagerAdapter?.clear()
                mPagerAdapter?.addFragment(PostListFragment.newInstance(1), "关注")
                mPagerAdapter?.addFragment(PostListFragment.newInstance(2), "热门")
                mPagerAdapter?.notifyDataSetChanged()
                radioGroupSquare.visibility = View.VISIBLE
                tv_title?.visibility = View.GONE
            }

        } else {
            if(mPagerAdapter?.count != 1) {
                mPagerAdapter?.clear()
                mPagerAdapter?.addFragment(PostListFragment.newInstance(2), "热门")
                mPagerAdapter?.notifyDataSetChanged()
                radioGroupSquare.visibility = View.GONE
                tv_title?.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 更新另外一个Fragment
     */
    fun updateOtherFragment() {
        if(viewpager.currentItem == 1){
            (mPagerAdapter?.getItem(0) as PostListFragment).onRefresh()
        } else {
            if(mPagerAdapter!!.count > 1){
                (mPagerAdapter?.getItem(1) as PostListFragment).onRefresh()
            }
        }
    }

}