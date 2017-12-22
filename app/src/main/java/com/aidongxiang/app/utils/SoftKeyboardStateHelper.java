package com.aidongxiang.app.utils;


import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.aiitec.openapi.utils.LogUtil;

import java.util.LinkedList;
import java.util.List;


/**
 * 软键盘弹出与收起监听类
 *
 * @author Anthony
 */
public class SoftKeyboardStateHelper implements OnGlobalLayoutListener {

    private final List<SoftKeyboardStateListener> listeners = new LinkedList<SoftKeyboardStateListener>();
    private final View activityRootView;
    private int lastSoftKeyboardHeightInPx;
    private int height;
    private boolean isSoftKeyboardOpened = true;

    public SoftKeyboardStateHelper(View activityRootView) {
        this(activityRootView, false);
    }

    public SoftKeyboardStateHelper(View activityRootView, boolean isSoftKeyboardOpened) {
        height = activityRootView.getContext().getResources().getDisplayMetrics().heightPixels / 9;
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        // r will be populated with the coordinates of your view that area
        // still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);
        final int heightDiff = activityRootView.getRootView().getHeight()
                - (r.bottom - r.top);
        LogUtil.e("heightDiff:"+heightDiff);
        if (!isSoftKeyboardOpened && heightDiff > height) {
            isSoftKeyboardOpened = true;
            notifyOnSoftKeyboardOpened(heightDiff);
        } else if (isSoftKeyboardOpened && heightDiff < height) {
            isSoftKeyboardOpened = false;
            notifyOnSoftKeyboardClosed();
        }
    }

    public void setIsSoftKeyboardOpened(boolean isSoftKeyboardOpened) {
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
    }

    public boolean isSoftKeyboardOpened() {
        return isSoftKeyboardOpened;
    }

    /**
     * * Default value is zero (0) * @return last saved keyboard height in px
     */
    public int getLastSoftKeyboardHeightInPx() {
        return lastSoftKeyboardHeightInPx;
    }

    public void addSoftKeyboardStateListener(SoftKeyboardStateListener listener) {
        listeners.add(listener);
    }

    public void removeSoftKeyboardStateListener(
            SoftKeyboardStateListener listener) {
        listeners.remove(listener);
    }

    private void notifyOnSoftKeyboardOpened(int keyboardHeightInPx) {
        this.lastSoftKeyboardHeightInPx = keyboardHeightInPx;
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardOpened(keyboardHeightInPx);
            }
        }
    }

    private void notifyOnSoftKeyboardClosed() {
        for (SoftKeyboardStateListener listener : listeners) {
            if (listener != null) {
                listener.onSoftKeyboardClosed();
            }
        }
    }

    /***
     * 键盘弹出收起监听回调接口
     *
     * @author Anthony
     *
     */
    public interface SoftKeyboardStateListener {
        /**
         * 键盘弹起
         *
         * @param keyboardHeightInPx 键盘高度
         */
        void onSoftKeyboardOpened(int keyboardHeightInPx);

        /**
         * 键盘收起
         */
        void onSoftKeyboardClosed();
    }

}
