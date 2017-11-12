package com.aidongxiang.app.adapter

import android.content.Context
import android.widget.ImageView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class HomeNewsAdapter(context: Context, datas:MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){
    override fun convert(h: CommonRecyclerViewHolder?, item: String?, position: Int) {

        val iv_item_img = h?.getView<ImageView>(R.id.iv_item_img)
        GlideImgManager.load(context, item,  iv_item_img)
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_home_news
}