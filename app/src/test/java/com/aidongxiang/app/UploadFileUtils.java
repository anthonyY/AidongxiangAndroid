package com.aidongxiang.app;



import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author Anthony
 *         createTime 2018/6/9.
 * @version 1.0
 */

public class UploadFileUtils {

    @Test
    public void testUploadFile(){
        String url = "http://bill-1251707083.cos.ap-chengdu.myqcloud.com/1528522012.231aa.png?sign=q-sign-algorithm%3Dsha1%26q-ak%3DAKIDAFD7rQTCMN5gmjs5r2A7M7oKfpnjQVGq%26q-sign-time%3D1528521952%3B1528522312%26q-key-time%3D1528521952%3B1528522312%26q-header-list%3D%26q-url-param-list%3D%26q-signature%3D6e3252f0e4843cf73441f71bb8f7ccdab87c089d";
        File file = new File("D:\\我的文档\\Pictures\\aa.png");
        uploadForCosFile(url, file, 1);
    }
    public static void uploadForCosFile(final String urlStr, final File file, final int index) {
//        final Handler hanlder = new Handler(Looper.getMainLooper());
//        if(aiiResponse != null){
//            aiiResponse.onStart(index);
//        }
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                OutputStream out = null;
                FileInputStream inputStream = null;
                InputStream in = null;
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
//                    connection.setChunkedStreamingMode(0);
                    connection.setRequestMethod("PUT");
//                    connection.setRequestMethod("POST");

                    out = connection.getOutputStream();
                    inputStream = new FileInputStream(file);
                    byte[] buff = new byte[64 * 1024];
                    int length = 0;
                    int total = 0;
                    while ((length = inputStream.read(buff)) > 0) {
                        total += length;
                        out.write(buff, 0, length);
                    }
//                    LogUtil.w("total  " + total+"   "+file.getAbsolutePath());
                    System.out.println(""+"total  " + total+"   "+file.getAbsolutePath());
                    // 写入要上传的数据

                    final int responseCode = connection.getResponseCode();
                    System.out.println("Service returned response code " + responseCode);
                    Map<String, List<String>> map = connection.getHeaderFields();
                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                        System.out.println("Key : " + entry.getKey() +
                                " ,Value : " + entry.getValue());
                    }
                    String eTag = connection.getHeaderField("ETag");
                    System.out.println(eTag);
                    in = connection.getInputStream();

                    InputStreamReader reader = new InputStreamReader(in);
                    BufferedReader buffer = new BufferedReader(reader);
                    String inputLine = null;
                    StringBuffer sb = new StringBuffer();
                    while (((inputLine = buffer.readLine()) != null)){
                        sb.append(inputLine);
                    }
//                    LogUtil.e("result:" + sb.toString());
//                    hanlder.post(new Runnable() {
//                        @Override
//                        public void run() {

                            if (responseCode == 200) {
//                                aiiResponse.onSuccess("", index);
//                                aiiResponse.onFinish(index);
                                System.out.println("请求成功"+sb.toString());
                            } else {
                                System.out.println("网络异常"+responseCode);
//                                aiiResponse.onFailure("网络异常", index);
//                                aiiResponse.onFinish(index);
                            }
//                        }
//                    });

                } catch (ProtocolException e) {
                    e.printStackTrace();
//                    LogUtil.e("httpUtils 上传文件 ProtocolException   "+e.getMessage());

//                    hanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            aiiResponse.onFailure("网络异常", index);
//                            aiiResponse.onFinish(index);
                    System.out.println("网络异常"+e.getMessage());
//                        }
//                    });

                } catch (IOException e) {
                    e.printStackTrace();
//                    LogUtil.e("httpUtils 上传文件 IOException   "+e.getMessage());
//                    hanlder.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            aiiResponse.onFailure("网络异常", index);
//                            aiiResponse.onFinish(index);
                    System.out.println("网络异常"+e.getMessage());
//                        }
//                    });

                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
//            }
//        }).start();
    }


}
