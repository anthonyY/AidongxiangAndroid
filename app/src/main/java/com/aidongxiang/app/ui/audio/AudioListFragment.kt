package com.aidongxiang.app.ui.audio

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeAudioAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.widgets.AdvertisementLayout
import com.aidongxiang.business.model.Ad
import com.aidongxiang.business.model.Video
import com.aidongxiang.business.request.AdListRquestQuery
import com.aidongxiang.business.response.AdListResponseQuery
import com.aidongxiang.business.response.VideoListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.fragment_list_with_edit.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * 视频列表Fragment
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list_with_edit)
class AudioListFragment : BaseListKtFragment(){

    val datas = ArrayList<Video>()
    lateinit var adapter : HomeAudioAdapter
    override fun getDatas(): List<*>? = datas
    val random = Random()
    var isEdit = false
    var headerView : AdvertisementLayout ?= null
    var type = TYPE_HONE

    companion object {
        val ARG_TYPE = "type"
        /**主页 带广告*/
        val TYPE_HONE = 1
        /**下载 不带广告*/
        val TYPE_DOWNLOAD = 4
        /**下载 不带广告*/
        val TYPE_COLLECT = 2
        /**观看历史 不带广告*/
        val TYPE_HISTORY = 3
        fun newInstance(type : Int) : AudioListFragment {
            val fragment = AudioListFragment()
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
        adapter = HomeAudioAdapter(context!!, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter

        arguments?.let {
            type = it.getInt(ARG_TYPE)
        }
        if(type == TYPE_HONE){
            headerView = LayoutInflater.from(activity).inflate(R.layout.advertisement, null, false) as AdvertisementLayout
            adapter.addHeaderView(headerView)
            setHeaderData()
        }
        adapter.setOnRecyclerViewItemClickListener { v, position ->
            switchToActivity(AudioDetailsActivity::class.java)
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
            video.name = "丢就不见长相思"
            video.timestamp = "07-12"
            video.playNum = 321
            datas.add(video)
        }
        adapter.update()

    }

    fun setHeaderData(){
        val ads = ArrayList<Ad>()
        for(i in 0..6){
            val ad = Ad()
            ad.name = "广告"
            ad.link = "http://www.baidu.com"
            ad.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
            ads.add(ad)
        }
        headerView?.startAD(ads.size, 3, true, ads)

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
    fun requestAdList(){
        val query = AdListRquestQuery()
        query.action = AIIAction.THREE

        App.aiiRequest?.send(query, object : AIIResponse<AdListResponseQuery>(activity, false){
            override fun onSuccess(response: AdListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.ads?.let {
                    headerView?.startAD(it.size, 3, true, it, 0.48f)
                }
            }
        })
    }

    fun requestAudioList(){
        if(type == 4){
            //下载的读数据库
            return
        }
        val query = ListRequestQuery("AudioList")
        query.table.page = page
        query.action = AIIAction.valueOf(type)
        App.aiiRequest?.send(query, object : AIIResponse<VideoListResponseQuery>(activity){
            override fun onSuccess(response: VideoListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let {
                    getVideoList(it)
                }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }

            override fun onCache(response: VideoListResponseQuery?, index: Int) {
                super.onCache(response, index)
                response?.let {
                    getVideoList(it)
                }
            }
        })
    }

    /**
     * 获取视频/音频数据，设置到adapter里
     */
    private fun getVideoList(response: VideoListResponseQuery) {
        total = response.total
        if(page == 1){
            datas.clear()
        }
        if(response.videos != null){
            datas.addAll(response.videos!!)
        }
        adapter.update()
        if(datas.size == 0){
            onNoData()
        }
    }
}