package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.adapter.PostAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseListKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.ui.square.MicroblogDetailsActivity
import com.aidongxiang.app.ui.square.PublishPostActivity
import com.aidongxiang.app.ui.square.ReportActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.app.widgets.CommentDialog
import com.aidongxiang.app.widgets.ItemDialog
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.model.User
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.request.FocusSwitchRequestQuery
import com.aidongxiang.business.response.MicroblogListResponseQuery
import com.aidongxiang.business.response.UserDetailsResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.jcodecraeer.xrecyclerview.XRecyclerView

/**
 * 个人中心
 * @author Anthony
 * createTime 2017-12-19
 */
@ContentView(R.layout.activity_person_center)
class PersonCenterActivity : BaseListKtActivity() {

    val datas = ArrayList<Microblog>()
    lateinit var adapter : PostAdapter
    var id : Long = -1
    lateinit var itemDialog: ItemDialog
    lateinit var shieldDialog: ItemDialog
    lateinit var deleteDialog: ItemDialog
    var clickMicroblog: Microblog? = null
    var clickPosition = -1
    lateinit var commentDialog: CommentDialog
    lateinit var ivAvatar : ImageView
    lateinit var tvName : TextView
    lateinit var tvFocus : TextView
    lateinit var tvFans : TextView
    lateinit var tvFollow : TextView
    lateinit var lineTop2 : View
    lateinit var tvHeaderMicroblogLabel : TextView

    override fun getDatas(): List<*>? = datas


    override fun requestData() {
        requestMicroblogList()
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        title = "个人资料"
        adapter = PostAdapter(this, datas)
        recyclerView?.layoutManager = LinearLayoutManager(this)

        val headerView = layoutInflater.inflate(R.layout.header_person_center, null, false)
        headerView.findViewById<View>(R.id.ibtn_back).setOnClickListener { onBackPressed() }
        StatusBarUtil.addStatusBarView(headerView, R.color.colorPrimaryDark)
        tvFans = headerView.findViewById(R.id.tvFans)
        tvFollow = headerView.findViewById(R.id.tvFollow)
        ivAvatar = headerView.findViewById(R.id.ivAvatar)
        tvName = headerView.findViewById(R.id.tvName)
        tvFocus = headerView.findViewById(R.id.tvFocus)
        lineTop2 = headerView.findViewById(R.id.lineTop2)
        tvHeaderMicroblogLabel = headerView.findViewById(R.id.tvHeaderMicroblogLabel)


        id = bundle.getLong(ARG_ID)
        adapter.addHeaderView(headerView)
        recyclerView?.adapter = adapter
        (recyclerView as XRecyclerView).setLoadingMoreEnabled(false)
        initDialog()
        adapter.setOnRecyclerViewItemClickListener { _, position ->
            if (position > 0) {
                val microblog = datas[position - 1]
                switchToActivity(MicroblogDetailsActivity::class.java, Constants.ARG_MICROBLOG to microblog)
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
                        switchToActivity(PublishPostActivity::class.java, Constants.ARG_MICROBLOG to datas[positon - 1])
                    }
                    R.id.ivItemAvatar -> {
                        switchToActivity(PersonCenterActivity::class.java, ARG_ID to datas[positon - 1].user!!.id)
                    }
                    R.id.tvItemFocus -> {
                        requestFocusSubmit(datas[positon - 1].id, 1)
                    }
                }
            }
        }, R.id.ivItemMore, R.id.flItemPraise, R.id.flItemForward, R.id.flItemComment, R.id.ivItemAvatar)
        requestUserDetailsFromUserId()
        requestMicroblogList()
    }


    private fun initDialog() {

        itemDialog = ItemDialog(this)
        val datas = ArrayList<String>()
        datas.add("屏蔽")
        datas.add("举报")
        datas.add("取消关注")



        itemDialog.setItems(datas)
        itemDialog.setOnItemClickListener { item, position ->
            clickMicroblog?.let {
//                if(type == 5) {
//                    //屏蔽列表, 这里取消屏蔽
//                    requestScreenSubmit(it.id, 2)
//                } else {
                    if (Constants.user != null && it.user?.id == Constants.user?.id) {
                        deleteDialog.show()
                    } else {
                        when (position) {
                            0 -> {
                                shieldDialog.show()
                            }
                            1 -> {
                                switchToActivity(ReportActivity::class.java, ARG_ID to it.id)
                            }
                            2 -> {
                                requestFocusSubmit(it.user!!.id, it.isFocus)
                            }
                        }
                    }
//                }
            }
        }
        shieldDialog = ItemDialog(this)
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

        deleteDialog = ItemDialog(this)
        val deleteDatas = ArrayList<String>()
        deleteDatas.add("删除此条内容")
        deleteDialog.setItems(datas)
        deleteDialog.setOnItemClickListener { _, _ ->
            clickMicroblog?.let { requestDeleteAction(it.id) }
        }

        commentDialog = CommentDialog(this)
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
        val query = SubmitRequestQuery()
        query.namespace = "ScreenSwitch"
//        1屏蔽评论，2屏蔽微博，3用户屏蔽(用户所有微博)
        query.action = AIIAction.valueOf(action)
        query.id = id
        query.open = 1
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
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
        val query = RequestQuery()
        query.namespace = "DeleteAction"
//        1屏蔽评论，2屏蔽微博，3用户屏蔽(用户所有微博)
        query.action = AIIAction.TWO
        query.id = id
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                onRefresh()
            }
        })
    }

    /**
     * 关注 / 取消关注
     */
    private fun requestFocusSubmit(id: Long, open: Int) {
        val query = FocusSwitchRequestQuery()
        query.namespace = "FocusSwitch"
        query.userId = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
//                onRefresh()
                if(clickPosition >= 0 && clickPosition < datas.size){
                    if(open == 1){
                        datas[clickPosition].isFocus = 2
                    } else {
                        datas[clickPosition].isFocus = 1
                    }
                    adapter.notifyItemChanged(clickPosition + 1)
                    clickMicroblog = null
                    clickPosition = -1
                }

            }
        })
    }



    private fun requestCommentSubmit(postId: Long, content: String) {

        val query = SubmitRequestQuery("CommentSubmit")
        query.action = AIIAction.THREE
        query.id = postId
        query.content = content
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                if(clickPosition >= 0 && clickPosition < datas.size){
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

        val query = SubmitRequestQuery("PraiseSwitch")
        query.action = AIIAction.THREE
        query.id = postId
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this){
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

                adapter.notifyItemChanged(clickPosition+1)
            }
        })
    }

    private fun requestMicroblogList() {
        val query = ListRequestQuery("MicroblogList")
        query.action = AIIAction.THREE
        query.table.page = page
        val where = Where()
        where.userId = id
        query.table.where = where
        App.aiiRequest.send(query, object : AIIResponse<MicroblogListResponseQuery>(this, progressDialog){
            override fun onSuccess(response: MicroblogListResponseQuery, index: Int) {
                super.onSuccess(response, index)
                getMicroblogList(response)
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

    private fun requestUserDetailsFromUserId() {
        val query = ListRequestQuery("UserDetails")
        query.id = id
        App.aiiRequest.send(query, object : AIIResponse<UserDetailsResponseQuery>(this, progressDialog){
            override fun onSuccess(response: UserDetailsResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.user?.let { getUserDetails(it) }

            }

            override fun onCache(content: UserDetailsResponseQuery?, index: Int) {
                super.onCache(content, index)
                content?.user?.let { getUserDetails(it) }
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
    fun getUserDetails(user: User){

        tvFans.text = user.fansNum.toString()
        tvFollow.text = user.focusNum.toString()
        GlideImgManager.load(this, user.imagePath, R.drawable.ic_avatar_default, ivAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)
        tvName.text = user.nickName.toString()
        if(Constants.user != null && user.id == Constants.user!!.id){
            //是我自己
            tvFocus.visibility = View.GONE
            lineTop2.visibility = View.GONE
            tvHeaderMicroblogLabel.text = "Ta的微博"
        } else {
            tvHeaderMicroblogLabel.text = "他的微博"
            tvFocus.visibility = View.VISIBLE
            lineTop2.visibility = View.VISIBLE
            if(user.isFocus == 2){
                tvFocus.text = "已关注"
            } else {
                tvFocus.text = "+关注"
            }
        }
    }

}
