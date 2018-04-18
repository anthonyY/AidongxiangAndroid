package com.aidongxiang.app.ui.square

import android.content.Context
import android.graphics.Color
import android.media.MediaMetadataRetriever
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
import android.widget.RelativeLayout
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.PostImgAdapter
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.Api
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_MICROBLOG
import com.aidongxiang.app.interfaces.AppBarStateChangeListener
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.ui.mine.PersonCenterActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.app.widgets.CommentDialog
import com.aidongxiang.app.widgets.CustomVideoView
import com.aidongxiang.business.model.Microblog
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import kotlinx.android.synthetic.main.activity_microblog_details.*
import kotlinx.android.synthetic.main.item_post_forward.*
import java.util.HashMap
import java.util.regex.Pattern
import kotlin.collections.ArrayList


/***
 * 微博详情
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
    override fun init(savedInstanceState: Bundle?) {
        StatusBarUtil.addStatusBarView(ll_statusBar2, android.R.color.transparent)
        microblog = bundle.getParcelable(ARG_MICROBLOG)
        microblog?.let {
            postId = it.id
        }
        title = "微博详情"
        adapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)
        commentFragment = PostCommentListFragment.newInstance(postId)
        forwardFragment = PostForwardListFragment.newInstance(postId)
        adapter.addFragment(commentFragment, "评论")
        adapter.addFragment(forwardFragment, "转发")
        adapter.addFragment(PostAppraiseListFragment.newInstance(postId), "赞")
        viewpager.adapter = adapter
        viewpager.offscreenPageLimit = 2

        commentDialog = CommentDialog(this)
        commentDialog.setOnCommentClickListener { requestCommentSubmit(it) }


        if (!TextUtils.isEmpty(microblog!!.videoPath)) {
            rlItemVideoPlay.visibility = View.VISIBLE
            var path : String ?= ""
            if (microblog!!.videoPath!!.startsWith("http")) {
                path = microblog!!.videoPath

            } else {
                path = Api.IMAGE_URL + microblog!!.videoPath
            }
            videoview_item.setVideoPath(path)
            resetVideoWidth(videoview_item, path)
        } else {
            rlItemVideoPlay.visibility = View.GONE
        }
        ivItemVideoPlay.setOnClickListener {
            videoview_item.start()
        }
        videoview_item.setOnPlayStateListener(object : CustomVideoView.OnPlayStateListener {
            override fun onPlay() {
                ivItemVideoPlay.visibility = View.GONE
            }

            override fun onPause() {
                ivItemVideoPlay.visibility = View.VISIBLE
            }

        })
        videoview_item.setOnCompletionListener {
            ivItemVideoPlay.visibility = View.VISIBLE
        }
        videoview_item.setOnPreparedListener {
            it.setOnBufferingUpdateListener { _, percent ->
                if(percent == 100){
                    loading.visibility = View.GONE
                } else {
                    loading.visibility = View.VISIBLE
                }
            }
        }

        if (!TextUtils.isEmpty(microblog?.originMicroblog?.videoPath)) {
            rlItemChildVideoPlay.visibility = View.VISIBLE
            var path : String ?= null
            if (microblog?.originMicroblog?.videoPath!!.startsWith("http")) {
                path = microblog?.originMicroblog?.videoPath
            } else {
                path = Api.IMAGE_URL + microblog?.originMicroblog?.videoPath
            }
            videoviewItemChild.setVideoPath(path)
            resetVideoWidth(videoviewItemChild, path)
        } else {
            rlItemChildVideoPlay.visibility = View.GONE
        }
        ivItemChildVideoPlay.setOnClickListener {
            videoviewItemChild.start()
        }
        videoviewItemChild.setOnPlayStateListener(object : CustomVideoView.OnPlayStateListener {
            override fun onPlay() {
                ivItemChildVideoPlay.visibility = View.GONE
            }

            override fun onPause() {
                ivItemChildVideoPlay.visibility = View.VISIBLE
            }

        })
        videoviewItemChild.setOnCompletionListener {
            ivItemChildVideoPlay.visibility = View.VISIBLE
        }
        videoviewItemChild.setOnPreparedListener {
            it.setOnBufferingUpdateListener { _, percent ->
                if(percent == 100){
                    loadingChild.visibility = View.GONE
                } else {
                    loadingChild.visibility = View.VISIBLE
                }
            }
        }
        setListener()
        setDatas()
    }

    private fun setListener() {

        rlCommentTab.setOnClickListener { selectFragmentIndex(0) }
        rlForwardTab.setOnClickListener { selectFragmentIndex(1) }
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
    }

    private fun requestPraise(open : Int) {

        val query = SubmitRequestQuery("PraiseSwitch")
        query.action = AIIAction.THREE
        query.id = postId
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this){
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

        val query = SubmitRequestQuery("CommentSubmit")
        query.action = AIIAction.THREE
        query.id = postId
        query.content = content
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this){
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
                rlCommentTab.isSelected = true
                rlForwardTab.isSelected = false
                rlAppraiseTab.isSelected = false
            }
            1->{
                rlCommentTab.isSelected = false
                rlForwardTab.isSelected = true
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

        if(item.isFocus == 1){
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
//     * 请求微博
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

    private fun resetVideoWidth(videoView: CustomVideoView, path : String?) {
        App.app.cachedThreadPool.execute {

            LogUtil.e("path:$path")
            var videoHeight = 0
            var videoWidth = 0
            try {
                val retr = MediaMetadataRetriever()
                retr.setDataSource(path, HashMap<String, String>())
                val videoHeightStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                val videoWidthStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                videoHeight = Integer.parseInt(videoHeightStr) // 视频高度
                videoWidth = Integer.parseInt(videoWidthStr) // 视频宽度
            } catch (e : Exception){
                e.printStackTrace()
            }
            var mVideoWidth = 0
            var mVideoHeight = 0
            runOnUiThread {
                if (videoWidth > 0 && videoHeight > 0) {
                    val videoScale = videoWidth.toFloat() / videoHeight
                    LogUtil.e("videoWidth:" + videoWidth + "   videoHeight:" + videoHeight + "    videoScale:$videoScale")
                    val parentView = videoView.parent as View
                    if (videoScale != 0f) {
                        if (videoWidth > videoHeight) {

                            mVideoHeight = parentView.measuredHeight
                            mVideoWidth = (mVideoHeight / videoScale).toInt()
                            val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                            parentView.layoutParams = layoutParams

//                            mVideoWidth = videoView.measuredWidth
//                            mVideoHeight = ((mVideoWidth / videoScale).toInt())
                        } else {

                            mVideoHeight = parentView.measuredHeight
                            mVideoWidth = (mVideoHeight * videoScale).toInt()
                            val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                            parentView.layoutParams = layoutParams

//                            mVideoHeight = videoView.measuredHeight
//                            mVideoWidth = ((mVideoHeight * videoScale).toInt())
                        }
                    }
                    // 设置surfaceview画布大小
                    videoView.holder.setFixedSize(mVideoWidth, mVideoHeight)
                    // 重绘VideoView大小，这个方法是在重写VideoView时对外抛出方法
                    videoView.setMeasure(mVideoWidth, mVideoHeight)
                    videoView.requestLayout()
                }
            }
        }
    }
}
