package com.aidongxiang.app.ui.audio

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.adapter.HomeAudioAdapter
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.App
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
import kotlin.collections.ArrayList

/**
 * 视频列表Fragment
 * @author Anthony
 * createTime 2017/11/4.
 * @version 1.0
 */
@ContentView(R.layout.fragment_list_with_edit)
class AudioListFragment : BaseListKtFragment(){

    val datas = ArrayList<Video>()
    lateinit var adapter : HomeAudioAdapter
    override fun getDatas(): List<*>? = datas
    val random = Random()
    var isEdit = false
    lateinit var deleteDialog : CommonDialog
    var deletePosition = -1
    var type = TYPE_HOME
    var categoryId = -1

    companion object {
        val ARG_TYPE = "type"
        val ARG_CATEGORY_ID = "categoryId"
        /**主页 带广告*/
        val TYPE_HOME = 1
        /**下载 不带广告*/
        val TYPE_DOWNLOAD = 4
        /**下载 不带广告*/
        val TYPE_COLLECT = 2
        /**观看历史 不带广告*/
        val TYPE_HISTORY = 3
        fun newInstance(type : Int, categoryId : Int) : AudioListFragment {
            val fragment = AudioListFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_TYPE, type)
            bundle.putInt(ARG_CATEGORY_ID, categoryId)
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun requestData() {
        requestAudioList()
    }

    override fun init(view: View) {
        super.init(view)
        adapter = HomeAudioAdapter(context!!, datas)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter

        arguments?.let {
            type = it.getInt(ARG_TYPE)
            categoryId = it.getInt(ARG_CATEGORY_ID)
        }
//        if(type == TYPE_HOME){
//            headerView = LayoutInflater.from(activity).inflate(R.layout.advertisement, null, false) as AdvertisementLayout
//            adapter.addHeaderView(headerView)
//            setHeaderData()
//        }
        adapter.setOnRecyclerViewItemClickListener { v, position ->
            val id = datas[position-1].audioId
            switchToActivity(AudioDetailsActivity::class.java, ARG_ID to id)
        }
        if(type == TYPE_COLLECT){
            adapter.setOnRecyclerViewItemLongClickListener { v, position ->
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
        requestAudioList()

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

    fun requestAudioList(){
        if(type == 4){
            //下载的读数据库
            return
        }
        val query = ListRequestQuery("AudioList")
        query.table.page = page
        query.action = AIIAction.valueOf(type)
        val where = Where()
        where.audioType = 2
        where.categoryId = categoryId
        query.table.where = where
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

    /**
     * 请求收藏
     */
    private fun requestCollection(open : Int, id: Long) {

        val query = SubmitRequestQuery("FavoritesSwitch")
        query.action = AIIAction.TWO
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
}