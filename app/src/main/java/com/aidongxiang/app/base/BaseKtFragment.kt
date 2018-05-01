package com.aidongxiang.app.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.ContentViewUtils
import com.aidongxiang.app.widgets.CustomProgressDialog
import com.aiitec.openapi.net.AIIRequest
import com.umeng.analytics.MobclickAgent
import java.io.Serializable

/**
 * Fragment 基类
 */
abstract class BaseKtFragment : Fragment() {

    var aiiRequest : AIIRequest?= null

    fun Fragment.toast(message: CharSequence) {
        Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_SHORT).show()
    }
    fun Fragment.toast(messageRes: Int) {
        Toast.makeText(activity?.applicationContext, messageRes, Toast.LENGTH_SHORT).show()
    }
    var progressDialog: CustomProgressDialog? = null
//    var toolbar: Toolbar? = null
    var tv_title: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressDialog = CustomProgressDialog.createDialog(activity)
        setHasOptionsMenu(true)
        aiiRequest = AIIRequest(activity, Api.API)
    }


    fun setToolBar(toolBar: Toolbar?) {
        if(toolBar == null) return
        (activity as AppCompatActivity).setSupportActionBar(toolBar)
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayHomeAsUpEnabled(false)
        actionBar.setHomeButtonEnabled(false)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = ContentViewUtils.inject(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tv_title = view.findViewById<TextView?>(R.id.tv_title)
        init(view)
    }

    protected abstract fun init(view: View)



    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        var context: Context?
        context = activity
        if (context == null) {
            context = App.context
        }
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(context!!)
        }

    }

    fun setTitle(title: CharSequence) {
        tv_title?.text = title
    }

    fun setTitle(titleRes: Int) {
        tv_title?.setText(titleRes)
    }

    fun switchToActivity(clazz: Class<*>) {
        val intent = Intent(activity, clazz)
        startActivity(intent)
    }

    fun switchToActivity(clazz: Class<*>, bundle: Bundle) {
        val intent = Intent(activity, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    fun switchToActivityForResult(clazz: Class<*>, requestCode: Int) {
        val intent = Intent(activity, clazz)
        startActivityForResult(intent, requestCode)
    }
    fun switchToActivityForResult(clazz: Class<*>, bundle: Bundle?, requestCode: Int) {
        val intent = Intent(activity, clazz)
        if (bundle != null)
            intent.putExtras(bundle)
        startActivityForResult(intent, requestCode)
    }

    fun switchToActivity(clazz: Class<*>, anim_in: Int, anim_out: Int) {
        val intent = Intent(activity, clazz)
        startActivity(intent)
        activity?.overridePendingTransition(anim_in, anim_out)
    }

    fun switchToActivity(clazz: Class<*>, bundle: Bundle, anim_in: Int, anim_out: Int) {
        val intent = Intent(activity, clazz)
        intent.putExtras(bundle)
        startActivity(intent)
        activity?.overridePendingTransition(anim_in, anim_out)
    }

    fun switchToActivity(clazz: Class<*>, vararg  pairs: Pair<String, Any?>) {
        val intent = Intent(activity, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivity(intent)
    }
    fun switchToActivityForResult(clazz: Class<*>, requestCode: Int,  vararg  pairs: Pair<String, Any?>) {
        val intent = Intent(activity, clazz)
        intent.putExtras(getBundleExtras(pairs))
        startActivityForResult(intent, requestCode)
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
    override fun onResume() {
        super.onResume()
        MobclickAgent.onPageStart(this::class.java.simpleName) //统计页面
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPageEnd(this::class.java.simpleName)
    }

    override fun onDestroy() {
        super.onDestroy()
        aiiRequest?.cancelHttpRequest()
    }

    fun progressDialogShow() {
        try {
            if (progressDialog != null && !progressDialog!!.isShowing) {
                progressDialog!!.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun progressDialogDismiss() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}