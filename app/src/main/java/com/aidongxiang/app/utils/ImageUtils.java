package com.aidongxiang.app.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.aidongxiang.app.base.Constants;
import com.aiitec.openapi.utils.AiiUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Anthony
 *         createTime 2016/10/13.
 * @version ${VERSION}
 */
public class ImageUtils {

    /**
     * 通过旧的图片路径获取到图片，然后压缩再保存到缓存路径，然后返回缓存路径
     * @param context
     * @param filePath
     * @return
     */
    public static String getCompressFile(Context context, String filePath){
        String fileName = "";
        int index = filePath.lastIndexOf("/");
        if(index > 0){
            fileName = filePath.substring(index+1, filePath.length()).replaceAll(" ", "");
        }
        String cachePath = getCacheDir(context);
        String cacheFilePath = cachePath + fileName;
        File file = new File(cacheFilePath);
        if (file.exists()) {//如果有缓存，直接返回缓存路径
            return cacheFilePath;
        }
        return bitmapToFile(context, compressImageFromFile(context, filePath), fileName);
    }
    @SuppressWarnings("deprecation")
    public static Bitmap compressImageFromFile(Context context, String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);


        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = AiiUtil.getScreenHeight(context);
        float ww = AiiUtil.getScreenWidth(context);
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
        newOpts.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        return bitmap;
    }
    public static String getCacheDir(Context context){
        String cachePath = "";
        if(context.getExternalCacheDir() != null){
            cachePath = context.getExternalCacheDir().getAbsolutePath()+"/";
        } else if(AiiUtil.isSDCardEnable()){
            cachePath = AiiUtil.getSDCardPath()+"/"+context.getPackageName()+"/";
            File file = new File(cachePath);
            if(!file.exists()){
                file.mkdir();
            }
        }
        return cachePath;
    }
    /**
     * 把bitmap 保存到缓存路径
     * @param context
     * @param image
     * @return
     */
    public static String bitmapToFile(Context context, Bitmap image, String fileName) {

        String cachePath = getCacheDir(context);
        String filePath = cachePath + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 600) { // 循环判断如果压缩后图片是否大于400kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            if (options <= 10) {
                options = options / 2 + 1;
            } else {
                options -= 10;// 每次都减少10
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] result = baos.toByteArray();
            try {
                fileOutputStream.write(result);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filePath;// 把压缩后的数据baos存放到ByteArrayInputStream中
    }
    public static Bitmap safeDecodeStream(Context context, Uri uri,
                                          int minWidth, int minHeight) {
        if (context == null || uri == null)
            return null;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        ContentResolver resolver = context.getContentResolver();
        try {
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(
                    new BufferedInputStream(resolver.openInputStream(uri),
                            16 * 1024), null, options);
            int oldWidth = options.outWidth;
            int oldHeight = options.outHeight;
            int scale = 1;
            if (oldWidth > oldHeight && oldHeight > minHeight) {
                scale = (int) (oldHeight / (float) minHeight);
            } else if (oldWidth <= oldHeight && oldWidth > minWidth) {
                scale = (int) (oldWidth / (float) minWidth);
            }
            scale = scale < 1 ? 1 : scale;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(new BufferedInputStream(
                    resolver.openInputStream(uri), 16 * 1024), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /* 读取照片exif信息中的旋转角度
     * @param path 照片路径
     * @return角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
//    那么我们只需要根据旋转角度将图片旋转过来就OK了
    public static Bitmap toturn(Bitmap img, int range){
        Matrix matrix = new Matrix();
        matrix.postRotate(range); /*翻转90度*/
        int width = img.getWidth();
        int height =img.getHeight();
        img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        return img;
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = Constants.INSTANCE.getCACHEDIR();
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + "/rotate/";
        }
        try {
            filePic = new File(savePath + System.currentTimeMillis() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }
}
