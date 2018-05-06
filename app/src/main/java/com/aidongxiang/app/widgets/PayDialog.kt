package com.aidongxiang.app.widgets

import android.content.Context
import android.view.View
import com.aidongxiang.app.R
import com.aiitec.openapi.utils.AiiUtil
import kotlinx.android.synthetic.main.dialog_pay.*

/**
 *
 * @author Anthony
 * createTime 2018/5/6.
 * @version 1.0
 */
class PayDialog(context : Context) : AbsCommonDialog(context){

    var payType = 1
    override fun widthScale(): Float = 1f

    override fun layoutId(): Int = R.layout.dialog_pay

    override fun findView(view: View?) {
        super.findView(view)
        ibtnDialogPayClose.setOnClickListener { dismiss() }
        btnDialogConfirmPay.setOnClickListener {
            payListener?.invoke(payType)
        }
        llDialogPayAlipay.setOnClickListener {
            llDialogPayCechart.isSelected = false
            llDialogPayAlipay.isSelected = true
            payType = 1
        }

        llDialogPayCechart.setOnClickListener {
            llDialogPayCechart.isSelected = true
            llDialogPayAlipay.isSelected = false
            payType = 2
        }
    }

    /**
     * 过时的方法，请使用
     * @see show(price : Float)
     */
    @Deprecated("unuse you need show with price @see show(price:Float)")
    override fun show() {
//        super.show()
    }

    fun show(price : Float) {
        val priceStr = AiiUtil.formatString(price)
        tvDialogPayPrice.text = "¥$priceStr"
        btnDialogConfirmPay.text = "确认支付¥$priceStr"
        super.show()
    }

    var payListener: ((payType : Int) -> Unit)?= null
    fun setOnPayListener(listener: (payType : Int) -> Unit) {
        payListener = listener
    }
}
