package com.aidongxiang.app.ui.video

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeVideoAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.widgets.CommonDialog
import com.aidongxiang.business.model.Video
import com.aidongxiang.business.model.Where
import com.aidongxiang.business.response.VideoListResponseQuery
import com.aiitec.moreschool.base.BaseListKtFragment
import com.aiitec.openapi.json.enums.AIIAction
import com.aiitec.openapi.model.ListRequestQuery
import com.aiitec.openapi.model.ResponseQuery
import com.aiitec.openapi.model.SubmitRequestQuery
import com.aiitec.openapi.net.AIIResponse
import kotlinx.android.synthetic.main.fragment_list_with_edit.*
import java.util.*

/**
 * 视频列表Fragment
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list_with_edit)
class VideoListFragment : BaseListKtFragment(){

    val datas = ArrayList<Video>()
    lateinit var deleteDialog : CommonDialog
    lateinit var adapter : HomeVideoAdapter
    override fun getDatas(): List<*>? = datas
    val random = Random()
    var isEdit = false
//    var headerView : AdvertisementLayout ?= null
    var type = TYPE_HOME
    var categoryId : Long = -1
    var deletePosition = -1

    companion object {
        val ARG_TYPE = "type"
        val ARG_CATEGORY_ID = "categoryId"
        /**主页 带广告*/
        val TYPE_HOME = 1
        /**下载 不带广告*/
        val TYPE_DOWNLOAD = 4
        /**收藏 不带广告*/
        val TYPE_COLLECT = 2
        /**观看历史 不带广告*/
        val TYPE_HISTORY = 3
        fun newInstance(type : Int, categoryId : Long) : VideoListFragment{
            val fragment = VideoListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_TYPE, type)
            bundle.putLong(ARG_CATEGORY_ID, categoryId)
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun requestData() {
        requestVideoList()
    }

    override fun init(view: View) {
        super.init(view)
        adapter = HomeVideoAdapter(activity!!, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
        arguments?.let {
            type = it.getInt(ARG_TYPE)
            categoryId = it.getLong(ARG_CATEGORY_ID)
        }
//        if(type == TYPE_HOME){
//            headerView = LayoutInflater.from(activity).inflate(R.layout.advertisement, null, false) as AdvertisementLayout
//            adapter.addHeaderView(headerView)
//            setHeaderData()
//        }
        adapter.setOnRecyclerViewItemClickListener { _, position ->
            val id = datas[position-1].audioId
            val title = datas[position-1].name
            switchToActivity(VideoDetails2Activity::class.java, ARG_ID to id, Constants.ARG_TITLE to title)
        }
        if(type == TYPE_COLLECT){
            adapter.setOnRecyclerViewItemLongClickListener { _, position ->
                if(position > 0){
                    val item = datas[position-1]
                    deletePosition = position-1
                    deleteDialog.setTitle("取消收藏${item.name}?")
                    deleteDialog.show()
                }
            }
        }
        tv_select_all.setOnClickListener {
            for(data in datas){
                data.isSelected = true
            }
            adapter.update()
        }
        tv_delete.setOnClickListener {
            datas.filter { it.isSelected }.forEach { datas.remove(it) }
            adapter.update()
        }

        deleteDialog = CommonDialog(activity!!)
        deleteDialog.setOnConfirmClickListener {
            deleteDialog.dismiss()
            if(deletePosition >= 0){
                requestCollection(2, datas[deletePosition].audioId)
            }

        }
        requestVideoList()

    }

    /**
     * 请求收藏
     */
    private fun requestCollection(open : Int, id: Long) {

        val query = SubmitRequestQuery("FavoritesSwitch")
        query.action = AIIAction.ONE
        query.id = id
        query.open = open
        App.aiiRequest.send(query, object : AIIResponse<ResponseQuery>(activity, progressDialog){
            override fun onSuccess(response: ResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                datas.removeAt(deletePosition)
                adapter.notifyDataSetChanged()
            }
        })
    }


    fun setIsEdit(isEdit : Boolean ){
        this.isEdit = isEdit
        adapter.setIsEdit(isEdit)
        if(isEdit){
            ll_bottom_btn.visibility = View.VISIBLE
        } else {
            ll_bottom_btn.visibility = View.GONE
        }
    }




    fun requestVideoList(){
        if(type == 4){
            //下载的读数据库
            return
        }
        val query = ListRequestQuery("AudioList")
        query.table.page = page
        val where = Where()
        where.categoryId = categoryId.toInt()
        where.audioType = 1
        query.table.where = where
        query.action = AIIAction.valueOf(type)
        App.aiiRequest.send(query, object : AIIResponse<VideoListResponseQuery>(activity, progressDialog){
            override fun onSuccess(response: VideoListResponseQuery?, index: Int) {
                super.onSuccess(response, index)
                response?.let {
                    getVideoList(it)
                }
            }

            override fun onFinish(index: Int) {
                super.onFinish(index)
                onLoadFinish()
            }

            override fun onCache(response: VideoListResponseQuery?, index: Int) {
                super.onCache(response, index)
                response?.let {
                    getVideoList(it)
                }
            }
        })
    }

    /**
     * 获取视频/音频数据，设置到adapter里
     */
    private fun getVideoList(response: VideoListResponseQuery) {
        total = response.total
        if(page == 1){
            datas.clear()
        }
        if(response.audios != null){
            datas.addAll(response.audios!!)
        }
        adapter.update()
        checkIsEmpty()
    }
}