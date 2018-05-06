package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.adapter.FansAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.business.model.Fans
import com.aidongxiang.business.response.FansListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.net.AIIResponse
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
        requestUserList()
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
        adapter.setOnViewInItemClickListener(CommonRecyclerViewAdapter.OnViewInItemClickListener { v, position ->
            if(v?.id == R.id.iv_item_avatar){
                val id = datas[position-1].id
                switchToActivity(PersonCenterActivity::class.java, ARG_ID to id)
            }
        }, R.id.iv_item_avatar)

        requestUserList()

    }

    private fun addHeaderView() {

        val header = TextView(activity)
        header.text = "已屏蔽的用户"
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        val padding = resources.getDimension(R.dimen.margin_screen_left).toInt()
        header.setPadding(padding, padding, padding , padding)
        adapter.addHeaderView(header)
    }


    private fun requestUserList(){
        val listQuery = ListRequestQuery("UserList")
        listQuery.action = AIIAction.FOUR
        listQuery.table.page = page
        App.aiiRequest.send(listQuery, object : AIIResponse<FansListResponseQuery>(activity){
            override fun onSuccess(response: FansListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                getUserList(response!!)
            }

            override fun onCache(content: FansListResponseQuery?, index: Int) {
                super.onCache(content, index)
                getUserList(content!!)
            }

            override fun onFailure(content: String?, index: Int) {
                super.onFailure(content, index)
                onNetError()
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }
        })
    }

    private fun getUserList(response: FansListResponseQuery) {
        total = response.total
        if(page == 1){
            datas.clear()
        }
        response.users?.let { datas.addAll(it) }

        adapter.update()
        if(datas.size == 0){
            onNoData()
        }
    }


}