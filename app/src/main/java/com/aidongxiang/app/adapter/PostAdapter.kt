package com.aidongxiang.app.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.ui.mine.PersonCenterActivity
import com.aidongxiang.app.ui.square.BigImageActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.Utils.setMicoblogVideoInfo
import com.aidongxiang.business.model.Microblog
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class PostAdapter(context: Context, datas: MutableList<Microblog>, val type : Int = 1) : CommonRecyclerViewAdapter<Microblog>(context, datas) {
    val random = Random()

    override fun convert(h: CommonRecyclerViewHolder, item: Microblog, position: Int) {
        val recyclerView = h.getView<RecyclerView>(R.id.recycler_post_img)
//        val ivItemOneImage = h.getView<ImageView>(R.id.ivItemOneImage)
        val tvItemFocus = h.getView<TextView>(R.id.tvItemFocus)
        val tvItemPraiseNum = h.getView<TextView>(R.id.tvItemPraiseNum)
        val tvItemCommentNum = h.getView<TextView>(R.id.tvItemCommentNum)
        val tvItemForwardNum = h.getView<TextView>(R.id.tvItemForwardNum)
        val tvItemName = h.getView<TextView>(R.id.tvItemName)
        val tvItemTime = h.getView<TextView>(R.id.tvItemTime)
        val ivItemAvatar = h.getView<ImageView>(R.id.ivItemAvatar)
        val tvItemContent = h.getView<TextView>(R.id.tvItemContent)
        val llItemAddress = h.getView<LinearLayout>(R.id.ll_item_address)
        val tvItemAddress = h.getView<TextView>(R.id.tv_item_address)
        val ivItemPraiseNum = h.getView<ImageView>(R.id.ivItemPraiseNum)
        val tvItemChildContent = h.getView<TextView>(R.id.tvItemChildContent)
        val recyclerItemChildImg = h.getView<RecyclerView>(R.id.recyclerItemChildImg)
        val includeItemForward = h.getView<View>(R.id.includeItemForward)
//        val videoview = h.getView<CustomVideoView>(R.id.videoview_item)
//        val videoviewChild = h.getView<CustomVideoView>(R.id.videoviewItemChild)
        val rlItemVideoPlay = h.getView<RelativeLayout>(R.id.rlItemVideoPlay)
        val ivItemVideoPlay = h.getView<ImageView>(R.id.ivItemVideoPlay)
        val ivItemMore = h.getView<ImageView>(R.id.ivItemMore)

        val rlItemChildVideoPlay = h.getView<RelativeLayout>(R.id.rlItemChildVideoPlay)
        val ivItemChildVideoPlay = h.getView<ImageView>(R.id.ivItemChildVideoPlay)
//        val loadingChild = h.getView<View>(R.id.loadingChild)
//        val loading = h.getView<View>(R.id.loading)
        val ivVideoThumb = h.getView<ImageView>(R.id.ivVideoThumb)
        val ivVideoThumbChild = h.getView<ImageView>(R.id.ivVideoThumbChild)
        val marginView = h.getView<View>(R.id.view_margin)
        val llItemBtns = h.getView<View>(R.id.llItemBtns)
        if(type == 5){
            tvItemFocus.visibility = View.VISIBLE
            ivItemMore.visibility = View.GONE
            llItemBtns.visibility = View.GONE
            marginView.visibility = View.GONE
            tvItemFocus.text = "取消屏蔽"
        } else {
            ivItemMore.visibility = View.VISIBLE
            llItemBtns.visibility = View.VISIBLE
            marginView.visibility = View.VISIBLE
            if (item.isFocus == 2 || item.isFocus == 4) {
                tvItemFocus.visibility = View.GONE
            } else {
                if (Constants.user != null && Constants.user!!.id == item.user!!.id) {
                    tvItemFocus.visibility = View.GONE
                } else {
                    tvItemFocus.visibility = View.VISIBLE
                }
            }
        }

        tvItemPraiseNum.text = item.praiseNum.toString()
        tvItemCommentNum.text = item.commentNum.toString()
        tvItemForwardNum.text = item.repeatNum.toString()
        var nickName = ""
        val timestamp = item.timestamp
        tvItemTime.text = timestamp
        var avatar = ""
        item.user?.let {
            it.nickName?.let { nickName = it }
            it.imagePath?.let { avatar = it }
        }
        GlideImgManager.load(context, avatar, R.drawable.ic_avatar_default, ivItemAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)

        tvItemName.text = nickName

        if (item.isPraise == 2) {
            ivItemPraiseNum.setImageResource(R.drawable.common_btn_like_pre)
        } else {
            ivItemPraiseNum.setImageResource(R.drawable.common_btn_like_nor)
        }
        tvItemContent.text = item.content
        var datas = ArrayList<String>()

        item.images?.let { datas = it }
        if (datas.size > 0) {
            val spanCount = if (datas.size == 1) {
                1
            } else {
                3
            }
            recyclerView?.visibility = View.VISIBLE
            recyclerView?.layoutManager = GridLayoutManager(context, spanCount)
            val adapter = PostImgAdapter(context, datas)
            adapter.setOnRecyclerViewItemClickListener { _, i ->
                val intent = Intent(context, BigImageActivity::class.java)

                val bundle = Bundle()
                bundle.putStringArrayList(BigImageActivity.ARG_IMAGES, datas)
                bundle.putInt(BigImageActivity.ARG_POSITION, i)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
            recyclerView?.adapter = adapter
        } else {
            recyclerView?.visibility = View.GONE
        }

        if (!TextUtils.isEmpty(item.address)) {
            llItemAddress.visibility = View.VISIBLE
            tvItemAddress.text = item.address
        } else {
            llItemAddress.visibility = View.GONE
        }

        setMicoblogVideoInfo(context, item.videoPath, rlItemVideoPlay, ivVideoThumb)
        if (item.originMicroblog != null) {
            includeItemForward.visibility = View.VISIBLE
            var forwardImages = ArrayList<String>()

            item.originMicroblog!!.images?.let { forwardImages = it }
            val forwardUserName = item.originMicroblog?.user?.nickName
//

            val content = "@${forwardUserName}  ${item.originMicroblog!!.content}"
            val pattern = Pattern.compile("@.*?\\s{1}")
            val spanableInfo = SpannableString(content)
            val originUser = item.originMicroblog?.user
            var userId : Long = -1
            if(originUser != null){
                userId = originUser.id
            }

            val m = pattern.matcher(content)
            if (m.find()) {
                val clickSpan = Clickable(context, userId)
                spanableInfo.setSpan(clickSpan, m.start(), m.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            tvItemChildContent?.text = spanableInfo
            val forwardSpanCount = if (forwardImages.size == 1) {
                1
            } else {
                3
            }
            recyclerItemChildImg?.layoutManager = GridLayoutManager(context, forwardSpanCount)
            val forwardAdapter = PostImgAdapter(context, forwardImages)
            forwardAdapter.setOnRecyclerViewItemClickListener { _, i ->
                val intent = Intent(context, BigImageActivity::class.java)

                val bundle = Bundle()
                bundle.putStringArrayList(BigImageActivity.ARG_IMAGES, forwardImages)
                bundle.putInt(BigImageActivity.ARG_POSITION, i)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
            recyclerItemChildImg?.adapter = forwardAdapter

            setMicoblogVideoInfo(context, item.originMicroblog?.videoPath, rlItemChildVideoPlay, ivVideoThumbChild)

        } else {
            includeItemForward.visibility = View.GONE
        }
    }


    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_post

    internal inner class Clickable(val context: Context, val userId: Long) : ClickableSpan() {

        /**
         * 重写父类点击事件
         */
        override fun onClick(v: View) {
            (context as BaseKtActivity).switchToActivity(PersonCenterActivity::class.java, ARG_ID to userId)
        }

        /**
         * 重写父类updateDrawState方法  我们可以给TextView设置字体颜色,背景颜色等等...
         */
        override fun updateDrawState(ds: TextPaint) {
            ds.color = Color.BLUE
        }
    }

//
//    private fun resetVideoWidth(videoView: CustomVideoView, path : String?) {
//        App.app.cachedThreadPool.execute {
//
//            LogUtil.e("path:$path")
//            var videoHeight = 0
//            var videoWidth = 0
//            try {
//                val retr = MediaMetadataRetriever()
//                retr.setDataSource(path, HashMap<String, String>())
//                val videoHeightStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
//                val videoWidthStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
//                videoHeight = Integer.parseInt(videoHeightStr) // 视频高度
//                videoWidth = Integer.parseInt(videoWidthStr) // 视频宽度
//            } catch (e : Exception){
//                e.printStackTrace()
//            }
//            var mVideoWidth = 0
//            var mVideoHeight = 0
//            (context as Activity).runOnUiThread {
//                if (videoWidth > 0 && videoHeight > 0) {
//                    val videoScale = videoWidth.toFloat() / videoHeight
//                    val parentView = videoView.parent as View
//                    if (videoScale != 0f) {
//                        if (videoWidth > videoHeight) {
//
//                            mVideoHeight = parentView.measuredHeight
//                            mVideoWidth = (mVideoHeight / videoScale).toInt()
//                            val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
//                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
//                            parentView.layoutParams = layoutParams
//
////                            mVideoWidth = videoView.measuredWidth
////                            mVideoHeight = ((mVideoWidth / videoScale).toInt())
//                        } else {
//
//                            mVideoHeight = parentView.measuredHeight
//                            mVideoWidth = (mVideoHeight * videoScale).toInt()
//                            val layoutParams = RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight)
//                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
//                            parentView.layoutParams = layoutParams
//
////                            mVideoHeight = videoView.measuredHeight
////                            mVideoWidth = ((mVideoHeight * videoScale).toInt())
//                        }
//                    }
//                    // 设置surfaceview画布大小
//                    videoView.holder.setFixedSize(mVideoWidth, mVideoHeight)
//                    // 重绘VideoView大小，这个方法是在重写VideoView时对外抛出方法
//                    videoView.setMeasure(mVideoWidth, mVideoHeight)
//                    videoView.requestLayout()
//                }
//            }
//        }
//    }
}