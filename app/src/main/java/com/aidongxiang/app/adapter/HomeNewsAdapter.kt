package com.aidongxiang.app.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.business.model.Article
import com.aiitec.openapi.utils.DateUtil

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class HomeNewsAdapter(context: Context, datas:MutableList<Article>) : CommonRecyclerViewAdapter<Article>(context, datas){
    var marginLeft = 0
    init {
        marginLeft = context.resources.getDimension(R.dimen.margin_screen_left).toInt()
    }
    override fun convert(h: CommonRecyclerViewHolder, item: Article, position: Int) {

        val ivImg = h.getView<ImageView>(R.id.iv_item_img)
        val tvContent = h.getView<TextView>(R.id.iv_item_content)
        val tvTime = h.getView<TextView>(R.id.iv_item_time)
        val tvCategory = h.getView<TextView>(R.id.tv_item_category)
        val lineItem = h.getView<View>(R.id.lineItem)

        if(position == itemCount-1){
            (lineItem?.layoutParams as LinearLayout.LayoutParams).setMargins(0, 0, 0, 0)
        } else {
            (lineItem?.layoutParams as LinearLayout.LayoutParams).setMargins(marginLeft, 0, 0, 0)
        }
        GlideImgManager.load(context, item.imagePath,  ivImg)
        tvContent.text = item.title
        tvTime.text = DateUtil.formatStr(item.timestamp, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd")
        tvCategory.text = ""

    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_home_news
}