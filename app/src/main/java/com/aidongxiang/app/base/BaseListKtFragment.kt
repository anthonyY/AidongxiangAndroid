package com.aiitec.moreschool.base

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.aidongxiang.app.NetErrDescActivity
import com.aidongxiang.app.R
import com.aidongxiang.app.base.BaseKtFragment
import com.jcodecraeer.xrecyclerview.XRecyclerView


/**
 * 列表 Fragment 基类
 * @author Anthony
 * CreatedTime 2017/5/29
 */
abstract class BaseListKtFragment : BaseKtFragment() , XRecyclerView.LoadingListener {

    var recyclerView: XRecyclerView? = null
    var tv_empty_nodata: TextView?= null
    var ll_no_net : LinearLayout?= null
    var tv_net_guide : TextView?= null
    var total: Int = 0
    var page = 1

    protected abstract fun getDatas(): List<*>?

    override fun init(view: View) {
        recyclerView = view.findViewById<XRecyclerView?>(R.id.recyclerView)
        tv_empty_nodata = view.findViewById<TextView?>(R.id.tv_no_data)
        tv_net_guide =  view.findViewById<TextView?>(R.id.tv_net_guide)
        ll_no_net =  view.findViewById<LinearLayout?>(R.id.ll_no_net)
        recyclerView?.setLoadingListener(this)
        tv_empty_nodata?.setOnClickListener { onRefresh() }
        ll_no_net?.setOnClickListener { onRefresh() }
//        点击网络设置的引导栏
        tv_net_guide?.setOnClickListener { switchToActivity(NetErrDescActivity::class.java) }
    }

    /**
     * 无数据
     */
    protected fun checkIsEmpty() {
        if (tv_empty_nodata != null && getDatas() != null && getDatas()!!.isEmpty()) {
            recyclerView?.emptyView = tv_empty_nodata
            tv_empty_nodata?.visibility = View.VISIBLE
        } else {
            tv_empty_nodata?.visibility = View.GONE
        }

    }



    /**
     * 无网络
     */
    protected fun onNetError() {
        if (getDatas()?.size === 0 && ll_no_net != null) {
            tv_empty_nodata?.visibility = View.GONE
            recyclerView?.emptyView = ll_no_net
            ll_no_net?.visibility = View.VISIBLE
        } else {
            ll_no_net?.visibility = View.GONE
        }
    }

    override fun onRefresh() {
        page = 1
        requestData()
    }

    protected abstract fun requestData()

    override fun onLoadMore() {
        if (getDatas()!!.size >= total) {
            Handler(Looper.getMainLooper()).post {/* toast( R.string.no_more);*/ onLoadFinish() }
        } else {
            page++
            requestData()
        }
    }

    fun onLoadFinish(){
        recyclerView?.refreshComplete()
        recyclerView?.loadMoreComplete()
    }
}