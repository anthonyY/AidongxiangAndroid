package com.aidongxiang.app.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.aiitec.openapi.utils.LogUtil
import java.io.PrintWriter
import java.io.StringWriter

/**
 *
 * @author Anthony
 * createTime 2018/5/6.
 * @version 1.0
 */
class CrashHandler : Thread.UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类
    private var mDefaultHandler : Thread.UncaughtExceptionHandler ?= null
    //程序的Context对象
    private var mContext : Context ?= null;
    //用来存储设备信息和异常信息
    private val infos = HashMap<String, String>()

    companion object {
        var crashHandler : CrashHandler ?= null
        fun getInstance() : CrashHandler{
            if (crashHandler == null)
                crashHandler = CrashHandler()
            return crashHandler!!
        }
    }



    /**
     * 初始化
     *
     * @param context
     */
    fun init(context : Context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException( thread : Thread,  ex: Throwable) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler?.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(3000);
            } catch (e : InterruptedException) {
                LogUtil.e(e.toString())
            }
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex : Throwable?) : Boolean{
        if (ex == null) {
            return false
        }
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    fun collectDeviceInfo(ctx : Context?) {

        try {
            ctx?.let {
                val pm = it.packageManager
                val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
                if (pi != null) {
                    val versionName = if(pi.versionName == null){"null"} else {pi.versionName}
                    val versionCode = pi.versionCode.toString()
                    infos.put("versionName", versionName)
                    infos.put("versionCode", versionCode)
                }
            }

        } catch (e : PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val fields = Build::class.java.declaredFields;
        for (field in fields) {
        try {
            field.isAccessible = true
            infos.put(field.name, field.get(null).toString())
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private fun saveCrashInfo2File(ex : Throwable) : String? {

        val sb = StringBuffer()

        for ( entry in infos.entries) {
            val key = entry.key
            val value = entry.value
            sb.append(key + "=" + value + "\n")
        }

        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        sb.append(result)
        sb.append("--------------------end---------------------------")
        LogUtil.e(sb.toString())
        return null
    }
}
