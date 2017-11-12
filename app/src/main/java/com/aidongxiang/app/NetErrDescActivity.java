package com.aidongxiang.app;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.aidongxiang.app.annotation.ContentView;
import com.aidongxiang.app.base.BaseActivity;


@ContentView(R.layout.activity_net_err_desc)
public class NetErrDescActivity extends BaseActivity {

//    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.tv_net_permission_set)
    TextView tv_net_permission_set;

    @Override
    protected void initView() {
        setToolBar(toolbar);
        setTitle(R.string.net_desc);
        String appName = getResources().getString(R.string.app_name);
        String desc_set_permission = getResources().getString(R.string.hint_net_desc_set_permission, appName);
        tv_net_permission_set.setText(desc_set_permission);

    }

//    @OnClick(R.id.btn_set_net)
//    public void onClicSet() {
//        setNetwork(this);//这个页面本来就是无网络点击进来的，也不方便刷新，所以就不管网络状态了，点击直接跳转
//    }


    //    我们做开发的都知道，由于Android的SDK版本不同(尤其在Android 3.0 及后面)的版本中,UI及显示方式都发生了比较大的变化,打开网络设置为例,代码如下:
//            1，我们判断网络是否打开：
    public static boolean isConnectNet(Context context) {
        boolean bisConnFlag = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null) {
            bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }

    //  2. 未开启网络时打开设置界面(如果不写在Activity里面则不需要参数),在相应的位置调用即可
    private void setNetwork(Context context) {
        Intent intent = null;
        //判断手机系统的版本  即API大于10 就是3.0或以上版本
        if (android.os.Build.VERSION.SDK_INT > 10) {
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            ComponentName component = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
            intent.setComponent(component);
            intent.setAction("android.intent.action.VIEW");
        }
        context.startActivity(intent);
    }

}
