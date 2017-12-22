package com.aidongxiang.app.widgets

import android.content.Context
import android.text.TextUtils
import android.view.Gravity
import android.view.WindowManager
import com.aidongxiang.app.R
import com.aiitec.openapi.utils.ToastUtil
import kotlinx.android.synthetic.main.dialog_comment.*

/**
 *
 * @author Anthony
 * createTime 2017/12/9.
 * @version 1.0
 */
class CommentDialog(context: Context) : AbsCommonDialog(context){
    override fun widthScale(): Float = 1f
    override fun animStyle(): Int = R.style.BottomAnimationStyle
    override fun layoutId(): Int = R.layout.dialog_comment

    override fun setLayoutParams(lp: WindowManager.LayoutParams?): WindowManager.LayoutParams {
        lp?.gravity = Gravity.BOTTOM
        return super.setLayoutParams(lp)
    }
    fun setOnCommentClickListener(listener : ((text : String) -> Unit) ){
        tv_dialog_confirm.setOnClickListener {
            if(TextUtils.isEmpty(et_dialog_content.text.toString())){
                ToastUtil.show(context, "请输入评论内容")
                return@setOnClickListener
            }
            dismiss()
            listener.invoke(et_dialog_content.text.toString())
        }
    }
}