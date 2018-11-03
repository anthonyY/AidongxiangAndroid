package com.aidongxiang.app.ui.square

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.adapter.PostAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.base.Constants.ARG_MICROBLOG
import com.aidongxiang.app.base.Constants.ARG_TYPE
import com.aidongxiang.app.event.RefreshMicrobolgEvent
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.ui.mine.PersonCenterActivity
import com.aidongxiang.app.ui.video.VideoPlayerActivity
import com.aidongxiang.app.widgets.CommentDialog
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.app.widgets.ItemDialog
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.request.DeleteActionRequestQuery
import com.aidongxiang.business.request.FocusSwitchRequestQuery
import com.aidongxiang.business.response.MicroblogListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list)
class PostListFragment : BaseListKtFragment() {

    val REQUEST_FORWARD = 2
    lateinit var adapter: PostAdapter
    val datas = ArrayList<Microblog>()
    /**
     * 1 关注
     * 2 热门
     * 3 我的
     * 4 微博转发列表
     * 5 屏蔽的
     */
    var type = 0

    override fun getDatas(): List<*>? = datas
    lateinit var itemDialog: ItemDialog
    lateinit var shieldDialog: ItemDialog
    lateinit var deleteDialog: CommonDialog
    var clickMicroblog: Microblog? = null
    var clickPosition = -1
    lateinit var commentDialog: CommentDialog
    var userId : Long = -1
    override fun requestData() {
        LogUtil.e("type:$type")
        requestMicroblogList()
    }


    override fun init(view: View) {
        super.init(view)
        arguments?.let {
            type = it.getInt(ARG_TYPE)
            userId = it.getLong(ARG_ID)
        }

        adapter = PostAdapter(context!!, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        if (type == 5) {
            addHeaderView()
        }
        recyclerView?.adapter = adapter

        initDialog()

        adapter.setOnRecyclerViewItemClickListener { _, position ->
            if (position > 0) {
                val microblog = datas[position - 1]
                switchToActivity(MicroblogDetailsActivity::class.java, ARG_MICROBLOG to microblog)
            }
        }
        adapter.setOnViewInItemClickListener(CommonRecyclerViewAdapter.OnViewInItemClickListener { v, positon ->
            if (positon > 0) {
                when (v.id) {
                    R.id.ivItemMore -> {
                        clickMicroblog = datas[positon - 1]
                        clickPosition = positon - 1
                        if (Constants.user != null && clickMicroblog!!.user?.id == Constants.user?.id) {
                            //如果是自己的微博
                            val itemDatas = ArrayList<String>()
                            itemDatas.add("删除")
                            itemDialog.setItems(itemDatas)
                        } else {
                            val itemDatas = ArrayList<String>()
                            itemDatas.add("屏蔽")
                            itemDatas.add("举报")
                            if (clickMicroblog!!.isFocus == 2) {
                                itemDatas.add("取消关注")
                            }/* else {
                                itemDatas.add("关注")
                            }*/
                            itemDialog.setItems(itemDatas)
                        }
                        itemDialog.show()
                    }

                    R.id.flItemPraise -> {
                        requestPraise(datas[positon - 1].id, datas[positon - 1].isPraise, positon - 1)
                    }
                    R.id.flItemComment -> {
                        clickMicroblog = datas[positon - 1]
                        clickPosition = positon - 1
                        commentDialog.show()
                    }
                    R.id.flItemForward -> {
                        switchToActivityForResult(PublishPostActivity::class.java, REQUEST_FORWARD, ARG_MICROBLOG to datas[positon - 1])
                    }
                    R.id.ivItemAvatar -> {
                        switchToActivity(PersonCenterActivity::class.java, ARG_ID to datas[positon - 1].user!!.id)
                    }
                    R.id.tvItemFocus -> {
                        datas[positon - 1].user?.let {
                            requestFocusSubmit(it.id, 1, positon-1)
                        }

                    }
                    R.id.rlItemVideoPlay -> {
                        datas[positon - 1].videoPath?.let {
                            switchToActivity(VideoPlayerActivity::class.java, VideoPlayerActivity.ARG_PATH to it)
                        }
                    }
                    R.id.rlItemChildVideoPlay -> {
                        datas[positon - 1].originMicroblog?.videoPath?.let {
                            switchToActivity(VideoPlayerActivity::class.java, VideoPlayerActivity.ARG_PATH to it)
                        }
                    }
                }
            }
        }, R.id.ivItemMore, R.id.flItemPraise, R.id.flItemForward, R.id.flItemComment, R.id.tvItemFocus, R.id.ivItemAvatar, R.id.rlItemVideoPlay, R.id.rlItemChildVideoPlay)
//        rlItemVideoPlay.setOnClickListener {
//            microblog?.let {
//
//            }
//        }
//        rlItemChildVideoPlay.setOnClickListener {
//            microblog?.originMicroblog?.let {
//                switchToActivity(VideoPlayerActivity::class.java, VideoPlayerActivity.ARG_PATH to it.videoPath)
//            }
//        }
        requestMicroblogList()

        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshMicroblog(event: RefreshMicrobolgEvent) {
        onRefresh()
    }

    private fun initDialog() {

        itemDialog = ItemDialog(activity!!)
        val datas = ArrayList<String>()
        if(type == 5){
            //屏蔽列表
            datas.add("取消屏蔽")
        } else {
            datas.add("屏蔽")
            datas.add("举报")
            datas.add("取消关注")
        }

        itemDialog.setItems(datas)
        itemDialog.setOnItemClickListener { item, position ->
            clickMicroblog?.let {
                if(type == 5) {
                    //屏蔽列表, 这里取消屏蔽
                    requestScreenSubmit(it.id, 2)
                } else {
                    if (Constants.user != null && it.user?.id == Constants.user?.id) {
                        deleteDialog.show()
                    } else {
                        when (position) {
                            0 -> {
                                shieldDialog.show()
                            }
                            1 -> {
                                switchToActivity(ReportActivity::class.java, ARG_ID to it.id, ARG_ACTION to 1)
                            }
                            2 -> {
                                requestFocusSubmit(it.user!!.id, it.isFocus, clickPosition)
                            }
                        }
                    }
                }
            }
        }
        shieldDialog = ItemDialog(activity!!)
        val shieldDatas = ArrayList<String>()
        shieldDatas.add("屏蔽此条内容")
        shieldDatas.add("屏蔽此人全部内容")
        shieldDialog.setItems(shieldDatas)
        shieldDialog.setOnItemClickListener { _, position ->
            clickMicroblog?.let {
                if (position == 0) {
                    requestScreenSubmit(it.id, 2)
                } else if (position == 1) {
                    requestScreenSubmit(it.user!!.id, 1)
                }
            }

        }

        deleteDialog = CommonDialog(activity!!)
        deleteDialog.setTitle("删除微博")
        deleteDialog.setContent("确定删除这条微博？")
//        val deleteDatas = ArrayList<String>()
//        deleteDatas.add("删除此条内容")
//        deleteDialog.setItems(datas)
        deleteDialog.setOnConfirmClickListener {
            deleteDialog.dismiss()
            clickMicroblog?.let { requestDeleteAction(it.id) }
        }

        commentDialog = CommentDialog(activity!!)
        commentDialog.setOnCommentClickListener {
            val content = it
            clickMicroblog?.let {
                requestCommentSubmit(it.id, content)
            }

        }
    }

    /**
     * 屏蔽
     */
    private fun requestScreenSubmit(id: Long, action: Int) {
        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = SubmitRequestQuery()
        query.namespace = "ScreenSwitch"
//        1屏蔽评论，2屏蔽微博，3用户屏蔽(用户所有微博)
        query.action = AIIAction.valueOf(action)
        query.id = id
        query.open = 1
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                onRefresh()
            }
        })
    }

    /**
     * 删除
     */
    private fun requestDeleteAction(id: Long) {
        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = DeleteActionRequestQuery()
        query.namespace = "DeleteAction"
//        1屏蔽评论，2屏蔽微博，3用户屏蔽(用户所有微博)
        query.action = AIIAction.TWO
        query.ids = arrayListOf(id)
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                toast("删除成功")
                onRefresh()
            }
        })
    }

    /**
     * 关注 / 取消关注
     */
    private fun requestFocusSubmit(id: Long, open: Int, position: Int) {
        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = FocusSwitchRequestQuery()
        query.namespace = "FocusSwitch"
        query.userId = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
//                onRefresh()
                if(position >= 0 && position < datas.size){
                    if(open == 1){
                        datas[position].isFocus = 2
                        toast("关注成功")
                    } else {
                        toast("已取消关注")
                        datas[position].isFocus = 1
                    }
                    adapter.notifyItemChanged(position + 1)
                }

            }
        })
    }

    /**
     * 添加头部局
     */
    private fun addHeaderView() {

        val header = TextView(activity)
        header.text = "已屏蔽的微博"
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val padding = resources.getDimension(R.dimen.margin_screen_left).toInt()
        header.setPadding(padding, padding, padding, padding)
        adapter.addHeaderView(header)
    }

    /**
     * 请求微博列表
     */
    fun requestMicroblogList() {
        if (type == 1 && Constants.user == null) {
            //关注 并且 没有登录
            return
        }
        val query = ListRequestQuery("MicroblogList")
        query.table.page = page
        query.action = AIIAction.valueOf(type)
        if(type == 3){
            val where = Where()
            where.userId = userId
            query.table.where = where
        }

        App.aiiRequest.send(query, object : AIIResponse<MicroblogListResponseQuery>(activity, progressDialog) {
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


    private fun requestCommentSubmit(postId: Long, content: String) {
        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = SubmitRequestQuery("CommentSubmit")
        query.action = AIIAction.THREE
        query.id = postId
        query.content = content
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                if(clickPosition >= 0 && clickPosition < datas.size){
                    toast("评论成功")
                    datas[clickPosition].commentNum = datas[clickPosition].commentNum + 1
                    adapter.notifyItemChanged(clickPosition + 1)
                    clickMicroblog = null
                    clickPosition = -1
                }
            }
        })
    }


    /**
     * 请求赞
     * @param open 1 赞， 2 取消赞
     */
    private fun requestPraise(postId : Long, open : Int, position : Int) {

        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = SubmitRequestQuery("PraiseSwitch")
        query.action = AIIAction.THREE
        query.id = postId
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, false){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                val isPraise = datas[position].isPraise
                var praiseNum = datas[position].praiseNum
                if(isPraise == 2){
                    datas[position].isPraise = 1
                    praiseNum--
                    datas[position].praiseNum = praiseNum
                } else {
                    datas[position].isPraise = 2
                    praiseNum++
                    datas[position].praiseNum = praiseNum
                }
                adapter.notifyItemChanged(position+1)

            }
        })
    }
    companion object {
        fun newInstance(type: Int): PostListFragment {
            return newInstance(type, -1)
        }
        fun newInstance(type: Int, userId : Long): PostListFragment {
            val fragment = PostListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_TYPE, type)
            bundle.putLong(ARG_ID, userId)
            fragment.arguments = bundle
            return fragment
        }
    }
}