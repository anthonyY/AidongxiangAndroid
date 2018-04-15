package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.audio.AudioListFragment
import com.aidongxiang.app.ui.video.VideoListFragment
import com.aidongxiang.app.ui.video.VideoListFragment.Companion.TYPE_COLLECT
import kotlinx.android.synthetic.main.activity_mine_tablelayout.*
import kotlinx.android.synthetic.main.layout_title_bar_with_right_text.*

/**
 * 我的收藏
 * @author Anthony
 * createTime 2017-12-17
 */
@ContentView(R.layout.activity_mine_tablelayout)
class MyCollectionActivity : BaseKtActivity() {

    var mPagerAdapter : SimpleFragmentPagerAdapter?= null
    var isEdit = false
    lateinit var videoListFragment : VideoListFragment
    lateinit var audioListFragment : AudioListFragment

    override fun init(savedInstanceState: Bundle?) {

        setTitle("我的收藏")
        btn_title_confirm.text = "编辑"
        mPagerAdapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)

        videoListFragment = VideoListFragment.newInstance(TYPE_COLLECT, -1)
        audioListFragment = AudioListFragment.newInstance(TYPE_COLLECT, -1)
        mPagerAdapter?.addFragment(videoListFragment, "视频")
        mPagerAdapter?.addFragment(audioListFragment, "音频")


        viewpager.adapter = mPagerAdapter
        tablayout.setupWithViewPager(viewpager)
        tablayout.tabMode = TabLayout.MODE_FIXED

        btn_title_confirm.setOnClickListener {
            isEdit = !isEdit
            if(isEdit){
                btn_title_confirm.text = "编辑"
            } else {
                btn_title_confirm.text = "完成"
            }
            if(viewpager.currentItem == 0){
                videoListFragment.setIsEdit(isEdit)
            } else {
                audioListFragment.setIsEdit(isEdit)
            }
        }
    }
}
