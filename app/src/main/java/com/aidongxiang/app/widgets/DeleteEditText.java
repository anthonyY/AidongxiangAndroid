package com.aidongxiang.app.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.aiitec.openapi.utils.ScreenUtils;

/**
 * @author Anthony
 * @createTime 2016-06-20
 * 带删除按钮的输入框，点击X就删除文字，并提供一个删除监听接口
 * 删除按钮icon用drawableRight 设置
 */
public class DeleteEditText extends AppCompatEditText {

    private int touchPadding;

    public DeleteEditText(Context context){
        super(context);
        init(null);
    }
    public DeleteEditText(Context context, AttributeSet attrs){
        super(context, attrs);
        init(attrs);
    }
    public DeleteEditText(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        init(attrs);
    }
    private void init(AttributeSet attrs){
        touchPadding = ScreenUtils.dip2px(getContext(), 4);
    }

    public void setTouchPadding(int padding) {
        this.touchPadding = padding;
    }

    private long downTime ;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                long upTime = System.currentTimeMillis();
                Drawable[] compoundDrawables = getCompoundDrawables();
                if(compoundDrawables.length > 2 && compoundDrawables[2] != null){
                    //获取到drawableRight 的宽高
                    Rect bounds = compoundDrawables[2].getBounds();
                    if(upTime-downTime < 500 ){
                        //点击区域比删除的icon每个方向都大touchPadding
                        //左边是 屏幕宽减去DrawablePadding 再减去 删除 icon宽度 再减触摸容错范围宽度
                        int left =  getWidth()-getCompoundDrawablePadding()-bounds.width()-touchPadding;
                        int top =  (getHeight()-bounds.height())/2-touchPadding;
                        int right = getWidth()-getCompoundDrawablePadding()+touchPadding;
                        int bottom = top+bounds.height()+touchPadding+touchPadding;
                        //短按才算，长按不算
                        if(event.getX() > left && event.getX()<right && event.getY() > top && event.getY()<bottom){
                            //点击删除按钮区域
                            setText("");
                            if(onDeleteListener != null){
                                onDeleteListener.onDelete();
                            }
                        }
                    }
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }
    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    /**
     * 删除按钮监听接口
     */
    public interface OnDeleteListener{
        void onDelete();
    }
}
