package com.aidongxiang.app.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.aidongxiang.app.R
import com.aidongxiang.app.observer.UserInfoSubject
import com.aidongxiang.app.utils.ContentViewUtils
import com.aidongxiang.app.utils.StatusBarUtil
import com.aidongxiang.app.widgets.CustomProgressDialog
import com.aidongxiang.business.response.UserDetailsResponseQuery
import com.aiitec.openapi.cache.AiiFileCache
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.PacketUtil
import com.umeng.analytics.MobclickAgent
import com.umeng.message.PushAgent
import java.io.Serializable


/**
 * @author Anthony
 * *
 * @version 1.0
 * * createTime 2017-05-29
 */
abstract class BaseKtActivity : AppCompatActivity() {


    private var tv_title : TextView ?= null

    fun Activity.toast(message: CharSequence) {
        runOnUiThread {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }

    }
    fun Activity.toast(@StringRes messageRes: Int) {
        runOnUiThread {
            Toast.makeText(applicationContext, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 在setContent 之前执行的内容， 因为有时候需要，但是父类已经setContent了，子类很难办，所以加个方法，子类可以重写这个方法实现
     */
    protected open fun doBeforeSetContent() {
        //竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doBeforeSetContent()
        ContentViewUtils.inject(this)
        val titleBar = findViewById<LinearLayout>(R.id.titlebar)
        if(titleBar != null){
            StatusBarUtil.addStatusBarView(titleBar, android.R.color.transparent)
        }

        progressDialog = CustomProgressDialog.createDialog(this)
        progressDialog?.setCancelable(true)
        progressDialog?.setCanceledOnTouchOutside(true)

        val myApp = application as App
        myApp.addInstance(this)
        tv_title = findViewById<TextView?>(R.id.tv_title)
        toolbar = findViewById<Toolbar?>(R.id.toolbar)
        setToolBar(toolbar)
        PushAgent.getInstance(this).onAppStart()
        init(savedInstanceState)


    }

    protected abstract fun init(savedInstanceState: Bundle?)

    protected var progressDialog: CustomProgressDialog? = null
    protected var toolbar: Toolbar? = null
    protected val bundle: Bundle
        get() {
            var bundle: Bundle? = intent.extras
            if (bundle == null) bundle = Bundle()
            return bundle
        }

    fun setToolBar(toolBar: Toolbar?) {
        if(toolBar == null) return
        this.toolbar = toolBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    override fun setTitle(title: CharSequence?) {
        tv_title?.text = title
    }

    override fun setTitle(titleRes: Int) {
        tv_title?.setText(titleRes)
    }


    @Synchronized fun progressDialogDismiss() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.cancel()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Synchronized fun progressDialogShow() {
        try {
            if (progressDialog != null && !progressDialog!!.isShowing) {
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun switchToActivity(clazz: Class<*>) {
        val intent = Intent(this, clazz)
        startActivity(intent)
    }

    fun switchToActivity(context: Context, clazz: Class<*>) {
        val intent = Intent(context, clazz)
        startActivity(intent)
    }

    fun switchToActivity(clazz: Class<*>, bundle: Bundle) {
        val intent = Intent(this, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
    }
    fun switchToActivity(clazz: Class<*>, vararg  pairs: Pair<String, Any?>) {
        val intent = Intent(this, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivity(intent)

    }
    private fun getBundleExtras( pairs: Array<out Pair<String, Any?>>) : Bundle{
        val bundle = Bundle()

        for (pair in pairs){
            pair.second?.let {
                if(Integer::class.java.isAssignableFrom(it::class.java) ){
                    bundle.putInt(pair.first, it as Int)
                }
                else if(String::class.java.isAssignableFrom(it::class.java) ){
                    bundle.putString(pair.first, it as String)
                }
                else if(Float::class.java.isAssignableFrom(it::class.java) ){
                    bundle.putFloat(pair.first, it as Float)
                }
                else if(Double::class.java.isAssignableFrom(it::class.java) ){
                    bundle.putDouble(pair.first, it as Double)
                }
                else if(Long::class.java.isAssignableFrom(it::class.java) ){
                    bundle.putLong(pair.first, it as Long)
                }
                else if(Serializable::class.java.isAssignableFrom(it::class.java) ){
                    bundle.putSerializable(pair.first, it as Serializable)
                }
                else if(Parcelable::class.java.isAssignableFrom(it::class.java) ){
                    bundle.putParcelable(pair.first, it as Parcelable)
                }
                else {

                }
            }
           
        }
        return bundle
    }
    fun switchToActivity(context: Context, clazz: Class<*>, bundle: Bundle) {
        val intent = Intent(context, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun switchToActivityForResult(context: Context, clazz: Class<*>,
                                  bundle: Bundle?, requestCode: Int) {
        val intent = Intent(context, clazz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(context: Context, clazz: Class<*>, requestCode: Int,  vararg  pairs: Pair<String, Any?>) {
        val intent = Intent(context, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivityForResult(intent, requestCode)
    }
    fun switchToActivityForResult(clazz: Class<*>, requestCode: Int,  vararg  pairs: Pair<String, Any?>) {
        val intent = Intent(this, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(clazz: Class<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(this, clazz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(clazz: Class<*>, requestCode: Int) {
        val intent = Intent(this, clazz)
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivityForResult(context: Context, clazz: Class<*>, requestCode: Int) {
        val intent = Intent(context, clazz)
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivity(context: Context, clazz: Class<*>, anim_in: Int, anim_out: Int) {
        val intent = Intent(context, clazz)
        startActivity(intent)
        overridePendingTransition(anim_in, anim_out)
    }

    fun switchToActivity(context: Context, clazz: Class<*>, bundle: Bundle, anim_in: Int, anim_out: Int) {
        val intent = Intent(context, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
        overridePendingTransition(anim_in, anim_out)
    }


    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this) // 友盟统计时长
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        val myApp = application as App
        myApp.removeInstance(this)

    }


    override fun onStop() {
        super.onStop()
        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
    }
    override fun onBackPressed() {
//        Utils.hideKeyboard(this)
        super.onBackPressed()
    }


    fun requestUserDetails(aiiResponse : AIIResponse<UserDetailsResponseQuery>){
        App.aiiRequest.send(RequestQuery("UserDetails"), aiiResponse)
    }
    fun requestUserDetails(){
        requestUserDetails(true)
    }
    fun requestUserDetails(isShowDialog : Boolean){
        App.aiiRequest.send(RequestQuery("UserDetails"), object : AIIResponse<UserDetailsResponseQuery>(applicationContext, progressDialog, isShowDialog) {
            override fun onSuccess(response: UserDetailsResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.user?.let {
                    Constants.user = it
                    AIIConstant.USER_ID = it.id
                    AiiFileCache.changeDir(PacketUtil.getCacheDir(this@BaseKtActivity))
                    UserInfoSubject.getInstance().update(it)
                }
            }

            override fun onLoginOut(status: Int) {
//                super.onLoginOut(status)
            }

            override fun onServiceError(content: String?, status: Int, index: Int) {
//                super.onServiceError(content, status, index)
            }
        })
    }
}
