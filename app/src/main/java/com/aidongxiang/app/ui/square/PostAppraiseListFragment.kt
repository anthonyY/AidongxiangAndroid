package com.aidongxiang.app.ui.square

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.PostAppraiseAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.business.model.User
import com.aidongxiang.business.response.UserListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.fragment_video_comment.*
import java.util.*

/**
 * 微博赞列表
 * @author Anthony
 * createTime 2017/12/7.
 * @version 1.0
 */
@ContentView(R.layout.fragment_video_comment)
class PostAppraiseListFragment : BaseListKtFragment(){

    lateinit var adapter : PostAppraiseAdapter
    val datas = ArrayList<User>()
    override fun getDatas(): List<*>? = datas

    var postId : Long = -1

    override fun init(view: View) {
        super.init(view)

        arguments?.let {  postId = it.getLong(ARG_ID)}
        adapter = PostAppraiseAdapter(activity!!, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        recyclerView?.setPullRefreshEnabled(false)
        faBtnComment.visibility = View.GONE
        requestUserList()
    }
    override fun requestData() {
        requestUserList()
    }

    private fun requestUserList(){
        val listQuery = ListRequestQuery("UserList")
        listQuery.action = AIIAction.THREE
        listQuery.id = postId
        listQuery.table.page = page
        App.aiiRequest.send(listQuery, object : AIIResponse<UserListResponseQuery>(activity, false){
            override fun onSuccess(response: UserListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getUserList(it) }
            }

            override fun onCache(content: UserListResponseQuery?, index: Int) {
                super.onCache(content, index)
                content?.let {getUserList(content)}
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

    private fun getUserList(response: UserListResponseQuery) {
        total = response.total
        if(page == 1){
            datas.clear()
        }
        response.users?.let { datas.addAll(it) }

        adapter.update()
        checkIsEmpty()
    }


    companion object {
        var ARG_ID = "id"
        fun newInstance(postId : Long) : PostAppraiseListFragment {
            val fragment = PostAppraiseListFragment()
            val bundle = Bundle()
            bundle.putLong(ARG_ID, postId)
            fragment.arguments = bundle
            return fragment
        }
    }
}