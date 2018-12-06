package com.aidongxiang.app.ui.audio

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
import com.aidongxiang.app.base.Constants.ARG_URL
import com.aidongxiang.app.ui.home.CommonWebViewActivity
import com.aidongxiang.app.ui.mine.MyDownloadActivity
import com.aidongxiang.app.ui.video.SearchActivity
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.business.model.Category
import com.aidongxiang.business.request.AdListRquestQuery
import com.aidongxiang.business.response.AdListResponseQuery
import com.aidongxiang.business.response.CategoryListResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.fragment_video_tablelayout.*

/**
 * 主页 - 音频
 */
@ContentView(R.layout.fragment_video_tablelayout)
class AudioFragment : BaseKtFragment() {

    var categorys = ArrayList<Category>()

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    override fun init(view: View) {
//        setToolBar(toolbar)
        setTitle("音频")
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
        ibtn_nav_menu.setOnClickListener { switchToActivity(MyDownloadActivity::class.java, MyDownloadActivity.ARG_POSITION to 1) }
        ibtn_title_search.setOnClickListener { switchToActivity(SearchActivity::class.java, Constants.ARG_TYPE to SearchActivity.TYPE_AUDIO) }
        ad_video.setOnItemClickListener {
            if(!TextUtils.isEmpty(it.link)){
                switchToActivity(CommonWebViewActivity::class.java, ARG_TITLE to it.name, ARG_URL to it.link)
            }
        }
        requestAdList()
        requestCategoryList()


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
            switchToActivity(SearchActivity::class.java, Constants.ARG_TYPE to SearchActivity.TYPE_AUDIO)
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun requestCategoryList(){
        val query = ListRequestQuery("CategoryList")
        query.action = AIIAction.TWO
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
                mPagerAdapter?.addFragment(AudioListFragment.newInstance(AudioListFragment.TYPE_HOME, category.id.toInt()), category.name)
            }
            if(categorys.size > 4){
                tablayout.tabMode = TabLayout.MODE_SCROLLABLE
            } else {
                tablayout.tabMode = TabLayout.MODE_FIXED
            }
            mPagerAdapter?.notifyDataSetChanged()
        }
    }


    private fun requestAdList(){
        val query = AdListRquestQuery()
        query.action = AIIAction.THREE
        query.positionId = 3
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