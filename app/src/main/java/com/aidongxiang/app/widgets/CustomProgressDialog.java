package com.aidongxiang.app.widgets;



import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.aidongxiang.app.R;


/**
 * 自定义ProgressDialog
 */
public class CustomProgressDialog extends Dialog {

	    public CustomProgressDialog(Context context){
	        super(context, R.style.CustomProgressDialog);
			setContentView(R.layout.customprogressdialog);
			getWindow().getAttributes().gravity = Gravity.CENTER;
	    }
	     
	    public CustomProgressDialog(Context context, int theme) {
	        super(context, theme);
			setContentView(R.layout.customprogressdialog);
			getWindow().getAttributes().gravity = Gravity.CENTER;
	    }


	    /**
	     *
	     * [Summary]
	     *       setMessage 提示内容
	     * @param strMessage
	     * @return
	     *
	     */
	    public CustomProgressDialog setMessage(String strMessage){
	        TextView tvMsg = (TextView)findViewById(R.id.id_tv_loadingmsg);
	         
	        if (tvMsg != null && !TextUtils.isEmpty(strMessage)){
	            tvMsg.setText(strMessage);
				tvMsg.setVisibility(View.VISIBLE);
	        } else {
				tvMsg.setVisibility(View.GONE);
			}
	         
	        return this;
	    }
}