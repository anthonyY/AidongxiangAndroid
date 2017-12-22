package com.aidongxiang.app.ui.square

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.PostAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.model.User
import com.aidongxiang.business.response.MicroblogListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import java.util.*

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list)
class PostListFragment : BaseListKtFragment(){

    lateinit var adapter : PostAdapter
    val datas = ArrayList<Microblog>()
    /**
     * 0 关注
     * 1 热门
     * 2 我的
     * 3 屏蔽的
     */
    var type = 0
    override fun getDatas(): List<*>? = datas

    override fun requestData() {
        LogUtil.e("type:$type")
        Handler().postDelayed({onLoadFinish()}, 1000)
    }

    override fun init(view: View) {
        super.init(view)

        type = arguments.getInt(ARG_TYPE)
        adapter = PostAdapter(context!!, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        if(type == 3){
            addHeaderView()
        }
        recyclerView?.adapter = adapter

        adapter.setOnRecyclerViewItemClickListener { v, position ->
            if(position > 0){
                val id = datas[position-1]
                switchToActivity(MicroblogDetailsActivity::class.java, ARG_ID to id)
            }
        }
        for((index, imagePath) in HomeFragment.imgs.withIndex()){
            val microblog = Microblog()
            val user = User()
            user.name = "腊汉尙重"
            user.imagePath = imagePath
            microblog.user = user
            microblog.commentNum = 1125
            microblog.praiseNum = 16
            microblog.isFocus = (type+1)%2
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
    private fun addHeaderView() {

        val header = TextView(activity)
        header.text = "已屏蔽的用户"
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val padding = resources.getDimension(R.dimen.margin_screen_left).toInt()
        header.setPadding(padding, padding, padding , padding)
        adapter.addHeaderView(header)
    }

    /**
     * 请求微博列表
     */
    fun requestMicroblogList(){
        val query = ListRequestQuery("MicroblogList")
        query.table.page = page
        query.action = AIIAction.valueOf(type+1)
        App.aiiRequest?.send(query, object : AIIResponse<MicroblogListResponseQuery>(activity){
            override fun onSuccess(response: MicroblogListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                getMicroblogList(response)
            }

            override fun onCache(content: MicroblogListResponseQuery?, index: Int) {
                super.onCache(content, index)
                getMicroblogList(content)
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

    private fun getMicroblogList(response: MicroblogListResponseQuery?) {
        response?.let {
            if(page == 1){
                datas.clear()
            }

            response.microblogs?.let { datas.addAll(it) }

        }

    }

    companion object {
        val ARG_TYPE = "type"
        fun newInstance(type : Int) : PostListFragment{
            val fragment = PostListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }
    }
}