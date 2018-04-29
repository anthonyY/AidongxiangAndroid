package com.aidongxiang.app.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_IS_THIRTY_PART_LOGIN
import com.aidongxiang.app.base.Constants.ARG_MOBILE
import com.aidongxiang.app.base.Constants.ARG_SMSCODE_ID
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.app.utils.UploadPhotoHelper
import com.aidongxiang.app.utils.Utils
import com.aidongxiang.business.request.UserRegisterRequestQuery
import com.aidongxiang.business.response.SMSResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.activity_perfect_data.*

/**
 * 完善资料
 * @author Anthony
 * createTime 2017-11-19
 * @version 1.0
 */
@ContentView(R.layout.activity_perfect_data)
class PerfectDataActivity : BaseKtActivity(), TextWatcher {

    var mobile : String ?= null
    var openId : String ?= null
    var unionId : String ?= null
    var partner : String ?= null
    var imageId : Long = -1
    var smscodeId = -1
    lateinit var uploadPhotoHelper : UploadPhotoHelper
    /**
     * 是否是第三方登录
     */
    var isThirtyPartLogin = false
    /**
     * 输入内容变化监听，如果有内容，按钮就可以点击，否则不可点击
     */
    override fun afterTextChanged(p0: Editable?) {
        btnFinish.isEnabled = !TextUtils.isEmpty(etNickname.text.toString()) && !TextUtils.isEmpty(etPassword.text.toString())
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun init(savedInstanceState: Bundle?) {
        smscodeId = bundle.getInt(ARG_SMSCODE_ID, -1)
        mobile = bundle.getString(ARG_MOBILE)
        isThirtyPartLogin = bundle.getBoolean(ARG_IS_THIRTY_PART_LOGIN)
        uploadPhotoHelper = UploadPhotoHelper(this, ivPerfaceAvatar)
        uploadPhotoHelper.setOnUploadFinishedListener { id, url ->
            imageId = id
        }
        setLitener()
    }
    fun setLitener(){
        btnFinish.setOnClickListener {
            if(TextUtils.isEmpty(etNickname.text.toString())){
                toast("请输入昵称")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(etPassword.text.toString())){
                toast("请输入密码")
                return@setOnClickListener
            }
//            switchToActivity(Main2Activity::class.java)
//            App.app.closeAllActivity()
            requestRegister()
        }
        etNickname.addTextChangedListener(this)
        etPassword.addTextChangedListener(this)
        ivPerfaceAvatar.setOnClickListener { uploadPhotoHelper.showPickDialog() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadPhotoHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        uploadPhotoHelper.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        uploadPhotoHelper.onRestoreInstanceState(savedInstanceState)
    }



    fun requestRegister(){

        if(etNickname.text.toString().isEmpty()){
            toast("请输入昵称")
            return
        }
        val nicknameLength = Utils.getUnicodeLength(etNickname.text.toString())
        if(nicknameLength < 2 || nicknameLength > 16){
            toast("昵称长度必须是2-16个英文字符，一个汉字代表2个字符")
            return
        }
        if(etPassword.text.toString().isEmpty()){
            toast("请输入密码")
            return
        }
        if(etPassword.text.length < 6 || etPassword.text.length > 16){
            toast("密码长度必须是6-16位")
            return
        }
        val query = UserRegisterRequestQuery()

        query.nickName = etNickname.text.toString()
        query.headImageId = imageId
        query.password = etPassword.text.toString()

        if(isThirtyPartLogin){
            query.action = AIIAction.TWO
            query.openId = openId
            query.unionId = unionId
            query.partner = partner
        } else {
            query.action = AIIAction.ONE
            query.mobile = mobile
            query.smscodeId = smscodeId
        }

        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)

                dismissDialog()
                toast("注册成功")
                switchToActivity(Main2Activity::class.java)
                requestUserDetails()
//                setResult(Activity.RESULT_OK)
//                finish()

            }
        })
    }


}
