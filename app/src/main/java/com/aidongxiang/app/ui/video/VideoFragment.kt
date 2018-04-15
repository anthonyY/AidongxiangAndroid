package com.aidongxiang.app.ui.video

import android.support.design.widget.TabLayout
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.business.model.Category
import com.aidongxiang.business.response.CategoryListResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.fragment_video_tablelayout.*
import kotlinx.android.synthetic.main.layout_title_bar_home.*

/**
 * 主页 - 视频
 */
@ContentView(R.layout.fragment_video_tablelayout)
class VideoFragment : BaseKtFragment() {

    var categorys = ArrayList<Category>()

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null

    override fun init(view: View) {
        setToolBar(toolbar)
        setTitle("视频")
        StatusBarUtil.addStatusBarView(titlebar, android.R.color.transparent)
        mPagerAdapter = SimpleFragmentPagerAdapter(childFragmentManager, activity)
        viewpager.offscreenPageLimit = 5
        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)
        if(categorys.size > 4){
            tablayout.tabMode = TabLayout.MODE_SCROLLABLE
        } else {
            tablayout.tabMode = TabLayout.MODE_FIXED
        }

        requestCategoryList()

    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_search, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            switchToActivity(VideoSearchActivity::class.java)
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
}