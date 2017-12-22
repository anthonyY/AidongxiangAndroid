package com.aidongxiang.app.widgets

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.TextView
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.CommonRecyclerViewAdapter
import com.aidongxiang.app.adapter.CommonRecyclerViewHolder
import kotlinx.android.synthetic.main.dialog_item.*

/**
 * 一条一条的那种dialog
 * @author Anthony
 * createTime 2017/12/3.
 * @version 1.0
 */
class ItemDialog(context : Context) : AbsCommonDialog(context){

    lateinit var datas : ArrayList<String>
    lateinit var adapter : ItemAdapter
    override fun widthScale(): Float = 0.8f
    override fun layoutId(): Int = R.layout.dialog_item

    override fun findView(view: View?) {
        super.findView(view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        datas = ArrayList()
        adapter = ItemAdapter(context, datas)
        recyclerView.adapter = adapter

        adapter.setOnRecyclerViewItemClickListener { _, position ->
            dismiss()
            onItemClick?.invoke(datas[position], position)
        }
    }
    fun setItems(newDatas : ArrayList<String>){
        datas.clear()
        datas.addAll(newDatas)
        adapter.update()
    }


    inner class ItemAdapter(context : Context, datas: MutableList<String>) : CommonRecyclerViewAdapter<String>(context, datas){
        override fun convert(h: CommonRecyclerViewHolder, item: String, position: Int) {
            val tvItemName =  h.getView<TextView>(R.id.tv_item_name)
            tvItemName?.text = item
        }
        override fun getLayoutViewId(viewType: Int): Int = R.layout.item_dialog
    }

    var onItemClick : ((item: String, position: Int) -> Unit) ?= null
    fun setOnItemClickListener(onItemClick: ((item: String, position: Int) -> Unit)) {
        this.onItemClick = onItemClick
    }

}