package com.aidongxiang.app.ui.square

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.ForwardAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.response.MicroblogListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import java.util.*

/**
 * 微博转发列表
 * @author Anthony
 * createTime 2017/12/7.
 * @version 1.0
 */
@ContentView(R.layout.fragment_video_comment)
class PostForwardListFragment : BaseListKtFragment(){

    lateinit var adapter : ForwardAdapter
    val datas = ArrayList<Microblog>()
    override fun getDatas(): List<*>? = datas

    var postId : Long = -1

    override fun init(view: View) {
        super.init(view)

        postId = arguments.getLong(ARG_ID)
        adapter = ForwardAdapter(activity, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        recyclerView?.setPullRefreshEnabled(false)


        requestMicroblogList()
    }

    override fun requestData() {
        requestMicroblogList()
    }

    /**
     * 请求微博列表
     */
    private fun requestMicroblogList() {
        val query = ListRequestQuery("MicroblogList")
        query.table.page = page
        query.action = AIIAction.FOUR
        query.id = postId
        App.aiiRequest.send(query, object : AIIResponse<MicroblogListResponseQuery>(activity) {
            override fun onSuccess(response: MicroblogListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getMicroblogList(it) }

            }

            override fun onCache(content: MicroblogListResponseQuery?, index: Int) {
                super.onCache(content, index)
                content?.let { getMicroblogList(it) }
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

    private fun getMicroblogList(response: MicroblogListResponseQuery) {

        total = response.total
        if (page == 1) {
            datas.clear()
        }
        response.microblogs?.let { datas.addAll(it) }

        adapter.update()

    }

    companion object {
        var ARG_ID = "id"
        fun newInstance(postId : Long) : PostForwardListFragment {
            val fragment = PostForwardListFragment()
            val bundle = Bundle()
            bundle.putLong(ARG_ID, postId)
            fragment.arguments = bundle
            return fragment
        }
    }
}