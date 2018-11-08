package com.aidongxiang.app.ui.mine

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.adapter.FansAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseListKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_TYPE
import com.aidongxiang.app.event.RefreshMicrobolgEvent
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.business.model.Fans
import com.aidongxiang.business.request.FocusSwitchRequestQuery
import com.aidongxiang.business.response.FansListResponseQuery
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.net.AIIResponse
import org.greenrobot.eventbus.EventBus
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
    var type = 1
    override fun getDatas(): List<*>? = datas

    override fun requestData() {
        requestUserList()
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        type = bundle.getInt(ARG_TYPE, 1)
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

        adapter.setOnViewInItemClickListener(CommonRecyclerViewAdapter.OnViewInItemClickListener { v, position ->
            if(v?.id == R.id.iv_item_avatar){
                val id = datas[position-1].id
                switchToActivity(PersonCenterActivity::class.java, Constants.ARG_ID to id)
            } else if(v?.id == R.id.tv_item_status){
                val data = datas[position-1]
                val isFocus = data.isFocus
                if(isFocus != 2 && type == 2){
                    requestFocusSubmit(data.id.toLong(), 1, position-1)
                }
            }
        }, R.id.iv_item_avatar, R.id.tv_item_status)
        requestUserList()
    }

    /**
     * 关注 / 取消关注
     */
    private fun requestFocusSubmit(id: Long, open: Int, position: Int) {
        if(Constants.user == null){
            switchToActivity(LoginActivity::class.java)
            return
        }
        val query = FocusSwitchRequestQuery()
        query.namespace = "FocusSwitch"
        query.userId = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(this, progressDialog) {
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
//                onRefresh()
                if(position >= 0 && position < datas.size){
                    if(open == 1){
                        datas[position].isFocus = 2
                        toast("关注成功")
                    } else {
                        toast("已取消关注")
                        datas[position].isFocus = 1
                    }
                    adapter.notifyItemChanged(position+2)
                }
                EventBus.getDefault().post(RefreshMicrobolgEvent())

            }
        })
    }
    private fun requestUserList(){
        val listQuery = ListRequestQuery("UserList")
        listQuery.action = AIIAction.valueOf(type)
        listQuery.table.page = page
        App.aiiRequest.send(listQuery, object : AIIResponse<FansListResponseQuery>(this){
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
        //我的粉丝
        if(type == 2){

        } else {
            //我关注的
            datas.filter { it.isFocus !=2 }.forEach {  it.isFocus = 0 }
        }


        adapter.update()
        if(datas.size == 0){
            onNoData()
        }
    }


}