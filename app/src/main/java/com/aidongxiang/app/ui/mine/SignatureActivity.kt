package com.aidongxiang.app.ui.mine

import android.app.Activity
import android.os.Bundle
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import kotlinx.android.synthetic.main.activity_signature.*

/**
 * 设置签名
 * @author Anthony
 * createTime 2017-12-22
 *
 */
@ContentView(R.layout.activity_signature)
class SignatureActivity : BaseKtActivity() {

    override fun init(savedInstanceState: Bundle?) {

        btnSave.setOnClickListener {
            intent.putExtra(Constants.ARG_NAME, etSignature.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}