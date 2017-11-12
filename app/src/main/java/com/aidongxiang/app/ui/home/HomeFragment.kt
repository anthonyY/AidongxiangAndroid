package com.aidongxiang.app.ui.home

import android.os.Handler
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeCategoryAdapter
import com.aidongxiang.app.adapter.HomeNewsAdapter
import com.aidongxiang.app.adapter.HomeVideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.model.Ad
import com.aidongxiang.app.utils.StatusBarUtil
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_title_bar_home.*
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
                "http://g.hiphotos.baidu.com/image/pic/item/10dfa9ec8a1363278cb2c3f9988fa0ec09fac799.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1510419591961&di=7d04ba01d67bd9129d1058bb1f587e69&imgtype=0&src=http%3A%2F%2Fimage.xinmin.cn%2F2013%2F03%2F19%2F20130319120213084246.jpg",
                "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1511014691&di=b141773d45525b0f9525b475dceb20a8&imgtype=jpg&er=1&src=http%3A%2F%2Fpic1.win4000.com%2Fwallpaper%2F4%2F54096b6a027bc.jpg",
                "http://c.hiphotos.baidu.com/image/pic/item/d833c895d143ad4b1bbf6bf688025aafa40f0676.jpg",
                "http://e.hiphotos.baidu.com/image/h%3D300/sign=91aecc5808087bf462ec51e9c2d3575e/37d3d539b6003af3b6bc2f733f2ac65c1038b69b.jpg"
        )
    }
    var homeVideoAdapter : HomeVideoAdapter ?= null
    var homeNewsAdapter : HomeNewsAdapter?= null
    var homeCategoryAdapter : HomeCategoryAdapter?= null
    val videoDatas = ArrayList<String>()
    val newsDatas = ArrayList<String>()
    val categoryDatas = ArrayList<String>()
    val random = Random()

    override fun init(view: View) {
        setToolBar(toolbar)
        StatusBarUtil.addStatusBarView(view)
        val linearLayoutManager = LinearLayoutManager(activity)
        //下面这段是解决NestedScrollView嵌套RecyclerView时滑动不流畅问题的解决办法
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled = true
        recycler_home_video.setHasFixedSize(true)
        recycler_home_video.isNestedScrollingEnabled = false
        recycler_home_video.layoutManager = linearLayoutManager
        homeVideoAdapter = HomeVideoAdapter(activity!!, videoDatas)
        homeVideoAdapter?.setOnRecyclerViewItemClickListener { v, position ->// switchToActivity(LiveBroadcastActivity::class.java)
        }

        val linearLayoutManager2 = LinearLayoutManager(activity)
        //下面这段是解决NestedScrollView嵌套RecyclerView时滑动不流畅问题的解决办法
        linearLayoutManager2.isSmoothScrollbarEnabled = true
        linearLayoutManager2.isAutoMeasureEnabled = true
        recycler_home_news.setHasFixedSize(true)
        recycler_home_news.isNestedScrollingEnabled = false
        recycler_home_news.layoutManager = linearLayoutManager2

        val gridLayoutManager = GridLayoutManager(activity, 4)
        //下面这段是解决NestedScrollView嵌套RecyclerView时滑动不流畅问题的解决办法
        gridLayoutManager.isSmoothScrollbarEnabled = true
        gridLayoutManager.isAutoMeasureEnabled = true
        recycler_home_category.setHasFixedSize(true)
        recycler_home_category.isNestedScrollingEnabled = false
        recycler_home_category.layoutManager = gridLayoutManager

        homeCategoryAdapter = HomeCategoryAdapter(activity!!, categoryDatas)
        recycler_home_category.adapter = homeCategoryAdapter

        homeNewsAdapter = HomeNewsAdapter(activity!!, newsDatas)
        recycler_home_video.adapter = homeVideoAdapter
        recycler_home_news.adapter = homeNewsAdapter
        homeNewsAdapter?.setOnRecyclerViewItemClickListener( {
            v, position ->{
//                switchToActivity(TutorDetailsActivity::class.java, "id" to 9, "name" to "张三")
            }
        })

        swipe_home_refresh.setOnRefreshListener { Handler().postDelayed({swipe_home_refresh.isRefreshing = false }, 1000) }
        setDatas()
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
            videoDatas.add(imgs[random.nextInt(imgs.size)])
            newsDatas.add(imgs[random.nextInt(imgs.size)])
            categoryDatas.add(imgs[random.nextInt(imgs.size)])
            ads.add(ad)
        }
        ad_home.startAD(ads.size, 3, true, ads, 0.544f)
        homeNewsAdapter?.update()
        homeVideoAdapter?.update()
    }



}
