package com.aidongxiang.app.adapter

import android.content.Context
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.model.Video
import com.aidongxiang.app.utils.GlideImgManager
import com.aiitec.openapi.utils.ScreenUtils

/**
 *
 * @author Anthony
 * createTime 2017/11/11.
 * @version 1.0
 */
class VideoAdapter(context : Context, datas:MutableList<Video>) : CommonRecyclerViewAdapter<Video>(context, datas){

    var width = 0
    init {
        width = ScreenUtils.getScreenWidth(context) / 3 - ScreenUtils.dip2px(context, 9f)
    }
    override fun convert(h: CommonRecyclerViewHolder, item: Video, position: Int) {
        val params = FrameLayout.LayoutParams(width, width)
        h.itemView.layoutParams = params

        val ivImage = h.getView<ImageView>(R.id.iv_item_img)
        ivImage.layoutParams = params
        val tvDuration = h.getView<TextView>(R.id.tv_item_duration)
        GlideImgManager.loadFile(context, item.path, ivImage)
        val hour = item.duration/1000/60/60
        val minute = item.duration/1000/60%60
        val second = item.duration/1000%60
        val hourStr = formatNumber(hour.toInt())
        val minuteStr = formatNumber(minute.toInt())
        val secondStr = formatNumber(second.toInt())
        if(item.duration > 60*60*1000){
            tvDuration.text = "$hourStr:$minuteStr:$secondStr"
        } else {
            tvDuration.text ="$minuteStr:$secondStr"
        }

    }

    fun formatNumber(value : Int) : String{
        if(value < 10){
            return "0$value"
        } else {
            return value.toString()
        }
    }

    override fun getLayoutViewId(viewType: Int): Int  = R.layout.item_video

}