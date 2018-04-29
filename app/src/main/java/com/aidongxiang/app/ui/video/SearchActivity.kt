package com.aidongxiang.app.ui.video

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.Tag2Adapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseListKtActivity
import com.aidongxiang.app.base.Constants.ARG_SEARCH_KEY
import com.aidongxiang.app.base.Constants.ARG_TYPE
import com.aidongxiang.business.model.SearchText
import com.aidongxiang.business.model.Video
import com.aiitec.openapi.db.AIIDBManager
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.layout_title_bar_search.*
import java.util.*

/**
 * @author Anthony
 * 视频搜索页
 * createTime 2018-01-20
 */
@ContentView(R.layout.activity_search)
class SearchActivity : BaseListKtActivity() {

    companion object {
        var TYPE_NEWS = 1
        var TYPE_VIDEO = 2
        var TYPE_AUDIO = 3
        var TYPE_MICROBLO = 4
        var TYPE_USER = 5
    }
    var datas = ArrayList<Video>()
    lateinit var aiidbManager : AIIDBManager
    lateinit var latelyAdapter : Tag2Adapter<SearchText>
    lateinit var hotAdapter : Tag2Adapter<SearchText>
//    lateinit var videoAdapter : HomeVideoAdapter
    var latelyDatas = ArrayList<SearchText>()
    var hotDatas = ArrayList<SearchText>()
    var type = 1


    override fun getDatas(): List<*>? = datas

    override fun requestData() {
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        aiidbManager = AIIDBManager(this)

        type = bundle.getInt(ARG_TYPE)
        latelyAdapter = Tag2Adapter(this, latelyDatas)
        hotAdapter = Tag2Adapter(this, hotDatas)
//        hotDatas.add(SearchText("斗牛"))
//        hotDatas.add(SearchText("牯藏节"))
//        hotDatas.add(SearchText("侗族大歌"))
        flow_hot.adapter = hotAdapter

        flow_lately.adapter = latelyAdapter

//        videoAdapter = HomeVideoAdapter(this, datas)
//        recyclerView?.layoutManager = LinearLayoutManager(this)
//        recyclerView?.adapter = videoAdapter
//
//        tv_empty_nodata?.text = "暂时没有找到你要的内容"

        setListener()
        setLatelyDatas()
    }

    private fun setListener() {
        searchView.setOnEditorActionListener(TextView.OnEditorActionListener { tv, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = searchView.text.toString()
                when(type){
                    TYPE_VIDEO->{
                        switchToActivity(VideoSearchActivity::class.java, ARG_TYPE to 1, ARG_SEARCH_KEY to text)
                    }
                    TYPE_AUDIO->{
                        switchToActivity(VideoSearchActivity::class.java, ARG_TYPE to 2, ARG_SEARCH_KEY to text)
                    }
                }
                aiidbManager.save(SearchText(text, Date()))
                return@OnEditorActionListener true
            }
            false
        })
        btn_search.setOnClickListener {
//            startSearch(searchView.text.toString())
            val text = searchView.text.toString()
            aiidbManager.save(SearchText(text, Date()))
            when(type){
                TYPE_VIDEO->{
                    switchToActivity(VideoSearchActivity::class.java, ARG_TYPE to 1, ARG_SEARCH_KEY to text)
                }
                TYPE_AUDIO->{
                    switchToActivity(VideoSearchActivity::class.java, ARG_TYPE to 2, ARG_SEARCH_KEY to text)
                }
            }
        }
//        videoAdapter.setOnRecyclerViewItemClickListener { v, position ->
//            switchToActivity(VideoDetailsActivity::class.java)
//        }
        flow_hot.setOnTagClickListener { parent, view, position ->
            val text = hotAdapter.getItem(position).text!!
//            startSearch(text)
            when(type){
                TYPE_VIDEO->{
                    switchToActivity(VideoSearchActivity::class.java, ARG_TYPE to 1, ARG_SEARCH_KEY to text)
                }
                TYPE_AUDIO->{
                    switchToActivity(VideoSearchActivity::class.java, ARG_TYPE to 2, ARG_SEARCH_KEY to text)
                }
            }
        }
        flow_lately.setOnTagClickListener { parent, view, position ->
            val text = latelyAdapter.getItem(position).text!!
            when(type){
                TYPE_VIDEO->{
                    switchToActivity(VideoSearchActivity::class.java, ARG_TYPE to 1, ARG_SEARCH_KEY to text)
                }
                TYPE_AUDIO->{
                    switchToActivity(VideoSearchActivity::class.java, ARG_TYPE to 2, ARG_SEARCH_KEY to text)
                }
            }
        }
        iv_search_delete_history.setOnClickListener {
            latelyDatas.clear()
            latelyAdapter.update()
            aiidbManager.deleteAll(SearchText::class.java)
        }
        ibtn_back.setOnClickListener{ finish() }
    }

    private fun setLatelyDatas() {

        val searchHistory = aiidbManager.findAll(SearchText::class.java, null, null, null)
        latelyDatas.clear()
        searchHistory?.filterIndexed { index, search -> index < 10 }?.forEach { latelyDatas.add(it) }
        latelyAdapter.update()

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

}