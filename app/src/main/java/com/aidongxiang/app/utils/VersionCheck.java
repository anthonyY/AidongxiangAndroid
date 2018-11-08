package com.aidongxiang.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


public class VersionCheck {

	private Handler handler = new Handler();
	private onNewVersionListener onNewVersionListener;
	private long totalBytes = 0;
	public static final String notification_id = "chxgo_notification_id";
	public static final String notification_channel = "chxgo_notification_channel_version_update";

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
				createDownloadNotification(totalBytes, currnet);
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
				if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上

					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
					FileProvider7.setIntentDataAndType(context, intent, type, file, true);

//					Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", file);//在AndroidManifest中的android:authorities值
//					intent.setDataAndType(apkUri, type);
				} else {
					intent.setDataAndType(Uri.fromFile(file), type);
				}

				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
					boolean installAllowed = context.getPackageManager().canRequestPackageInstalls();
					boolean hasPermission = PermissionsUtils.checkSelfPermission(context, android.Manifest.permission.REQUEST_INSTALL_PACKAGES );
					try {
						context.startActivity(intent);
					} catch (Exception e){
						e.printStackTrace();
					}
				} else {
					try {
						context.startActivity(intent);
					} catch (Exception e){
						e.printStackTrace();
					}
				}
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
	 * @param current 当前下载进度
	 * @param total   总大小
	 */
	@SuppressLint("NewApi")
	private void createDownloadNotification(long current, long total) {
		if (total == 0) {
			return;
		}


		if (nm == null) {
			nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		RemoteViews contentView = new RemoteViews(context.getPackageName(),
				R.layout.notification_version);
		int count = (int) (current * 100 / total);
		if(progressDialog != null){
			progressDialog.setTitle(""+count+"%");
		}
		contentView.setTextViewText(R.id.n_title, "当前进度：" + count + "% ");
        contentView.setProgressBar(R.id.n_progress, 100, count, false);
//		contentView.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher);

		Notification notify;
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
			NotificationChannel channel = new NotificationChannel(notification_id, notification_channel, NotificationManager.IMPORTANCE_HIGH);

			// 设置通知出现时不震动
			channel.enableVibration(false);
			channel.setVibrationPattern(new long[]{0});

			nm.createNotificationChannel(channel);
			notify = new Notification.Builder(context, notification_id)
					.setCustomContentView(contentView)
					.setWhen(System.currentTimeMillis())
//                    .setContentTitle(title)
//                    .setContentText(content)
					.setSmallIcon(R.mipmap.ic_launcher)
					.setAutoCancel(false).build();

		} else {
			Notification.Builder builder = new Notification.Builder(context)
					.setContent(contentView)
					.setWhen(System.currentTimeMillis())
					.setAutoCancel(false)
					.setSmallIcon(R.mipmap.ic_launcher);

			notify = builder.build();
		}


		notify.contentView.setProgressBar(R.id.n_progress, 100, count, false);
		nm.notify(downloadId, notify);// 显示通知

		if (current >= total) {
			nm.cancel(downloadId);
		}
	}

}
