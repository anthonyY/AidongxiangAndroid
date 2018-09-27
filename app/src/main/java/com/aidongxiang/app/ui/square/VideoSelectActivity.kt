package com.aidongxiang.app.ui.square

import android.app.Activity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.GridLayoutManager
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.VideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.model.Video
import com.aidongxiang.app.utils.GridSpacingItemDecoration
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.ScreenUtils
import kotlinx.android.synthetic.main.activity_video_select.*
import java.io.File


/**
 *
 * 选择视频类
 * @author Anthony
 * createTime 2017/11/11.
 * @version 1.0
 */
@ContentView(R.layout.activity_video_select)
class VideoSelectActivity : BaseKtActivity()/*, LoaderManager.LoaderCallbacks<Cursor>*/{

    companion object {
        val ARG_PATH = "path"
        val ARG_THUMB_PATH = "thumbPath"
    }
    val datas = ArrayList<Video>()
    lateinit var adapter : VideoAdapter

    val proj = arrayOf( MediaStore.Video.Thumbnails._ID
            , MediaStore.Video.Thumbnails.DATA
            ,MediaStore.Video.Media.DURATION
            ,MediaStore.Video.Media.SIZE
            ,MediaStore.Video.Media.DISPLAY_NAME
            ,MediaStore.Video.Media.DATE_MODIFIED)
    
//    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
//
//        return CursorLoader(
//                this,
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                proj,
//                MediaStore.Video.Media.MIME_TYPE + "=?",
//                arrayOf("video/mp4"),
//                MediaStore.Images.Media.DATE_MODIFIED + " DESC"
//        )
//
//    }
//
//    override fun onLoaderReset(loader: Loader<Cursor>?) {
//        datas.clear()
//        adapter?.notifyDataSetChanged()
//    }
//
//    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
//        datas.clear()
//        if(data!=null){
//            while (data.moveToNext()) {
//                // 获取视频的路径
//                val videoId = data.getLong(data.getColumnIndex(MediaStore.Video.Media._ID))
//                val path = data.getString(data.getColumnIndex(MediaStore.Video.Media.DATA))
//                val duration = data.getLong(data.getColumnIndex(MediaStore.Video.Media.DURATION))
//                var size = data.getLong(data.getColumnIndex(MediaStore.Video.Media.SIZE))/1024 //单位kb
//                if(size<0){
//                    //某些设备获取size<0，直接计算
//                    LogUtil.e("dml","this video size < 0 " + path)
//                    size = File(path).length()/1024
//                }
//                val displayName = data.getString(data.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
////                val modifyTime = data.getLong(data.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))//暂未用到
//
//                //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
//                MediaStore.Video.Thumbnails.getThumbnail(contentResolver, videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null)
//                val projection = arrayOf( MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA)
//                val cursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
//                        , projection
//                        , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
//                        , arrayOf(videoId.toString())
//                        , null)
//                var thumbPath = ""
//                while (cursor.moveToNext()){
//                    thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
//                }
//                cursor.close();
//                // 获取该视频的父路径名
//                val dirPath = File(path).parentFile.absolutePath
//
//                val video = Video()
//                video.id = videoId
//                video.name = displayName
//                video.path = path
//                video.dirPath = dirPath
//                video.duration = duration
//                video.thumbPath = thumbPath
//                video.size = size
//
//                datas.add(video)
//            }
//            data.close()
//        }
//        adapter?.update()
//    }




    override fun init(savedInstanceState: Bundle?) {

        recycler_video.layoutManager = GridLayoutManager(this, 3)
        recycler_video.addItemDecoration(GridSpacingItemDecoration(3, ScreenUtils.dip2px(applicationContext, 10f), true))
        adapter = VideoAdapter(this, datas)
        recycler_video.adapter = adapter
        adapter.setOnRecyclerViewItemClickListener { v, position ->

            intent.putExtra(ARG_PATH, datas[position].path)
            intent.putExtra(ARG_THUMB_PATH, datas[position].thumbPath)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }


    }

    override fun onStart() {
        super.onStart()
//        supportLoaderManager.initLoader(0, null, this)
        loadVideoDatas()
    }

    fun loadVideoDatas(){
        progressDialogShow()
        App.app.cachedThreadPool.execute {
            val cursor = contentResolver.query( MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj, MediaStore.Video.Media.MIME_TYPE + "=?", arrayOf("video/mp4"), MediaStore.Images.Media.DATE_MODIFIED + " DESC")
            val videos = ArrayList<Video>()
            if(cursor!=null){
                while (cursor.moveToNext()) {
                    // 获取视频的路径
                    val videoId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                    var size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE))/1024 //单位kb
                    if(size<0){
                        //某些设备获取size<0，直接计算
                        LogUtil.e("dml","this video size < 0 " + path)
                        size = File(path).length()/1024
                    }
                    val displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
//                val modifyTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED))//暂未用到

                    //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                    MediaStore.Video.Thumbnails.getThumbnail(contentResolver, videoId, MediaStore.Video.Thumbnails.MICRO_KIND, null)
                    val projection = arrayOf( MediaStore.Video.Thumbnails._ID, MediaStore.Video.Thumbnails.DATA)
                    val cursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI
                            , projection
                            , MediaStore.Video.Thumbnails.VIDEO_ID + "=?"
                            , arrayOf(videoId.toString())
                            , null)
                    var thumbPath = ""
                    while (cursor.moveToNext()){
                        thumbPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
                    }
                    cursor.close();
                    // 获取该视频的父路径名
                    val dirPath = File(path).parentFile.absolutePath

                    val video = Video()
                    video.id = videoId
                    video.name = displayName
                    video.path = path
                    video.dirPath = dirPath
                    video.duration = duration
                    video.thumbPath = thumbPath
                    video.size = size

                    videos.add(video)
                }
                cursor.close()
            }
            runOnUiThread {
                datas.clear()
                datas.addAll(videos)
                adapter.update()
                progressDialogDismiss()
            }
        }
    }


}