package com.aidongxiang.app.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.aidongxiang.app.R
import com.aidongxiang.app.ui.home.HomeFragment
import java.util.*

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
class PostAdapter(context: Context, datas: MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){
    val random = Random()

    override fun convert(h: CommonRecyclerViewHolder?, item: String?, position: Int) {
        val recyclerView = h?.getView<RecyclerView>(R.id.recycler_post_img)

        val size = position %9 +1
        val datas = ArrayList<String>()
        for(i in 0 until size){
            val img = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
            datas.add(img)
        }
        var spanCount = 1
        spanCount = if(size == 1){ 1 } else if(size == 2 || size == 4){  2 } else { 3 }
        recyclerView?.layoutManager = GridLayoutManager(context, spanCount)
        val adapter = PostImgAdapter(context, datas)
        recyclerView?.adapter = adapter
    }

    override fun getLayoutViewId(viewType: Int): Int = R.layout.item_post

}