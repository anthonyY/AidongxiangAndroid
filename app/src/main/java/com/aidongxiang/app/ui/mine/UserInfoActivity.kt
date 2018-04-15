package com.aidongxiang.app.ui.mine

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_NAME
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.app.utils.UploadPhotoHelper
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.app.widgets.ItemDialog
import com.aidongxiang.business.model.User
import com.aidongxiang.business.request.UserUpdateRequestQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.RequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.net.AIIResponse
import com.aiitec.openapi.utils.LogUtil
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.layout_title_bar_with_right_text.*

/**
 * 个人信息页
 * @author Anthony
 * createTime 2017-12-22
 */
@ContentView(R.layout.activity_user_info)
class UserInfoActivity : BaseKtActivity() {

    val REQUEST_NICKNAME = 1
    val REQUEST_SIGNATURE = 2
    var sex = 1
    var imageId : Long = -1
    lateinit var sexDialog : ItemDialog
    lateinit var uploadPhotoHelper : UploadPhotoHelper
    lateinit var exitDialog : CommonDialog
    var isChange = false
    override fun init(savedInstanceState: Bundle?) {

        title = "个人主页"
        exitDialog = CommonDialog(this)
        exitDialog.setTitle("确定退出")
        exitDialog.setContent("您有修改未保存，确定不保存直接退出？")
        exitDialog.setOnConfirmClickListener {
            exitDialog.dismiss()
            finish()
        }
        sexDialog = ItemDialog(this)
        val sexs = ArrayList<String>()
        sexs.add("男")
        sexs.add("女")
        sexDialog.setItems(sexs)
        sexDialog.setOnItemClickListener { item, position ->
            val tempSex = position+1
            if(tempSex != sex){
                tvUserInfoSex.text = item
                sex = tempSex
                isChange = true
            }
        }
        llUserInfoSex.setOnClickListener {
            sexDialog.show()
        }
        llUserInfoNickname.setOnClickListener {
            switchToActivityForResult(NicknameActivity::class.java, REQUEST_NICKNAME,ARG_NAME to tvUserInfoNickname.text.toString())
        }
        ivUserInfoAvatar.setOnClickListener {
            uploadPhotoHelper.showPickDialog()
        }

        uploadPhotoHelper = UploadPhotoHelper(this, ivUserInfoAvatar)
        uploadPhotoHelper.setOnUploadFinishedListener { id, path ->
            imageId = id
            GlideImgManager.load(this@UserInfoActivity, path, R.drawable.ic_avatar_default, ivUserInfoAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)
            LogUtil.e("上传图片id:"+id)
            requestUserUpdateImage()
        }

        llUserInfoSignature.setOnClickListener {
            switchToActivityForResult(SignatureActivity::class.java, REQUEST_SIGNATURE)
        }

        btn_title_confirm.setOnClickListener {
            requestUpdateUser()
        }

        Constants.user?.let {
            sex = it.sex
            if(sex == 2){
                tvUserInfoSex.text = "女"
            } else {
                tvUserInfoSex.text = "男"
            }
            tvUserInfoNickname.text = it.nickName
            tvUserInfoSignature.text = it.description
            GlideImgManager.load(this, it.imagePath, R.drawable.ic_avatar_default, ivUserInfoAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)
        }
    }


    private fun requestUserUpdateImage(){
        val query = RequestQuery("UserUpdateImage")
        query.action = AIIAction.ONE
        query.id = imageId
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                requestUserDetails()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        uploadPhotoHelper.onActivityResult(requestCode, resultCode, data)
        if(RESULT_OK == resultCode){
            if(REQUEST_NICKNAME == requestCode){
                data?.getStringExtra(ARG_NAME)?.let {
                    tvUserInfoNickname.text = it
                    if(it != Constants.user!!.nickName){
                        //昵称变了，所以要显示保存按钮
                        isChange = true
                        btn_title_confirm.visibility = View.VISIBLE
                    }
                }
            }
            if(REQUEST_SIGNATURE == requestCode){
                data?.getStringExtra(ARG_NAME)?.let {
                    tvUserInfoSignature.text = it
                    if(it != Constants.user!!.description){
                        //昵称变了，所以要显示保存按钮
                        isChange = true
                        btn_title_confirm.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        uploadPhotoHelper.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        uploadPhotoHelper.onRestoreInstanceState(savedInstanceState)
    }


    fun requestUpdateUser(){
        val query = UserUpdateRequestQuery()
        query.action = AIIAction.ONE
        val user = User()
        user.nickName = tvUserInfoNickname.text.toString()
        user.description = tvUserInfoSignature.text.toString()
        user.sex = sex
        query.user = user
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                requestUserDetails()
                dismissDialog()
                finish()
            }
        })
    }

    override fun onBackPressed() {

        if(isChange){
            exitDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}
