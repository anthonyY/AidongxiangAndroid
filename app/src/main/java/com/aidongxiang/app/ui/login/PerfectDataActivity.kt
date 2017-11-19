package com.aidongxiang.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.app.utils.UploadPhotoHelper
import kotlinx.android.synthetic.main.activity_perfect_data.*

/**
 * 完善资料
 * @author Anthony
 * createTime 2017-11-19
 * @version 1.0
 */
@ContentView(R.layout.activity_perfect_data)
class PerfectDataActivity : BaseKtActivity(), TextWatcher {

    var imageId : Long = 0
    var uploadPhotoHelper : UploadPhotoHelper?= null
    /**
     * 输入内容变化监听，如果有内容，按钮就可以点击，否则不可点击
     */
    override fun afterTextChanged(p0: Editable?) {
        btnFinish.isEnabled = !TextUtils.isEmpty(etNickname.text.toString()) && !TextUtils.isEmpty(etPassword.text.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun init(savedInstanceState: Bundle?) {
        uploadPhotoHelper = UploadPhotoHelper(this, ivPerfaceAvatar)
        uploadPhotoHelper?.setOnUploadFinishedListener { id, url ->
            imageId = id
        }
        setLitener()
    }
    fun setLitener(){
        btnFinish.setOnClickListener {
            if(TextUtils.isEmpty(etNickname.text.toString())){
                toast("请输入手机号")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(etPassword.text.toString())){
                toast("请输入验证码")
                return@setOnClickListener
            }
            switchToActivity(Main2Activity::class.java)
            App.app?.closeAllActivity()
        }
        etNickname.addTextChangedListener(this)
        etPassword.addTextChangedListener(this)
        ivPerfaceAvatar.setOnClickListener { uploadPhotoHelper?.showPickDialog() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadPhotoHelper?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        uploadPhotoHelper?.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        uploadPhotoHelper?.onRestoreInstanceState(savedInstanceState)
    }
}
