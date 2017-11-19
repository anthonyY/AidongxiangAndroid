package com.aidongxiang.app.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aidongxiang.app.R;


/**
 * @author Anthony
 *         createTime 2016/12/19.
 * @version ${VERSION}
 */

public class CustomToast {


    public static void show(Context context, String text){
        Toast toast;
        if(context instanceof Activity){
            toast = new Toast(context.getApplicationContext());
        } else {
            toast = new Toast(context);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        toast.setView(view);

        textView.setText(text);
        toast.show();
    }
}
