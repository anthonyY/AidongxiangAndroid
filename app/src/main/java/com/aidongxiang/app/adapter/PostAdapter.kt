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
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.ui.mine.PersonCenterActivity
import com.aidongxiang.app.ui.square.BigImageActivity
import com.aidongxiang.app.utils.GlideImgManager
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
class PostAdapter(context: Context, datas: MutableList<Microblog>) : CommonRecyclerViewAdapter<Microblog>(context, datas) {
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

        if(item.isFocus == 2){
            tvItemFocus.visibility = View.GONE
        } else {
            tvItemFocus.visibility = View.VISIBLE
        }
        tvItemPraiseNum.text = item.praiseNum.toString()
        tvItemCommentNum.text = item.commentNum.toString()
        tvItemForwardNum.text = item.repeatNum.toString()
        var nickName = ""
        val timestamp = item.timestamp
        tvItemTime.text = timestamp
        var avatar = ""
        item.user?.let {
            it.nickName?.let {  nickName = it }
            it.imagePath?.let { avatar = it }
        }
        GlideImgManager.load(context, avatar, R.drawable.ic_avatar_default, ivItemAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)

        tvItemName.text = nickName

        if(item.isPraise == 2){
            ivItemPraiseNum.setImageResource(R.drawable.common_btn_like_pre)
        } else {
            ivItemPraiseNum.setImageResource(R.drawable.common_btn_like_nor)
        }
        tvItemContent.text = item.content
        var datas = ArrayList<String>()

        item.images?.let { datas = it }
        if(datas.size > 0){
            val spanCount = if (datas.size == 1) { 1 } else { 3 }
            recyclerView?.visibility = View.VISIBLE
            recyclerView?.layoutManager = GridLayoutManager(context, spanCount)
            val adapter = PostImgAdapter(context, datas)
            adapter.setOnRecyclerViewItemClickListener { _, position ->
                val intent = Intent(context, BigImageActivity::class.java)

                val bundle = Bundle()
                bundle.putStringArrayList(BigImageActivity.ARG_IMAGES, datas)
                bundle.putInt(BigImageActivity.ARG_POSITION, position)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
            recyclerView?.adapter = adapter
        } else {
            recyclerView?.visibility = View.GONE
        }

        if(!TextUtils.isEmpty(item.address)){
            llItemAddress.visibility = View.VISIBLE
            tvItemAddress.text = item.address
        } else {
            llItemAddress.visibility = View.GONE
        }


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
                val clickSpan = Clickable(context, userId!!)
                spanableInfo.setSpan(clickSpan, m.start(), m.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            tvItemChildContent?.text = spanableInfo
            val forwardSpanCount = if (forwardImages.size == 1) { 1 } else { 3 }
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

        } else {
            includeItemForward.visibility = View.GONE
        }
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_post

    internal inner class Clickable(val context: Context, val userId : Long ) : ClickableSpan() {

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
}