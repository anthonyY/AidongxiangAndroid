package com.aidongxiang.app.ui.mine

import android.content.Intent
import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_NAME
import com.aidongxiang.app.widgets.ItemDialog
import kotlinx.android.synthetic.main.activity_user_info.*

/**
 * 个人信息页
 * @author Anthony
 * createTime 2017-12-22
 */
@ContentView(R.layout.activity_user_info)
class UserInfoActivity : BaseKtActivity() {

    val REQUEST_NICKNAME = 1
    var sex = 1
    lateinit var sexDialog : ItemDialog
    override fun init(savedInstanceState: Bundle?) {

        sexDialog = ItemDialog(this)
        val sexs = ArrayList<String>()
        sexDialog.setItems(sexs)

        llUserInfoSex.setOnClickListener {
            sexDialog.setOnItemClickListener { item, position ->
                sex = position+1
                tvUserInfoSex.text = item
            }
        }
        llUserInfoNickname.setOnClickListener {
            switchToActivityForResult(NicknameActivity::class.java, REQUEST_NICKNAME,ARG_NAME to tvUserInfoNickname.text.toString())
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(RESULT_OK == resultCode){
            if(REQUEST_NICKNAME == requestCode){
                data?.getStringExtra(ARG_NAME)?.let {  tvUserInfoNickname.text = it }
            }
        }
    }
}
