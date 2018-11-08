package com.aidongxiang.app.ui.square

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.adapter.CommonRecyclerViewHolder
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseListKtActivity
import com.aidongxiang.app.base.Constants
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import kotlinx.android.synthetic.main.activity_address_select.*


/**
 *
 * @author Anthony
 * createTime 2018-11-04.
 * @version 1.0
 */
@ContentView(R.layout.activity_address_select)
class AddressSelectActivity : BaseListKtActivity(){

    lateinit var adapter : AddressAdapter
    var poiSearch : PoiSearch ?= null
    val datas = ArrayList<PoiItem>()
    override fun getDatas(): List<*>? = datas

    override fun requestData() {
        poiSearch?.query?.pageNum = page
        poiSearch?.searchPOIAsyn()
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

        adapter = AddressAdapter(this, datas)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.adapter = adapter

        var searchBound : PoiSearch.SearchBound ?= null
        var cityCode = ""
        if(Constants.location != null){
            cityCode = Constants.location!!.adCode!!
            //点附近2000米内的搜索结果
            searchBound = PoiSearch.SearchBound(LatLonPoint(Constants.location!!.latitude, Constants.location!!.longitude), 1000)

        } else {
            //默认黎平县内搜索
            cityCode = "522631"
        }
        val nearQuery = PoiSearch.Query("", "", cityCode)

        val nearPoiSearch = PoiSearch(this, nearQuery)
        searchBound?.let { nearPoiSearch.bound = searchBound }

        nearPoiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener{
            override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {}
            override fun onPoiSearched(result: PoiResult?, p1: Int) {
                result?.pois?.let {
                    datas.clear()
                    datas.addAll(it)
                    adapter.update()
                }
            }
        })
        nearPoiSearch.searchPOIAsyn()

        btn_search.setOnClickListener {
            if(!TextUtils.isEmpty(et_search.text.toString())){
                val query = PoiSearch.Query(et_search.text.toString(), "", cityCode)
                query.pageSize = 20// 设置每页最多返回多少条poiitem
                query.pageNum = page//设置查询页码
                poiSearch = PoiSearch(this, query)

                poiSearch?.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener{
                    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {}
                    override fun onPoiSearched(result: PoiResult?, p1: Int) {
                        result?.pois?.let {
                            datas.clear()
                            datas.addAll(it)
                            adapter.update()
                        }
                    }
                })
                poiSearch?.searchPOIAsyn()
            } else {
                datas.clear()
                adapter.update()
            }
        }
        adapter.setOnRecyclerViewItemClickListener { _, position ->
            if(position > 0){
                val data = datas[position-1]
                intent.putExtra("address", data.title)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        et_search.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                btn_search.performClick()
                return@OnEditorActionListener true
            }
            false
        })
    }

    class AddressAdapter(context: Context, datas : MutableList<PoiItem>) : CommonRecyclerViewAdapter<PoiItem>(context, datas){
        override fun convert(h: CommonRecyclerViewHolder?, item: PoiItem?, position: Int) {
            val tvSnippet = h?.getView<TextView>(R.id.tv_item_snippet)
            val tvTitle = h?.getView<TextView>(R.id.tv_item_title)
            tvTitle?.text = item?.title
            tvSnippet?.text = item?.snippet

        }

        override fun getLayoutViewId(viewType: Int): Int = R.layout.item_address
    }
}