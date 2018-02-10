package com.aidongxiang.app.ui.home

import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Html.ImageGetter
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeAudioAdapter
import com.aidongxiang.app.adapter.HomeCategoryAdapter
import com.aidongxiang.app.adapter.HomeNewsAdapter
import com.aidongxiang.app.adapter.HomeVideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.base.Constants.ARG_TITLE
import com.aidongxiang.app.base.Constants.ARG_URL
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.app.ui.audio.AudioDetailsActivity
import com.aidongxiang.app.ui.mine.MyDownloadActivity
import com.aidongxiang.app.ui.video.VideoDetailsActivity
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.app.widgets.NoticeDialog
import com.aidongxiang.business.model.Ad
import com.aidongxiang.business.model.Article
import com.aidongxiang.business.model.Video
import com.aidongxiang.business.request.AdListRquestQuery
import com.aidongxiang.business.response.AdListResponseQuery
import com.aidongxiang.business.response.ArticleListResponseQuery
import com.aidongxiang.business.response.VideoListResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.AiiUtil
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_title_bar_home.*
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


/**
 * 主页
 */
@ContentView(R.layout.fragment_home)
class HomeFragment : BaseKtFragment() {
    companion object {
        val imgs = arrayListOf<String>("http://h.hiphotos.baidu.com/image/pic/item/5243fbf2b211931397f7ff7a6c380cd790238d47.jpg",
                "http://a.hiphotos.baidu.com/image/pic/item/21a4462309f79052983046f405f3d7ca7acbd5c8.jpg",
                "http://f.hiphotos.baidu.com/image/pic/item/3bf33a87e950352afba6083e5943fbf2b3118bc4.jpg",
//                "http://g.hiphotos.baidu.com/image/pic/item/10dfa9ec8a1363278cb2c3f9988fa0ec09fac799.jpg",
//                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510419591961&di=7d04ba01d67bd9129d1058bb1f587e69&imgtype=0&src=http%3A%2F%2Fimage.xinmin.cn%2F2013%2F03%2F19%2F20130319120213084246.jpg",
//                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1511014691&di=b141773d45525b0f9525b475dceb20a8&imgtype=jpg&er=1&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F4%2F54096b6a027bc.jpg",
//                "http://c.hiphotos.baidu.com/image/pic/item/d833c895d143ad4b1bbf6bf688025aafa40f0676.jpg",
                "http://cdn.duitang.com/uploads/item/201404/16/20140416184252_Hvjxi.jpeg",
                "http://img5.duitang.com/uploads/item/201512/15/20151215132152_t2jhd.thumb.700_0.jpeg",
                "http://f1.topitme.com/1/ed/14/1129731340b7414ed1o.jpg",
                "http://e.hiphotos.baidu.com/image/h%3D300/sign=91aecc5808087bf462ec51e9c2d3575e/37d3d539b6003af3b6bc2f733f2ac65c1038b69b.jpg"
        )
    }

    val content = "<p style=\"word-wrap: break-word; font-family: &quot;sans serif&quot;, tahoma, verdana, helvetica; font-size: 12px; white-space: normal;\">\n" +
            "    时逢丁酉盛世，金秋佳节，五谷丰登，各家族大武正精力旺盛，正适宜斗武比赛，发扬民族文化。经牯藏头组织，各家族商议，决定农历八月十三（公历10月1日国庆节）在巴命牛场举行斗牛大赛，欢迎各位村民和外来游客参加观赏。赛况如下：<br/>\n" +
            "</p>\n" +
            "<ol style=\"font-family: &quot;sans serif&quot;, tahoma, verdana, helvetica; font-size: 12px; white-space: normal;\" class=\" list-paddingleft-2\">\n" +
            "    <li>\n" +
            "        <p>\n" +
            "            务弄八大家 pk 牯藏头&nbsp; 5分钟\n" +
            "        </p>\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <p>\n" +
            "            务弄孟孖 pk 高赧&nbsp; 3分钟\n" +
            "        </p>\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <p>\n" +
            "            顿路宰便 pk 纪登&nbsp; 5分钟\n" +
            "        </p>\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <p>\n" +
            "            绞洞 pk 宰瓜 3分钟\n" +
            "        </p>\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <p>\n" +
            "            顿路腊王 pk 尚重宰虎 5分钟\n" +
            "        </p>\n" +
            "    </li>\n" +
            "    <li>\n" +
            "        <p>\n" +
            "            平养登云 pk 尚重大水井 10分钟\n" +
            "        </p>\n" +
            "    </li>\n" +
            "</ol>\n" +
            "<p>\n" +
            "    <br/><span style=\"font-family: &quot;sans serif&quot;, tahoma, verdana, helvetica; font-size: 12px;\">&nbsp;&nbsp;&nbsp;&nbsp;</span><span style=\"font-family: &quot;sans serif&quot;, tahoma, verdana, helvetica; font-size: 12px;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</span><span style=\"font-family: &quot;sans serif&quot;, tahoma, verdana, helvetica; font-size: 12px;\">牯藏头宰海宣</span><br/><span style=\"font-family: &quot;sans serif&quot;, tahoma, verdana, helvetica; font-size: 12px;\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</span><span style=\"font-family: &quot;sans serif&quot;, tahoma, verdana, helvetica; font-size: 12px;\">农历2017年8月初五</span><br/>\n" +
            "</p>\n" +
            "<p style=\"word-wrap: break-word; font-family: &quot;sans serif&quot;, tahoma, verdana, helvetica; font-size: 12px; white-space: normal;\">\n" +
            "    <br/>\n" +
            "</p>\n" +
            "<p>\n" +
            "    <br/>\n" +
            "</p>"
    lateinit var homeVideoAdapter: HomeVideoAdapter
    lateinit var homeAudioAdapter: HomeAudioAdapter
    lateinit var homeNewsAdapter: HomeNewsAdapter
    lateinit var homeCategoryAdapter: HomeCategoryAdapter
    lateinit var noticeDialog: NoticeDialog
    val videoDatas = ArrayList<Video>()
    val audioDatas = ArrayList<Video>()
    val newsDatas = ArrayList<Article>()
    val categoryDatas = ArrayList<String>()
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
            switchToActivity(VideoDetailsActivity::class.java)
        }
        setLayoutManagerInScroolView(recycler_home_video, videoLayoutManager)
        recycler_home_video.adapter = homeVideoAdapter


        //音频
        val audioManager = LinearLayoutManager(activity)
        setLayoutManagerInScroolView(recycler_home_audio, audioManager)
        homeAudioAdapter = HomeAudioAdapter(activity!!, audioDatas)
        homeAudioAdapter.setOnRecyclerViewItemClickListener { v, position ->
             switchToActivity(AudioDetailsActivity::class.java)
        }
        recycler_home_audio.adapter = homeAudioAdapter


        //新闻
        homeNewsAdapter = HomeNewsAdapter(activity!!, newsDatas)
        val newsLayoutManager = LinearLayoutManager(activity)
        setLayoutManagerInScroolView(recycler_home_news, newsLayoutManager)
        recycler_home_news.adapter = homeNewsAdapter
        homeNewsAdapter.setOnRecyclerViewItemClickListener { v, position ->
            switchToActivity(CommonWebViewActivity::class.java, ARG_TITLE to "新闻详情", ARG_URL to "http://www.baidu.com")
        }

        //分类 （扩展内容）
        val gridLayoutManager = GridLayoutManager(activity, 4)
        setLayoutManagerInScroolView(recycler_home_category, gridLayoutManager)
        homeCategoryAdapter = HomeCategoryAdapter(activity!!, categoryDatas)
        recycler_home_category.adapter = homeCategoryAdapter
        homeCategoryAdapter.setOnRecyclerViewItemClickListener { v, position ->
            when (position) {
                0 -> {
                    noticeDialog.show()
                }
            }
        }
        noticeDialog = NoticeDialog(activity)

        val charSequence = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, imgGetter, null)
        } else {
            Html.fromHtml(content, imgGetter, null)
        }
        noticeDialog.setContent(charSequence)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE)
        val isShowNoticeToday = AiiUtil.getBoolean(activity, "isShowNotice" + today)
        if (!isShowNoticeToday) {
            noticeDialog.show()
            AiiUtil.putBoolean(activity, "isShowNotice" + today, true)
        }
        llHomeNewsMore.setOnClickListener { switchToActivity(NewsActivity::class.java) }
        swipe_home_refresh.setOnRefreshListener {
            loadState = 0
            Handler().postDelayed({ onLoadFinish() }, 1000)
        }

        //点更多视频  切换到视频模块
        iv_home_more_video.setOnClickListener { (activity as Main2Activity).swicthFragment(1) }
        //点更多音频  切换到音频模块
        iv_home_more_audio.setOnClickListener { (activity as Main2Activity).swicthFragment(3) }
        //点更多热门资讯  跳转到资讯页
        iv_home_more_news.setOnClickListener {  switchToActivity(NewsActivity::class.java) }
        //点击我的下载按钮
        ibtn_nav_menu.setOnClickListener{ switchToActivity(MyDownloadActivity::class.java) }

        setDatas()
    }

    private fun onLoadFinish() {
        loadState++
//        if(loadState >= 4){
            swipe_home_refresh.isRefreshing = false
//        }

    }

    var imgGetter: ImageGetter = ImageGetter { source ->
        val drawable: Drawable?
        val url: URL
        try {
            url = URL(source)
            drawable = Drawable.createFromStream(url.openStream(), "")  //获取网路图片
        } catch (e: Exception) {
            return@ImageGetter null
        }
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        return@ImageGetter drawable
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

    /**
     * 设置数据
     */
    private fun setDatas() {
        val ads = ArrayList<Ad>()
        for (i in 0..4) {
            val ad = Ad()
            ad.name = "广告"
            ad.link = "http://www.baidu.com"
            ad.imagePath = imgs[random.nextInt(imgs.size)]
            if (i < 3) {
                val video = Video()
                video.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
                video.audioLength = "12:10"
                video.name = "精彩斗牛啦啦啦啦"
                video.timestamp = "07-12"
                video.playNum = 321
                videoDatas.add(video)
                audioDatas.add(video)
            }
            val article = Article()
            article.timestamp = "2018-0$i-1${i + 2} 12:15:34"
            article.title = "农耕部落初见成效"
            article.abstract = "农耕部落初见成效农耕部落初见成效农耕部落初见成效"
            article.id = i
            article.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
            newsDatas.add(article)
            ads.add(ad)
            if (i < 4) {
                categoryDatas.add(imgs[random.nextInt(imgs.size)])
            }

        }
        ad_home.startAD(ads.size, 3, true, ads, 0.544f)
        homeNewsAdapter.update()
        homeVideoAdapter.update()
        homeAudioAdapter.update()
    }

    fun requestAdList() {
        val query = AdListRquestQuery()
        query.action = AIIAction.ONE

        App.aiiRequest?.send(query, object : AIIResponse<AdListResponseQuery>(activity, false) {
            override fun onSuccess(response: AdListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.ads?.let {
                    ad_home.startAD(it.size, 3, true, it, 0.48f)
                }
            }
        })
    }

    /**
     * 请求文章列表
     */
    fun requestArticleList() {
        val query = ListRequestQuery("ArticleList")
        query.table.page = 1
        query.action = AIIAction.TWO
        App.aiiRequest?.send(query, object : AIIResponse<ArticleListResponseQuery>(activity) {
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
    fun requestAudioList(){

        val query = ListRequestQuery("AudioList")
        query.table.page = 1
        query.table.limit = 3
        query.action = AIIAction.ONE
        App.aiiRequest?.send(query, object : AIIResponse<VideoListResponseQuery>(activity){
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
    fun requestVideoList(){

        val query = ListRequestQuery("VideoList")
        query.table.page = 1
        query.table.limit = 3
        query.action = AIIAction.ONE
        App.aiiRequest?.send(query, object : AIIResponse<VideoListResponseQuery>(activity){
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
        response.videos?.let { audioDatas.addAll(it) }
        homeAudioAdapter.update()
    }
    /**
     * 获取视频数据，设置到adapter里
     */
    private fun getVideoList(response: VideoListResponseQuery) {
        videoDatas.clear()
        response.videos?.let { videoDatas.addAll(it) }
        homeVideoAdapter.update()
    }
}
