package com.aidongxiang.app.ui.video

import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_TITLE
import com.aidongxiang.app.base.Constants.ARG_TYPE
import com.aidongxiang.app.base.Constants.ARG_URL
import com.aidongxiang.app.interfaces.AppBarStateChangeListener
import com.aidongxiang.app.ui.home.CommonWebViewActivity
import com.aidongxiang.app.ui.mine.MyDownloadActivity
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.business.model.Category
import com.aidongxiang.business.request.AdListRquestQuery
import com.aidongxiang.business.response.AdListResponseQuery
import com.aidongxiang.business.response.CategoryListResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import kotlinx.android.synthetic.main.fragment_video_tablelayout.*

/**
 * 主页 - 视频
 */
@ContentView(R.layout.fragment_video_tablelayout)
class VideoFragment : BaseKtFragment() {

    var categorys = ArrayList<Category>()

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    override fun init(view: View) {
//        setToolBar(toolbar)
        setTitle("视频")
        StatusBarUtil.addStatusBarView(ll_statusBar2, android.R.color.transparent)
        StatusBarUtil.addStatusBarView(titlebar, android.R.color.transparent)
        mPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, activity)
        viewpager_video.offscreenPageLimit = 5
        viewpager_video.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager_video)
        if(categorys.size > 4){
            tablayout.tabMode = TabLayout.MODE_SCROLLABLE
        } else {
            tablayout.tabMode = TabLayout.MODE_FIXED
        }
        ibtn_nav_menu.setOnClickListener { switchToActivity(MyDownloadActivity::class.java, MyDownloadActivity.ARG_POSITION to 0) }
        ibtn_title_search.setOnClickListener { switchToActivity(SearchActivity::class.java, ARG_TYPE to SearchActivity.TYPE_VIDEO) }

        ad_video.setOnItemClickListener {
            if(!TextUtils.isEmpty(it.link)){
                switchToActivity(CommonWebViewActivity::class.java, ARG_TITLE to it.name, ARG_URL to it.link)
            }
        }
        mAppBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {
//                LogUtil.d("STATE", state.name)
                when (state) {
                    AppBarStateChangeListener.State.EXPANDED -> {
                        ll_statusBar2.visibility = View.GONE
                        LogUtil.e("展开状态")
                    }

                    AppBarStateChangeListener.State.COLLAPSED ->{
                        ll_statusBar2.visibility = View.VISIBLE
                        LogUtil.e("折叠状态")
                    } //折叠状态

                    else -> //中间状态
                        ll_statusBar2.visibility = View.GONE
                }
            }
        })
        requestCategoryList()
        requestAdList()

//        Handler().postDelayed({
//            val titleBarHeight = titlebar.measuredHeight
//            val params = CollapsingToolbarLayout.LayoutParams(CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT)
//            params.topMargin = titleBarHeight
//            ad_video.layoutParams = params
//        }, 100)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        if (childFragmentManager.backStackEntryCount == 0) {
            inflater?.inflate(R.menu.menu_search, menu)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            switchToActivity(SearchActivity::class.java, Constants.ARG_TYPE to SearchActivity.TYPE_VIDEO)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestCategoryList(){
        val query = ListRequestQuery("CategoryList")
        query.action = AIIAction.ONE
        App.aiiRequest.send(query, object : AIIResponse<CategoryListResponseQuery>(activity, progressDialog){
            override fun onSuccess(response: CategoryListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let { getCategoryList(it) }
            }

            override fun onCache(content: CategoryListResponseQuery?, index: Int) {
                super.onCache(content, index)
                content?.let { getCategoryList(it) }
            }
        })
    }

    private fun getCategoryList(response: CategoryListResponseQuery){
        response.categorys?.let {
            categorys.clear()
            mPagerAdapter?.clear()

            categorys.addAll(it)

            for(category in categorys){
                mPagerAdapter?.addFragment(VideoListFragment.newInstance(VideoListFragment.TYPE_HOME, category.id), category.name)
            }
            if(categorys.size > 4){
                tablayout.tabMode = TabLayout.MODE_SCROLLABLE
            } else {
                tablayout.tabMode = TabLayout.MODE_FIXED
            }
            mPagerAdapter?.notifyDataSetChanged()
        }
    }

    fun requestAdList(){
        val query = AdListRquestQuery()
        query.action = AIIAction.THREE
        query.positionId = 2
        App.aiiRequest.send(query, object : AIIResponse<AdListResponseQuery>(activity, false){
            override fun onSuccess(response: AdListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.ads?.let {
                    ad_video.startAD(it.size, 3, true, it, 0.48f)
                }
            }
        })
    }
}