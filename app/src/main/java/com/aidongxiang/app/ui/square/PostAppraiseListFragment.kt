package com.aidongxiang.app.ui.square

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.PostAppraiseAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.widgets.CommentDialog
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
 * 微博赞列表
 * @author Anthony
 * createTime 2017/12/7.
 * @version 1.0
 */
@ContentView(R.layout.fragment_video_comment)
class PostAppraiseListFragment : BaseListKtFragment(){

    lateinit var adapter : PostAppraiseAdapter
    val datas = ArrayList<Comment>()
    override fun getDatas(): List<*>? = datas
    lateinit var commentDialog : CommentDialog

    var postId : Long = -1

    override fun init(view: View) {
        super.init(view)

        postId = arguments.getLong(ARG_ID)
        adapter = PostAppraiseAdapter(activity, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        recyclerView?.setPullRefreshEnabled(false)

        commentDialog = CommentDialog(activity)
        commentDialog.setOnCommentClickListener { requestCommentSubmit(it) }
        faBtnComment.visibility = View.GONE
//        setDatas()
        requestCommentList()
    }

//    fun setDatas(){
//        for(i in 0..9){
//            val comment = Comment()
//            comment.content = "啦啦十大傻傻的啊实打实大收到"
//            comment.praiseNum = 1555+i
//            comment.timestamp = "2017-12-0$i 12:42:15"
//            val user = User()
//            user.id = i
//            if(i > 3){
//                user.imagePath = HomeFragment.imgs[Random().nextInt(HomeFragment.imgs.size)]
//            } else {
//                user.imagePath = ""
//            }
//
//            user.name = "小淘气"
//            comment.user = user
//            datas.add(comment)
//        }
//        adapter.update()
//    }
    override fun requestData() {
        Handler().postDelayed({onLoadFinish()}, 1000)
    }

    fun requestCommentList(){
        val listQuery = ListRequestQuery("CommentList")
        listQuery.action = AIIAction.ONE
        listQuery.id = postId
        listQuery.table.page = page
        App.aiiRequest?.send(listQuery, object : AIIResponse<CommentListResponseQuery>(activity){
            override fun onSuccess(response: CommentListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                getCommentList(response!!)
            }

            override fun onCache(content: CommentListResponseQuery?, index: Int) {
                super.onCache(content, index)
                getCommentList(content!!)
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

    fun requestCommentSubmit(content : String){
        val query = SubmitRequestQuery("CommentSubmit")
        query.action = AIIAction.ONE
        query.id = postId
        query.content = content
        App.aiiRequest?.send(query, object : AIIResponse<ResponseQuery>(activity){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                toast("评论成功")
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
        fun newInstance(postId : Long) : PostAppraiseListFragment {
            val fragment = PostAppraiseListFragment()
            val bundle = Bundle()
            bundle.putLong(ARG_ID, postId)
            fragment.arguments = bundle
            return fragment
        }
    }
}