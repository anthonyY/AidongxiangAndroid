package com.aidongxiang.app.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.R.id.tvItemPraise
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.ui.square.BigImageActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.business.model.Image
import com.aidongxiang.business.model.Microblog
import java.util.*
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
        val ivItemOneImage = h.getView<ImageView>(R.id.ivItemOneImage)
        val tvItemAttention = h.getView<TextView>(R.id.tvItemAttention)
        val tvItemPraiseNum = h.getView<TextView>(R.id.tvItemPraiseNum)
        val tvItemCommentNum = h.getView<TextView>(R.id.tvItemCommentNum)
        val tvItemForwardNum = h.getView<TextView>(R.id.tvItemForwardNum)
        val tvItemName = h.getView<TextView>(R.id.tvItemName)
        val tvItemTime = h.getView<TextView>(R.id.tvItemTime)
        val ivItemAvatar = h.getView<ImageView>(R.id.ivItemAvatar)
        val ivItemPraiseNum = h.getView<ImageView>(R.id.ivItemPraiseNum)
        val tvItemContent = h.getView<TextView>(R.id.tvItemContent)

        if(item.isFocus == 1){
            tvItemAttention.visibility = View.GONE
        } else {
            tvItemAttention.visibility = View.VISIBLE
        }
        tvItemPraiseNum.text = item.praiseNum.toString()
        tvItemCommentNum.text = item.commentNum.toString()
        tvItemForwardNum.text = item.repeatNum.toString()
        var name = ""
        val timestamp = item.timestamp
        tvItemTime.text = timestamp
        var avatar = ""
        item.user?.let {
            it.name?.let {  name = it }
            it.imagePath?.let { avatar = it }
        }
        GlideImgManager.load(context, avatar, ivItemAvatar)

        var datas = ArrayList<String>()

        item.images?.let { datas = it }
        tvItemContent.text = item.content
        val spanCount = if (datas.size == 1) { 1 } else { 3 }
        ivItemOneImage?.visibility = View.GONE
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


    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_post

}