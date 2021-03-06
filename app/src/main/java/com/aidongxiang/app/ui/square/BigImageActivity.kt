package com.aidongxiang.app.ui.square

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.Api
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.utils.StatusBarUtil
import com.aiitec.openapi.net.ProgressResponseBody
import com.aiitec.openapi.utils.PacketUtil
import com.aiitec.openapi.utils.ToastUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_big_image.*
import java.io.File
import java.lang.Exception


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
            var path = datas[viewPager.currentItem]
            if(!path.startsWith("http")){
                path = Api.IMAGE_URL + path
            }

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
            App.aiiRequest.download(path, file, object : ProgressResponseBody.ProgressListener{
                override fun update(totalBytes: Long, currnet: Long, progress: Int) {

                }

                override fun onPreExecute(contentLength: Long) {
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




    inner class SamplePagerAdapter : PagerAdapter() {


        override fun getCount(): Int {
            return datas.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): View {
            val photoView = PhotoView(container.context)
            val imageLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
            photoView.layoutParams = imageLayoutParams
            photoView.setOnPhotoTapListener { _, _, _ -> finish() }
            var path = datas[position]
            if(!path.startsWith("http") && !path.startsWith("/storage") && !path.startsWith("/sdcard")){
                path = Api.IMAGE_URL+path
            }

//            LogUtil.e("path:"+path)
            val relativeLayout = RelativeLayout(this@BigImageActivity)
            relativeLayout.addView(photoView)

            val progressBar = ProgressBar(this@BigImageActivity)
            val progressLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            progressLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            progressBar.layoutParams = progressLayoutParams
            relativeLayout.addView(progressBar)

            val textview = TextView(this@BigImageActivity)
            val textviewLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            textviewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            textview.layoutParams = textviewLayoutParams
            textview.setTextColor(Color.WHITE)
            relativeLayout.addView(textview)

            container.addView(relativeLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            return relativeLayout

            Glide.with(this@BigImageActivity).load(path).placeholder(R.color.black3).listener(object: RequestListener<String, GlideDrawable > {
                override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                    textview.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                    ToastUtil.show(this@BigImageActivity, "图片加载失败")
                    textview.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    return false
                }
            }).into(photoView)
            return relativeLayout
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

    }
}
