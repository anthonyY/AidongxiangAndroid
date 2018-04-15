package com.aidongxiang.app.ui.square

import android.app.Activity
import android.os.Bundle
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants.ARG_NAME
import kotlinx.android.synthetic.main.activity_comment.*

/**
 * 评论
 * @author Anthony
 * createTime 2018/4/14.
 * @version 1.0
 */
class CommentActivity : BaseKtActivity(){


    override fun init(savedInstanceState: Bundle?) {

        title = "评论"
        btnSubmit.setOnClickListener {
            intent.putExtra(ARG_NAME, etCommentContent.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }

}