package com.aidongxiang.app.utils;


import com.aidongxiang.app.base.Api;

/**
 * @Author Xiaobing
 * @Version 1.0
 * Created on 2017/10/19
 * @effect 拼接图片路径的工具
 */

public class ImagePathUtil {

    /**
     * 获取完整图片路径
     *
     * @param relativeImagePath 相对路径
     * @return
     */
    public static String getWholeImagePath(String relativeImagePath) {
        if (relativeImagePath != null) {
            if (relativeImagePath.startsWith("http")) {
                return relativeImagePath;
            } else {
                return Api.BASE_URL + "/uploadfiles/" + relativeImagePath;
            }
        } else {
            return "";
        }
    }
}
