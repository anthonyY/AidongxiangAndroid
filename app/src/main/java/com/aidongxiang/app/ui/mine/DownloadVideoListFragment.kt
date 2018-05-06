package com.aidongxiang.app.ui.mine

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.DownloadVideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.App.Companion.aiidbManager
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.ui.video.VideoDetailsActivity
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.model.Download
import com.aiitec.openapi.net.download.DownloadManager
import kotlinx.android.synthetic.main.fragment_list_with_edit.*
import java.util.*

/**
 * 下载视频列表Fragment
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list_with_edit)
class DownloadVideoListFragment : BaseListKtFragment(){

    val datas = ArrayList<Download>()
    lateinit var adapter : DownloadVideoAdapter
    lateinit var downloadManager : DownloadManager
    override fun getDatas(): List<*>? = datas
    val random = Random()
    var isEdit = false
    var type = 2

    companion object {
        val ARG_TYPE = "type"
        fun newInstance(type : Int) : DownloadVideoListFragment{
            val fragment = DownloadVideoListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_TYPE, type)
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun requestData() {
    }

    override fun init(view: View) {
        super.init(view)
        adapter = DownloadVideoAdapter(activity, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        arguments?.let {
            type = it.getInt(ARG_TYPE)
        }

        adapter.setOnRecyclerViewItemClickListener { v, position ->
            if(position > 0){
                val id = datas[position-1].id
                switchToActivity(VideoDetailsActivity::class.java, ARG_ID to id)
            }
        }
        tv_select_all.setOnClickListener {
            for(data in datas){
                data.isSelect = true
            }
            adapter.update()
        }
        tv_delete.setOnClickListener {
            datas.filter { it.isSelect }.forEach { datas.remove(it) }
            adapter.update()
        }

        downloadManager = DownloadManager.getInstance(activity)



        loadData()
    }




    fun setIsEdit(isEdit : Boolean ){
        this.isEdit = isEdit
        adapter.setIsEdit(isEdit)
        if(isEdit){
            ll_bottom_btn.visibility = View.VISIBLE
        } else {
            ll_bottom_btn.visibility = View.GONE
        }
    }


    fun loadData() {
        App.app.cachedThreadPool.execute({
            val downloads = aiidbManager.findAll(Download::class.java, "type=?", arrayOf(type.toString()))
            activity?.runOnUiThread {
                datas.clear()
                if (downloads != null) {
                    datas.addAll(downloads)
                    adapter.update()
                }
                if (datas.size == 0) {
                    onNoData()
                }
            }
        })
    }


    fun update(intent: Intent) {
        val type = intent.getIntExtra(DownloadManager.ARG_TYPE, DownloadManager.ARG_TYPE_UPDATE)
        val current = intent.getLongExtra(DownloadManager.ARG_CURRENT, 0)
        val total = intent.getLongExtra(DownloadManager.ARG_TOTAL, 0)
        val id = intent.getLongExtra(DownloadManager.ARG_DOWNLOAD_ID, 0)
        val percentage = intent.getIntExtra(DownloadManager.ARG_PERCENTAGE, 0)
        val speed = intent.getStringExtra(DownloadManager.ARG_SPPED)
        val isdownloadFinish = type == DownloadManager.ARG_TYPE_FINISH

        datas.forEachIndexed { _, data ->
            if (data.id == id) {
                data.breakPoint = current
                data.isDownloadFinish = isdownloadFinish
                data.totalBytes = total
                data.percentage = percentage
                data.speed = speed
                adapter.update()
            }
        }
    }

}