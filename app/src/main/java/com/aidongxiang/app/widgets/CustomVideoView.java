package com.aidongxiang.app.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * @author Anthony
 *         createTime 2017/12/3.
 * @version 1.0
 */

public class CustomVideoView extends VideoView {

    int width ;
    int height ;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
// 默认高度，为了自动获取到focus
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
//        int height = width;
// 这个之前是默认的拉伸图像
        if (this.width > 0 && this.height > 0)
        {
            width = this.width;
            height = this.height;
        }
        setMeasuredDimension(width, height);

    }
    public void setMeasure(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void pause() {
        super.pause();
        if (mListener != null) {
            mListener.onPause();
        }
    }

    @Override
    public void start() {
        super.start();
        if (mListener != null) {
            mListener.onPlay();
        }
    }

    private OnPlayStateListener mListener;

    public void setOnPlayStateListener(OnPlayStateListener mListener) {
        this.mListener = mListener;
    }

    public interface OnPlayStateListener {
        void onPlay();
        void onPause();
    }

}
