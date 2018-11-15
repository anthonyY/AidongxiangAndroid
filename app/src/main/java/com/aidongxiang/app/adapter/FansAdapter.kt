package com.aidongxiang.app.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.business.model.Fans

/**
 *
 * @author Anthony
 * createTime 2017/12/22.
 * @version 1.0
 */
class FansAdapter(context: Context, datas: MutableList<Fans>, val isScreen: Boolean = false) : CommonRecyclerViewAdapter<Fans>(context, datas){
    override fun convert(h: CommonRecyclerViewHolder, item: Fans, position: Int) {

        val tvSignature = h.getView<TextView>(R.id.tv_item_signature)
        val ivAvatar = h.getView<ImageView>(R.id.iv_item_avatar)
        val tvName = h.getView<TextView>(R.id.tv_item_name)
        val tvStatus = h.getView<TextView>(R.id.tv_item_status)


        tvName.text = item.name
        tvSignature.text = item.description
        if(isScreen){
            tvStatus.visibility = View.GONE
        } else {
            when {
                item.isFocus == 1 -> {
                    //未关注
                    tvStatus.text = "关注"
                    tvStatus.isSelected = true
                    tvStatus.visibility = View.VISIBLE
                }
            //已关注，但是对方未关注我
                item.isFocus == -1 -> {
//                tvStatus.visibility = View.GONE
                    //已互相关注
                    tvStatus.text = "取消关注"
                    tvStatus.isSelected = false
                    tvStatus.visibility = View.VISIBLE
                }
                item.isFocus == 0 -> {
//                tvStatus.visibility = View.GONE
                    //已互相关注
                    tvStatus.text = "取消关注"
                    tvStatus.isSelected = false
                    tvStatus.visibility = View.VISIBLE
                }
                else -> {
                    //已互相关注
                    tvStatus.text = "互相关注"
                    tvStatus.isSelected = false
                    tvStatus.visibility = View.VISIBLE
                }
            }

        }

        GlideImgManager.load(context, item.imagePath, R.drawable.ic_avatar_default, ivAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_fans

}