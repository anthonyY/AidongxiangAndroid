package com.aidongxiang.app.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aiitec.openapi.utils.ScreenUtils

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class PostImgAdapter(context: Context, datas: MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){


    override fun convert(h: CommonRecyclerViewHolder?, item: String?, position: Int) {
        val ivImg = h?.getView<ImageView>(R.id.iv_item_img)

        var width = ViewGroup.LayoutParams.WRAP_CONTENT
        var height = 0
        val padding = ScreenUtils.dip2px(context, 8f)
        var paddingRight = 0
        var paddingTop = 0
        if(itemCount == 1){
            ivImg?.scaleType  = ImageView.ScaleType.FIT_START
//            width =  ViewGroup.LayoutParams.WRAP_CONTENT
//            height =  ViewGroup.LayoutParams.WRAP_CONTENT
            height = ScreenUtils.dip2px(context, 200f)
            width  = ScreenUtils.getScreenWidth(context)
            ivImg?.maxHeight = ScreenUtils.dip2px(context, 200f)
            ivImg?.maxWidth = ScreenUtils.getScreenWidth(context)

        } else {
            width = (ScreenUtils.getScreenWidth(context)-ScreenUtils.dip2px(context, 30f))/3
            if(position  %3 != 2){
                paddingRight = padding
            }
            ivImg?.scaleType  = ImageView.ScaleType.CENTER_CROP
            if(position > 2){
                paddingTop = padding
            }
            height = width
        }

        h?.itemView?.setPadding(0, paddingTop, paddingRight, 0)
        ivImg?.layoutParams?.width = width
        ivImg?.layoutParams?.height = height
        GlideImgManager.load(context, item, ivImg)
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_post_img

}