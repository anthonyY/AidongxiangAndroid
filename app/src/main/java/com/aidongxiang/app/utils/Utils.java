package com.aidongxiang.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.aidongxiang.app.base.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anthony
 * @version 1.0
 * createTime 2017/11/2.
 */

public class Utils {

    /**
     * 返回中文和英文字符的统一长度，中文字符占2个字节长度，英文占1个
     *
     * @param str
     * @return
     */
    public static int getUnicodeLength(String str) {
        if (TextUtils.isEmpty(str)) {
            return 0;
        }
        try {
            return (new String(str.getBytes("gb2312"), "iso-8859-1").length());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str.length();
    }
    /**
     * 高亮显示
     *
     * @param all
     *            全部文字
     * @param target
     *            需要加高亮文字内容 "[0-9|.]" 正则，匹配数字
     * @param color
     *            高亮颜色
     * @return
     */
    public static SpannableStringBuilder highlightText(String all,
                                                       String target, int color) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(all);
        CharacterStyle span = null;
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(all);
        while (m.find()) {
            span = new ForegroundColorSpan(color);// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static SpannableStringBuilder getDifferenceSixeText(String all,
                                                               String target, int spSize) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(all);
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(all);
        while (m.find()) {
            AbsoluteSizeSpan span = new AbsoluteSizeSpan(spSize, true);// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public static SpannableString getSizeSpanUseSp(String str, int start, int end, int spSize) {
        SpannableString ss = new SpannableString(str);
        ss.setSpan(new AbsoluteSizeSpan(spSize, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    /** 显示虚拟键盘 */
    public static void showKeyboard(View v, Context context) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /** 隐藏软键盘 */
    public static void hideKeyboard(Activity context) {
        try {
            if (context != null && context.getCurrentFocus() != null) {
                InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(context.getCurrentFocus()
                            .getApplicationWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
	 * 给ImageView设置视频截图， 先读取出视频截图的Bitmap， 然后存储到sdcard， 下次读取的时候，如果有这个文件，就直接读文件
	 * @param filePath 视频路径
	 * @param iv imageView 控件
	 * @param width 宽
	 * @param height 高
	 */
	public static void setVideoThumbnailForImageView(final String filePath, final ImageView iv, final int width, final int height) {
		iv.setImageBitmap(null);
		String fileName = "";
		int index = filePath.lastIndexOf("/");
		int index2 = filePath.lastIndexOf(".");
		if(index != -1){
			fileName = filePath.substring(index + 1, index2);
		}
		final String imagePath = Constants.VIDEOS_DIR +fileName;
		//本来想用AsynTask的，但是经常会有很多线程同时执行，AsynTask非常慢，受不了
		final android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback(){
			@Override
			public boolean handleMessage(Message msg) {
				if(msg.what ==1){
					Bitmap bitmap = (Bitmap) msg.obj;
					if(bitmap != null){
						iv.setImageBitmap(bitmap);
					}
				}
				return false;
			}
		});
		File file = new File(imagePath);
		if(file.exists()) {
		    GlideImgManager.loadFile(iv.getContext(), filePath, iv);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Bitmap img = createVideoThumbnail(filePath, width, height);
					saveBitmapToSdcard(imagePath, img);
					Message msg = new Message();
					msg.obj = img;
					msg.what = 1;
					handler.sendMessage(msg);
				}
			}).start();
		}
	}

    public static void saveBitmapToSdcard(String path, Bitmap mBitmap){
        if(mBitmap == null)
            return;
        File f = new File(path);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 50, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

}
