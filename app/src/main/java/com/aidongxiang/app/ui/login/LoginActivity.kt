package com.aidongxiang.app.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ACTION
import com.aidongxiang.app.base.Constants.ARG_TYPE
import com.aidongxiang.app.base.Constants.TYPE_THIRTY_PART_LOGIN
import com.aidongxiang.app.observer.UserInfoSubject
import com.aidongxiang.app.ui.Main2Activity
import com.aidongxiang.app.ui.mine.ChangeMobileNextActivity
import com.aidongxiang.app.utils.ShareUtils
import com.aidongxiang.business.request.UserPartnerLoginRequestQuery
import com.aidongxiang.business.response.UserDetailsResponseQuery
import com.aiitec.openapi.cache.AiiFileCache
import com.aiitec.openapi.constant.AIIConstant
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import com.aiitec.openapi.utils.PacketUtil
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登录类
 * @author Anthony
 * createTime 2017-11-19
 */
@ContentView(R.layout.activity_login)
class LoginActivity : BaseKtActivity(), UMAuthListener {
    override fun onStart(p0: SHARE_MEDIA?) {


    }

    override fun onComplete(p0: SHARE_MEDIA?, p1: Int, map: MutableMap<String, String>?) {
        val it = map?.entries?.iterator() ?: return
        var nickname = ""
        var openId = ""
        var sexString = ""
        var imagePath = ""
        var unionId = ""
        var signature = ""
        while (it.hasNext()) {
            val entry = it.next()
            val key = entry.key
            LogUtil.e("$key:${entry.value}")
            if (key == "screen_name" || key == "nickname" || key == "name") {
                if (!TextUtils.isEmpty(entry.value)) {
                    nickname = entry.value
                }
            } else if (key == "openId" || key == "openid") {
                openId = entry.value
            } else if (key == "gender" || key == "sexString") {
                sexString = entry.value
            } else if (key == "profile_image_url" || key == "headimgurl"
                    || key == "figureurl" || key == "iconurl") {
                imagePath = entry.value
            } else if (key == "unionid" || key == "uid") {
                unionId = entry.value
            } else if (key == "signature") {
                signature = entry.value
            } else if (key == "remark" || key == "signature") {
                signature = entry.value
            }
        }
        if(TextUtils.isEmpty(openId)){
            openId = unionId
        }
        if(TextUtils.isEmpty(unionId)){
            unionId = openId
        }
        var sex = 0
        if (sexString == "男" || sexString == "1") {
            sex = 1
        } else if (sexString == "女" || sexString == "2") {
            sex = 2
        }
        var partner = 1
        if(p0 == SHARE_MEDIA.QQ){
            partner = 1
        } else if(p0 == SHARE_MEDIA.WEIXIN){
            partner = 2
        } else if(p0 == SHARE_MEDIA.SINA){
            partner = 3
        }
        requestUserPartnerLogin(partner, imagePath, nickname, openId, unionId, sex)
    }

    override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
    }

    override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
        LogUtil.e("登录失败 $p1  ${p2?.message}")
    }


    lateinit var shareUtils : ShareUtils
    /**
     * action 登录完成后要执行什么动作， 1 跳转到主页， 默认 0 返回
     */
    var action = 0
    override fun init(savedInstanceState: Bundle?) {

        shareUtils = ShareUtils(this)

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
        ibtnWeixin.setOnClickListener {
            shareUtils.getUserData(SHARE_MEDIA.WEIXIN, this)
        }
        ibtnQQ.setOnClickListener {
            shareUtils.getUserData(SHARE_MEDIA.QQ, this)
        }
        ibtnSina.setOnClickListener {
            shareUtils.getUserData(SHARE_MEDIA.SINA, this)
        }

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
     * 请求登录
     */
    private fun requestUserPartnerLogin(partner : Int, imagePath : String, nickname: String, openId: String, unionId: String, sex: Int) {

        val query = UserPartnerLoginRequestQuery()
        query.openId = openId
        query.unionId = unionId
        query.partner = partner
        query.imageUrl = imagePath
        query.nickname = nickname
        query.sex = sex
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {

            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let {
                    if(it.id <= 0){
                        switchToActivity(ChangeMobileNextActivity::class.java,
                                ARG_TYPE to TYPE_THIRTY_PART_LOGIN,
                                Constants.ARG_OPEN_ID to openId,
                                Constants.ARG_UNION_ID to unionId,
                                Constants.ARG_PARTNER to partner)
                    } else {
                        requestUserDetails2()
                    }
                }


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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shareUtils.onActivityResult(requestCode, resultCode, data)
    }

}
