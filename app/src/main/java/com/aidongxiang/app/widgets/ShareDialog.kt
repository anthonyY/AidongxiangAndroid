package com.aiitec.widgets

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.ShareUtils
import com.aidongxiang.app.widgets.AbsCommonDialog
import com.umeng.socialize.bean.SHARE_MEDIA
import kotlinx.android.synthetic.main.dialog_share.*

/**
 * 分享弹窗
 * @author  Anthony
 * @version 1.0
 * createTime 2017/11/3.
 */
class ShareDialog(context : Context) : AbsCommonDialog(context){

    override fun animStyle(): Int = R.style.BottomAnimationStyle
    fun widthScale(): Float = 1f
    fun layoutId(): Int = R.layout.dialog_share
    var shareUtils : ShareUtils?= null

    override fun findView(view: View?) {
        super.findView(view)
        shareUtils = ShareUtils(context)
        tv_dialog_share_wechat.setOnClickListener{
            dismiss()
            shareUtils?.directShare(SHARE_MEDIA.WEIXIN)
        }
        tv_dialog_share_wechat_circle.setOnClickListener{
            dismiss()
            shareUtils?.directShare(SHARE_MEDIA.WEIXIN_CIRCLE)
        }
    }

    override fun setLayoutParams(lp: WindowManager.LayoutParams?): WindowManager.LayoutParams {
        lp?.gravity = Gravity.BOTTOM
        return super.setLayoutParams(lp)

    }
    fun onActivityResult(requestCode : Int, resultCode:Int, data:Intent?){
        shareUtils?.onActivityResult(requestCode, resultCode, data)
    }

    fun setShareData(title : String, content : String, imagePath: String, url : String){
        shareUtils?.setShareTitle(title)
        shareUtils?.setShareContent(content)
        shareUtils?.setShareImage(imagePath)
        shareUtils?.setShareUrl(url)
    }
}