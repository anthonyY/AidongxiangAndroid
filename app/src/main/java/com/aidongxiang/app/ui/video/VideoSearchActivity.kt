package com.aidongxiang.app.ui.video

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeVideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseListKtActivity
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.base.Constants.ARG_SEARCH_KEY
import com.aidongxiang.app.base.Constants.ARG_TYPE
import com.aidongxiang.app.ui.audio.AudioDetailsActivity
import com.aidongxiang.business.model.Video
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.response.VideoListResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.layout_title_bar_search.*

/**
 * @author Anthony
 * 视频搜索页
 * createTime 2018-01-20
 */
@ContentView(R.layout.activity_search_result)
class VideoSearchActivity : BaseListKtActivity() {

    var datas = ArrayList<Video>()
    lateinit var videoAdapter : HomeVideoAdapter
    var TYPE_VIDEO = 1
    var TYPE_AUDIO = 2
    var type = TYPE_VIDEO
    var searchKey : String ?= ""
    override fun getDatas(): List<*>? = datas

    override fun requestData() {
        requestVideoList()
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        videoAdapter = HomeVideoAdapter(this, datas)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = videoAdapter

        tv_empty_nodata?.text = "暂时没有找到你要的内容"

        type = bundle.getInt(ARG_TYPE)
        searchKey = bundle.getString(ARG_SEARCH_KEY)
        searchKey?.let {
            searchView.setText(it)
            searchView.setSelection(it.length)
        }

        setListener()

        requestVideoList()
    }

    private fun setListener() {
        searchView.setOnEditorActionListener(TextView.OnEditorActionListener { tv, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ) {
                requestVideoList()
                return@OnEditorActionListener true
            }
            false
        })
        btn_search.setOnClickListener {
            requestVideoList()
        }
        videoAdapter.setOnRecyclerViewItemClickListener { v, position ->
            if(position > 0){
                val id = datas[position-1].id
                if(type == TYPE_AUDIO){
                    switchToActivity(AudioDetailsActivity::class.java, ARG_ID to id)
                } else {
                    switchToActivity(VideoDetails2Activity::class.java, ARG_ID to id)
                }
            }
        }

        ibtn_back.setOnClickListener{ finish() }
    }

//    fun startSearch(text : String){
//        aiidbManager.save(SearchText(text))
//        ll_search_hot.visibility = View.GONE
//        ll_search_lately.visibility = View.GONE
//        if(text.equals("空")){
//            tv_empty_nodata?.visibility = View.VISIBLE
//            datas.clear()
//            videoAdapter.update()
//        } else {
//            datas.clear()
//            for(i in 0..5){
//                val video = Video()
//                video.timestamp = "2017-12-03 15:12:24"
//                video.name = "精彩斗牛集锦"
//                video.playNum = 32
//                video.audioLength = "12:10"
//                video.imagePath = HomeFragment.imgs[Random().nextInt(HomeFragment.imgs.size-1)]
//                datas.add(video)
//            }
//            videoAdapter.update()
//
//            tv_empty_nodata?.visibility = View.GONE
//
//        }
//    }


    fun requestVideoList(){
        searchKey = searchView.text.toString()
        if(TextUtils.isEmpty(searchKey)){
            toast("请输入关键字")
            return
        }
        val query = ListRequestQuery("AudioList")
        query.table.page = page
        val where = Where()
        where.audioType = type
        where.searchKey = searchKey
        query.table.where = where
        query.action = AIIAction.ONE
        App.aiiRequest.send(query, object : AIIResponse<VideoListResponseQuery>(this, progressDialog){
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
        if(response.audios != null){
            datas.addAll(response.audios!!)
        }
        videoAdapter.update()
        if(datas.size == 0){
            onNoData()
        }
    }
}
