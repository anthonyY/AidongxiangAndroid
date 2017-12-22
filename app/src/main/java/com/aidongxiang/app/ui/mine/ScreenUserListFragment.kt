package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.FansAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.business.model.Fans
import com.aiitec.moreschool.base.BaseListKtFragment
import java.util.*

/**
 * 屏蔽的用户列表
 * @author Anthony
 * createTime 2017/11/20.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list)
class ScreenUserListFragment : BaseListKtFragment(){

    lateinit var adapter : FansAdapter
    val datas = ArrayList<Fans>()
    var random = Random()

    override fun getDatas(): List<*>? = datas

    override fun requestData() {
        requestNewsList()
    }

    private fun requestNewsList() {
        for(i in 0..10){
            val fans = Fans()
            fans.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
            fans.id = i
            fans.name = "张三"
            fans.isFocus = -1
            fans.description = "是大放送大放送防守打法胜多负少的发送到"

            datas.add(fans)
        }
        if(activity != null){
            adapter.update()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = FansAdapter(activity, datas)
        requestData()
    }

    override fun init(view: View) {
        super.init(view)

        recyclerView?.layoutManager = LinearLayoutManager(activity)
        addHeaderView()
        recyclerView?.adapter = adapter
        adapter.setOnRecyclerViewItemClickListener { v, position ->
//            switchToActivity(CommonWebViewActivity::class.java, Constants.ARG_TITLE to "新闻详情", Constants.ARG_URL to "http://www.baidu.com")

        }

    }

    private fun addHeaderView() {

        val header = TextView(activity)
        header.text = "已屏蔽的用户"
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val padding = resources.getDimension(R.dimen.margin_screen_left).toInt()
        header.setPadding(padding, padding, padding , padding)
        adapter.addHeaderView(header)
    }

}