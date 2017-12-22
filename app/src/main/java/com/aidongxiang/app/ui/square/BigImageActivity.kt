package com.aidongxiang.app.ui.square

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.support.v4.view.PagerAdapter
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.StatusBarUtil
import com.aiitec.openapi.net.ProgressResponseBody
import com.aiitec.openapi.utils.PacketUtil
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_big_image.*
import java.io.File

/**
 * 查看大图类
 * @author Anthony
 * createTime 2017-11-26
 * @version 1.0
 */
@ContentView(R.layout.activity_big_image)
class BigImageActivity : BaseKtActivity() {

    companion object {
        val ARG_IMAGES = "images"
        val ARG_POSITION = "position"
    }
    val datas = ArrayList<String>()


    override fun init(savedInstanceState: Bundle?) {
        StatusBarUtil.addStatusBarView(appBar, android.R.color.transparent)
        val tempDatas = bundle.getStringArrayList(ARG_IMAGES)
        val position = bundle.getInt(ARG_POSITION)
        if(tempDatas != null){
            datas.addAll(tempDatas)
        }
        viewPager.adapter = SamplePagerAdapter()
        if(position >= 0 && position < datas.size){
            viewPager.currentItem = position
        }
        floatingSave.setOnClickListener {
            val path = datas[viewPager.currentItem]
            val index = path.lastIndexOf("/")
            var fileName = ""
            fileName = if(index != -1){
                path.substring(index+1)
            } else { path }
            val cachePath = PacketUtil.getCacheDir(this)
            val dir = File(cachePath)
            if(!dir.exists()){
                if(!dir.parentFile.exists()){
                    dir.parentFile.mkdir()
                }
                dir.mkdir()
            }
            val file = File(cachePath, fileName)
            App.aiiRequest?.download(path, file, object : ProgressResponseBody.ProgressListener{
                override fun onPreExecute(contentLength: Long) {
                }

                override fun update(totalBytes: Long) {
                }

                override fun onSuccess(file: File?) {
                    //图片加入相册
                    MediaStore.Images.Media.insertImage(contentResolver, file?.absolutePath, fileName, "")
                    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)))
                    toast("保存成功")
                    progressDialogDismiss()
                }

                override fun onStart() {
                    progressDialogShow()
                }

                override fun onFailure() {
                    toast("下载失败")
                    progressDialogDismiss()
                }
            })
        }
    }




    internal inner class SamplePagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return datas.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            photoView.setOnPhotoTapListener { _, _, _ -> finish() }
            GlideImgManager.load(this@BigImageActivity, datas[position], photoView)
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            return photoView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View?, obj: Any?): Boolean {

            return view === obj
        }


    }
}
