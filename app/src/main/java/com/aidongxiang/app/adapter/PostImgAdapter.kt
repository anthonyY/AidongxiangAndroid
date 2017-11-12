package com.aidongxiang.app.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import com.aidongxiang.app.R
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.utils.GlideImgManager
import com.aiitec.openapi.utils.ScreenUtils
import java.util.*

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class PostImgAdapter(context: Context, datas: MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){


    override fun convert(h: CommonRecyclerViewHolder?, item: String?, position: Int) {
        val iv_item_img = h?.getView<ImageView>(R.id.iv_item_img)

        var width = ViewGroup.LayoutParams.WRAP_CONTENT
        val padding = ScreenUtils.dip2px(context, 8f)
        var paddingRight = 0
        var paddingTop = 0
        if(itemCount == 1){
            iv_item_img?.scaleType  = ImageView.ScaleType.FIT_START
        } else if(itemCount == 2 || itemCount == 4){

            width = (ScreenUtils.getScreenWidth(context)-ScreenUtils.dip2px(context, 38f))/2
            if(position  %3 != 1){
                paddingRight = padding

            }
            iv_item_img?.scaleType  = ImageView.ScaleType.CENTER_CROP
        } else {
            width = (ScreenUtils.getScreenWidth(context)-ScreenUtils.dip2px(context, 46f))/3
            if(position  %3 != 2){
                paddingRight = padding
            }
            iv_item_img?.scaleType  = ImageView.ScaleType.CENTER_CROP
        }
        if(position > 2){
            paddingTop = padding
        }
        h?.itemView?.setPadding(0, paddingTop, paddingRight, 0)
        iv_item_img?.layoutParams?.width = width
        iv_item_img?.layoutParams?.height = width

        GlideImgManager.load(context, item, iv_item_img)
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_post_img

}