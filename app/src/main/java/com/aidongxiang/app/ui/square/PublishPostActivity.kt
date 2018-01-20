package com.aidongxiang.app.ui.square

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.adapter.PublishImageAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.video.VideoPlayerActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.SoftKeyboardStateHelper
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.app.widgets.ItemDialog
import com.aiitec.openapi.utils.LogUtil
import com.mabeijianxi.smallvideorecord2.MediaRecorderBase
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig
import kotlinx.android.synthetic.main.activity_publish_post.*


/**
 * 发帖（微博）类
 * @author Anthony
 * createTime 2017-11-11
 */

@ContentView(R.layout.activity_publish_post)
class PublishPostActivity : BaseKtActivity() {

    var videoUri: String? = null
    lateinit var adapter: PublishImageAdapter
    var datas = ArrayList<String>()
    lateinit var itemDialog : ItemDialog
    lateinit var softKeyboardStateHelper : SoftKeyboardStateHelper
    companion object {
        val REQUEST_IMAGE = 1
        val REQUEST_RECORD_VIDEO = 2
        val REQUEST_SELECT_VIDEO = 3
    }

    override fun init(savedInstanceState: Bundle?) {

        itemDialog = ItemDialog(this)
        itemDialog.setItems(arrayListOf("拍摄小视频", "选择小视频"))
        itemDialog.setOnItemClickListener{ item, position ->
            when(position){
                0->{
                    recordVideo()
                }
                1->{
                    chooseVideo()
                }
            }
        }
        datas.add("add")
        adapter = PublishImageAdapter(this, datas)
        recycler_image.layoutManager = GridLayoutManager(this, 3)
        recycler_image.adapter = adapter

        adapter.setOnRecyclerViewItemClickListener { _, position ->
            if(position == datas.size-1){
                if(videoUri != null){
                    toast("不能同时发布图片和视频")
                    return@setOnRecyclerViewItemClickListener
                }
                switchToActivityForResult(ImageSelectActivity::class.java, REQUEST_IMAGE)
            }
        }
        adapter.setOnViewInItemClickListener(CommonRecyclerViewAdapter.OnViewInItemClickListener{
            v, position ->
            if(v.id == R.id.iv_item_delete){
                datas.removeAt(position)
                adapter.update()
            }
        }, R.id.iv_item_delete)
        rl_publish_video.setOnClickListener {
            if(videoUri != null){
                switchToActivity(VideoPlayerActivity::class.java, "path" to videoUri!!)
            }
        }
        iv_publish_image.setOnClickListener {
            if(!TextUtils.isEmpty(videoUri)){
                toast("不能同时发布图片和视频")
                return@setOnClickListener
            }
            switchToActivityForResult(ImageSelectActivity::class.java, REQUEST_IMAGE)
        }
        iv_publish_video.setOnClickListener {
            if(datas.size > 1){
                toast("不能同时发布图片和视频")
                return@setOnClickListener
            }
            itemDialog.show()
        }

        softKeyboardStateHelper = SoftKeyboardStateHelper(window.decorView)
        softKeyboardStateHelper.addSoftKeyboardStateListener(object : SoftKeyboardStateHelper.SoftKeyboardStateListener {
            override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
                val payoutParams = tempaView.layoutParams
                payoutParams.height = keyboardHeightInPx-StatusBarUtil.getStatusBarHeight(this@PublishPostActivity)
                tempaView.layoutParams = payoutParams
            }

            override fun onSoftKeyboardClosed() {
                val payoutParams = tempaView.layoutParams
                payoutParams.height = 0
                tempaView.layoutParams = payoutParams

            }

        })

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        if (intent.hasExtra(MediaRecorderActivity.VIDEO_URI) && intent.hasExtra(MediaRecorderActivity.VIDEO_SCREENSHOT)) {
            videoUri = intent.getStringExtra(MediaRecorderActivity.VIDEO_URI)
            val videoScreenshot = intent.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT)
            if (!TextUtils.isEmpty(videoUri) && !TextUtils.isEmpty(videoScreenshot)) {
                GlideImgManager.loadFile(this, videoScreenshot, iv_video_thumb)
                rl_publish_video.visibility = View.VISIBLE
            }
        }
    }


    private fun recordVideo() {
        if(datas.size > 1){
            toast("不能同时发布图片和视频")
            return
        }
        LogUtil.e("MediaRecorderBase.mSupportedPreviewWidth:"+MediaRecorderBase.mSupportedPreviewWidth)
        val config = MediaRecorderConfig.Buidler()
//                .doH264Compress(AutoVBRMode()
//                        //                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
//                )
//                .setMediaBitrateConfig(AutoVBRMode()
//                        //                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
//                )


//                .smallVideoWidth(720 )
//                .smallVideoHeight(720)
                .recordTimeMax(10 * 1000)
                .maxFrameRate(20)
                .captureThumbnailsTime(3)
                .recordTimeMin((3 * 1000))
//                .videoBitrate(6000000)
//                .fullScreen(true)
                .build()
        MediaRecorderActivity.goSmallVideoRecorder(this, config, REQUEST_RECORD_VIDEO)
    }
    private fun chooseVideo() {
        if(datas.size > 1){
            toast("不能同时发布图片和视频")
            return
        }
        switchToActivityForResult(VideoSelectActivity::class.java, REQUEST_SELECT_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 选取视频返回
        if (requestCode == REQUEST_SELECT_VIDEO && resultCode == Activity.RESULT_OK) {

                if(data == null){
                    return
                }
                val path  = data.getStringExtra(VideoSelectActivity.ARG_PATH)
                val thumbPath = data.getStringExtra(VideoSelectActivity.ARG_THUMB_PATH)
                if(path != null){
                    videoUri = path
                    GlideImgManager.loadFile(this, thumbPath, iv_video_thumb)
                    rl_publish_video.visibility = View.VISIBLE
                    recycler_image.visibility = View.GONE
                }

        } else if (requestCode == REQUEST_IMAGE) {
            // 选取图片返回
            if (data != null) {
                val paths = data.getStringArrayListExtra(ImageSelectActivity.EXTRA_RESULT)
                if (paths != null) {
//                    if (paths.size == 0) {
//                        recycler_image.layoutManager = GridLayoutManager(this, 1)
//                    } else {
//                        recycler_image.layoutManager = GridLayoutManager(this, 3)
//                    }
                    datas.clear()
                    datas.addAll(paths)
                    datas.add("add")//添加按钮，也是一个item
                    adapter.update()
                }
                rl_publish_video.visibility = View.GONE
                recycler_image.visibility = View.VISIBLE
            }
        } else if(requestCode == REQUEST_RECORD_VIDEO && resultCode == Activity.RESULT_OK){
            //录制视频返回
            if(data == null){
                return
            }
            videoUri = data.getStringExtra(MediaRecorderActivity.VIDEO_URI)
            val videoScreenshot = data.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT)
            if (!TextUtils.isEmpty(videoUri) && !TextUtils.isEmpty(videoScreenshot)) {
                GlideImgManager.loadFile(this, videoScreenshot, iv_video_thumb)
                rl_publish_video.visibility = View.VISIBLE
                recycler_image.visibility = View.GONE
            }
        }
    }
}


