package com.aidongxiang.app.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.aidongxiang.app.NetErrDescActivity
import com.aidongxiang.app.R
import com.jcodecraeer.xrecyclerview.XRecyclerView


/**
 * 列表基类 Kotlin
 * @author Anthony
 * createTime 2017-06-07
 */
abstract class BaseListKtActivity : BaseKtActivity(), XRecyclerView.LoadingListener {

    var recyclerView: RecyclerView?= null
    var tv_empty_nodata: TextView?= null
    var ll_no_net : LinearLayout?= null
    var tv_net_guide : TextView?= null
    internal var total: Int = 0
    internal var page = 1
    override fun init(savedInstanceState: Bundle?) {
        recyclerView = findViewById(R.id.recyclerView)
        tv_empty_nodata = findViewById(R.id.tv_no_data)
        tv_net_guide = findViewById(R.id.tv_no_data)
        ll_no_net = findViewById(R.id.ll_no_net)
        if(recyclerView != null && recyclerView is XRecyclerView){
            (recyclerView as XRecyclerView).setLoadingListener(this)
        }

        tv_empty_nodata?.setOnClickListener { onRefresh() }
        ll_no_net?.setOnClickListener { onRefresh() }
//        点击网络设置的引导栏
        tv_net_guide?.setOnClickListener { switchToActivity(NetErrDescActivity::class.java) }
    }

    protected abstract fun getDatas(): List<*>?


    /**
     * 无数据
     */
    protected fun checkIsEmpty() {
        if (tv_empty_nodata != null && getDatas() != null && getDatas()!!.isEmpty()) {
            if(recyclerView != null && recyclerView is XRecyclerView) {
                (recyclerView as XRecyclerView).emptyView = tv_empty_nodata
                tv_empty_nodata?.visibility = View.VISIBLE
            } else {
                tv_empty_nodata?.visibility = View.GONE
            }
        }
        tv_empty_nodata?.setText(R.string.no_data)
    }



    /**
     * 无网络
     */
    protected fun onNetError() {
        if (getDatas()?.size == 0 && ll_no_net != null) {
            tv_empty_nodata?.visibility = View.GONE
            if(recyclerView != null && recyclerView is XRecyclerView) {
                (recyclerView as XRecyclerView).emptyView = ll_no_net
                ll_no_net?.visibility = View.VISIBLE
            } else {
                ll_no_net?.visibility = View.GONE
            }
        }
    }


    override fun onRefresh() {
        page = 1
        requestData()
    }

    protected abstract fun requestData()

    override fun onLoadMore() {
        if (getDatas()!!.size >= total) {

            Handler(Looper.getMainLooper()).post { /*toast(R.string.no_more);*/ onLoadFinish() }
        } else {
            page++
            requestData()
        }
    }

    fun onLoadFinish(){
        if(recyclerView != null && recyclerView is XRecyclerView){
            (recyclerView as XRecyclerView).refreshComplete()
            (recyclerView as XRecyclerView).loadMoreComplete()
        }

    }


}