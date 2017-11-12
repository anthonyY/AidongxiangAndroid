package com.aidongxiang.app.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import com.aidongxiang.app.R
import com.aidongxiang.app.model.Image
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
        var width = ViewGroup.LayoutParams.WRAP_CONTENT
        val padding = ScreenUtils.dip2px(context, 8f)
        var paddingRight = 0
        var paddingTop = 0
        if(itemCount == 1){
            ivImage?.scaleType  = ImageView.ScaleType.FIT_START
        } else {
            width = (ScreenUtils.getScreenWidth(context)- ScreenUtils.dip2px(context, 46f))/4
            if(position  %3 != 2){
                paddingRight = padding
            }
            ivImage?.scaleType  = ImageView.ScaleType.CENTER_CROP
        }
        if(position > 2){
            paddingTop = padding
        }

        h?.itemView?.setPadding(0, paddingTop, paddingRight, 0)
        ivImage?.layoutParams?.width = width
        ivImage?.layoutParams?.height = width

        GlideImgManager.loadFile(context, item, ivImage)
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_post_img

}