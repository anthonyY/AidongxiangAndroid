package com.aiitec.openapi.net.download;

/**
 * 下载监听
 *
 * @author Cheny
 */
public interface DownloadListner {
    void onFinished();

    void onProgress(long current, long total, int progress);

    void onPause();

    void onCancel();
}
