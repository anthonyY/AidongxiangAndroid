package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.PostAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseListKtActivity
import com.aidongxiang.app.base.Constants.ARG_TYPE
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.model.User
import com.aidongxiang.business.response.MicroblogListResponseQuery
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import java.util.*
import kotlin.collections.ArrayList

/**
 * 个人中心
 * @author Anthony
 * createTime 2017-12-19
 */
@ContentView(R.layout.activity_person_center)
class PersonCenterActivity : BaseListKtActivity() {

    val datas = ArrayList<Microblog>()
    lateinit var adapter : PostAdapter
    override fun getDatas(): List<*>? = datas


    override fun requestData() {
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        title = "个人资料"
        adapter = PostAdapter(this, datas)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        val headerView = layoutInflater.inflate(R.layout.header_person_center, null, false)
        headerView.findViewById<View>(R.id.ibtn_back).setOnClickListener { onBackPressed() }
        StatusBarUtil.addStatusBarView(headerView, R.color.transparent)
        val tvFans = headerView.findViewById<TextView>(R.id.tvFans)
        val tvFollow = headerView.findViewById<TextView>(R.id.tvFollow)


        adapter.addHeaderView(headerView)
        recyclerView?.adapter = adapter


        setTestData()
    }

    private fun setTestData(){
        for((index, imagePath) in HomeFragment.imgs.withIndex()){
            val microblog = Microblog()
            val user = User()
            user.name = "腊汉尙重"
            user.imagePath = imagePath
            microblog.user = user
            microblog.commentNum = 1125
            microblog.praiseNum = 16
            microblog.repeatNum = 352
            microblog.timestamp = "2017-12-10 12:15:24"
            val images = ArrayList<String>()
            for(i in 0..index){
                images.add(HomeFragment.imgs[Random().nextInt(HomeFragment.imgs.size)])
            }
            microblog.images = images
            microblog.content = "阿三大大飒飒大是是否单独的发按时发斯蒂芬阿斯蒂芬斯蒂芬手动阀手动阀手动阀手动阀 "
            datas.add(microblog)
        }
        adapter.update()
    }

    private fun requestMicroblogList() {
        val query = ListRequestQuery("MicroblogList")
        query.table.page = page
        App.aiiRequest?.send(query, object : AIIResponse<MicroblogListResponseQuery>(this){
            override fun onSuccess(response: MicroblogListResponseQuery, index: Int) {
                super.onSuccess(response, index)
                total = response.total
                if(page == 0){
                    datas.clear()
                }
                response.microblogs?.let {
                    datas.addAll(it)
                }
                if(datas.size == 0){
                    onNoData()
                }
                adapter.update()
            }

            override fun onFailure(content: String?, index: Int) {
                super.onFailure(content, index)
                onNetError()
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }
        })
    }
}
