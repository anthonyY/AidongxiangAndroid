package com.aidongxiang.app.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aidongxiang.app.R;
import com.aidongxiang.business.model.SearchText;
import com.hhl.library.OnInitSelectedPosition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anthony
 * @version 1.0
 * createTime 2018/1/20.
 */
public class Tag2Adapter<T> extends BaseAdapter implements OnInitSelectedPosition {

    private Context context;
    private List<T> datas = new ArrayList<T>();
    public Tag2Adapter(Context context){
        this.context = context;
    }
    public Tag2Adapter(Context context, List<T> datas){
        this(context);
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public T getItem(int position) {
        return datas.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_tag, null, false);

        TextView textView = view.findViewById(R.id.tv_item_title);
        T t = datas.get(position);

        if(t instanceof SearchText){
            textView.setText(((SearchText) t).getText());
        } else {
            textView.setText(t.toString());
        }
        return view;
    }

    public void addAll(List<T> tempDatas) {
        datas.addAll(tempDatas);
        notifyDataSetChanged();
    }
    public void add(T t) {
        datas.add(t);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<T> tempDatas) {
        datas.clear();
        addAll(tempDatas);
    }
    public void clear() {
        datas.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean isSelectedPosition(int position) {
        return position % 2 == 0;
    }

    public void update(){
        notifyDataSetChanged();
    }
}
