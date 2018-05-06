package com.aidongxiang.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.aidongxiang.app.R;
import com.aidongxiang.app.ui.Main2Activity;
import com.aidongxiang.business.model.Result;
import com.aidongxiang.business.response.VersionCheckResponse;
import com.aiitec.openapi.net.AIIRequest;
import com.aiitec.openapi.net.AIIResponse;
import com.aiitec.openapi.net.ProgressResponseBody;
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.DateUtil;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.PacketUtil;
import com.aiitec.openapi.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class VersionCheck {

	private Handler handler = new Handler();
	private onNewVersionListener onNewVersionListener;
	private long totalBytes = 0;

	public void setOnNewVersionListener(VersionCheck.onNewVersionListener onNewVersionListener) {
		this.onNewVersionListener = onNewVersionListener;
	}

	public interface onNewVersionListener{
		void onNew();
	}
	private Context context;
	private ProgressDialog progressDialog;
	private NotificationManager nm;
	private int downloadId;
	// 版本检查返回数组的第二个才是安卓，第一个是ios
	private static final int androidIndex = 0x01;
	private Dialog forceddialog;
	private boolean showAgain;

	public VersionCheck(Context context) {
		this.context = context;
		progressDialog = new ProgressDialog(context);
		nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

	}

	public void startCheck(final String url, boolean showAgain) {
		this.showAgain = showAgain;
		AIIRequest aiiRequest = new AIIRequest(context);
		aiiRequest.sendOthers(url, null, new AIIResponse<VersionCheckResponse>(context, showAgain){
			@Override
			public void onSuccess(VersionCheckResponse response, int index) {
				super.onSuccess(response, index);
				if (response == null) {
					return;
				}
				ArrayList<Result> results = response.getResults();
				if (results != null && results.size() > androidIndex) {
					checkVersionState(results);
				}
			}

			@Override
			public void onFailure(String content, int index) {
				super.onFailure(content, index);
				handler.post(new Runnable() {
					@Override
					public void run() {
						ToastUtil.show(context, "请检查您的网络！");
					}
				});
			}
		}, 1);
	}



	private void checkVersionState(ArrayList<Result> results) {

		String localVersion = PacketUtil.getVersionName(context);

		String onlineVersion = results.get(androidIndex).getVersion();

		if (onlineVersion.compareToIgnoreCase(localVersion) > 0) { // 有更新
			String downloadUrl = results.get(androidIndex).getTrackViewUrl();
			String desc = results.get(androidIndex).getDescription();
			if (results.get(androidIndex).getForcedUpdate() == 1) {// 强制更新
				showForcedDialog(onlineVersion, desc, downloadUrl);
			} else {

				Message msg = downloadHandler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putString("onlineVersion", onlineVersion);
				bundle.putString("description", desc);
				bundle.putString("trackViewUrl", downloadUrl);
				msg.setData(bundle);
				msg.what = 1;
				if(showAgain){
					downloadHandler.sendMessage(msg);
				} else {
					String today = DateUtil.date2Str(new Date(), "yyyy-MM-dd");
					boolean checkVersionToday = AiiUtil.getBoolean(context, "checkVersionToday"+today, false);
					if(!checkVersionToday){
						downloadHandler.sendMessage(msg);
						AiiUtil.putBoolean(context, "checkVersionToday"+today, true);
					}
				}

			}
		} else {
			if(onNewVersionListener != null){
				onNewVersionListener.onNew();
			}
			if(showAgain){
				ToastUtil.show(context, "已是最新版本");
			}
		}
	}


	private Handler downloadHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message arg0) {
			switch (arg0.what) {
			case 1:
				String onlineVersion = arg0.getData()
						.getString("onlineVersion");
				String description = arg0.getData().getString("description");
				String trackViewUrl = arg0.getData().getString("trackViewUrl");
				showVersionDialog(onlineVersion, description, trackViewUrl);
				break;
			}
			return false;
		}
	});

	/**
	 * 显示版本更新对话框
	 * 
	 * @param onlineVersion
	 *            线上版本
	 * @param description
	 *            版本更新描述
	 * @param downloadUrl
	 *            下载地址
	 */
	private void showVersionDialog(final String onlineVersion,
			final String description, final String downloadUrl) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("新版本更新" + "(V" + onlineVersion + ")");
		builder.setMessage(description);
		builder.setPositiveButton("马上更新",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downLoad(downloadUrl);
					}
				});
		builder.setNegativeButton("以后再说",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		if (!((Activity) context).isFinishing()) {
			builder.create().show();
		}

	}

	/**
	 * 显示强制更新对话框
	 * 
	 * @param onlineVersion
	 *            线上版本
	 * @param description
	 *            更新内容描述
	 * @param downloadUrl
	 *            下载地址
	 */
	private void showForcedDialog(final String onlineVersion,
			final String description, final String downloadUrl) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("发现" + onlineVersion + "有重大版本更新，请更新");
		builder.setMessage(description);
		builder.setPositiveButton("马上更新",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downLoad(downloadUrl);
					}
				});
		forceddialog = builder.create();
		forceddialog.setCanceledOnTouchOutside(false);
		if (!((Activity) context).isFinishing()) {
			forceddialog.show();
		}

	}

//	/**
//	 * 将 String 对象编码为 JSON格式，只需处理好特殊字符
//	 *
//	 * @param str
//	 *            String 对象
//	 * @return String:JSON格式
//	 * @version 1.0
//	 * @date 2015-10-11
//	 * @Author zhou.wenkai
//	 */
//	private static String stringToJson(final String str) {
//		if (str == null || str.length() == 0) {
//			return "\"\"";
//		}
//		final StringBuilder sb = new StringBuilder(str.length() + 2 << 4);
//		for (int i = 0; i < str.length(); i++) {
//			final char c = str.charAt(i);
//			// c == '\"' ? "\\\"" : c == '\\' ? "\\\\" :
//			sb.append(c == '/' ? "\\/" : c == '\b' ? "\\b" : c == '\f' ? "\\f"
//					: c == '\n' ? "" : c == '\r' ? "" : c == '\t' ? "" : c);
//		}
//		return sb.toString();
//	}

	/**
	 * 下载
	 * 
	 * @param url
	 */
	private void downLoad(String url) {
		if (forceddialog != null && forceddialog.isShowing()) {
			forceddialog.dismiss();
		}
		if (!AiiUtil.isSDCardEnable()) {
			ToastUtil.show(context, "sdcard不存在或不可用，请检查sdcard");
			return;
		}
		int ran = new Random().nextInt(100);
		String dir = "";
		String fileName = "t" + ran + "_"
				+ url.substring(url.lastIndexOf("/") + 1, url.length());

		/*
		 * List<String> paths = SDCardUtils.getExtSDCardPath(); if(paths != null
		 * && paths.size() > 0){ dir = paths.get(0);//外置sdcard因权限问题还未解决，暂时不用 }
		 * else
		 */
		if (!TextUtils.isEmpty(AiiUtil.getSDCardPath())) {
			dir = AiiUtil.getSDCardPath();
		}
		// else {
		// dir = Environment.getDataDirectory().getAbsolutePath();
		// }
		dir = dir + "/download/";// download/
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdir();
		}
		progressDialog.setMessage("正在下载......");
		final String fileName2 = dir + fileName;
		LogUtil.e("-----下载----"+url+fileName2);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
		AIIRequest aiiRequest = new AIIRequest(context);
		aiiRequest.download(url,  new File(fileName2), new ProgressResponseBody.ProgressListener(){

			@Override
			public void onPreExecute(long contentLength) {
				totalBytes = contentLength;
			}

			@Override
			public void update(long totalBytes, long currnet, int progress) {
				createDownloadNotification(totalBytes, currnet, progress);
			}

			@Override
			public void onSuccess(File file) {
// 下载完成就会重新安装，再次打开那就是相当于第一次打开了
				AiiUtil.putBoolean(context, "isFirstApp", true);
				LogUtil.e("----下载完成----file-"+file);
				// String path = responseInfo.result.getAbsolutePath();
				Intent intent = new Intent();
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(Intent.ACTION_VIEW);
				// 获得下载好的文件类型
				String type = "application/vnd.android.package-archive";
				intent.setDataAndType(Uri.fromFile(file), type);
				((Activity) context).startActivity(intent);
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onFailure() {
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		});
	}

	/**
	 * 创建下载进度的通知
	 * 
	 * @param current
	 *            当前下载进度
	 * @param total
	 *            总大小
	 */
	@SuppressLint("NewApi")
	private void createDownloadNotification(long total, long current, int progress) {

		if (nm == null)
			nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

		RemoteViews contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification_version);
		contentView.setTextViewText(R.id.n_title, "当前进度：" + progress + "% ");
		contentView.setProgressBar(R.id.n_progress, 100, 0, false);


		Notification notify;
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {

			Intent notificationIntent1 = new Intent(context, Main2Activity.class);
			notificationIntent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// addflag设置跳转类型
			PendingIntent contentIntent1 = PendingIntent.getActivity(context,
					0, notificationIntent1, 0);
			notify = new Notification();
			notify.when = System.currentTimeMillis();
			notify.number = 1;
			notify.contentView = contentView;
			notify.flags |= Notification.FLAG_NO_CLEAR; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
			notify.sound = null;
			notify.vibrate = null;
			notify.contentIntent = contentIntent1;

		} else {

			Notification.Builder builder = new Notification.Builder(context)
					.setContent(contentView)
					.setWhen(System.currentTimeMillis())// 设置时间发生时间
					.setAutoCancel(false)// 设置可以清除
					.setSmallIcon(R.mipmap.ic_launcher);

			notify = builder.build();
		}
		notify.contentView.setProgressBar(R.id.n_progress, 100, progress, false);
		nm.notify(downloadId, notify);// 显示通知

		if (current >= total) {
			nm.cancel(downloadId);
		}
	}
}
