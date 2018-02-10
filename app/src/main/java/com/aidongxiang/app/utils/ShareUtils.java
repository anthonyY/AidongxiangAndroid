package com.aidongxiang.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.aidongxiang.app.R;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.ToastUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;
import java.util.Map;

/**
 * 分享工具类
 * 
 * @author Anthony
 * 
 */
public class ShareUtils {

	private Context context;
	private String content, path, shareUrl, title;
	private ShareSuccessedListener listener = null;
	// private boolean isDirectShare = false;
	private UMShareAPI mShareAPI;
	private static final int TYPE_SHARE = 0x01;
	private static final int TYPE_LOGIN = 0x02;
	private static final String SINA_CALLBACK_URL = "";

	// private static final int TYPE_NULL = 0x03;
	public void setShareContent(String content) {
		this.content = content;
	}

	public void setShareImage(String path) {
		this.path = path;
	}

	public void setShareUrl(String url) {
		this.shareUrl = url;
	}

	public void setShareTitle(String title) {
		this.title = title;
	}

	public ShareUtils(Context context) {
		this.context = context;
		mShareAPI = UMShareAPI.get(context);
		com.umeng.socialize.utils.Log.LOG = false;
		// Android 6.0 要用下面内容
		// String[] mPermissionList = new
		// String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE,
		// Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SET_DEBUG_APP,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.GET_ACCOUNTS};
		//
		// ActivityCompat.requestPermissions((Activity)context,mPermissionList,
		// 100);

//		PlatformConfig.setWeixin(context.getString(R.string.weixinId),
//				context.getString(R.string.weixinSecret));


	}

	/**
	 * 登录监听
	 */
	private UMAuthListener umAuthLoginListener = new UMAuthListener() {
		@Override
		public void onStart(SHARE_MEDIA share_media) {

		}

		@Override
		public void onComplete(SHARE_MEDIA platform, int action,
				Map<String, String> data) {
			if (onLoginSuccessedListener != null) {
				onLoginSuccessedListener.onsuccessed(platform, data);
			}
		}

		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {
			LogUtil.w("登录失败" + action);
			Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			// Toast.makeText( context, "登录取消", Toast.LENGTH_SHORT).show();
		}
	};

	private String sinaUid;

	public String getSinaUid() {
		return sinaUid;
	}



	public void getUserData(final SHARE_MEDIA platfrom,
			UMAuthListener umListener) {
		// this.currentPlatfrom = platfrom;
		try {
			mShareAPI.getPlatformInfo((Activity) context, platfrom, umListener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onStart(SHARE_MEDIA share_media) {

		}
		@Override
		public void onResult(SHARE_MEDIA platform) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					ToastUtil.show(context, " 分享成功");
					if (listener != null) {
						listener.onsuccessed();
					}
				}
			});

		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					ToastUtil.show(context, " 分享失败");
				}
			});

		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			// Toast.makeText(context,platform + " 分享取消了",
			// Toast.LENGTH_SHORT).show();
		}
	};

	/**
	 * 直接分享
	 */
	public void directShare(SHARE_MEDIA platform) {
		if (content == null) {
			content = "";
		}
		if (title == null) {
			title = "";
		}
		UMImage image = null;
		if (!TextUtils.isEmpty(path)) {
			if(path.startsWith("http")){
				image = new UMImage(context, path);
			} else {
				File file = new File(path);
				if(file.exists()){
					image = new UMImage(context, file);
				}
			}

		} else {
			image = new UMImage(context, BitmapFactory.decodeResource(
					context.getResources(), R.mipmap.ic_launcher));
		}

		if(image != null){
			image.setDescription(content);
			image.setTitle(title);
		}
		ShareAction shareAction = new ShareAction((Activity) context).setPlatform(platform)
				.setCallback(umShareListener).withMedia(image).withText(title);

		if(!TextUtils.isEmpty(shareUrl)){
			UMWeb web = new UMWeb(shareUrl);
			web.setDescription(content);
			web.setTitle(title);
			web.setThumb(image);
			shareAction.withMedia(web);
		}
		shareAction.share();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		UMShareAPI.get(context).onActivityResult(requestCode,
				resultCode, data);
	}

	public void setShareSuccessedListener(ShareSuccessedListener listener) {
		this.listener = listener;
	}

	public interface ShareSuccessedListener {
		void onsuccessed();
	}

	private OnLoginSuccessedListener onLoginSuccessedListener;

	public void setOnLoginSuccessedListener(
			OnLoginSuccessedListener onLoginSuccessedListener) {
		this.onLoginSuccessedListener = onLoginSuccessedListener;
	}

	public interface OnLoginSuccessedListener {
		void onsuccessed(SHARE_MEDIA platform, Map<String, String> map);
	}
}
