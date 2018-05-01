package com.aidongxiang.app.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.support.multidex.MultiDex
import android.support.v4.app.FragmentActivity
import com.aidongxiang.app.BuildConfig
import com.aidongxiang.app.R
import com.aiitec.openapi.constant.CommonKey
import com.aiitec.openapi.db.AIIDBManager
import com.aiitec.openapi.json.JSON
import com.aiitec.openapi.json.enums.CombinationType
import com.aiitec.openapi.net.AIIRequest
import com.aiitec.openapi.utils.AiiUtil
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.PacketUtil
import com.mabeijianxi.smallvideorecord2.DeviceUtils
import com.mabeijianxi.smallvideorecord2.JianXiCamera
import com.meituan.android.walle.WalleChannelReader
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.IUmengRegisterCallback
import com.umeng.message.PushAgent
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors



/**
 *
 * @author Anthony
 * createTime 2017/11/11.
 * @version 1.0
 */
class App : Application (){
    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
        lateinit var app: App
        lateinit var aiiRequest: AIIRequest
        lateinit var aiidbManager: AIIDBManager
    }


    private val activities = ArrayList<FragmentActivity>()
    lateinit var cachedThreadPool : ExecutorService

    override fun onCreate() {
        super.onCreate()
        app = this
        context = applicationContext
        JSON.combinationType = CombinationType.NORMAL
        LogUtil.showLog = BuildConfig.DEBUG
        aiiRequest = AIIRequest(this, Api.API)
        cachedThreadPool = Executors.newCachedThreadPool()

        aiidbManager = AIIDBManager(this)
        initUmeng()
        initSmallVideo(this)


    }

    private fun initUmeng() {
        val weixinId = resources.getString(R.string.weixinId)
        val weixinSecretKey = resources.getString(R.string.weixinSecret)
        val qqId = resources.getString(R.string.qqId)
        val qqSecretKey = resources.getString(R.string.qqSecretKey)
        val sinaId = resources.getString(R.string.sinaId)
        val sinaKey = resources.getString(R.string.sinaSecretKey)
        val sinaRedirectUrl = ""
//                resources.getString(R.string.sinaRedirectUrl)
        PlatformConfig.setWeixin(weixinId, weixinSecretKey)
        PlatformConfig.setQQZone(qqId, qqSecretKey)
        PlatformConfig.setSinaWeibo(sinaId, sinaKey, sinaRedirectUrl)

        val umengKay = resources.getString(R.string.umeng_key)
        val pushSecret = resources.getString(R.string.umeng_push_secret)
        val channel = WalleChannelReader.getChannel(this.applicationContext, "aidongxiang")
        val deviceType = 1
        UMConfigure.init(this, umengKay, channel, deviceType, pushSecret)

        UMShareAPI.get(this)

        val mPushAgent = PushAgent.getInstance(this)
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(object: IUmengRegisterCallback {
            override fun onSuccess(deviceToken: String?) {
                LogUtil.i("友盟注册成功 $deviceToken")
                AiiUtil.putString(applicationContext, CommonKey.KEY_DEVICETOKEN, deviceToken)
                PacketUtil.session_id = null
            }

            override fun onFailure(msg: String?, msg2: String?) {
                LogUtil.i("友盟注册失败 $msg   $msg2")
            }

        })
    }

    override fun attachBaseContext(base : Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
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
                activity.finish()
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
//        context.filesDir.absolutePath + "/video"
        // 设置拍摄视频缓存路径
        val dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        var path = ""
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                path = dcim.absolutePath + "/aidongxiang/"

            } else {
                path = dcim.path.replace("/sdcard/", "/sdcard-ext/") + "/aidongxiang/"
            }
        } else {
            path = dcim.absolutePath + "/aidongxiang/"
        }
        Constants.VIDEOS_DIR = path
        val dirFile = File(Constants.VIDEOS_DIR)
        if(!dirFile.exists()){
            if(!dirFile.parentFile.exists()){
                dirFile.parentFile.mkdir()
            }
            dirFile.mkdir()
        }
        JianXiCamera.setVideoCachePath(path)
        JianXiCamera.initialize(false, null)
    }

}