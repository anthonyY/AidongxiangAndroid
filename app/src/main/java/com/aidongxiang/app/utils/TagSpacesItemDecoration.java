package com.aidongxiang.app.utils;


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class TagSpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int spanCount;

    public TagSpacesItemDecoration(int spanCount, int space) {
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column
        int row = position / spanCount; // item column

        outRect.top = space;
        outRect.left = space;
        outRect.bottom = space;
        if(column == spanCount-1){
            outRect.right = space;
        } else {
            outRect.right = 0;
        }

        if (position < spanCount) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}