package com.aiitec.widgets

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.adapter.CommonRecyclerViewHolder
import com.aidongxiang.app.utils.ShareUtils
import com.aidongxiang.app.widgets.AbsCommonDialog
import com.aiitec.openapi.utils.ToastUtil
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
    override fun widthScale(): Float = 1f
    override fun layoutId(): Int = R.layout.dialog_share
    var shareUtils : ShareUtils?= null
    lateinit var adapter : ShareAdapter
    lateinit var clipboardManager : ClipboardManager
    var datas = ArrayList<String>()
    var shareUrl : String ?= null
    override fun findView(view: View?) {
        super.findView(view)
        //剪贴板
        clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager


        datas = ArrayList()
        datas.add("微信好友")
        datas.add("朋友圈")
        datas.add("QQ好友")
        datas.add("QQ空间")
//        datas.add("新浪微博")
        datas.add("复制链接")
        adapter = ShareAdapter(context, datas)
        adapter.setOnRecyclerViewItemClickListener { v, position ->
            dismiss()
            when(position){
                0->{ shareUtils?.directShare(SHARE_MEDIA.WEIXIN) }
                1->{ shareUtils?.directShare(SHARE_MEDIA.WEIXIN_CIRCLE) }
                2->{ shareUtils?.directShare(SHARE_MEDIA.QQ) }
                3->{ shareUtils?.directShare(SHARE_MEDIA.QZONE) }
                4->{ shareUtils?.directShare(SHARE_MEDIA.SINA) }
                5->{ shareUrl?.let { copyText(it) } }
            }
        }
        shareUtils = ShareUtils(context)
        val layoutManager = GridLayoutManager(context, 4)
        recyclerViewShare.layoutManager = layoutManager
        recyclerViewShare.adapter = adapter

    }
    /**
     * 复制链接
     */
    private fun copyText(url: String) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        // 将文本内容放到系统剪贴板里。
        clipboardManager.primaryClip = ClipData.newPlainText(null, url)
        ToastUtil.show(context, "复制成功")
    }


    override fun setLayoutParams(lp: WindowManager.LayoutParams?): WindowManager.LayoutParams {
        lp?.gravity = Gravity.BOTTOM
        return super.setLayoutParams(lp)

    }
    fun onActivityResult(requestCode : Int, resultCode:Int, data:Intent?){
        shareUtils?.onActivityResult(requestCode, resultCode, data)
    }

    fun setShareData(title : String?, content : String?, imagePath: String?, url : String?){
        shareUtils?.setShareTitle(title)
        shareUtils?.setShareContent(content)
        shareUtils?.setShareImage(imagePath)
        shareUtils?.setShareUrl(url)
        this.shareUrl = url
    }

    class ShareAdapter(context: Context, datas : MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){
        override fun convert(h: CommonRecyclerViewHolder, item: String, position: Int) {
            val tvName = h.getView<TextView>(R.id.tv_item_name)
            val ivImg = h.getView<ImageView>(R.id.iv_item_img)
            tvName.text = item
            when(position){
                0->{ ivImg.setImageResource(R.drawable.home_btn_wechat) }
                1->{ ivImg.setImageResource(R.drawable.home_btn_friend) }
                2->{ ivImg.setImageResource(R.drawable.home_btn_qq) }
                3->{ ivImg.setImageResource(R.drawable.home_btn_qq_zone) }
                4->{ ivImg.setImageResource(R.drawable.home_btn_sina) }
                5->{ ivImg.setImageResource(R.drawable.home_btn_link) }
            }
        }

        override fun getLayoutViewId(viewType: Int): Int = R.layout.item_share

    }
}