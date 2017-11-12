package com.aidongxiang.app.widgets;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.aidongxiang.app.R;
import com.aiitec.openapi.utils.ScreenUtils;



public abstract class AbsCommonDialog extends Dialog {
    public static final int THEME_DEFAULT = R.style.LoadingDialog;

    protected Context context;
    private View view;
    protected TextView tv_title, tv_content;
    protected TextView tv_dialog_confirm;
    protected TextView tv_dialog_cancel;

    abstract float widthScale() ;
    abstract int layoutId() ;
    abstract int animStyle();

    private View.OnClickListener onConfirmClickListener;
    private View.OnClickListener onCancelClickListener;

    public void setConfirmBtnText(String confirmBtnText) {
        if (!TextUtils.isEmpty(confirmBtnText) && tv_dialog_confirm != null) {
            tv_dialog_confirm.setText(confirmBtnText);
        }
    }

    public AbsCommonDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        init(layoutId());
    }


    public AbsCommonDialog(Context context) {
        super(context, THEME_DEFAULT);
        this.context = context;
        init(layoutId());
    }

    protected void init(int layoutId) {
        view = LayoutInflater.from(context).inflate(layoutId, null);
        setContentView(view);
        findView(view);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER_VERTICAL;
        window.setAttributes(setLayoutParams(params));
        if(animStyle() < 0){
            setAnimationStyle(R.style.dialogAnimationStyle);
        } else {
            setAnimationStyle(animStyle());
        }

    }

    public void setAnimationStyle(int animStyle) {
        getWindow().setWindowAnimations(animStyle);
    }

    /**
     * 初始化布局，可重写
     *
     * @param view
     */
    protected void findView(View view) {
        tv_title = (TextView) view.findViewById(R.id.tv_dialog_title);
        tv_content = (TextView) view.findViewById(R.id.tv_dialog_content);
        tv_dialog_confirm = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        tv_dialog_cancel = (TextView) view.findViewById(R.id.tv_dialog_cancel);

        if (tv_dialog_confirm != null && onConfirmClickListener != null) {
            tv_dialog_confirm.setOnClickListener(onConfirmClickListener);
        }
        if (tv_dialog_cancel != null) {
            if (onCancelClickListener != null) {
                tv_dialog_cancel.setOnClickListener(onCancelClickListener);
            } else {
                tv_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancel();
                    }
                });
            }
        }
    }

    @Override
    public void show() {
        if (!isShowing()) {
            super.show();
        }
    }

    public WindowManager.LayoutParams setLayoutParams(
            WindowManager.LayoutParams lp) {
        lp.width = (int) (ScreenUtils.getScreenWidth(context) * widthScale()); // 设置宽度
        return lp;
    }

    public void setTitle(String title) {
        if (tv_title != null && !TextUtils.isEmpty(title)) {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }
    }

    public void setTitle(CharSequence title) {
        if (tv_title != null && !TextUtils.isEmpty(title)) {
            tv_title.setText(title);
            tv_title.setVisibility(View.VISIBLE);
        }
    }

    public void setTitle(@StringRes int titleId) {
        if (tv_title != null && titleId > 0) {
            tv_title.setText(titleId);
            tv_title.setVisibility(View.VISIBLE);
        }
    }

    public void setContent(String content) {
        if (tv_content != null && !TextUtils.isEmpty(content)) {
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(content);
        }
    }

    /**
     * 隐藏部分控件
     *
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
        if (isShowing()) {
            super.dismiss();
        }
    }

    /**
     * 确认按钮监听
     *
     * @param listener
     */
    public void setOnConfirmClickListener(View.OnClickListener listener) {
        onConfirmClickListener = listener;
        if (tv_dialog_confirm != null) {
            tv_dialog_confirm.setOnClickListener(onConfirmClickListener);
        }
    }

    /**
     * 取消按钮监听
     *
     * @param listener
     */
    public void setOnCancelClickListener(View.OnClickListener listener) {
        onCancelClickListener = listener;
        if (tv_dialog_cancel != null) {
            tv_dialog_cancel.setOnClickListener(onCancelClickListener);
        }
    }


}