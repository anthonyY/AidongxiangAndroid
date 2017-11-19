package com.aidongxiang.app.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.aidongxiang.app.R;


public class PhotoDialog extends Dialog {
	public static final int THEME_DEFAULT = R.style.LoadingDialog;

	private Animation animation;
	protected Context context;
	private View view;
	private TextView tv_dialog_camera, tv_dialog_photo, tv_dialog_cancel;

	private boolean animEnable;

	public PhotoDialog(Context context) {
		super(context, THEME_DEFAULT);
		this.context = context;
		init(R.layout.dialog_photo);
	}

	private void init(int layoutId) {
		view = LayoutInflater.from(context).inflate(layoutId, null);
		findView(view);
		setContentView(view);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.gravity = Gravity.BOTTOM;
		params.width = WindowManager.LayoutParams.MATCH_PARENT;
		window.setAttributes(params);
	}

	/**
	 * 初始化布局，可重写
	 * 
	 * @param view
	 */
	protected void findView(View view) {
		tv_dialog_camera = (TextView) view.findViewById(R.id.tv_dialog_camera);
		tv_dialog_photo = (TextView) view.findViewById(R.id.tv_dialog_photo);
		tv_dialog_cancel = (TextView) view.findViewById(R.id.tv_photo_dialog_cacel);
		tv_dialog_camera.setOnClickListener(onClickListener);
		tv_dialog_photo.setOnClickListener(onClickListener);
		tv_dialog_cancel.setOnClickListener(onClickListener);
	}

	@Override
	public void show() {
		if (!isShowing()) {
			super.show();
		}
		if (view != null && animEnable) {
			animation = AnimationUtils.loadAnimation(context, R.anim.push_bottom_in);
			view.startAnimation(animation);
		}

	}
	public void setAnimationEnable(boolean flag) {
		animEnable = flag;
	}

	/**
	 * 隐藏部分控件
	 * @param id
	 */
	public void goneView(int id) {
		if (view != null) {
			View goneView = view.findViewById(id);
			if (goneView != null) {
				goneView.setVisibility(View.GONE);
			}
		}
	}

	public void visibilityView(int id) {
		if (view != null) {
			View goneView = view.findViewById(id);
			if (goneView != null) {
				goneView.setVisibility(View.VISIBLE);
			}
		}
	}

	public View getView() {
		return view;
	}

	@Override
	public void dismiss() {

		if (view != null) {
			view.clearAnimation();
		}
//		if (view != null && animEnable) {
//			animation = AnimationUtils.loadAnimation(context,
//					R.anim.push_bottom_out1);
//			view.startAnimation(animation);
//		}
		if (isShowing()) {
			super.dismiss();
		}
	}


	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(onButtonClickListener != null){
				switch(v.getId()){
					case R.id.tv_dialog_photo:
						onButtonClickListener.onPhotoClick(v);
						break;
					case R.id.tv_dialog_camera:
						onButtonClickListener.onCameraClick(v);
						break;
					case R.id.tv_photo_dialog_cacel:
						onButtonClickListener.onCancelClick(v);
						break;
				}

			}
		}
	};
	private OnButtonClickListener onButtonClickListener;

	public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
		this.onButtonClickListener = onButtonClickListener;
	}

	public interface OnButtonClickListener{
		void onPhotoClick(View view);
		void onCameraClick(View view);
		void onCancelClick(View view);
	}
}