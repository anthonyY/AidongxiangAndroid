package com.aidongxiang.app.ui.video

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeVideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseListKtActivity
import com.aidongxiang.app.ui.home.HomeFragment
import com.aiitec.moreschool.base.BaseListKtFragment
import java.util.*

/**
 * 视频列表Fragment
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list)
class VideoListFragment : BaseListKtFragment(){

    val datas = ArrayList<String>()
    var adapter : HomeVideoAdapter ?= null
    override fun getDatas(): List<*>? = datas
    val random = Random()
    override fun requestData() {
    }

    override fun init(view: View) {
        super.init(view)
        adapter = HomeVideoAdapter(context!!, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter

        for (i in 0..10){
            datas.add(HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)])
        }
        adapter?.update()

    }


}