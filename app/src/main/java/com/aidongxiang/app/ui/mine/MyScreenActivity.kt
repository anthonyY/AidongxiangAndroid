package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.square.PostListFragment
import kotlinx.android.synthetic.main.activity_mine_tablelayout.*

/**
 * 我的屏蔽设置
 * @author Anthony
 * createTime 2017-12-17
 */
@ContentView(R.layout.activity_mine_tablelayout)
class MyScreenActivity : BaseKtActivity() {

    companion object {
        val ARG_POSITION = "position"
    }
    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    lateinit var screenUserListFragment : ScreenUserListFragment
    lateinit var postListFragment : PostListFragment
    override fun init(savedInstanceState: Bundle?) {

        val position = bundle.getInt(ARG_POSITION, 0)
        title = "屏蔽设置"
        mPagerAdapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)

        screenUserListFragment = ScreenUserListFragment()
        postListFragment = PostListFragment.newInstance(5)
        mPagerAdapter?.addFragment(screenUserListFragment, "用户")
        mPagerAdapter?.addFragment(postListFragment, "侗言")


        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)
        tablayout.tabMode = TabLayout.MODE_FIXED


        viewpager.currentItem = position
    }



}