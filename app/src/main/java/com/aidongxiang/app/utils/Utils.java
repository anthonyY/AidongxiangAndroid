package com.aidongxiang.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anthony
 * @version 1.0
 * createTime 2017/11/2.
 */

public class Utils {

    static String VIDEOS_DIR = Environment.getExternalStorageDirectory().toString() + "/file/com.aidongxiang.app/videos/";
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
		final String imagePath = VIDEOS_DIR +fileName;
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




    /**
     * make true current connect service is wifi
     * @param mContext
     * @return
     */
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    static Pattern pattern = Pattern.compile("[0-9]*");

    /**
     * 判断字符串是否全部是数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


    public static void switchFullScreen(Window window, boolean isFullScreen){
        if (isFullScreen){
            //设置为全屏
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }else{
            //设置为非全屏
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


    private final static Pattern ATTR_PATTERN = Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"]?\\s.*?>",Pattern.CASE_INSENSITIVE);

    //替换body里面的图片路径问题
    public static String getAbsSource(String source, String bigpath) {
        try {
            Matcher matcher = ATTR_PATTERN.matcher(source);
            List<String> list = new ArrayList<String>();  // 装载了匹配整个的Tag
            List<String> list2 = new ArrayList<String>(); // 装载了src属性的内容
            while (matcher.find()) {
                list.add(matcher.group(0));
                list2.add(matcher.group(1));
            }
            StringBuilder sb = new StringBuilder();
            String[] dataSplit =  source.split("<img");
            if(dataSplit != null && dataSplit.length > 0){
                String data0 = dataSplit[0];
                // 连接<img之前的内容
                sb.append(data0);
            }
            // 遍历list
            for (int i = 0; i < list.size(); i++) {
                String imagePath = list2.get(i).substring(1);

                String relace1= list.get(i).replace("img", "img style=\"width:100%\" ");
                String relaceAfter = "";
                if(!imagePath.startsWith("http")){
                    relaceAfter = relace1.replace(list2.get(i), // 对每一个Tag进行替换
                            bigpath + imagePath);
                } else {
                    relaceAfter = relace1;
                }

                sb.append(relaceAfter);
            }
            String[] data = source.split("(?:<img[^<>]*?\\s.*?['\"]?\\s.*?>)+");
            if(data != null && data.length > 1){
                sb.append(data[1]);
            }

            return sb.toString();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

}
