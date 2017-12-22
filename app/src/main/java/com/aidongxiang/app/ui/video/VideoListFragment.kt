package com.aidongxiang.app.ui.video

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeVideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.business.model.Video
import com.aiitec.moreschool.base.BaseListKtFragment
import kotlinx.android.synthetic.main.fragment_list_with_edit.*
import java.util.*

/**
 * 视频列表Fragment
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list_with_edit)
class VideoListFragment : BaseListKtFragment(){

    val datas = ArrayList<Video>()
    lateinit var adapter : HomeVideoAdapter
    override fun getDatas(): List<*>? = datas
    val random = Random()
    var isEdit = false

    override fun requestData() {
    }

    override fun init(view: View) {
        super.init(view)
        adapter = HomeVideoAdapter(activity, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        adapter.setOnRecyclerViewItemClickListener { v, position ->
            switchToActivity(VideoDetailsActivity::class.java)
        }
        tv_select_all.setOnClickListener {
            for(data in datas){
                data.isSelected = true
            }
            adapter.update()
        }
        tv_delete.setOnClickListener {
            datas.filter { it.isSelected }.forEach { datas.remove(it) }
            adapter.update()
        }
        for (i in 0..10){
            val video = Video()
            video.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
            video.audioLength = "12:10"
            video.name = "精彩斗牛啦啦啦啦"
            video.timestamp = "07-12"
            video.playNum = 321
            datas.add(video)
        }
        adapter.update()

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
}