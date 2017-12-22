package com.aidongxiang.app.ui.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeNewsAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.ui.home.HomeFragment.Companion.imgs
import com.aiitec.moreschool.base.BaseListKtFragment
import java.util.*

/**
 *
 * @author Anthony
 * createTime 2017/11/20.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list)
class NewsListFragment : BaseListKtFragment(){

    lateinit var adapter : HomeNewsAdapter
    val datas = ArrayList<String>()
    var random = Random()

    override fun getDatas(): List<*>? = datas

    override fun requestData() {
        requestNewsList()
    }

    private fun requestNewsList() {
        for(i in 0..10){
            datas.add(imgs[random.nextInt(HomeFragment.imgs.size)])
        }
        if(activity != null){
            adapter.update()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = HomeNewsAdapter(activity, datas)
        requestData()
    }

    override fun init(view: View) {
        super.init(view)

        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        adapter.setOnRecyclerViewItemClickListener { v, position ->
            switchToActivity(CommonWebViewActivity::class.java, Constants.ARG_TITLE to "新闻详情", Constants.ARG_URL to "http://www.baidu.com")

        }

    }
    companion object {

        fun newInstance(categoryId : Int) : NewsListFragment{
            val fragment = NewsListFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.ARG_ID, categoryId)
            fragment.arguments = bundle
            return fragment
        }
    }
}