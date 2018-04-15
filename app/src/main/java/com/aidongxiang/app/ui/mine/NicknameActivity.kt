package com.aidongxiang.app.ui.mine

import android.app.Activity
import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_NAME
import kotlinx.android.synthetic.main.activity_nickname2.*

/**
 * 设置昵称
 * @author Anthony
 * createTime 2017-12-22
 *
 */
@ContentView(R.layout.activity_nickname2)
class NicknameActivity : BaseKtActivity() {

    override fun init(savedInstanceState: Bundle?) {

        setTitle("昵称")
        Constants.user?.nickName?.let {  etNickname.setText(it) }
        btnSave.setOnClickListener {
            intent.putExtra(ARG_NAME, etNickname.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }




}
