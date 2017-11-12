//package com.aidongxiang.app.base;
//
//import android.app.Activity;
//import android.app.Application;
//import android.content.Context;
//import android.content.Intent;
//import android.support.v4.app.FragmentActivity;
//
//import com.aiitec.openapi.utils.LogUtil;
//
//import java.util.ArrayList;
//
///**
// * @author Anthony
// * @version 1.0
// * createTime 2017/11/04.
// */
//
//public class App extends Application {
//
//    private ArrayList<FragmentActivity> activities = new ArrayList<>();
//    private static App app;
//    private static Context context;
//
//    public static App getInstance() {
//        return app;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        App.app = this;
//        App.context = getApplicationContext();
//        LogUtil.showLog = true;
//        Constants.VIDEOS_DIR = context.getFilesDir().getAbsolutePath()+"/video";
//    }
//    /**
//     * 关闭所有页面
//     *
//     * @param instance activity实例对象
//     */
//    public void removeInstance(FragmentActivity instance) {
//        activities.remove(instance);
//    }
//
//    /**
//     * 把所有页面添加到列表统一管理
//     *
//     * @param instance activity实例对象
//     */
//    public void addInstance(FragmentActivity instance) {
//        activities.add(instance);
//    }
//
//    /**
//     * 退出 关闭所有页面并杀死所有线程
//     */
//    public void exit() {
//        try {
//            for (FragmentActivity activity : activities) {
//                if (activity != null) {
//                    activity.finish();
//                }
//            }
//        } catch (Exception e) {
//        } finally {
//            android.os.Process.killProcess(android.os.Process.myPid());
//            System.exit(0);
//        }
//        // AiiQueryQueue.getInstance().destroy();
//    }
//
//
//
//    /**
//     * 退出 关闭所有页面 不杀死后台线程
//     */
//    public void closeAllActivity(){
//        try {
//            for (Activity activity : activities) {
//                if (activity != null) {
//                    activity.finish();
//                }
//            }
//        } catch (Exception e) {
//        }
//    }
//
//    /**
//     * 模拟触摸home键
//     */
//    public void touchHome(){
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
//    }
//}
