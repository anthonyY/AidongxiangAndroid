package com.aidongxiang.app.ui.login

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.observer.UserInfoSubject
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.business.response.UserDetailsResponseQuery
import com.aiitec.openapi.cache.AiiFileCache
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.PacketUtil
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登录类
 * @author Anthony
 * createTime 2017-11-19
 */
@ContentView(R.layout.activity_login)
class LoginActivity : BaseKtActivity() {


    /**
     * action 登录完成后要执行什么动作， 1 跳转到主页， 默认 0 返回
     */
    var action = 0
    override fun init(savedInstanceState: Bundle?) {

        action = bundle.getInt(ARG_ACTION, 0)
        tv_login_forget.setOnClickListener { switchToActivity(ForgetPasswordActivity::class.java) }
        tv_login_register.setOnClickListener { switchToActivity(RegisterActivity::class.java) }
        btnLogin.setOnClickListener {
            requestLogin()
//            switchToActivity(Main2Activity::class.java)
        }

        Constants.user = null
        AIIConstant.USER_ID = -1
        UserInfoSubject.getInstance().logout()
    }

    /**
     * 请求登录
     */
    private fun requestLogin() {
        if(TextUtils.isEmpty(et_login_mobile.text.toString())){
            toast("请输入手机号")
            return
        }
        if(et_login_mobile.text.toString().length != 11){
            toast("手机号长度必须是11位数")
            return
        }
        if(TextUtils.isEmpty(et_login_password.text.toString())){
            toast("请输入密码")
            return
        }
        val pLength = et_login_password.text.toString().length
        if(pLength < 6 || pLength > 16){
            toast("密码长度必须是6-16位数")
            return
        }
        val query = SubmitRequestQuery("UserLogin")
        query.mobile = et_login_mobile.text.toString()
        query.password = et_login_password.text.toString()

        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {

            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                requestUserDetails2()
            }
        })
    }

    /**
     * 请求用户详情
     */
    private fun requestUserDetails2() {

        requestUserDetails(object : AIIResponse<UserDetailsResponseQuery>(this) {

            override fun onSuccess(response: UserDetailsResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.user?.let {
                    Constants.user = it
                    AIIConstant.USER_ID = it.id
                    AiiFileCache.changeDir(PacketUtil.getCacheDir(this@LoginActivity))
                    UserInfoSubject.getInstance().update(it)
                }
                dismissDialog()
                if(action == 1){
                    switchToActivity(Main2Activity::class.java)
                    App.app.closeAllActivity()
                } else {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        })
    }


}
