package com.aidongxiang.app.base

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.support.v4.app.FragmentActivity
import com.aidongxiang.app.BuildConfig
import com.aiitec.openapi.net.AIIRequest
import com.aiitec.openapi.utils.LogUtil
import com.mabeijianxi.smallvideorecord2.DeviceUtils
import com.mabeijianxi.smallvideorecord2.JianXiCamera
import java.util.ArrayList

/**
 *
 * @author Anthony
 * createTime 2017/11/11.
 * @version 1.0
 */
class App : Application (){
    companion object {
        var app: App? = null
        var context: Context? = null
        var aiiRequest: AIIRequest? = null
    }
    private val activities = ArrayList<FragmentActivity>()


    override fun onCreate() {
        super.onCreate()
        App.app = this
        App.context = applicationContext
        LogUtil.showLog = BuildConfig.DEBUG
        aiiRequest = AIIRequest(this, Api.API)

        initSmallVideo(this)
    }

    /**
     * 关闭所有页面
     *
     * @param instance activity实例对象
     */
    fun removeInstance(instance: FragmentActivity) {
        activities.remove(instance)
    }

    /**
     * 把所有页面添加到列表统一管理
     *
     * @param instance activity实例对象
     */
    fun addInstance(instance: FragmentActivity) {
        activities.add(instance)
    }

    /**
     * 退出 关闭所有页面并杀死所有线程
     */
    fun exit() {
        try {
            for (activity in activities) {
                activity?.finish()
            }
        } catch (e: Exception) {
        } finally {
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(0)
        }
        // AiiQueryQueue.getInstance().destroy();
    }


    /**
     * 退出 关闭所有页面 不杀死后台线程
     */
    fun closeAllActivity() {
        try {
            for (activity in activities) {
                activity?.finish()
            }
        } catch (e: Exception) {
        }

    }

    /**
     * 模拟触摸home键
     */
    fun touchHome() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    /**
     * 初始化短视频录制
     */
    fun initSmallVideo(context: Context) {
        //设置视频缩略图路径
        Constants.VIDEOS_DIR = Companion.context!!.filesDir.absolutePath + "/video"
        // 设置拍摄视频缓存路径
        val dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath(dcim.absolutePath + "/mabeijianxi/")
            } else {
                JianXiCamera.setVideoCachePath(dcim.path.replace("/sdcard/",
                        "/sdcard-ext/") + "/mabeijianxi/")
            }
        } else {
            JianXiCamera.setVideoCachePath(dcim.absolutePath + "/mabeijianxi/")
        }
        JianXiCamera.initialize(false, null)
    }

}