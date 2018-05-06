package com.aidongxiang.app.ui.mine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.TabLayout
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.SimpleFragmentPagerAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aiitec.openapi.net.download.DownloadManager
import kotlinx.android.synthetic.main.activity_mine_tablelayout.*
import kotlinx.android.synthetic.main.layout_title_bar_with_right_text.*

/**
 * 我的下载
 * @author Anthony
 * createTime 2017-12-17
 */
@ContentView(R.layout.activity_mine_tablelayout)
class MyDownloadActivity : BaseKtActivity() {

    companion object {
        val ARG_POSITION = "position"
    }
    var mPagerAdapter : SimpleFragmentPagerAdapter?= null
    var isEdit = false
    lateinit var videoListFragment : DownloadVideoListFragment
    lateinit var audioListFragment : DownloadVideoListFragment
    lateinit var downloadReceiver : DownloadReceiver

    override fun init(savedInstanceState: Bundle?) {

        val position = bundle.getInt(ARG_POSITION, 0)
        setTitle("我的下载")
        mPagerAdapter = SimpleFragmentPagerAdapter(supportFragmentManager, this)

        videoListFragment = DownloadVideoListFragment.newInstance(2)
        audioListFragment = DownloadVideoListFragment.newInstance(1)
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
        viewpager.currentItem = position

        //这个需要在这里设置广播监听，如果在Fragment里设置，因为注册都是用activity来注册，那就会导致一个Fragment注册的有效，一个无效
        downloadReceiver = DownloadReceiver()
        val intentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_UPDATE)
        registerReceiver(downloadReceiver, intentFilter)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadReceiver)
    }

    inner class DownloadReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            intent?.let {
                videoListFragment.update(it)
                audioListFragment.update(it)
            }
        }
    }
}