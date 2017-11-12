package com.aidongxiang.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class CommonAdapter<T> extends BaseAdapter {

	protected Context mContext;
	protected List<T> mDataList;

	public CommonAdapter(Context context, List<T> list) {
		this.mContext = context;
		this.mDataList = list;
	}

	@Override
	public int getCount() {
		return mDataList == null? 0 : mDataList.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 获取 ViewHolder
		final ViewHolder holder = ViewHolder.get(mContext, convertView, parent,layoutId(), position);
		// 暴露该方法给子类实现更多业务逻辑
		convert(holder, getItem(position),position);
		
		return holder.getConvertView();
	}


	public void updateList(List<T> list){
		this.mDataList = list;
		this.notifyDataSetChanged();
	}
	public void addList(List<T> list){
		this.mDataList.addAll(list);
		this.notifyDataSetChanged();
	}

	public void update(){
		this.notifyDataSetChanged();
	}

	/**
	 * 暴露 viewholder 给子类，绑定控件并设置数据的相关操作
	 * @param holder
	 * @param item data
	 */
	public abstract void convert(ViewHolder holder, T item, int position);
	public abstract int layoutId();
}







