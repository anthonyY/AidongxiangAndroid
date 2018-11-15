package com.aidongxiang.app.ui.home

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeAudioAdapter
import com.aidongxiang.app.adapter.HomeCategoryAdapter
import com.aidongxiang.app.adapter.HomeNewsAdapter
import com.aidongxiang.app.adapter.HomeVideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ABSTRACT
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.base.Constants.ARG_IMAGE_PATH
import com.aidongxiang.app.base.Constants.ARG_TITLE
import com.aidongxiang.app.base.Constants.ARG_URL
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.app.ui.audio.AudioDetailsActivity
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.ui.mine.MyDownloadActivity
import com.aidongxiang.app.ui.video.VideoDetails2Activity
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.app.widgets.NoticeDialog
import com.aidongxiang.business.model.Article
import com.aidongxiang.business.model.Navigation
import com.aidongxiang.business.model.Video
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.request.AdListRquestQuery
import com.aidongxiang.business.response.*
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.AiiUtil
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_title_bar_home.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * 主页
 */
@ContentView(R.layout.fragment_home)
class HomeFragment : BaseKtFragment() {
    lateinit var homeVideoAdapter: HomeVideoAdapter
    lateinit var homeAudioAdapter: HomeAudioAdapter
    lateinit var homeNewsAdapter: HomeNewsAdapter
    lateinit var homeCategoryAdapter: HomeCategoryAdapter
    lateinit var noticeDialog: NoticeDialog
    val videoDatas = ArrayList<Video>()
    val audioDatas = ArrayList<Video>()
    val newsDatas = ArrayList<Article>()
    val categoryDatas = ArrayList<Navigation>()
    val random = Random()
    var loadState = 0

    override fun init(view: View) {
        setTitle(R.string.app_name)
        setToolBar(toolbar)
        StatusBarUtil.addStatusBarView(titlebar, android.R.color.transparent)
        val videoLayoutManager = LinearLayoutManager(activity)

        //视频
        homeVideoAdapter = HomeVideoAdapter(activity!!, videoDatas)
        homeVideoAdapter.setOnRecyclerViewItemClickListener { v, position ->
            switchToActivity(VideoDetails2Activity::class.java, ARG_ID to videoDatas[position].audioId)
        }
        setLayoutManagerInScroolView(recycler_home_video, videoLayoutManager)
        recycler_home_video.adapter = homeVideoAdapter


        //音频
        val audioManager = LinearLayoutManager(activity)
        setLayoutManagerInScroolView(recycler_home_audio, audioManager)
        homeAudioAdapter = HomeAudioAdapter(activity!!, audioDatas)
        homeAudioAdapter.setOnRecyclerViewItemClickListener { v, position ->
             switchToActivity(AudioDetailsActivity::class.java, ARG_ID to audioDatas[position].audioId)
        }
        recycler_home_audio.adapter = homeAudioAdapter


        //新闻
        homeNewsAdapter = HomeNewsAdapter(activity!!, newsDatas, true)
        val newsLayoutManager = LinearLayoutManager(activity)
        setLayoutManagerInScroolView(recycler_home_news, newsLayoutManager)
        recycler_home_news.adapter = homeNewsAdapter
        homeNewsAdapter.setOnRecyclerViewItemClickListener { _, position ->
            val title = newsDatas[position].title
            val id = newsDatas[position].id
            val abstract = newsDatas[position].abstract
            val imagePath = newsDatas[position].imagePath
            switchToActivity(ArticleDetailsActivity::class.java, ARG_TITLE to title, ARG_ABSTRACT to abstract, ARG_IMAGE_PATH to imagePath, ARG_ID to id)
        }

        //分类 （扩展内容）
        val gridLayoutManager = GridLayoutManager(activity, 4)
        setLayoutManagerInScroolView(recycler_home_category, gridLayoutManager)
        homeCategoryAdapter = HomeCategoryAdapter(activity!!, categoryDatas)
        recycler_home_category.adapter = homeCategoryAdapter
        homeCategoryAdapter.setOnRecyclerViewItemClickListener { _, position ->
//            ：1外部链接，2视频 3音频 4资讯
            val navigation = categoryDatas[position]
            val id = navigation.fromId
            val url = navigation.link
            val name = navigation.name
            when(navigation.fromType){
                1->{
                    switchToActivity(CommonWebViewActivity::class.java, ARG_URL to url, ARG_TITLE to navigation.name)
                }
                2->{
                    switchToActivity(VideoDetails2Activity::class.java, ARG_ID to id)
                }
                3->{
                    switchToActivity(AudioDetailsActivity::class.java, ARG_ID to id)
                }
                4->{
                    switchToActivity(ArticleDetailsActivity::class.java, ARG_ID to id, ARG_ABSTRACT to name, ARG_ACTION to 1)
                }
                5->{
                    noticeDialog.show()
                }
            }

        }
        noticeDialog = NoticeDialog(activity!!)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
        val isShowNoticeToday = AiiUtil.getBoolean(activity, "isShowNotice" + today)
        if (!isShowNoticeToday) {
            noticeDialog.show()
            AiiUtil.putBoolean(activity, "isShowNotice" + today, true)
        }
        llHomeNewsMore.setOnClickListener { switchToActivity(NewsActivity::class.java) }
        swipe_home_refresh.setOnRefreshListener {
            loadState = 0

            refresh()
        }

        //点更多视频  切换到视频模块
        iv_home_more_video.setOnClickListener { (activity as Main2Activity).swicthFragment(1) }
        //点更多音频  切换到音频模块
        iv_home_more_audio.setOnClickListener { (activity as Main2Activity).swicthFragment(3) }
        //点更多热门资讯  跳转到资讯页
        iv_home_more_news.setOnClickListener {  switchToActivity(NewsActivity::class.java) }
        //点击我的下载按钮
        ibtn_nav_menu.setOnClickListener{
            if(Constants.user == null){
                switchToActivity(LoginActivity::class.java)
                return@setOnClickListener
            }
            switchToActivity(MyDownloadActivity::class.java)
        }
        ad_home.setOnItemClickListener {
            if(!TextUtils.isEmpty(it.link)){
                switchToActivity(CommonWebViewActivity::class.java, ARG_TITLE to it.name, ARG_URL to it.link)
            }
        }

        refresh()
    }

    private fun refresh(){
        requestAdList()
        requestAudioList()
        requestVideoList()
        requestArticleList()
        requestNavigationList()
        requestSetting()
    }
    private fun onLoadFinish() {
        loadState++
        if(loadState >= 5){
            swipe_home_refresh.isRefreshing = false
        }

    }



    /**
     * 设置RecyclerView 的LayoutManager , 并对其做兼容scrollView 滑动卡顿问题的处理
     */
    private fun setLayoutManagerInScroolView(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {

        //下面这段是解决NestedScrollView嵌套RecyclerView时滑动不流畅问题的解决办法
        layoutManager.isSmoothScrollbarEnabled = true
        layoutManager.isAutoMeasureEnabled = true
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = layoutManager
    }


    private fun requestAdList() {
        val query = AdListRquestQuery()
        query.action = AIIAction.ONE

        App.aiiRequest.send(query, object : AIIResponse<AdListResponseQuery>(activity, false) {
            override fun onSuccess(response: AdListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.ads?.let {
                    ad_home.startAD(it.size, 3, true, it, 0.48f)
                }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }
        })
    }

    /**
     * 请求文章列表
     */
    private fun requestArticleList() {
        val query = ListRequestQuery("ArticleList")
        query.table.page = 1
        query.action = AIIAction.ONE
        App.aiiRequest.send(query, object : AIIResponse<ArticleListResponseQuery>(activity, progressDialog) {
            override fun onSuccess(response: ArticleListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getArticleList(it) }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }

            override fun onCache(response: ArticleListResponseQuery?, index: Int) {
                super.onCache(response, index)
                response?.let { getArticleList(it) }
            }
        })
    }
    /**
     * 请求导航列表
     */
    private fun requestNavigationList() {
        val query = ListRequestQuery("NavigationList")
        query.table.page = 1
        query.action = AIIAction.ONE
        App.aiiRequest.send(query, object : AIIResponse<NavigationListListResponseQuery>(activity, progressDialog) {
            override fun onSuccess(response: NavigationListListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getNavigationListListResponseQuery(it) }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }

            override fun onCache(response: NavigationListListResponseQuery?, index: Int) {
                super.onCache(response, index)
                response?.let { getNavigationListListResponseQuery(it) }
            }
        })
    }

    /**
     * 获取新闻数据，设置到adapter里
     */
    private fun getNavigationListListResponseQuery(response: NavigationListListResponseQuery) {
        categoryDatas.clear()
        if(!TextUtils.isEmpty(Constants.poster)){
            val navigation = Navigation()
            navigation.fromType = 5
            navigation.link = Constants.poster
            navigation.name = "海报"
            navigation.resId = R.drawable.ic_broadcast
            noticeDialog.setContent(Constants.poster)
            categoryDatas.add(navigation)
        }
        response.navigations?.let { categoryDatas.addAll(it) }
        homeCategoryAdapter.update()
    }

    /**
     * 获取新闻数据，设置到adapter里
     */
    private fun getArticleList(response: ArticleListResponseQuery) {
        newsDatas.clear()
        response.articles?.let { newsDatas.addAll(it) }
        homeNewsAdapter.update()
    }


    /**
     * 请求音频列表
     */
    private fun requestAudioList(){

        val query = ListRequestQuery("AudioList")
        query.table.page = 1
        query.table.limit = 3
        query.action = AIIAction.ONE
        val where = Where()
        where.audioType = 2
        query.table.where = where
        App.aiiRequest.send(query, object : AIIResponse<VideoListResponseQuery>(activity, progressDialog){
            override fun onSuccess(response: VideoListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getAudioList(it) }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }

            override fun onCache(response: VideoListResponseQuery?, index: Int) {
                super.onCache(response, index)
                response?.let { getAudioList(it) }
            }
        })
    }

    /**
     * 请求视频列表
     */
    private fun requestVideoList(){

        val query = ListRequestQuery("AudioList")
        query.table.page = 1
        query.table.limit = 3
        query.action = AIIAction.ONE
        val where = Where()
        where.audioType = 1
        query.table.where = where
        App.aiiRequest.send(query, object : AIIResponse<VideoListResponseQuery>(activity, progressDialog){
            override fun onSuccess(response: VideoListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getVideoList(it) }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }

            override fun onCache(response: VideoListResponseQuery?, index: Int) {
                super.onCache(response, index)
                response?.let { getVideoList(it) }
            }
        })
    }

    /**
     * 获取音频数据，设置到adapter里
     */
    private fun getAudioList(response: VideoListResponseQuery) {
        audioDatas.clear()
        response.audios?.let { audioDatas.addAll(it) }
        homeAudioAdapter.update()
    }
    /**
     * 获取视频数据，设置到adapter里
     */
    private fun getVideoList(response: VideoListResponseQuery) {
        videoDatas.clear()
        response.audios?.let { videoDatas.addAll(it) }
        homeVideoAdapter.update()
    }


    private fun requestSetting(){
        val query = RequestQuery("Setting")
        App.aiiRequest.send(query, object : AIIResponse<SettingResponseQuery>(activity, false){

            override fun onSuccess(response: SettingResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                Constants.poster = response?.poster
            }
        })
    }
}
