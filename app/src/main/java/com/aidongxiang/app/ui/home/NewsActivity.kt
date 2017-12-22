package com.aidongxiang.app.ui.home

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import kotlinx.android.synthetic.main.activity_news.*

/**
 * 新闻资讯类
 * @author Anthony
 * createTime 2017-11-20
 * @version 1.0
 */
@ContentView(R.layout.activity_news)
class NewsActivity : BaseKtActivity() {

    lateinit var mPagerAdapter: SimpleFragmentPagerAdapter
    private var position: Int = 0


    override fun init(savedInstanceState: Bundle?) {
        position = bundle.getInt("position")
        mPagerAdapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)


        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)


        setDatas()
//        if (position == 1) {
//            viewpager.currentItem = 1
//        }

    }

    private fun setDatas(){
        val titles = arrayOf("热点新闻","旅游指南","民族风情","侗乡美食","摄影分享")
        for((index, title) in titles.withIndex()){
            mPagerAdapter.addFragment(NewsListFragment.newInstance(index), title)
        }
        if(titles.size > 4){
            tablayout.tabMode = TabLayout.MODE_SCROLLABLE
        } else {
            tablayout.tabMode = TabLayout.MODE_FIXED
        }

        mPagerAdapter.notifyDataSetChanged()
    }


}
