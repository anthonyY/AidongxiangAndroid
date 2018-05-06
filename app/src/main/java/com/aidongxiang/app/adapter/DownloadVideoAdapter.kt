package com.aidongxiang.app.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aiitec.openapi.model.Download
import com.aiitec.openapi.net.download.DownloadManager
import com.aiitec.openapi.utils.CacheUtils

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class DownloadVideoAdapter(context: Context, datas:MutableList<Download>) : CommonRecyclerViewAdapter<Download>(context, datas){

    var downloadManager = DownloadManager.getInstance(context)
    var isEdit = false
    override fun convert(h: CommonRecyclerViewHolder?, item: Download?, position: Int) {
        val ivAudio = h?.getView<ImageView?>(R.id.iv_item_play)
        val ivImg = h?.getView<ImageView?>(R.id.iv_item_img)
        val tvTitle = h?.getView<TextView?>(R.id.tv_item_title)
        val tvDuration = h?.getView<TextView?>(R.id.tv_item_duration)
        val ivItemSelect = h?.getView<ImageView?>(R.id.ivItemSelect)
        val progressBar = h?.getView<ProgressBar?>(R.id.pb_item_download_progress)
        val tvSpeed = h?.getView<TextView?>(R.id.tv_item_speed)
        val tvSize = h?.getView<TextView?>(R.id.tv_item_size)
        if(isEdit){
            ivItemSelect?.visibility = View.VISIBLE
            if(item!!.isSelect){
                ivItemSelect?.setImageResource(R.drawable.common_btn_select_pre)
            } else {
                ivItemSelect?.setImageResource(R.drawable.common_btn_select_nor)
            }
        } else {
            ivItemSelect?.visibility = View.GONE
        }
        if(item!!.type == 2){
            ivAudio?.setImageResource(R.drawable.common_icon_play)
        } else {
            ivAudio?.setImageResource(R.drawable.common_icon_music)
        }

        GlideImgManager.load(context, item.imagePath, ivImg)
        tvTitle?.text = item.title
        tvDuration?.text = item.playLength
        tvSize?.text = CacheUtils.getFormatSize(item.totalBytes.toDouble())

        val lineItem = h?.getView<View>(R.id.lineItem)
        if(position == itemCount-1){
            lineItem?.visibility = View.GONE
        } else {
            lineItem?.visibility = View.VISIBLE
        }
        progressBar?.progress = item.percentage
        if(item.isDownloadFinish){
            tvSpeed?.visibility = View.GONE
            tvDuration?.visibility = View.VISIBLE
            progressBar?.visibility = View.GONE
        } //既没有暂停也没有下载，就当是等待状态
        else {
            tvSpeed?.visibility = View.VISIBLE
            tvDuration?.visibility = View.GONE
            progressBar?.visibility = View.VISIBLE
            if (item.breakPoint <= 0) {
                tvSpeed?.text = "等待中"
                progressBar?.progress = 0
            } else if (downloadManager.isDownloading(item.id.toInt())) {
                if (!TextUtils.isEmpty(item.speed)) {
                    tvSpeed?.text = item.speed
                } else {
                    tvSpeed?.text = "下载中"
                }
            } else {
                tvSpeed?.text = "已暂停"
            }
        }

    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_download_video

    fun setIsEdit(isEdit : Boolean){
        this.isEdit = isEdit
        notifyDataSetChanged()
    }
}