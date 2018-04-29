package com.aidongxiang.app.ui.square

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommentAdapter
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.business.model.Comment
import com.aidongxiang.business.response.CommentListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.fragment_video_comment.*
import java.util.*

/**
 * 微博评论列表
 * @author Anthony
 * createTime 2017/12/7.
 * @version 1.0
 */
@ContentView(R.layout.fragment_video_comment)
class PostCommentListFragment : BaseListKtFragment(){

    lateinit var adapter : CommentAdapter
    val datas = ArrayList<Comment>()
    override fun getDatas(): List<*>? = datas

    var postId : Long = -1

    override fun init(view: View) {
        super.init(view)

        postId = arguments.getLong(ARG_ID)
        adapter = CommentAdapter(activity, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        recyclerView?.setPullRefreshEnabled(false)

        adapter.setOnViewInItemClickListener(CommonRecyclerViewAdapter.OnViewInItemClickListener { _, position ->
            if(position < 1){
                return@OnViewInItemClickListener
            }
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@OnViewInItemClickListener
            }
            val isPraise = datas[position-1].isPraise
            val id = datas[position-1].id
            requestPraise(isPraise, id, position-1)
        })

        faBtnComment.visibility = View.GONE
//        setDatas()
        requestCommentList()
    }


    private fun requestPraise(open : Int, id : Long, position : Int) {

        val query = SubmitRequestQuery("PraiseSwitch")
        query.action = AIIAction.TWO
        query.id = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, false){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                if(open == 1){
                    datas[position].isPraise = 2
                } else {
                    datas[position].isPraise = 1
                }
                adapter.update()
            }
        })
    }

    override fun requestData() {
        requestCommentList()
    }

    private fun requestCommentList(){
        val listQuery = ListRequestQuery("CommentList")
        listQuery.action = AIIAction.THREE
        listQuery.id = postId
        listQuery.table.page = page
        App.aiiRequest.send(listQuery, object : AIIResponse<CommentListResponseQuery>(activity){
            override fun onSuccess(response: CommentListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getCommentList(it) }
            }

            override fun onCache(content: CommentListResponseQuery?, index: Int) {
                super.onCache(content, index)
                content?.let { getCommentList(it) }

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


    private fun getCommentList(response: CommentListResponseQuery) {
        total = response.total
        if(page == 1){
            datas.clear()
        }
        response.comments?.let { datas.addAll(it) }

        adapter.update()
        if(datas.size == 0){
            onNoData()
        }
    }


    companion object {
        var ARG_ID = "id"
        fun newInstance(postId : Long) : PostCommentListFragment {
            val fragment = PostCommentListFragment()
            val bundle = Bundle()
            bundle.putLong(ARG_ID, postId)
            fragment.arguments = bundle
            return fragment
        }
    }
}