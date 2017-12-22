package com.aidongxiang.app.ui.square

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.PostImgAdapter
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.model.User
import kotlinx.android.synthetic.main.activity_microblog_details.*
import java.util.*
import kotlin.collections.ArrayList
import android.support.design.widget.AppBarLayout
import com.aidongxiang.app.interfaces.AppBarStateChangeListener
import com.aidongxiang.app.utils.StatusBarUtil
import com.aiitec.openapi.utils.LogUtil


/***
 * 微博详情
 * @author Anthony
 * createTime 2017-12-10
 */
@ContentView(R.layout.activity_microblog_details)
class MicroblogDetailsActivity : BaseKtActivity() {

    var postId = 0
    lateinit var adapter : SimpleFragmentPagerAdapter
    override fun init(savedInstanceState: Bundle?) {
        StatusBarUtil.addStatusBarView(ll_statusBar2, android.R.color.transparent)
        postId = bundle.getInt(ARG_ID)
        title = "微博详情"
        adapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)
        adapter.addFragment(PostCommentListFragment.newInstance(postId, 1), "转发")
        adapter.addFragment(PostCommentListFragment.newInstance(postId, 2), "评论")
        adapter.addFragment(PostAppraiseListFragment.newInstance(postId), "赞")
        viewpager.adapter = adapter

        rlCommentTab.setOnClickListener { selectFragmentIndex(0) }
        rlForwardTab.setOnClickListener { selectFragmentIndex(1) }
        rlAppraiseTab.setOnClickListener { selectFragmentIndex(2) }
        left_icon.setOnClickListener{ finish() }
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                selectFragmentIndex(position)
            }
        })
        rlCommentTab.isSelected = true
//        val height = llTitleBar2.measuredHeight
        mAppBarLayout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: AppBarStateChangeListener.State) {
                LogUtil.d("STATE", state.name)
                if (state == AppBarStateChangeListener.State.EXPANDED) {
                    //展开状态
                    ll_statusBar2.visibility = View.GONE
                } else if (state == AppBarStateChangeListener.State.COLLAPSED) {
                    //折叠状态
                    ll_statusBar2.visibility = View.VISIBLE
                } else {
                    //中间状态
                }
            }
        })
        setDatas()
    }

    fun selectFragmentIndex(index : Int){
        viewpager.currentItem = index
        when(index){
            0->{
                rlCommentTab.isSelected = true
                rlForwardTab.isSelected = false
                rlAppraiseTab.isSelected = false
            }
            1->{
                rlCommentTab.isSelected = false
                rlForwardTab.isSelected = true
                rlAppraiseTab.isSelected = false
            }
            2->{
                rlCommentTab.isSelected = false
                rlForwardTab.isSelected = false
                rlAppraiseTab.isSelected = true
            }
        }
    }

    private fun setDatas() {

        val item = Microblog()
        item.isFocus = 1
        item.praiseNum = 1232
        item.commentNum = 32
        item.repeatNum = 17552
        item.timestamp = "2017-12-10 12:12:32"
        item.content = "第三方发是2017-阿萨飒飒的啊实打实大师的-10手动阀 12:第三方手动阀12:32手动阀手动阀"
        val user = User()
        user.name = "张小凡"
        user.imagePath = HomeFragment.imgs[Random().nextInt(HomeFragment.imgs.size)]
        item.user = user

        val imgs = ArrayList<String>()
        imgs.add(HomeFragment.imgs[Random().nextInt(HomeFragment.imgs.size)])
        imgs.add(HomeFragment.imgs[Random().nextInt(HomeFragment.imgs.size)])
        imgs.add(HomeFragment.imgs[Random().nextInt(HomeFragment.imgs.size)])
        item.images = imgs

        if(item.isFocus == 1){
            tvItemAttention.visibility = View.GONE
        } else {
            tvItemAttention.visibility = View.VISIBLE
        }
        var name = ""
        val timestamp = item.timestamp
        tvItemTime.text = timestamp
        var avatar = ""
        item.user?.let {
            it.name?.let {  name = it }
            it.imagePath?.let { avatar = it }
        }
        GlideImgManager.load(this, avatar, ivItemAvatar)

        var datas = ArrayList<String>()

        item.images?.let { datas = it }
        tvItemContent.text = item.content
        val spanCount = if (datas.size == 1) { 1 } else { 3 }
        ivItemOneImage?.visibility = View.GONE
        recycler_post_img.visibility = View.VISIBLE
        recycler_post_img.layoutManager = GridLayoutManager(this, spanCount)
        val adapter = PostImgAdapter(this, datas)
        adapter.setOnRecyclerViewItemClickListener { _, position ->
            switchToActivity(BigImageActivity::class.java, BigImageActivity.ARG_IMAGES to datas, BigImageActivity.ARG_POSITION to position)
        }
        recycler_post_img.adapter = adapter
    }

}
