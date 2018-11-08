package com.aidongxiang.app.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.aidongxiang.app.R;
import com.aidongxiang.app.base.Api;
import com.aidongxiang.app.base.App;
import com.aidongxiang.app.base.BaseKtActivity;
import com.aidongxiang.app.widgets.PhotoDialog;
import com.aiitec.openapi.json.enums.AIIAction;
import com.aiitec.openapi.model.FileListResponseQuery;
import com.aiitec.openapi.model.UploadImageRequestQuery;
import com.aiitec.openapi.net.AIIResponse;
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.ToastUtil;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * 拍照与上传图像
 * <p>
 * 用法：使用showPickDialog弹出对话框，getImageId()获得上传后的图片id
 * <p>
 * 必须：在onActivityResult调用同名方法
 */
public class UploadPhotoHelper {

    private BaseKtActivity context;
    private ImageView imageView;
    private PhotoDialog photoDialog;
    private long imageId = 0;
    private int defaultImgRes = 0;

    private final int CAMERA;
    private final int PHOTO;
    private final int CUT;
    String CACHEDIR = "";
    private String filePath; // 头像上传本地图片的缓存路径
    // private Uri lastUri; // 最后保存的图像uri
    private boolean isUpload = true;
    private boolean cutEnable = true;

    private OnUploadFinishedListener listener;

    public boolean isCutEnable() {
        return cutEnable;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public void setCutEnable(boolean cutEnable) {
        this.cutEnable = cutEnable;
    }

    public UploadPhotoHelper(BaseKtActivity context) {
        this(context, null);
    }


    public UploadPhotoHelper(BaseKtActivity context, ImageView imageView) {
        this(context, imageView, 0);
    }

    public UploadPhotoHelper(BaseKtActivity context, ImageView imageView,
                             int defaultImgRes) {
        this(context, imageView, defaultImgRes, 0);
    }

    public UploadPhotoHelper(BaseKtActivity context, ImageView imageView,
                             int defaultImgRes, int baseRequestCode) {
        this.context = context;
        this.imageView = imageView;
        this.defaultImgRes = defaultImgRes;

        CAMERA = 0x10 + baseRequestCode * 4;
        PHOTO = 0x11 + baseRequestCode * 4;
        CUT = 0x12 + baseRequestCode * 4;

        photoDialog = new PhotoDialog(context);
        photoDialog.setOnButtonClickListener(onButtonClickListener);
        CACHEDIR = Environment.getExternalStorageDirectory().toString() + "/file/com.aidongxiang.app/uploadfiles/";

    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            filePath = savedInstanceState.getString("filePath");
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("filePath", filePath);
    }

    /**
     * 获取头像上传后的图像id
     */
    private void getUploadFiles(FileListResponseQuery response) {
        if (photo != null) {
            if (!photo.isRecycled()) {
                photo.recycle();
            }
            photo = null;
        }
        if (response.getStatus() == 0) {
            List<com.aiitec.openapi.model.File> files = response.getFiles();
//            ToastUtil.show(context, "上传成功");
            List<Long> ids = response.getIds();
            if (ids != null && ids.size() > 0) {
                imageId = ids.get(0);
            }
            String path = "";
            if (files != null && files.size() > 0) {
                com.aiitec.openapi.model.File file = files.get(0);
                path = Api.IMAGE_URL + file.getPath();
            }

            if (listener != null) {
                listener.onUploadFinished(imageId, path);
            }
        } else {
            LogUtil.i("info", "upload is failed!");
            ToastUtil.show(context,response.getDesc());
        }
    }

    /**
     * 在activity的onActivityResult里加入这个方法（必须）
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 如果是直接从相册获取


        if (requestCode == PHOTO) {
            if (resultCode == Activity.RESULT_OK && data != null)
                if (cutEnable) {
                    startPhotoZoom(data.getData());
                } else {
                    setPicToView(data.getData());
                }
//			if(!isCutEnable()){
//				//不管成功还是失败，都dismiss吧
//				photoDialog.dismiss();
//			}
        }
        // 如果是调用相机拍照时
        else if (requestCode == CAMERA && data == null) {
            if (resultCode == Activity.RESULT_OK) {
                if (!TextUtils.isEmpty(filePath)) {
                    File temp = new File(filePath);
                    Uri fileUri = FileProvider7.getUriForFile(context, temp);
                    if (cutEnable) {
                        startPhotoZoom(fileUri);
                    } else {
                        setPicToView(fileUri);
                    }
                }
            }
//			if(!isCutEnable()){
//				//不管成功还是失败，都dismiss吧
//				photoDialog.dismiss();
//			}
        }
        // 取得裁剪后的图片
        else if (requestCode == CUT) {
            if (resultCode == Activity.RESULT_OK) {
                // setPicToView(tempPhotoUri);
                tempPhotoUri = getTempUri();
                if (tempPhotoUri != null) {

                    setPicToView(tempPhotoUri);
                }
            }
            //不管成功还是失败，都dismiss吧
            photoDialog.dismiss();
        }

    }

    PhotoDialog.OnButtonClickListener onButtonClickListener = new PhotoDialog.OnButtonClickListener() {
        @Override
        public void onPhotoClick(View view) {
            photoDialog.dismiss();
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                ToastUtil.show(context.getApplicationContext(), R.string.err_sdcard);
                return;
            }
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            context.startActivityForResult(intent, PHOTO);
        }

        @Override
        public void onCameraClick(View view) {
            photoDialog.dismiss();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String path = CACHEDIR;
            File file = new File(path);

            //如果没有这个目录的话，部分手机无法保存照片
            if (!file.exists()) {
                if (!file.getParentFile().exists()) {
                    if (!file.getParentFile().getParentFile().exists()) {
                        file.getParentFile().getParentFile().mkdir();
                    }
                    file.getParentFile().mkdir();
                }
                file.mkdir();
            }
            filePath = path + (new Date().getTime()) + ".jpg";
            Uri fileUri = FileProvider7.getUriForFile(context, new File(filePath));
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            context.startActivityForResult(intent, CAMERA);
        }

        @Override
        public void onCancelClick(View view) {
            photoDialog.dismiss();
        }
    };

    /**
     * 选择上传图片提示对话框
     */
    public void showPickDialog() {

        photoDialog.show();
    }

    // 选择存储位置
    Uri tempPhotoUri;
    File tempPhotoFile;

    private Uri getTempUri() {
        tempPhotoUri = Uri.fromFile(getTempFile());

        return tempPhotoUri;
    }

    private File getTempFile() {
        if (AiiUtil.isSDCardEnable()) {
            String path = CACHEDIR;
            File f = new File(path, "temp.jpg");
            try {
                if (!f.exists()) {
                    if (!f.getParentFile().exists()) {
                        f.getParentFile().mkdirs();
                    }
                    f.createNewFile();
                }
                tempPhotoFile = f;
            } catch (IOException e) {
            }
            return f;
        }
        return null;
    }

    private double cropRatio = 1;
    private int photoMinSize = 480;

    public double getCropRatio() {
        return cropRatio;
    }

    public void setCropRatio(double cropRatio) {
        this.cropRatio = cropRatio;
    }

    public int getPhotoMinSize() {
        return photoMinSize;
    }

    public void setPhotoMinSize(int photoSize) {
        this.photoMinSize = photoSize;
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", (int) (cropRatio * 10000));
        intent.putExtra("aspectY", 10000);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX",
                cropRatio > 1 ? (int) (photoMinSize * cropRatio) : photoMinSize);
        intent.putExtra("outputY", cropRatio > 1 ? photoMinSize
                : (int) (photoMinSize / cropRatio));
        // intent.putExtra("return-data", true);

        intent.putExtra("noFaceDetection", true);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        context.startActivityForResult(intent, CUT);
    }

    private Bitmap photo;

    /**
     * 保存裁剪之后的图片数据
     *
     * @param uri
     */
    private void setPicToView(Uri uri) {
        if (photo != null) {
            if (!photo.isRecycled()) {
                photo.recycle();
            }
            photo = null;
        }

        photo = ImageUtils.safeDecodeStream(context, uri, photoMinSize, photoMinSize);
        String path = ImageUtils.getPathByUri4kitkat(context, uri);
        if (photo != null) {
            if (imageView != null && !photo.isRecycled()) {
                imageView.setImageBitmap(photo);
            }
            if (TextUtils.isEmpty(path)) {
                return;
            }
            final File file = new File(path);
            if (file.exists() && isUpload) {
                requestUpload(file);
            } else {
                if (listener != null) {
                    imageId = 1;//临时写的，到时候要删除
                    listener.onUploadFinished(imageId, path);
                }
            }
        }

    }

    private void requestUpload(final File file){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UploadImageRequestQuery query = new UploadImageRequestQuery();
                LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();
                query.setAction(AIIAction.ONE);
                params.put(file.getName(), file);
                App.Companion.getAiiRequest().sendFiles(query, params, new AIIResponse<FileListResponseQuery>(context){
                    @Override
                    public void onSuccess(FileListResponseQuery response, int index) {
                        super.onSuccess(response, index);
                        context.progressDialogDismiss();
                        getUploadFiles(response);

                    }

                    @Override
                    public void onFailure(String content, int index) {
                        super.onFailure(content, index);
                        UploadPhotoHelper.this.context.progressDialogDismiss();
                        ToastUtil.show(context, "上传失败");
                    }

                    @Override
                    public void onServiceError(String content, int status, int index) {
                        super.onServiceError(content, status, index);
                        UploadPhotoHelper.this.context.progressDialogDismiss();
                        ToastUtil.show(context, "上传失败");
                    }
                });
                context.progressDialogShow();
            }
        }, 1);

    }

    /**
     * 获得上传后图片的id，默认为0
     *
     * @return
     */
    public long getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public void setOnUploadFinishedListener(
            OnUploadFinishedListener onUploadFinishedListener) {
        this.listener = onUploadFinishedListener;
    }

    public interface OnUploadFinishedListener {
        void onUploadFinished(long id, String url);
    }


}
