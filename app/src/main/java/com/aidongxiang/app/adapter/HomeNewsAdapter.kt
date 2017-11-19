package com.aidongxiang.app.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class HomeNewsAdapter(context: Context, datas:MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){
    var marginLeft = 0
    init {
        marginLeft = context.resources.getDimension(R.dimen.margin_screen_left).toInt()
    }
    override fun convert(h: CommonRecyclerViewHolder?, item: String?, position: Int) {

        val iv_item_img = h?.getView<ImageView>(R.id.iv_item_img)
        val lineItem = h?.getView<View>(R.id.lineItem)

        if(position == itemCount-1){
            (lineItem?.layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, 0)
        } else {
            (lineItem?.layoutParams as LinearLayout.LayoutParams).setMargins(marginLeft, 0, 0, 0)
        }
        GlideImgManager.load(context, item,  iv_item_img)

    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_home_news
}