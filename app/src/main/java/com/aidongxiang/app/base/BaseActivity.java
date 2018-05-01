package com.aidongxiang.app.base;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aidongxiang.app.R;
import com.aidongxiang.app.utils.ContentViewUtils;
import com.aidongxiang.app.utils.StatusBarUtil;
import com.aidongxiang.app.widgets.CustomProgressDialog;
import com.umeng.analytics.MobclickAgent;

/**
 * Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {


    public CustomProgressDialog progressDialog;
    //    public SystemBarTintManager tintManager;
    private View mFocusView;// 当前点击的按钮
    protected int mActivityType;

    protected Toolbar toolbar;
    /**
     * 禁止重启app
     */
    private boolean forbitRestartApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ContentViewUtils.inject(this);
        //绑定activity
        StatusBarUtil.addCustomStatusBar(this);
        progressDialog = CustomProgressDialog.createDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);

        App myApp = (App) getApplication();
        myApp.addInstance(this);

        initView();

    }

    protected abstract void initView();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setToolBar(Toolbar toolBar) {
        this.toolbar = toolBar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void setTitle(CharSequence title) {
        TextView tv_title = findViewById(R.id.tv_title);
        if (tv_title != null) {
            tv_title.setText(title);
        }
    }

    public void setTitle(int titleRes) {
        TextView tv_title = findViewById(R.id.tv_title);
        if (tv_title != null) {
            tv_title.setText(titleRes);
        }
    }


    public synchronized void progressDialogDismiss() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void progressDialogShow() {
        try {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void switchToActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void switchToActivity(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
    }
    public void switchToActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void switchToActivity(Context context, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void switchToActivityForResult(Context context, Class<?> clazz,
                                          Bundle bundle, int requestCode) {
        Intent intent = new Intent(context, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
    public void switchToActivityForResult(Class<?> clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
    public void switchToActivityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    public void switchToActivityForResult(Context context, Class<?> clazz,
                                          int requestCode) {
        Intent intent = new Intent(context, clazz);
        startActivityForResult(intent, requestCode);
//        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void switchToActivity(Context context, Class<?> clazz, int anim_in,
                                 int anim_out) {
        Intent intent = new Intent(context, clazz);
        startActivity(intent);
        overridePendingTransition(anim_in, anim_out);
    }

    public void switchToActivity(Context context, Class<?> clazz,
                                 Bundle bundle, int anim_in, int anim_out) {
        Intent intent = new Intent(context, clazz);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(anim_in, anim_out);
    }


    public void setActivityType(int type) {
        mActivityType = type;
    }



    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); // 统计时长
        //	Bugtags.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        //Bugtags.onPause(this);
    }



    @Override
    public void finish() {
//        Utils.HideKeyboard(this);
        super.finish();
    }


    /**
     * listview滑动时里面记录的item有些需要重新生成 所以要清空，避免mFocusView记录的对象与新生成的view不一致
     */
    public void clearFocusView() {
        this.mFocusView = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.Companion.getApp().removeInstance(this);
    }


    @Override
    protected void onStop() {
//        Utils.HideKeyboard(this);
        super.onStop();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setForbitRestartApp(boolean forbitRestartApp) {
        this.forbitRestartApp = forbitRestartApp;
    }



}
