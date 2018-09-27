//package com.aidongxiang.app.ui.login
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextUtils
//import android.text.TextWatcher
//import com.aidongxiang.app.R
//import com.aidongxiang.app.annotation.ContentView
//import com.aidongxiang.app.base.App
//import com.aidongxiang.app.base.BaseKtActivity
//import com.aidongxiang.app.base.Constants
//import com.aidongxiang.app.ui.mine.ChangeMobileNextActivity
//import com.aidongxiang.app.utils.SmscodeCountDown
//import com.aidongxiang.business.model.Where
//import com.aidongxiang.business.response.SMSResponseQuery
//import com.aiitec.openapi.json.enums.AIIAction
//import com.aiitec.openapi.model.SubmitRequestQuery
//import com.aiitec.openapi.net.AIIResponse
//import kotlinx.android.synthetic.main.activity_perfect_information.*
//
///**
// * 完善资料
// * @author Anthony
// * createTime 2017-11-24
// *
// */
//@ContentView(R.layout.activity_perfect_information)
//class PerfectInformationActivity : BaseKtActivity() {
//
//    var smscodeId = -1
//    lateinit var smscodeCountDown : SmscodeCountDown
//    override fun init(savedInstanceState: Bundle?) {
//        title = "完善资料"
//        smscodeCountDown = SmscodeCountDown(60 * 1000, 1000)
//        smscodeCountDown.setSmscodeBtn(btnSmscode)
//        etSmscode.addTextChangedListener(object : TextWatcher {
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
//            override fun afterTextChanged(p0: Editable?) {
//                btnNext.isEnabled = !TextUtils.isEmpty(p0.toString())
//            }
//        })
//
//        btnNext.setOnClickListener {
//            if(TextUtils.isEmpty(etSmscode.text.toString().trim())){
//                toast("请输入验证码")
//                return@setOnClickListener
//            }
////            requestSMSCode(2)
//            switchToActivity(ChangeMobileNextActivity::class.java)
//        }
//        tvMobileCannotUse.setOnClickListener {
//            switchToActivity(MobileAppealActivity::class.java)
//        }
//        btnSmscode.setOnClickListener {
//            smscodeCountDown.start()
//            requestSMSCode(1)
//        }
//    }
//
//    fun cancelCountDown(){
//        smscodeCountDown.cancel()
//        smscodeCountDown = SmscodeCountDown(1000, 60 * 1000)
//        smscodeCountDown.setSmscodeBtn(btnSmscode)
//    }
//
//
//    fun requestSMSCode(action : Int){
//        val mobile = Constants.user?.mobile
//        val query = SubmitRequestQuery()
//        query.namespace = "SMSCode"
//        query.action = AIIAction.valueOf(action)
//        query.type = 2
//        query.mobile = mobile
//        if(action == 2){
//            if(etSmscode.text.toString().isEmpty()){
//                toast("请输入验证码")
//                return
//            }
//            val where = Where()
//            val code = etSmscode.text.toString().toInt()
//            where.code = code
//            query.where = where
//        }
//        App.aiiRequest.send(query, object : AIIResponse<SMSResponseQuery>(this){
//
//            override fun onSuccess(response: SMSResponseQuery?, index: Int) {
//                super.onSuccess(response, index)
//                response?.let {
//                    if(index == 1) {
//                        smscodeId = it.id.toInt()
//                        it.code?.let { etSmscode.setText(it) }
//                        switchToActivityForResult(ChangeMobileNextActivity::class.java, 1, Constants.ARG_SMSCODE_ID to smscodeId)
//                    } else if(index == 2){
//                        switchToActivityForResult(ChangeMobileNextActivity::class.java, 1, Constants.ARG_MOBILE to mobile, Constants.ARG_SMSCODE_ID to smscodeId)
//                    } else {
//
//                    }
//                }
//            }
//
//            override fun onFailure(content: String?, index: Int) {
//                super.onFailure(content, index)
//                cancelCountDown()
//            }
//
//        }, action)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode == Activity.RESULT_OK){
//            setResult(resultCode)
//            finish()
//
//        }
//    }
//}