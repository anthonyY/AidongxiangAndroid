package com.aidongxiang.app.ui.square

import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.PostAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.ui.home.HomeFragment
import com.aiitec.moreschool.base.BaseListKtFragment

/**
 *
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list)
class PostListFragment : BaseListKtFragment(){

    var adapter : PostAdapter?= null
    val datas = ArrayList<String>()
    override fun getDatas(): List<*>? = datas

    override fun requestData() {
        Handler().postDelayed({onLoadFinish()}, 1000)
    }

    override fun init(view: View) {
        super.init(view)

        adapter = PostAdapter(context!!, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter

        for(title in HomeFragment.imgs){
            datas.add(title)
        }
        adapter?.update()
    }

}