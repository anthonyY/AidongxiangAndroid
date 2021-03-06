package com.aidongxiang.app.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.business.model.Video
import com.aiitec.openapi.utils.AiiUtil
import com.aiitec.openapi.utils.DateUtil
import java.util.*

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class HomeVideoAdapter(context: Context, datas:MutableList<Video>) : CommonRecyclerViewAdapter<Video>(context, datas){

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    var isEdit = false
    override fun convert(h: CommonRecyclerViewHolder, item: Video, position: Int) {
        val ivAudio = h.getView<ImageView?>(R.id.iv_item_play)
        val ivImg = h.getView<ImageView?>(R.id.iv_item_img)
        val tvTitle = h.getView<TextView?>(R.id.tv_item_title)
        val tvDuration = h.getView<TextView?>(R.id.tv_item_duration)
        val tvTime = h.getView<TextView?>(R.id.tv_item_time)
        val ivItemSelect = h.getView<ImageView?>(R.id.ivItemSelect)
        val tvPlayNum = h.getView<TextView?>(R.id.tv_item_play_num)
        if(isEdit){
            ivItemSelect?.visibility = View.VISIBLE
            if(item.isSelected){
                ivItemSelect?.setImageResource(R.drawable.common_btn_select_pre)
            } else {
                ivItemSelect?.setImageResource(R.drawable.common_btn_select_nor)
            }
        } else {
            ivItemSelect?.visibility = View.GONE
        }
        if(item.audioType == 2){
            ivAudio?.setImageResource(R.drawable.common_icon_music)
        } else {
            ivAudio?.setImageResource(R.drawable.common_icon_play)
        }

        GlideImgManager.load(context, item.imagePath, ivImg)
        tvTitle?.text = item.name
        var audioLength = ""
        item.audioLength?.let {
            if(it.length == 8 && it.startsWith("00")){
                audioLength = it.substring(3)
            } else {
                audioLength = it
            }
        }

        tvDuration?.text = audioLength

        var outFormat = "yyyy-MM-dd"
        //这样就可以表示同一年了，没有必要去转换成Calendar再比较
        if(item.timestamp!!.startsWith(currentYear.toString())){
            outFormat = "MM-dd"
        }
        tvTime?.text = DateUtil.formatStr(item.timestamp, "yyyy-MM-dd HH:mm:ss", outFormat)
        if(item.playNum > 10000){
            tvPlayNum?.text = AiiUtil.formatString((item.playNum/10000.0))
        } else {
            tvPlayNum?.text = item.playNum.toString()
        }
        val lineItem = h.getView<View>(R.id.lineItem)
        if(position == itemCount-1){
            lineItem?.visibility = View.GONE
        } else {
            lineItem?.visibility = View.VISIBLE
        }
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_home_video

    fun setIsEdit(isEdit : Boolean){
        this.isEdit = isEdit
        notifyDataSetChanged()
    }
}