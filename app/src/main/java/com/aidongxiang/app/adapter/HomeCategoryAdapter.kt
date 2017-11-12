package com.aidongxiang.app.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aiitec.openapi.utils.ScreenUtils

/**
 *
 * @author Anthony
 * createTime 2017/11/11.
 * @version 1.0
 */
class HomeCategoryAdapter(context: Context, datas : MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){
    override fun convert(h: CommonRecyclerViewHolder, item: String, position: Int) {
        val ivImage = h.getView<ImageView>(R.id.iv_item_img)
        val tvName = h.getView<TextView>(R.id.tv_item_name)
//        var width = ViewGroup.LayoutParams.WRAP_CONTENT
        val padding = ScreenUtils.dip2px(context, 8f)
        var paddingRight = 0
        var paddingTop = 0
        if(itemCount == 1){
            ivImage?.scaleType  = ImageView.ScaleType.FIT_START
        } else {
//            width = (ScreenUtils.getScreenWidth(context) - ScreenUtils.dip2px(context, 46f))/4
            if(position  %4 != 3){
                paddingRight = padding
            }
            ivImage?.scaleType  = ImageView.ScaleType.CENTER_CROP
        }
//        if(position > 3){
            paddingTop = padding
//        }

        h.itemView?.setPadding(0, paddingTop, paddingRight, 0)
        tvName.text = "分类$position"
//        ivImage?.layoutParams?.width = width
//        ivImage?.layoutParams?.height = width

        GlideImgManager.load(context, item, ivImage)
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_home_category

}