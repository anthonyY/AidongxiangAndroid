package com.aidongxiang.app.adapter

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.business.model.User

/**
 * 微博赞 adapter
 * @author Anthony
 * createTime 2017/12/7.
 * @version 1.0
 */
class PostAppraiseAdapter(context: Context, datas: MutableList<User>) : CommonRecyclerViewAdapter<User>(context, datas){
    override fun convert(h: CommonRecyclerViewHolder, item: User, position: Int) {

        val tvName = h.getView<TextView>(R.id.tv_item_name)
        val ivItemAvatar = h.getView<ImageView>(R.id.ivItemAvatar)

        val name = item.name
        if(TextUtils.isEmpty(name)){
            tvName.text = ""
        } else {
            tvName.text = name
        }
        GlideImgManager.load(context, item.imagePath, R.drawable.ic_avatar_default, ivItemAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)

    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_post_appraise

}