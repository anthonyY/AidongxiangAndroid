package com.aidongxiang.app.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aiitec.openapi.utils.ScreenUtils

/**
 *
 * @author Anthony
 * createTime 2017/11/11.
 * @version 1.0
 */
class PublishImageAdapter(context: Context, datas : MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){
    override fun convert(h: CommonRecyclerViewHolder, item: String, position: Int) {
        val ivImage = h.getView<ImageView>(R.id.iv_item_img)
        val ivDelete = h.getView<ImageView>(R.id.iv_item_delete)
        val padding = ScreenUtils.dip2px(context, 8f)
        var paddingRight = 0
        var paddingTop = 0

        val width = (ScreenUtils.getScreenWidth(context)- ScreenUtils.dip2px(context, 46f))/3
        if(position  %3 != 2){
            paddingRight = padding
        }

        if(position > 2){
            paddingTop = padding
        }

        h.itemView?.setPadding(0, paddingTop, paddingRight, 0)
        h.itemView?.layoutParams?.width = width
        h.itemView?.layoutParams?.height = width

        if(position == itemCount-1){
            ivImage.setImageResource(R.drawable.common_btn_add)
            ivDelete.visibility = View.GONE
        } else {
            GlideImgManager.loadFile(context, item).centerCrop().into(ivImage)
            ivDelete.visibility = View.VISIBLE
        }

    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_publish_img

}