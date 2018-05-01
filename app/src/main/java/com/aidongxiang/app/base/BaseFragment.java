package com.aidongxiang.app.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aidongxiang.app.R;
import com.aidongxiang.app.utils.ContentViewUtils;
import com.aidongxiang.app.widgets.CustomProgressDialog;
import com.umeng.analytics.MobclickAgent;


/**
 * Fragment 基类
 */
public abstract class BaseFragment extends Fragment {

    public CustomProgressDialog progressDialog;
    public Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = CustomProgressDialog.createDialog(getActivity());
        setHasOptionsMenu(true);

    }

    public void setToolBar(Toolbar toolBar) {
        this.toolbar = toolBar;
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    protected abstract void initView(View view);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = ContentViewUtils.inject(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Context context;
        context = getActivity();
        if (context == null) {
            context = App.Companion.getApp().getApplicationContext();
        }
        if (progressDialog == null) {
            progressDialog = CustomProgressDialog.createDialog(context);
        }

    }

    public void setTitle(CharSequence title) {
        if (toolbar != null) {
            TextView tv_title = (TextView) toolbar.findViewById(R.id.tv_title);
            if (tv_title != null) {
                tv_title.setText(title);
            }
        }

    }

    public void setTitle(int titleRes) {
        if (toolbar != null) {
            TextView tv_title = (TextView) toolbar.findViewById(R.id.tv_title);
            if (tv_title != null) {
                tv_title.setText(titleRes);
            }
        }

    }


    public void switchToActivity(Class<?> clazz) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void switchToActivity(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void switchToActivityForResult(Class<?> clazz, Bundle bundle, int requestCode) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) intent.putExtras(bundle);
        startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void switchToActivity(Class<?> clazz, int anim_in, int anim_out) {
        Intent intent = new Intent(getActivity(), clazz);
        startActivity(intent);
        getActivity().overridePendingTransition(anim_in, anim_out);
    }

    public void switchToActivity(Class<?> clazz, Bundle bundle, int anim_in, int anim_out) {
        Intent intent = new Intent(getActivity(), clazz);
        if (bundle != null) intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(anim_in, anim_out);
    }


    public void onResume() {
        super.onResume();
          MobclickAgent.onPageStart(getClass().getSimpleName()); //统计页面
    }

    public void onPause() {
        super.onPause();
           MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  aiiRequest.cancelHttpRequest();
    }

    public void progressDialogShow() {
        try {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void progressDialogDismiss() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
