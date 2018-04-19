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
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.event.RefreshMicrobolgEvent
import com.aidongxiang.app.ui.video.VideoPlayerActivity
import com.aidongxiang.app.utils.CosManager
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.SoftKeyboardStateHelper
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.app.widgets.ItemDialog
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.request.MicroblogSubmitRequestQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.FileListResponseQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.UploadImageRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import com.mabeijianxi.smallvideorecord2.MediaRecorderBase
import com.mabeijianxi.smallvideorecord2.model.MediaRecorderConfig
import kotlinx.android.synthetic.main.activity_publish_post.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.lang.Exception
import java.util.*


/**
 * 发帖（微博）类
 * @author Anthony
 * createTime 2017-11-11
 */

@ContentView(R.layout.activity_publish_post)
class PublishPostActivity : BaseKtActivity() {

    var videoUri: String? = null
    lateinit var adapter: PublishImageAdapter
    var images = ArrayList<String>()
    var imageIds = ArrayList<Long>()
    var parentId: Long = -1
    var videoId: Long = -1
    var latitude: Double = -1.0
    var longitude: Double = -1.0
    var regionId = -1
    var address: String? = null
    lateinit var itemDialog: ItemDialog
    lateinit var softKeyboardStateHelper: SoftKeyboardStateHelper

    companion object {
        val REQUEST_IMAGE = 1
        val REQUEST_RECORD_VIDEO = 2
        val REQUEST_SELECT_VIDEO = 3
    }

    override fun init(savedInstanceState: Bundle?) {

        val microblog = bundle.getParcelable<Microblog>(Constants.ARG_MICROBLOG)
        if (microblog != null) {
            parentId = microblog.id
            llForward.visibility = View.VISIBLE
            GlideImgManager.load(this, microblog.user?.imagePath, ivForwardAvatar)
            tvForwardName.text = microblog.user?.nickName
            tvForwardContent.text = microblog.content
            recycler_image.visibility = View.GONE
            llSelectButton.visibility = View.GONE
        } else {
            llForward.visibility = View.GONE
        }

        images.add("add")
        adapter = PublishImageAdapter(this, images)
        recycler_image.layoutManager = GridLayoutManager(this, 3)
        recycler_image.adapter = adapter

        initDialog()
        initSoftKeyboard()
        setListener()

        Constants.location?.let {
            address = it.address
            regionId = it.adCode!!.toInt()
            latitude = it.latitude
            longitude = it.longitude
            tv_publish_address.text = address
        }
    }

    private fun initDialog() {

        itemDialog = ItemDialog(this)
        itemDialog.setItems(arrayListOf("拍摄小视频", "选择小视频"))
        itemDialog.setOnItemClickListener { _, position ->
            when (position) {
                0 -> {
                    recordVideo()
                }
                1 -> {
                    chooseVideo()
                }
            }
        }
    }

    private fun initSoftKeyboard() {

        softKeyboardStateHelper = SoftKeyboardStateHelper(window.decorView)
        softKeyboardStateHelper.addSoftKeyboardStateListener(object : SoftKeyboardStateHelper.SoftKeyboardStateListener {
            override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
                val payoutParams = tempaView.layoutParams
                payoutParams.height = keyboardHeightInPx - StatusBarUtil.getStatusBarHeight(this@PublishPostActivity)
                tempaView.layoutParams = payoutParams
            }

            override fun onSoftKeyboardClosed() {
                val payoutParams = tempaView.layoutParams
                payoutParams.height = 0
                tempaView.layoutParams = payoutParams

            }

        })
    }

    private fun setListener() {
        iv_publish_address_delete.setOnClickListener {
            setAddressData(null)
        }

        btn_publish.setOnClickListener {
            if (TextUtils.isEmpty(et_publish_content.text.toString()) && TextUtils.isEmpty(videoUri) && images.size <= 1) {
                //没有任何数据，images.size = 1 时 其实就是一个加号，所以也是没有数据
                //这三样只要有一样就可以发布
                return@setOnClickListener
            }
            if (!TextUtils.isEmpty(videoUri)) {
                //有视频，先上传视频
//                    requestUploadFile(2)
                CosManager().upload(File(videoUri), object : CosManager.OnUploadListener {
                    override fun onStart() {
                        progressDialogShow()
                    }

                    override fun onFinish() {
                        progressDialogDismiss()
                    }

                    override fun onSuccess(accessUrl: String?) {
                        requestMicroblogSubmit(accessUrl)
                    }

                    override fun onFailure(e: Exception?) {
                        LogUtil.e("上传失败"+e?.message)
                    }

                    override fun onUploadProgress(progress: Long, max: Long) {
                    }

                })
            } else if (images.size > 1) {
                //有图片，先上传图片
                requestUploadFile(1)
            } else {
                //没有图片，没有视频，只有文字
                requestMicroblogSubmit()
            }

        }
        adapter.setOnViewInItemClickListener(CommonRecyclerViewAdapter.OnViewInItemClickListener { v, position ->
            if (v.id == R.id.iv_item_delete) {
                images.removeAt(position)
                adapter.update()
            }
        }, R.id.iv_item_delete)
        adapter.setOnRecyclerViewItemClickListener { _, position ->
            if (position == images.size - 1) {
                if (videoUri != null) {
                    toast("不能同时发布图片和视频")
                    return@setOnRecyclerViewItemClickListener
                }
                switchToActivityForResult(ImageSelectActivity::class.java, REQUEST_IMAGE)
            }
        }
        rl_publish_video.setOnClickListener {
            if (videoUri != null) {
                switchToActivity(VideoPlayerActivity::class.java, "path" to videoUri!!)
            }
        }
        iv_publish_image.setOnClickListener {
            if (!TextUtils.isEmpty(videoUri)) {
                toast("不能同时发布图片和视频")
                return@setOnClickListener
            }
            switchToActivityForResult(ImageSelectActivity::class.java, REQUEST_IMAGE)
        }
        iv_publish_video.setOnClickListener {
            if (images.size > 1) {
                toast("不能同时发布图片和视频")
                return@setOnClickListener
            }
            itemDialog.show()
        }

    }

    private fun setAddressData(addr: String?) {
        if (TextUtils.isEmpty(addr)) {
            tv_publish_address.text = ""
            iv_publish_address_delete.visibility = View.GONE
        } else {
            tv_publish_address.text = addr
            iv_publish_address_delete.visibility = View.VISIBLE
        }
        address = addr
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
        if (images.size > 1) {
            toast("不能同时发布图片和视频")
            return
        }
        LogUtil.e("MediaRecorderBase.mSupportedPreviewWidth:" + MediaRecorderBase.mSupportedPreviewWidth)
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
        if (images.size > 1) {
            toast("不能同时发布图片和视频")
            return
        }
        switchToActivityForResult(VideoSelectActivity::class.java, REQUEST_SELECT_VIDEO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 选取视频返回
        if (requestCode == REQUEST_SELECT_VIDEO && resultCode == Activity.RESULT_OK) {

            if (data == null) {
                return
            }
            val path = data.getStringExtra(VideoSelectActivity.ARG_PATH)
            val thumbPath = data.getStringExtra(VideoSelectActivity.ARG_THUMB_PATH)
            if (path != null) {
                videoUri = path
                LogUtil.e("选择视频 videoUri:"+videoUri)
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
                    LogUtil.e("paths:"+paths)
                    images.clear()
                    images.addAll(paths)
                    if (paths.size < 9) {
                        images.add("add")//添加按钮，也是一个item
                    }

                    adapter.update()
                }
                rl_publish_video.visibility = View.GONE
                recycler_image.visibility = View.VISIBLE
            }
        } else if (requestCode == REQUEST_RECORD_VIDEO && resultCode == Activity.RESULT_OK) {
            //录制视频返回
            if (data == null) {
                return
            }
            videoUri = data.getStringExtra(MediaRecorderActivity.VIDEO_URI)
            LogUtil.e("录制视频 videoUri:"+videoUri)
            val videoScreenshot = data.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT)
            if (!TextUtils.isEmpty(videoUri) && !TextUtils.isEmpty(videoScreenshot)) {
                GlideImgManager.loadFile(this, videoScreenshot, iv_video_thumb)
                rl_publish_video.visibility = View.VISIBLE
                recycler_image.visibility = View.GONE
            }
        }
    }


    private fun requestMicroblogSubmit() {
        requestMicroblogSubmit(null)
    }

    private fun requestMicroblogSubmit(accessUrl: String?) {
        val query = MicroblogSubmitRequestQuery()
        val microblog = Microblog()
        microblog.content = et_publish_content.text.toString()
        if (imageIds.size > 0) {
            microblog.imageIds = imageIds
        }
        if (!TextUtils.isEmpty(accessUrl)) {
            microblog.accessUrl = accessUrl
        }
        microblog.videoId = videoId
        if (!TextUtils.isEmpty(address)) {
            microblog.address = address
            microblog.regionId = regionId
            microblog.longitude = longitude
            microblog.latitude = latitude
        }
        if (parentId > 0) {
            microblog.parentId = parentId
        }
        query.microblog = microblog
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)

                EventBus.getDefault().post(RefreshMicrobolgEvent())
                toast("发布成功")
                dismissDialog()
                finish()
            }
        })
    }

    private fun requestUploadFile(action: Int) {
        val query = UploadImageRequestQuery()
        query.action = AIIAction.valueOf(action)
        val map = LinkedHashMap<String, Any>()
        if (action == 2) {
            val file = File(videoUri)
            map.put(file.name, file)
        } else {
            images.filter { !TextUtils.isEmpty(it) && it != "add" }
                    .map { File(it) }
                    .forEach { map.put(it.name, it) }
        }
        App.aiiRequest.sendFiles(query, map, object : AIIResponse<FileListResponseQuery>(this) {

            override fun onSuccess(response: FileListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let {
                    if (index == 2) {
                        if (it.files.size > 0) {
                            videoId = it.files[0].id
                        }
                    } else {
                        imageIds = it.ids as ArrayList<Long>
                    }
                    requestMicroblogSubmit()
                }
            }
        }, action)
    }
}


