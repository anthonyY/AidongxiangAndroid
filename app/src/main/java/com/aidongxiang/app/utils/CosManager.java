package com.aidongxiang.app.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.aidongxiang.app.base.App;
import com.aidongxiang.app.cos.LocalCredentialProvider;
import com.aiitec.openapi.utils.LogUtil;
import com.aiitec.openapi.utils.ToastUtil;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.UploadService;

import java.io.File;

/**
 * @author Anthony
 *         createTime 2018/4/19.
 * @version 1.0
 */

public class CosManager {

    private static final String TAG = "cos";
    private CosXmlService cosXmlService;
    private Handler handler = new Handler(Looper.getMainLooper());
    public CosManager(){
        initCos();
    }
    private void initCos(){
        String appid = "1251707083";
        String region = "ap-guangzhou";

        String secretId = "AKIDAFD7rQTCMN5gmjs5r2A7M7oKfpnjQVGq";
        String secretKey ="Xs11uc7RZWLIKAqLeBbpqeGl42ua4Mfw";
        long keyDuration = 600; //SecretKey 的有效时间，单位秒

//创建 CosXmlServiceConfig 对象，根据需要修改默认的配置参数
        CosXmlServiceConfig serviceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(appid, region)
                .setDebuggable(true)
                .builder();

//创建获取签名类(请参考下面的生成签名示例，或者参考 sdk中提供的ShortTimeCredentialProvider类）
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(secretId, secretKey, keyDuration);

//创建 CosXmlService 对象，实现对象存储服务各项操作.
        Context context = App.app.getApplicationContext(); //应用的上下文

        cosXmlService = new CosXmlService(context,serviceConfig, localCredentialProvider);
    }


    public void upload(final File file, final OnUploadListener onUploadListener){
//        UploadService 封装了上述分片上传请求一系列过程的类

        if(file == null || !file.exists()){
            ToastUtil.show(App.app.getApplicationContext(), "文件不存在");
            return;
        }
        if(onUploadListener != null){
            onUploadListener.onStart();
        }


        App.Companion.getApp().cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                UploadService.ResumeData resumeData = new UploadService.ResumeData();
                resumeData.bucket = "aidongxiang";
                resumeData.cosPath = "video/"+System.currentTimeMillis()+"_"+file.getName(); //格式如 cosPath = "/test.txt";
                String localPath = file.getAbsolutePath();
                LogUtil.e("localPath:"+localPath);
                resumeData.srcPath = localPath;
                resumeData.sliceSize = 1024 * 1024; //每个分片的大小
                resumeData.uploadId = null; //若是续传，则uploadId不为空


                UploadService uploadService = new UploadService(cosXmlService, resumeData);

/*设置进度显示
  实现 CosXmlProgressListener.onProgress(long progress, long max)方法，
  progress 已上传的大小， max 表示文件的总大小
*/
                uploadService.setProgressListener(new CosXmlProgressListener() {
                    @Override
                    public void onProgress(final long progress, final long max) {
                        if(max != 0){
                            float result = (float) (progress * 100.0/max);
                            LogUtil.w("TEST","progress =" + (long)result + "%");

                            if(onUploadListener != null){
                                onUploadListener.onUploadProgress(progress, max);
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(onUploadListener != null){
                                        onUploadListener.onUploadProgress(progress, max);
                                        onUploadListener.onFinish();
                                    }
                                }
                            });
                        }
                    }
                });
                try {
                    final CosXmlResult cosXmlResult = uploadService.upload();
                    LogUtil.w(TAG, "success: " + cosXmlResult.accessUrl );
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(onUploadListener != null){
                                onUploadListener.onSuccess(cosXmlResult.accessUrl);
                                onUploadListener.onFinish();
                            }
                        }
                    });

                } catch (final CosXmlClientException e) {
                    e.printStackTrace();
                    //抛出异常
                    LogUtil.w(TAG,"CosXmlClientException =" + e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(onUploadListener != null){
                                onUploadListener.onFailure(e);
                                onUploadListener.onFinish();
                            }
                        }
                    });
                } catch (final CosXmlServiceException e) {
                    e.printStackTrace();
                    //抛出异常
                    LogUtil.w(TAG, "CosXmlServiceException =" + e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(onUploadListener != null){
                                onUploadListener.onFailure(e);
                                onUploadListener.onFinish();
                            }
                        }
                    });
                }
            }
        });

    }

//    private OnUploadListener onUploadListener;
//
//    public void setOnUploadListener(OnUploadListener onUploadListener) {
//        this.onUploadListener = onUploadListener;
//    }

    public interface OnUploadListener{
        void onStart();
        void onSuccess(String accessUrl);
        void onFailure(Exception e);
        void onUploadProgress(long progress, long max);
        void onFinish();
    }
}
