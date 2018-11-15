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
class HomeAudioAdapter(context: Context, datas:MutableList<Video>) : CommonRecyclerViewAdapter<Video>(context, datas){
    var isEdit = false
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    fun setIsEdit(isEdit : Boolean){
        this.isEdit = isEdit
        notifyDataSetChanged()
    }
    override fun convert(h: CommonRecyclerViewHolder, item: Video, position: Int) {

        val ivAudio = h.getView<ImageView?>(R.id.iv_item_play)
        val tvTitle = h.getView<TextView?>(R.id.tv_item_title)
        val tvDuration = h.getView<TextView?>(R.id.tv_item_duration)
        val tvPlayNum = h.getView<TextView?>(R.id.tv_item_play_num)
        val tvTime = h.getView<TextView?>(R.id.tv_item_time)
        ivAudio?.setImageResource(R.drawable.common_icon_music)
        val ivImg = h.getView<ImageView?>(R.id.iv_item_img)
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

        val ivItemSelect = h.getView<ImageView?>(R.id.ivItemSelect)
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

        if(item.playNum > 10000){
            tvPlayNum?.text = AiiUtil.formatString((item.playNum/10000.0))
        } else {
            tvPlayNum?.text = item.playNum.toString()
        }
        GlideImgManager.load(context, item.imagePath, ivImg)
        val lineItem = h.getView<View>(R.id.lineItem)
        if(position == itemCount-1){
            lineItem?.visibility = View.GONE
        } else {
            lineItem?.visibility = View.VISIBLE
        }
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_home_video
}