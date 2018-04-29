package com.aidongxiang.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.RelativeLayout;

import com.aidongxiang.app.base.Api;
import com.aidongxiang.app.base.App;
import com.aidongxiang.app.base.Constants;
import com.aidongxiang.app.widgets.CustomVideoView;
import com.aiitec.openapi.utils.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anthony
 * @version 1.0
 *          createTime 2017/11/2.
 */

public class Utils {

//    static String VIDEOS_DIR = Environment.getExternalStorageDirectory().toString() + "/file/com.aidongxiang.app/videos/";

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
     * @param all    全部文字
     * @param target 需要加高亮文字内容 "[0-9|.]" 正则，匹配数字
     * @param color  高亮颜色
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

    /**
     * 显示虚拟键盘
     */
    public static void showKeyboard(View v, Context context) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 隐藏软键盘
     */
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
     *
     * @param filePath 视频路径
     */
    public static void setVideoThumbnailForImageView(final String filePath, final OnVideoThumbCallback listener) {
        String fileName = "";
        int index = filePath.lastIndexOf("/");
        int index2 = filePath.lastIndexOf(".");
        if (index != -1) {
            fileName = filePath.substring(index + 1, index2)+".jpg";
        }
        final String imagePath = Constants.INSTANCE.getVIDEOS_DIR() + fileName;
        LogUtil.e("Thumbnail:"+imagePath);
        File file = new File(imagePath);
        if (file.exists()) {
            if (listener != null) {
                listener.getVideoThumb(imagePath);
            }
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap img = createVideoThumbnail(filePath);
                    saveBitmapToSdcard(imagePath, img);
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.getVideoThumb(imagePath);
                            }
                        }
                    }, 100);
                }
            }).start();
        }
    }

    public static void saveBitmapToSdcard(String path, Bitmap mBitmap) {
        if (mBitmap == null)
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
            LogUtil.e("存储："+path+"      bitmap:"+mBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap createVideoThumbnail(String url) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int width = 640;
        int height = 480;
        try {
            retriever.setDataSource(url,new Hashtable<String,String>());

            String videoHeightStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String videoWidthStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String videoRotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            if(videoRotation.equals("90") || videoRotation.equals("270")){
                height = Integer.parseInt(videoWidthStr); // 视频高度
                width = Integer.parseInt(videoHeightStr); // 视频宽度
            } else {
                height = Integer.parseInt(videoHeightStr); // 视频高度
                width = Integer.parseInt(videoWidthStr); // 视频宽度
            }
            bitmap = retriever.getFrameAtTime();
//            缩略图的分辨率：MINI_KIND、MICRO_KIND、FULL_SCREEN_KIND
//            bitmap = ThumbnailUtils.createVideoThumbnail(url, MediaStore.Video.Thumbnails.MICRO_KIND);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        LogUtil.e(">>>>bitmap:"+bitmap);
        if(bitmap == null){
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        LogUtil.e(">>>>width:"+width+"   height:"+height+"     bitmap："+bitmap);
        return bitmap;
    }


    /**
     * make true current connect service is wifi
     *
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
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }


    public static void switchFullScreen(Window window, boolean isFullScreen) {
        if (isFullScreen) {
            //设置为全屏
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            //设置为非全屏
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(lp);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }


    private final static Pattern ATTR_PATTERN = Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"]?\\s.*?>", Pattern.CASE_INSENSITIVE);

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
            String[] dataSplit = source.split("<img");
            if (dataSplit != null && dataSplit.length > 0) {
                String data0 = dataSplit[0];
                // 连接<img之前的内容
                sb.append(data0);
            }
            // 遍历list
            for (int i = 0; i < list.size(); i++) {
                String imagePath = list2.get(i).substring(1);

                String relace1 = list.get(i).replace("img", "img style=\"width:100%\" ");
                String relaceAfter = "";
                if (!imagePath.startsWith("http")) {
                    relaceAfter = relace1.replace(list2.get(i), // 对每一个Tag进行替换
                            bigpath + imagePath);
                } else {
                    relaceAfter = relace1;
                }

                sb.append(relaceAfter);
            }
            String[] data = source.split("(?:<img[^<>]*?\\s.*?['\"]?\\s.*?>)+");
            if (data != null && data.length > 1) {
                sb.append(data[1]);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 设置微博的视频信息
     *
     * @param videoview       视频控件
     * @param videoPath       视频路径
     * @param rlItemVideoPlay 包裹视频相关的大布局
     * @param ivVideoThumb    视频缩略图控件
     * @param ivItemVideoPlay 视频播放按钮
     * @param loading         加载进度条
     */
    public static void setMicoblogVideoInfo(final Context context, final CustomVideoView videoview, String videoPath, View rlItemVideoPlay, final ImageView ivVideoThumb, final ImageView ivItemVideoPlay, final View loading) {

        if (!TextUtils.isEmpty(videoPath)) {
            if(rlItemVideoPlay != null){
                rlItemVideoPlay.setVisibility(View.VISIBLE);
            }

            String path;
            if (videoPath.startsWith("http")) {
                path = videoPath;
            }
            else if (videoPath.startsWith("/storage") || videoPath.startsWith("/sdcard")) {
                path = videoPath;
            } else {
                path = Api.IMAGE_URL + videoPath;
            }
            Utils.setVideoThumbnailForImageView(path, new OnVideoThumbCallback() {
                @Override
                public void getVideoThumb(String thumb) {
                    if( context != null){
                        GlideImgManager.loadFile(context, thumb, ivVideoThumb);
                    }
                }
            });

            videoview.setVideoPath(path);
            resetVideoWidth(context, videoview, path);

//            ivItemVideoPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    videoview.start();
//                }
//            });
            videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    ivVideoThumb.setVisibility(View.GONE);
                }
            });

            videoview.setOnPlayStateListener(new CustomVideoView.OnPlayStateListener() {
                @Override
                public void onPlay() {
                    ivItemVideoPlay.setVisibility(View.GONE);
                    ivVideoThumb.setVisibility(View.GONE);
                }

                @Override
                public void onPause() {
                    ivItemVideoPlay.setVisibility(View.VISIBLE);
                    ivVideoThumb.setVisibility(View.VISIBLE);
                }

            });
            videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    ivItemVideoPlay.setVisibility(View.VISIBLE);
                    ivVideoThumb.setVisibility(View.VISIBLE);
                }
            });
            videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                        @Override
                        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
                            if (percent == 100) {
                                loading.setVisibility(View.GONE);
                            } else {
                                loading.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
        } else {
            if(rlItemVideoPlay != null) {
                rlItemVideoPlay.setVisibility(View.GONE);
            }
        }

    }


    private static void resetVideoWidth(final Context context, final CustomVideoView videoView, final String path) {
        App.app.cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                resetVideoWidthOnThread(context, videoView, path);
            }
        });
    }

    private static void resetVideoWidthOnThread(Context context, final CustomVideoView videoView, String path) {

        LogUtil.e("path:"+path);
        int videoHeight = 0;
        int videoWidth = 0;
//        int rotation = 0;
        try {
            MediaMetadataRetriever retr = new MediaMetadataRetriever();
            retr.setDataSource(path, new HashMap<String, String>());
            String videoHeightStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String videoWidthStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String rotationStr = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            LogUtil.e("rotationStr:"+rotationStr);
            if(rotationStr.equals("90") || rotationStr.equals("270")){
                videoWidth = Integer.parseInt(videoHeightStr); // 视频宽度
                videoHeight = Integer.parseInt(videoWidthStr);// 视频高度
            } else {
                videoHeight = Integer.parseInt(videoHeightStr); // 视频高度
                videoWidth = Integer.parseInt(videoWidthStr); // 视频宽度
            }

//            rotation = Integer.parseInt(rotationStr); // 视频宽度
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (videoWidth > 0 && videoHeight > 0) {

            final int finalVideoWidth = videoWidth;
            final int finalVideoHeight = videoHeight;
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int mVideoWidth = 0;
                    int mVideoHeight = 0;
                    float videoScale = finalVideoWidth * 1.0f / finalVideoHeight;
                    LogUtil.e("finalVideoWidth:"+finalVideoWidth+"   finalVideoHeight: "+finalVideoHeight+"      "+videoScale);
                    View parentView = (View) videoView.getParent();
                    if (videoScale != 0f) {
//                        if (finalVideoWidth > finalVideoHeight) {
                            mVideoHeight = parentView.getMeasuredHeight();
//                            if(finalRotation == 90 || finalRotation == 270){
//                                mVideoWidth = (int) (mVideoHeight / videoScale);
//                            } else {
                                mVideoWidth = (int) (mVideoHeight * videoScale);
//                            }
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight);
                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                            parentView.setLayoutParams(layoutParams);
//                        } else {
//                            mVideoHeight = parentView.getMeasuredHeight();
//                            if(finalRotation == 90 || finalRotation == 270){
//                                mVideoWidth = (int) (mVideoHeight / videoScale);
//                            } else {
//                                mVideoWidth = (int) (mVideoHeight * videoScale);
//                            }
//                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mVideoWidth, mVideoHeight);
//                            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//                            parentView.setLayoutParams(layoutParams);
//                        }
                    }
                    // 设置surfaceview画布大小
                    videoView.getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                    // 重绘VideoView大小，这个方法是在重写VideoView时对外抛出方法
                    videoView.setMeasure(mVideoWidth, mVideoHeight);
                    videoView.requestLayout();
                }
            });
        }
    }

    public interface OnVideoThumbCallback {
        void getVideoThumb(String thumb);
    }
}
