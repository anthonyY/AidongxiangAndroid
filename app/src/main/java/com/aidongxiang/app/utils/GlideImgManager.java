package com.aidongxiang.app.utils;

import android.content.Context;
import android.widget.ImageView;

import com.aidongxiang.app.R;
import com.aidongxiang.app.base.Api;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Random;


/**
 * @Author Anthony
 * @Version 1.0
 * createTime 2017/3/27.
 */

public class GlideImgManager {

    static int[] defailtColorIds = {R.color.defaultColors1, R.color.defaultColors3, R.color.defaultColors4, R.color.defaultColors5,R.color.defaultColors6, R.color.defaultColors7, R.color.defaultColors8, R.color.defaultColors9};
    private static final int DEFAULT_ROUND = 10;
    public enum GlideType{
        /**加载圆形的图*/
        TYPE_CIRCLE,
        /**加载带圆角的图*/
        TYPE_ROUND}


    public static void load(Context context, String url, ImageView iv) {
        load(context, url, -1, iv, null, 0);
    }
    public static void load(Context context, String url, int emptyImg, ImageView iv) {
        load(context, url, emptyImg, emptyImg, iv, null, 0);
    }
    public static void load(Context context, String url, int emptyImg, ImageView iv, GlideType type) {
        load(context, url, emptyImg, emptyImg, iv, type, 0);
    }
    public static void load(Context context, String url, int emptyImg, ImageView iv, GlideType type, int roundDp) {
        load(context, url, emptyImg, emptyImg, iv, type, roundDp);
    }

    public static DrawableTypeRequest<String> load(Context context, String url) {
        if(!url.startsWith("http")){
            url = Api.IMAGE_URL+url;
        }
        return Glide.with(context).load(url);
    }

    /**
     * load normal  for  circle or round img
     *
     * @param url
     * @param erroImg
     * @param emptyImg
     * @param iv
     * @param type
     */
    public static void load(Context context, String url, int erroImg, int emptyImg, ImageView iv, GlideType type, int roundDp) {
        if(context == null){
            return;
        } if(url == null){
            url = "";
        }

        if(!url.startsWith("http")){
            url = Api.IMAGE_URL +url;
        }

        Random random = new Random();
        if(emptyImg == -1) {
            emptyImg = defailtColorIds[random.nextInt(defailtColorIds.length)];
        }
        if(erroImg == -1) {
            erroImg = defailtColorIds[random.nextInt(defailtColorIds.length)];
        }
        DrawableRequestBuilder<String> request = Glide.with(context).load(url).placeholder(emptyImg).error(erroImg);
        if (GlideType.TYPE_CIRCLE == type) {
            request.transform(new GlideCircleTransform(context)).into(iv);
        } else if (GlideType.TYPE_ROUND == type) {
            request.transform(new GlideRoundTransform(context,roundDp)).into(iv);
        } else {
            request.into(iv);
        }
    }


    public static void loadFile(Context context, String path, ImageView iv) {
        loadFile(context, new File(path), -1, -1, iv, null, 0);
    }
    public static void loadFile(Context context, String path, int emptyImg, ImageView iv) {
        loadFile(context, new File(path), emptyImg, emptyImg, iv, null, 0);
    }
    public static void loadFile(Context context, String path, int erroImg, int emptyImg, ImageView iv) {
        loadFile(context, new File(path), erroImg, emptyImg, iv, null, 0);
    }
    public static void loadFile(Context context, String path, int erroImg, int emptyImg, ImageView iv, GlideType type) {
        loadFile(context, new File(path), erroImg, emptyImg, iv, type, 0);
    }
    public static void loadFile(Context context, File file, int erroImg, int emptyImg, ImageView iv, GlideType type, int roundDp) {

        Random random = new Random();
        if(emptyImg == -1) {
            emptyImg = defailtColorIds[random.nextInt(defailtColorIds.length)];
        }
        if(erroImg == -1) {
            erroImg = defailtColorIds[random.nextInt(defailtColorIds.length)];
        }
        DrawableRequestBuilder<File> request = Glide.with(context).load(file).placeholder(emptyImg).error(erroImg);

        if (GlideType.TYPE_CIRCLE == type) {
            request.transform(new GlideCircleTransform(context)).into(iv);
        } else if (GlideType.TYPE_ROUND == type) {
            request.transform(new GlideRoundTransform(context,roundDp)).into(iv);
        } else {
            request.into(iv);
        }
    }
}
