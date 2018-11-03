package com.aidongxiang.app.utils;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.aidongxiang.app.base.App;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.ScreenUtils;

import java.lang.reflect.Method;
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
        height = activityRootView.getContext().getResources().getDisplayMetrics().heightPixels / 8;
        this.activityRootView = activityRootView;
        this.isSoftKeyboardOpened = isSoftKeyboardOpened;
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout() {
        final Rect r = new Rect();
        activityRootView.getWindowVisibleDisplayFrame(r);
        int heightDiff = 0;
        //是否开启全面屏
        boolean isOpenFullscreen = Settings.Global.getInt(App.app.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
        if(isOpenFullscreen){
            heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
        } else {
            heightDiff = ScreenUtils.getScreenHeight(App.app) - (r.bottom - r.top);
        }
//


        LogUtil.w("isSoftKeyboardOpened:"+isSoftKeyboardOpened+"   heightDiff:"+heightDiff+"    height:"+height);
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


    /**
     *  判断存在NavigationBar，是否有虚拟按钮
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }
    /**
     * 获取虚拟按钮ActionBar的高度
     *
     * @param activity activity
     * @return ActionBar高度
     */
    public static int getActionBarHeight(Activity activity) {
        TypedValue tv = new TypedValue();
        if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
        }
        return 0;
    }
}
