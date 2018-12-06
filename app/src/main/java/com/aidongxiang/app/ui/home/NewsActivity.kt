package com.aidongxiang.app.ui.home

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.business.model.Category
import com.aidongxiang.business.response.CategoryListResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
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
    var categorys = ArrayList<Category>()


    override fun init(savedInstanceState: Bundle?) {
        setTitle("资讯")
        position = bundle.getInt("position")
        mPagerAdapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)

        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)
        requestCategoryList()


    }

    private fun setDatas(){
        //测试数据
//        val titles = arrayOf("热点新闻","旅游指南","民族风情","侗乡美食","摄影分享")
//        for(i in 1 until titles.size){
//            val category = Category()
//            category.id = i+1L
//            category.name = titles[i]
//        }

        mPagerAdapter.clear()
        for(category in categorys){
            mPagerAdapter.addFragment(NewsListFragment.newInstance(category.id.toInt()), category.name)
        }
        if(categorys.size > 4){
            tablayout.tabMode = TabLayout.MODE_SCROLLABLE
        } else {
            tablayout.tabMode = TabLayout.MODE_FIXED
        }

        mPagerAdapter.notifyDataSetChanged()

        if (position > 0) {
            viewpager.currentItem = position
        }
    }


    fun requestCategoryList() {
        val query = ListRequestQuery("CategoryList")
        query.action = AIIAction.THREE

        App.aiiRequest.send(query, object : AIIResponse<CategoryListResponseQuery>(this, progressDialog) {
            override fun onSuccess(response: CategoryListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                categorys.clear()
                response?.categorys?.let { categorys.addAll(it) }
                setDatas()
            }

            override fun onCache(response: CategoryListResponseQuery?, index: Int) {
                super.onCache(response, index)
                categorys.clear()
                response?.categorys?.let { categorys.addAll(it) }
                setDatas()
            }
        })
    }

}
