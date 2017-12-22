package com.aidongxiang.app.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.business.model.Comment
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.utils.DateUtil

/**
 *
 * @author Anthony
 * createTime 2017/12/7.
 * @version 1.0
 */
class CommentAdapter(context: Context, datas: MutableList<Comment>) : CommonRecyclerViewAdapter<Comment>(context, datas){
    override fun convert(h: CommonRecyclerViewHolder, item: Comment, position: Int) {

        val tvContent = h.getView<TextView>(R.id.tv_item_content)
        val tvName = h.getView<TextView>(R.id.tv_item_name)
        val tvTime = h.getView<TextView>(R.id.tv_item_time)
        val tvItemPraise = h.getView<TextView>(R.id.tvItemPraise)
        val ivItemAvatar = h.getView<ImageView>(R.id.ivItemAvatar)
        val ivItemPraise = h.getView<ImageView>(R.id.ivItemPraise)
        tvContent.text = item.content

        if(item.user != null && item.user!!.id == AIIConstant.USER_ID){
            ivItemPraise.setImageResource(R.drawable.common_btn_like_pre)
            tvItemPraise.setTextColor(ContextCompat.getColor(context, R.color.purple))
        } else {
            ivItemPraise.setImageResource(R.drawable.common_btn_like_nor)
            tvItemPraise.setTextColor(ContextCompat.getColor(context, R.color.gray7))
        }

        if(item.praiseNum >= 1000){
            tvItemPraise.text = "${item.praiseNum/10000}万"
        } else {
            tvItemPraise.text = item.praiseNum.toString()
        }
        item.user?.name?.let{ tvName.text = it }

        tvTime.text = DateUtil.formatStr(item.timestamp, "yyyy-MM-dd HH:mm:ss", "MM-dd HH:mm")
        GlideImgManager.load(context, item.user?.imagePath, ivItemAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)

    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_comment

}