package com.aidongxiang.app.ui.mine

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_OPEN_ID
import com.aidongxiang.app.base.Constants.ARG_PARTNER
import com.aidongxiang.app.base.Constants.ARG_UNION_ID
import com.aidongxiang.app.base.Constants.TYPE_CHANGE_MOBILE
import com.aidongxiang.app.base.Constants.TYPE_THIRTY_PART_LOGIN
import com.aidongxiang.app.ui.login.PerfectDataActivity
import com.aidongxiang.app.utils.SmscodeCountDown
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.response.SMSResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_change_mobile_next.*

/**
 * 更改手机下一步
 * @author Anthony
 * createTime 2017-11-24
 */
@ContentView(R.layout.activity_change_mobile_next)
class ChangeMobileNextActivity : BaseKtActivity() {

    var mobile : String ?= null

    var smscodeId = -1
    var type = TYPE_CHANGE_MOBILE
    var openId : String ?= null
    var unionId : String ?= null
    var partner = -1

    lateinit var smscodeCountDown : SmscodeCountDown
    override fun init(savedInstanceState: Bundle?) {
        mobile = Constants.user?.mobile
//        smscodeId = bundle.getInt(ARG_SMSCODE_ID)
        title = "修改手机"
        type = bundle.getInt(Constants.ARG_TYPE)
        if(type == TYPE_THIRTY_PART_LOGIN){
            title = "设置手机号"
            openId = bundle.getString(ARG_OPEN_ID)
            unionId = bundle.getString(ARG_UNION_ID)
            partner = bundle.getInt(ARG_PARTNER, -1)
        } else {
//            mobile = Constants.user?.mobile
//            etMobile.setText(mobile)
//            etMobile.isEnabled = false
        }

        smscodeCountDown = SmscodeCountDown(60*1000, 1000)
        smscodeCountDown.setSmscodeBtn(btnSmscode)
        etSmscode.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                btnFinish.isEnabled = !TextUtils.isEmpty(p0.toString())
            }
        })

        btnSmscode.setOnClickListener {
            if(TextUtils.isEmpty(etMobile.text.toString().trim())){
                toast("请输入手机号码")
                return@setOnClickListener
            }
            if(etMobile.text.toString().length != 11){
                toast("手机号码长度必须是11位")
                return@setOnClickListener
            }
            requestSMSCode(1)
            smscodeCountDown.start()

        }

        btnFinish.setOnClickListener {
            if(TextUtils.isEmpty(etSmscode.text.toString().trim())){
                toast("请输入验证码")
                return@setOnClickListener
            }
            requestSMSCode(2)
        }
    }

//

    fun requestSMSCode(action : Int){
        val query = SubmitRequestQuery()
        query.namespace = "SMSCode"
        query.action = AIIAction.valueOf(action)
        query.type = type
        query.mobile = etMobile.text.toString()
        if(action == 2){
            if(etSmscode.text.toString().isEmpty()){
                toast("请输入验证码")
                return
            }
            val where = Where()
            val code = etSmscode.text.toString().toInt()
            where.code = code
            query.where = where
        }
        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                if(index == 1) {
                    smscodeId = response!!.id.toInt()
                    tvChangeMobileSendTo.text = "验证码已发送至${etMobile.text.toString()}手机"
                    response.code?.let { etSmscode.setText(it) }
                } else if(index == 2) {
                    if(type == TYPE_THIRTY_PART_LOGIN){
                        switchToActivityForResult(PerfectDataActivity::class.java, 1,
                                Constants.ARG_MOBILE to etMobile.text.toString(),
                                Constants.ARG_SMSCODE_ID to smscodeId,
                                Constants.ARG_OPEN_ID to openId,
                                Constants.ARG_UNION_ID to unionId,
                                Constants.ARG_PARTNER to partner,
                                Constants.ARG_IS_THIRTY_PART_LOGIN to true
                                )
                    } else {
                        requestUserUpdateMobile()
                    }

                }
            }

        }, action)
    }
    fun requestUserUpdateMobile(){
        val query = SubmitRequestQuery()
        query.namespace = "UserUpdateMobile"
        query.smscodeId = smscodeId
        query.mobile = mobile
        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){

            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                ToastUtil.show(this@ChangeMobileNextActivity, "修改完成")
                setResult(Activity.RESULT_OK)
                dismissDialog()
                finish()
            }

        })
    }

}