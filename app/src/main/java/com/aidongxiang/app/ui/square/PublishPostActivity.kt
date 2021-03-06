package com.aidongxiang.app.ui.square

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
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
import com.aidongxiang.app.ui.video.VideoPlayerActivity.Companion.ARG_PATH
import com.aidongxiang.app.utils.*
import com.aidongxiang.app.widgets.ItemDialog
import com.aidongxiang.business.model.Microblog
import com.aidongxiang.business.request.MicroblogSubmitRequestQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.FileListResponseQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.UploadImageRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import com.mabeijianxi.smallvideorecord2.LocalMediaCompress
import com.mabeijianxi.smallvideorecord2.model.AutoVBRMode
import com.mabeijianxi.smallvideorecord2.model.LocalMediaConfig
import kotlinx.android.synthetic.main.activity_publish_post.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


/**
 * 发帖（侗言）类
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
    lateinit var permissionUtils : PermissionsUtils

    companion object {
        val REQUEST_IMAGE = 1
        val REQUEST_RECORD_VIDEO = 2
        val REQUEST_SELECT_VIDEO = 3
        val REQUEST_RECORD_VIDEO_BY_SYSTEM = 4
        val REQUEST_ADDRESS = 5
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
        tv_publish_address.setOnClickListener { switchToActivityForResult(AddressSelectActivity::class.java, REQUEST_ADDRESS) }
    }

    private fun initDialog() {

        itemDialog = ItemDialog(this)
        itemDialog.setItems(arrayListOf("拍摄小视频", "选择小视频"))
        itemDialog.setOnItemClickListener { _, position ->
            when (position) {
                0 -> {
                    permissionUtils.requestPermissions(REQUEST_RECORD_VIDEO, android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO)
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
                LogUtil.e("onSoftKeyboardOpened("+keyboardHeightInPx+")  payoutParams.height:"+payoutParams.height)
                tempaView.layoutParams = payoutParams
            }

            override fun onSoftKeyboardClosed() {
                LogUtil.e("onSoftKeyboardClosed() ")
                val payoutParams = tempaView.layoutParams
                payoutParams.height = 0
                tempaView.layoutParams = payoutParams

            }

        })
    }

    private fun setListener() {
        permissionUtils = PermissionsUtils(this)
        permissionUtils.setOnPermissionsListener(object : PermissionsUtils.OnPermissionsListener{
            override fun onPermissionsSuccess(requestCode: Int) {
                if(requestCode == REQUEST_RECORD_VIDEO){
                    recordVideo()
                } else if(requestCode == REQUEST_IMAGE){
                    val images2 = ArrayList<String>()
                    images.filterTo(images2) { it != "add" }
                    switchToActivityForResult(ImageSelectActivity::class.java, REQUEST_IMAGE, ImageSelectActivity.EXTRA_RESULT to images2)
                }

            }

            override fun onPermissionsFailure(requestCode: Int) {
                if(requestCode == REQUEST_RECORD_VIDEO){
                    toast("您拒绝了权限，不能录制视频，请在系统设置中赋予拍照和录制音频权限")
                } else if(requestCode == REQUEST_RECORD_VIDEO){
                    val images2 = ArrayList<String>()
                    images.filterTo(images2) { it != "add" }
                    switchToActivityForResult(ImageSelectActivity::class.java, REQUEST_IMAGE, ImageSelectActivity.EXTRA_RESULT to images2,  ImageSelectActivity.EXTRA_SHOW_CAMERA to false)

                }
            }
        })
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
                    requestUploadFile(3)
//                CosManager().upload(File(videoUri), object : CosManager.OnUploadListener {
//                    override fun onStart() {
//                        progressDialogShow()
//                    }
//
//                    override fun onFinish() {
//                        progressDialogDismiss()
//                    }
//
//                    override fun onSuccess(accessUrl: String?) {
//                        requestMicroblogSubmit(accessUrl)
//                    }
//
//                    override fun onFailure(e: Exception?) {
//                        LogUtil.e("上传失败"+e?.message)
//                    }
//
//                    override fun onUploadProgress(progress: Long, max: Long) {
//                    }
//
//                })
            } else if (images.size > 1) {
                //有图片，先上传图片
                requestUploadFile(3)
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
                permissionUtils.requestPermissions(REQUEST_IMAGE, android.Manifest.permission.CAMERA)

            }
        }
        rl_publish_video.setOnClickListener {
            videoUri?.let { switchToActivity(VideoPlayerActivity::class.java, ARG_PATH to it) }
        }
        iv_publish_image.setOnClickListener {
            if (!TextUtils.isEmpty(videoUri)) {
                toast("不能同时发布图片和视频")
                return@setOnClickListener
            }
            permissionUtils.requestPermissions(REQUEST_IMAGE, android.Manifest.permission.CAMERA)
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
//        LogUtil.e("MediaRecorderBase.mSupportedPreviewWidth:" + MediaRecorderBase.mSupportedPreviewWidth)
//        val config = MediaRecorderConfig.Buidler()
////                .doH264Compress(AutoVBRMode().setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
////                )
////                .setMediaBitrateConfig(AutoVBRMode()
////                        //                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
////                )
//
//
//                .smallVideoWidth(720 )
////                .smallVideoHeight(400)
//                .recordTimeMax(10 * 1000)
//                .maxFrameRate(20)
//                .captureThumbnailsTime(3)
//                .recordTimeMin((3 * 1000))
////                .videoBitrate(6000000)
//                .fullScreen(true)
//                .build()
//        MediaRecorderActivity.goSmallVideoRecorder(this, config, REQUEST_RECORD_VIDEO)
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        // 录制视频最大时长15s
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10)
        startActivityForResult(intent, REQUEST_RECORD_VIDEO_BY_SYSTEM)
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
                if(path.contains(Constants.VIDEOS_DIR!!)){
                    //如果是这个文件目录下的，就是本app拍出来的，是已经压缩过的，无需再压缩
                    videoUri = path
                } else {
                    compressVideo(path)
                }
                GlideImgManager.loadFile(this, thumbPath, iv_video_thumb)
                rl_publish_video.visibility = View.VISIBLE
                recycler_image.visibility = View.GONE
            }

        } else if (requestCode == REQUEST_IMAGE) {
            // 选取图片返回
            if (data != null) {
                val paths = data.getStringArrayListExtra(ImageSelectActivity.EXTRA_RESULT)
                for((index, path) in paths.withIndex()){
                    val degree = ImageUtils.readPictureDegree(path)
                    if(degree != 0){
                        val rotateBitmap = ImageUtils.toturn(BitmapFactory.decodeFile(path), degree)
                        val newPath = ImageUtils.saveBitmap(this@PublishPostActivity, rotateBitmap)
                        if(newPath != null){
                            paths[index] = newPath
                        }
                    }
                }

                if (paths != null) {
//                    if (paths.size == 0) {
//                        recycler_image.layoutManager = GridLayoutManager(this, 1)
//                    } else {
//                        recycler_image.layoutManager = GridLayoutManager(this, 3)
//                    }
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
            val videoScreenshot = data.getStringExtra(MediaRecorderActivity.VIDEO_SCREENSHOT)
            if (!TextUtils.isEmpty(videoUri) && !TextUtils.isEmpty(videoScreenshot)) {
                GlideImgManager.loadFile(this, videoScreenshot, iv_video_thumb)
                rl_publish_video.visibility = View.VISIBLE
                recycler_image.visibility = View.GONE
            }
        } else if (requestCode == REQUEST_RECORD_VIDEO_BY_SYSTEM && resultCode == Activity.RESULT_OK) {
            //录制视频返回
            if (data == null) {
                return
            }

            val uri = data.data
            val cursor = this.contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID))
                // 视频路径
                videoUri = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                // ThumbnailUtils类2.2以上可用  Todo 获取视频缩略图
//                val bitmap = ThumbnailUtils.createVideoThumbnail(videoUri, MediaStore.Images.Thumbnails.MICRO_KIND)
                Utils.setVideoThumbnailForImageView(videoUri) { thumb ->
                    if (!supportFragmentManager.isDestroyed) {
                        GlideImgManager.loadFile(this@PublishPostActivity, thumb, iv_video_thumb)
                    }
                }
//                // 图片Bitmap转file
//                File file = CommonUtils.compressImage(bitmap);
//                // 保存成功后插入到图库，其中的file是保存成功后的图片path。这里只是插入单张图片
//                // 通过发送广播将视频和图片插入相册
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                compressVideo(videoUri!!)
                if (!TextUtils.isEmpty(videoUri)) {

                    rl_publish_video.visibility = View.VISIBLE
                    recycler_image.visibility = View.GONE
                }
                cursor.close()
            }

        } else if(requestCode == REQUEST_ADDRESS && resultCode == Activity.RESULT_OK){
            address = data?.getStringExtra("address")
            tv_publish_address.text = address
        }
    }


    private fun requestMicroblogSubmit() {
        if(isCompressing){
            progressDialogShow()
            //如果正在压缩中，就等会儿在提交
            progressDialog?.setOnDismissListener {
                //如果点了返回，则不能提交
                isNeedSubmit = false
            }
            isNeedSubmit = true
            return
        }
        val query = MicroblogSubmitRequestQuery()
        val microblog = Microblog()
        microblog.content = et_publish_content.text.toString()
        if (imageIds.size > 0) {
            microblog.imageIds = imageIds
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
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
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
        if(!TextUtils.isEmpty(videoUri)){
            val file = File(videoUri)
            map.put(file.name, file)
        } else {
            images.filter { !TextUtils.isEmpty(it) && it != "add" }
                    .map { ImageUtils.getCompressFile(this, it) }
                    .map { File(it) }
                    .forEach { map.put(it.name, it) }
//
//            images.filter { !TextUtils.isEmpty(it) && it != "add" }
//                    .map { File(it) }
//                    .forEach { map.put(it.name, it) }
        }
        App.aiiRequest.sendFiles(query, map, object : AIIResponse<FileListResponseQuery>(this, progressDialog) {

            override fun onSuccess(response: FileListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let {
                    if(!TextUtils.isEmpty(videoUri)){
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


    var isCompressing = false
    var isNeedSubmit = false
    /**
     * 压缩视频
     */
    fun compressVideo(path: String) {
        val buidler = LocalMediaConfig.Buidler()
        val config = buidler
                .setVideoPath(path)
                .captureThumbnailsTime(3)
                .doH264Compress(AutoVBRMode())
                .setFramerate(15)
                .setScale(1.0f)
                .build()
//        progressDialogShow()

        isCompressing = true
        App.app.cachedThreadPool.execute {
            val onlyCompressOverBean = LocalMediaCompress(config).startCompress()
            //压缩完成，亲测 原来拍摄12M压缩至5M左右
            if (onlyCompressOverBean.isSucceed) {
                //压缩完成后，删除原始视频，因为太占空间了
//                val originalFile = File(path)
//                originalFile.delete()
                videoUri = onlyCompressOverBean.videoPath
            } else {
                videoUri = path
            }
//            runOnUiThread { progressDialogDismiss() }
            LogUtil.i("是否压缩成功" + onlyCompressOverBean.isSucceed + "   压缩前" + path + "  \n压缩后" + videoUri)
            isCompressing = false
//            压缩完了判断是否点击了提交按钮，如果点击了就直接提交
            if(isNeedSubmit){
                requestUploadFile(3)

            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}


