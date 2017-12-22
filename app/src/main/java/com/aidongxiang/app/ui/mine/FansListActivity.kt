package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.FansAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseListKtActivity
import com.aidongxiang.app.base.Constants.ARG_TYPE
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.business.model.Fans
import java.util.*

/**
 * 关注列表
 * 粉丝列表
 * @author Anthony
 * createTime 2017/12/22.
 * @version 1.0
 */
@ContentView(R.layout.activity_list)
class FansListActivity : BaseListKtActivity(){

    lateinit var adapter : FansAdapter
    val datas = ArrayList<Fans>()
    override fun getDatas(): List<Fans>? = datas

    override fun requestData() {

    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        val type = bundle.getInt(ARG_TYPE, 1)
        adapter = FansAdapter(this, datas)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter
        val header = TextView(this)
        if(type == 2){
            header.text = "我的粉丝"
            title = "我的粉丝"
        } else {
            header.text = "我的关注"
            title = "我关注的用户"
        }

        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val padding = resources.getDimension(R.dimen.margin_screen_left).toInt()
        header.setPadding(padding, padding, padding , padding)
        adapter.addHeaderView(header)
        setTestDatas()
    }

    private fun setTestDatas() {

        val random = Random()
        for(i in 0..10){
            val fans = Fans()
            fans.description = "的是大放送大放送防守打法胜多负少对方水电费手动阀"
            fans.name = "张晓霞"
            fans.id = i
            fans.isFocus = i%2
            fans.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
            datas.add(fans)
        }
        adapter.update()
    }

}