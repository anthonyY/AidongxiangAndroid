package com.aidongxiang.app.ui.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeNewsAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.Constants
import com.aidongxiang.business.model.Article
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.response.ArticleListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
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
    val datas = ArrayList<Article>()
    private var categoryId = -1

    override fun getDatas(): List<*>? = datas

    override fun requestData() {
        requestArticleList()
    }

//    private fun requestNewsList() {
//        for(i in 0..10){
//            val article = Article()
//            article.timestamp = "2018-0$i-1${i + 2} 12:15:34"
//            article.title = "农耕部落初见成效"
//            article.abstract = "农耕部落初见成效农耕部落初见成效农耕部落初见成效"
//            article.id = i
//            article.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
//            datas.add(article)
//        }
//        if(activity != null){
//            adapter.update()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = HomeNewsAdapter(activity, datas, false)

        arguments?.let { categoryId = it.getInt(Constants.ARG_ID) }
        requestData()
    }

    override fun init(view: View) {
        super.init(view)

        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        adapter.setOnRecyclerViewItemClickListener { _, position ->
            if(position > 0){
                val id = datas[position-1].id
                switchToActivity(ArticleDetailsActivity::class.java, Constants.ARG_TITLE to "新闻详情", Constants.ARG_ID to id)

            }

        }

        requestArticleList()

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


    /**
     * 请求文章列表
     */
    private fun requestArticleList() {
        val query = ListRequestQuery("ArticleList")
        query.table.page = 1
        query.action = AIIAction.ONE
        val where = Where()
        where.categoryId = categoryId
        query.table.where = where
        App.aiiRequest.send(query, object : AIIResponse<ArticleListResponseQuery>(activity, progressDialog) {
            override fun onSuccess(response: ArticleListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getArticleList(it) }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }

            override fun onCache(response: ArticleListResponseQuery?, index: Int) {
                super.onCache(response, index)
                response?.let { getArticleList(it) }
            }
        })
    }

    /**
     * 获取新闻数据，设置到adapter里
     */
    private fun getArticleList(response: ArticleListResponseQuery) {
        total = response.total
        if(page == 1){
            datas.clear()
        }
        response.articles?.let { datas.addAll(it) }
        adapter.update()
    }

}