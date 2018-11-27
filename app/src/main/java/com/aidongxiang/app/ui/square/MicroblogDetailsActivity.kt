package com.aidongxiang.app.ui.square

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.PostImgAdapter
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.base.Constants.ARG_MICROBLOG
import com.aidongxiang.app.event.RefreshMicrobolgEvent
import com.aidongxiang.app.interfaces.AppBarStateChangeListener
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.ui.mine.PersonCenterActivity
import com.aidongxiang.app.ui.video.VideoPlayerActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.app.utils.Utils
import com.aidongxiang.app.widgets.CommentDialog
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.app.widgets.ItemDialog
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.request.DeleteActionRequestQuery
import com.aidongxiang.business.request.FocusSwitchRequestQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.activity_microblog_details.*
import kotlinx.android.synthetic.main.item_post_forward.*
import org.greenrobot.eventbus.EventBus
import java.util.regex.Pattern


/***
 * 侗言详情
 * @author Anthony
 * createTime 2017-12-10
 */
@ContentView(R.layout.activity_microblog_details)
class MicroblogDetailsActivity : BaseKtActivity() {

    val REQUEST_FORWARD = 1
    var postId : Long = 0
    var microblog : Microblog ?= null
    lateinit var adapter : SimpleFragmentPagerAdapter
    lateinit var commentDialog : CommentDialog
    lateinit var commentFragment : PostCommentListFragment
    lateinit var forwardFragment : PostForwardListFragment
    lateinit var itemDialog: ItemDialog
    lateinit var shieldDialog: ItemDialog
    lateinit var deleteDialog: CommonDialog

    override fun init(savedInstanceState: Bundle?) {
        StatusBarUtil.addStatusBarView(ll_statusBar2, android.R.color.transparent)
        microblog = bundle.getParcelable(ARG_MICROBLOG)
        microblog?.let {
            postId = it.id
        }
        title = "侗言详情"
        adapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)
        commentFragment = PostCommentListFragment.newInstance(postId)
        forwardFragment = PostForwardListFragment.newInstance(postId)

        adapter.addFragment(forwardFragment, "转发")
        adapter.addFragment(commentFragment, "评论")
        adapter.addFragment(PostAppraiseListFragment.newInstance(postId), "赞")
        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 2


        initDialog()

        setListener()
        setDatas()
    }

    private fun initDialog() {
        commentDialog = CommentDialog(this)
        commentDialog.setOnCommentClickListener { requestCommentSubmit(it) }

        itemDialog = ItemDialog(this)
        val datas = ArrayList<String>()
        if(microblog?.isScreen == 2){
            //屏蔽列表
            datas.add("取消屏蔽")
        } else {
            datas.add("屏蔽")
            datas.add("举报")
            datas.add("取消关注")
        }

        itemDialog.setItems(datas)
        itemDialog.setOnItemClickListener { item, position ->
            microblog?.let {
                if(it.isScreen == 2) {
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
                                if(Constants.user == null){
                                    switchToActivity(LoginActivity::class.java)
                                    return@setOnItemClickListener
                                }
                                switchToActivity(ReportActivity::class.java, Constants.ARG_ID to it.id, ARG_ACTION to 1)
                            }
                            2 -> {
                                requestFocusSubmit(it.user!!.id, 2)
                            }
                        }
                    }
                }
            }
        }
        shieldDialog = ItemDialog(this)
        val shieldDatas = ArrayList<String>()
        shieldDatas.add("屏蔽此条内容")
        shieldDatas.add("屏蔽此人全部内容")
        shieldDialog.setItems(shieldDatas)
        shieldDialog.setOnItemClickListener { _, position ->
            microblog?.let {
                if (position == 0) {
                    requestScreenSubmit(it.id, 2)
                } else if (position == 1) {
                    requestScreenSubmit(it.user!!.id, 1)
                }
            }

        }

        deleteDialog = CommonDialog(this)
        deleteDialog.setTitle("删除侗言")
        deleteDialog.setContent("确定删除这条侗言？")
//        val deleteDatas = ArrayList<String>()
//        deleteDatas.add("删除此条内容")
//        deleteDialog.setItems(datas)
        deleteDialog.setOnConfirmClickListener {
            deleteDialog.dismiss()
            microblog?.let { requestDeleteAction(it.id) }
        }
    }

    private fun setListener() {
        rlForwardTab.setOnClickListener { selectFragmentIndex(0) }
        rlCommentTab.setOnClickListener { selectFragmentIndex(1) }
        rlAppraiseTab.setOnClickListener { selectFragmentIndex(2) }
        left_icon.setOnClickListener{ finish() }
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                selectFragmentIndex(position)
            }
        })
        rlCommentTab.isSelected = true
        selectFragmentIndex(1)
//        val height = llTitleBar2.measuredHeight
        mAppBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {
//                LogUtil.d("STATE", state.name)
                when (state) {
                    AppBarStateChangeListener.State.EXPANDED -> //展开状态
                        ll_statusBar2.visibility = View.GONE
                    AppBarStateChangeListener.State.COLLAPSED -> //折叠状态
                        ll_statusBar2.visibility = View.VISIBLE
                    else -> //中间状态
                        ll_statusBar2.visibility = View.GONE
                }
            }
        })
        flForward.setOnClickListener {
            switchToActivityForResult(PublishPostActivity::class.java, REQUEST_FORWARD, ARG_MICROBLOG to microblog)
        }
        flComment.setOnClickListener {
//            switchToActivityForResult(CommentActivity::class.java, REQUEST_COMMENT)
            commentDialog.show()
        }
        llPraise.setOnClickListener {
            if(microblog == null){
               return@setOnClickListener
            }
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }

            requestPraise(microblog!!.isPraise)
        }
        ivItemMore.setOnClickListener {
            if (Constants.user != null && microblog?.user?.id == Constants.user?.id) {
                //如果是自己的侗言
                val itemDatas = ArrayList<String>()
                itemDatas.add("删除")
                itemDialog.setItems(itemDatas)
            } else {
                val itemDatas = ArrayList<String>()
                itemDatas.add("屏蔽")
                itemDatas.add("举报")
                if (microblog?.isFocus == 2 || microblog?.isFocus == 4) {
                    itemDatas.add("取消关注")
                }
                itemDialog.setItems(itemDatas)
            }
            itemDialog.show()
        }

        tvItemAttention.setOnClickListener {
            microblog?.user?.let {
                requestFocusSubmit(it.id, 1)
            }

        }
        rlItemVideoPlay.setOnClickListener {
            microblog?.videoPath?.let {
                switchToActivity(VideoPlayerActivity::class.java, VideoPlayerActivity.ARG_PATH to it)
            }
        }
        rlItemChildVideoPlay.setOnClickListener {
            microblog?.originMicroblog?.videoPath?.let {
                switchToActivity(VideoPlayerActivity::class.java, VideoPlayerActivity.ARG_PATH to it)
            }
        }
    }

    private fun requestPraise(open : Int) {
        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = SubmitRequestQuery("PraiseSwitch")
        query.action = AIIAction.THREE
        query.id = postId
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                microblog?.let {
                    if(it.isPraise == 2){
                        it.isPraise = 1
                        val praiseNum = it.praiseNum+1
                        ivItemPraiseNum.setImageResource(R.drawable.common_btn_like_nor)
                        tvItemPraiseNum.text = praiseNum.toString()
                        tv_appraise_num.text = praiseNum.toString()
                        it.praiseNum = praiseNum
                    } else {
                        it.isPraise = 2
                        val praiseNum = it.praiseNum+1
                        ivItemPraiseNum.setImageResource(R.drawable.common_btn_like_pre)
                        tvItemPraiseNum.text = praiseNum.toString()
                        tv_appraise_num.text = praiseNum.toString()
                        it.praiseNum = praiseNum
                    }
                }

            }
        })
    }
    private fun requestCommentSubmit(content : String) {
        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = SubmitRequestQuery("CommentSubmit")
        query.action = AIIAction.THREE
        query.id = postId
        query.content = content
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                microblog?.let {
                    it.commentNum ++
                    tvItemCommentNum.text = it.commentNum.toString()
                    tv_comment_num.text = it.commentNum.toString()
                    commentFragment.onRefresh()
                }

            }
        })
    }

    fun selectFragmentIndex(index : Int){
        viewpager.currentItem = index
        when(index){
            0->{
                rlForwardTab.isSelected = true
                rlCommentTab.isSelected = false
                rlAppraiseTab.isSelected = false
            }
            1->{
                rlForwardTab.isSelected = false
                rlCommentTab.isSelected = true
                rlAppraiseTab.isSelected = false
            }
            2->{
                rlCommentTab.isSelected = false
                rlForwardTab.isSelected = false
                rlAppraiseTab.isSelected = true
            }
        }
    }

    private fun setDatas() {

        val item = microblog ?: return

        if(item.isFocus == 2 || item.isFocus == 4){
            tvItemAttention.visibility = View.GONE
        } else if(Constants.user?.id == item.user?.id){
            tvItemAttention.visibility = View.GONE
        } else {
            tvItemAttention.visibility = View.VISIBLE
        }
        var nickName = ""
        val timestamp = item.timestamp
        tvItemTime.text = timestamp
        var avatar = ""
        item.user?.let {
            it.nickName?.let {  nickName = it }
            it.imagePath?.let { avatar = it }
        }
        GlideImgManager.load(this, avatar, R.drawable.ic_avatar_default, ivItemAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)
        tvItemName.text = nickName

        var datas = ArrayList<String>()

        item.images?.let { datas = it }
        tvItemContent.text = item.content
        val spanCount = if (datas.size == 1) { 1 } else { 3 }
        recycler_post_img.visibility = View.VISIBLE
        recycler_post_img.layoutManager = GridLayoutManager(this, spanCount)
        val adapter = PostImgAdapter(this, datas)
        adapter.setOnRecyclerViewItemClickListener { _, position ->
            switchToActivity(BigImageActivity::class.java, BigImageActivity.ARG_IMAGES to datas, BigImageActivity.ARG_POSITION to position)
        }
        recycler_post_img.adapter = adapter

        Utils.setMicoblogVideoInfo(this, item.videoPath, rlItemVideoPlay, ivVideoThumb)
        Utils.setMicoblogVideoInfo(this, microblog?.originMicroblog?.videoPath, rlItemChildVideoPlay, ivVideoThumbChild)


        if(item.originMicroblog != null){
            includeItemForward.visibility = View.VISIBLE
            var forwardImages = ArrayList<String>()

            item.originMicroblog!!.images?.let { forwardImages = it }
            val forwardUserName =  item.originMicroblog?.user?.nickName
//

            val content = "@${forwardUserName}  ${item.originMicroblog!!.content}"
            val pattern = Pattern.compile("@.*?\\s{1}")
            val spanableInfo = SpannableString(content)
            val userId = item.originMicroblog?.user?.id
            val m = pattern.matcher(content)
            if (m.find()) {
                val clickSpan = Clickable(this, userId!!)
                spanableInfo.setSpan(clickSpan, m.start(), m.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            tvItemChildContent?.text = spanableInfo
            val forwardSpanCount = if (forwardImages.size == 1) { 1 } else { 3 }
            recyclerItemChildImg?.layoutManager = GridLayoutManager(this, forwardSpanCount)
            val forwardAdapter = PostImgAdapter(this, forwardImages)
            forwardAdapter.setOnRecyclerViewItemClickListener { _, i ->
                switchToActivity(BigImageActivity::class.java, BigImageActivity.ARG_IMAGES to forwardImages, BigImageActivity.ARG_POSITION to i)
            }
            recyclerItemChildImg?.adapter = forwardAdapter

        } else {
            includeItemForward.visibility = View.GONE
        }


        if(!TextUtils.isEmpty(item.address)){
            llItemAddress.visibility = View.VISIBLE
            tvItemAddress.text = item.address
        } else {
            llItemAddress.visibility = View.GONE
        }
        tvItemPraiseNum.text = item.praiseNum.toString()
        tvItemCommentNum.text = item.commentNum.toString()
        tvItemForwardNum.text = item.repeatNum.toString()
        tv_appraise_num.text = item.praiseNum.toString()
        tv_comment_num.text = item.commentNum.toString()
        tv_forward_num.text = item.repeatNum.toString()
        if(item.isPraise == 2){
            ivItemPraiseNum.setImageResource(R.drawable.common_btn_like_pre)
        } else {
            ivItemPraiseNum.setImageResource(R.drawable.common_btn_like_nor)
        }
    }


//    /**
//     * 请求侗言
//     */
//    fun requestMicroblogDetails() {
//
//        val query = RequestQuery("MicroblogDetails")
//        query.action = AIIAction.valueOf(type)
//        App.aiiRequest.send(query, object : AIIResponse<MicroblogListResponseQuery>(this) {
//            override fun onSuccess(response: MicroblogListResponseQuery?, index: Int) {
//                super.onSuccess(response, index)
//
//            }
//
//            override fun onCache(content: MicroblogListResponseQuery?, index: Int) {
//                super.onCache(content, index)
//                content?.let { getMicroblogList(it) }
//            }
//        })
//    }

    internal inner class Clickable(val context: Context, val userId : Long ) : ClickableSpan() {

        /**
         * 重写父类点击事件
         */
        override fun onClick(v: View) {
            (context as BaseKtActivity).switchToActivity(PersonCenterActivity::class.java, Constants.ARG_ID to userId)
        }

        /**
         * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
         */
        override fun updateDrawState(ds: TextPaint) {
            ds.color = Color.BLUE
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
//       2屏蔽侗言，1用户屏蔽(用户所有侗言)
        query.action = AIIAction.valueOf(action)
        query.id = id
        query.open = 1
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                microblog?.let {
                    if(it.isScreen == 2){
                        it.isScreen = 1
                    } else {
                        it.isScreen = 2
                    }
                    EventBus.getDefault().post(RefreshMicrobolgEvent())
                }
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
//        1屏蔽评论，2屏蔽侗言，3用户屏蔽(用户所有侗言)
        query.action = AIIAction.TWO
        query.ids = arrayListOf(id)
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                toast("删除成功")
                EventBus.getDefault().post(RefreshMicrobolgEvent())
                dismissDialog()
                finish()
            }
        })
    }


    /**
     * 关注 / 取消关注
     */
    private fun requestFocusSubmit(id: Long, open: Int) {
        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = FocusSwitchRequestQuery()
        query.namespace = "FocusSwitch"
        query.userId = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                microblog?.let {
                    if(open == 1){
                        if(it.isFocus < 2){
                            it.isFocus = 2
                        } else {
                            it.isFocus = 4
                        }

                        tvItemAttention.visibility = View.GONE
                    } else {
                        if(it.isFocus == 4){
                            it.isFocus = 3
                        } else {
                            it.isFocus = 1
                        }

                        if(Constants.user?.id == it.user?.id){
                            tvItemAttention.visibility = View.GONE
                        } else {
                            tvItemAttention.visibility = View.VISIBLE
                        }
                    }
                    EventBus.getDefault().post(RefreshMicrobolgEvent())
                }
            }
        })
    }

}
