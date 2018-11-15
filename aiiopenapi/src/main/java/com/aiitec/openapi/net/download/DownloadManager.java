package com.aiitec.openapi.net.download;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;

import com.aiitec.openapi.db.AIIDBManager;
import com.aiitec.openapi.model.Download;
import com.aiitec.openapi.utils.AiiUtil;
import com.aiitec.openapi.utils.LogUtil;

import java.io.File;
import java.util.List;

/**
 * 下载管理器，断点续传
 *
 * @author Cheny
 */
public class DownloadManager {

    public static final String ACTION_DOWNLOAD_UPDATE = "com.aidongxiang.app.download.update";
    public static final String ARG_DOWNLOAD_ID = "id";
    public static final String ARG_TOTAL = "total";
    public static final String ARG_CURRENT = "current";
    public static final String ARG_PERCENTAGE = "percentage";
    public static final String ARG_SPPED = "speed";
    public static final String ARG_TYPE = "type";
    public static final int ARG_TYPE_FINISH = 1;
    public static final int ARG_TYPE_UPDATE = 2;
    public static final int ARG_TYPE_PAUSE = 3;
    public static final int ARG_TYPE_CANCEL = 4;
    private String DEFAULT_FILE_DIR;//默认下载目录
    private SparseArray<DownloadTask> mDownloadTasks;//文件下载任务索引，String为url,用来唯一区别并操作下载的文件

    public SparseArray<DownloadTask> getDownloadTasks() {
        return mDownloadTasks;
    }

    private static DownloadManager mInstance;
    private static final String TAG = "DownloadManager";
    private AIIDBManager aiidbManager;
    private Context context;
    /**
     * 下载文件
     */
    public void download(int... ids) {
        //单任务开启下载或多任务开启下载
        for (int id : ids) {
            if (mDownloadTasks.get(id) != null) {
                mDownloadTasks.get(id).start();
            }
        }
    }

    /**
     * 添加下载任务，需要先添加任务，再调下载方法
     * @param downloads
     */
    public void download(Download... downloads) {
        DefaultDownloadListener[] listeners = new DefaultDownloadListener[downloads.length];
        int index = 0;
        for(Download download : downloads){
            listeners[index] = new DefaultDownloadListener(download);
            index++;
        }
        //添加下载任务
        download(listeners, downloads);
    }

    /**
     * 添加下载任务，需要先添加任务，再调下载方法
     * @param downloadListners
     * @param ids
     */
    public void download(DownloadListner[] downloadListners, Download... ids) {
        //单任务开启下载或多任务开启下载
        int index = 0;
        for (final Download download : ids) {
            if (mDownloadTasks.get((int) download.getId()) != null) {
                //如果存在下载队列，但是未下载，那么就下载
                if(!isDownloading((int)download.getId())){
                    mDownloadTasks.get((int)download.getId()).start();
                }
            } else {//如果下载队列里没有，就添加下载
                String sdcardPath = AiiUtil.getSDCardPath();
                String cacheDir = sdcardPath+"file/"+context.getPackageName()+"/cache";
                String videoDir = sdcardPath+"file/"+context.getPackageName()+"/video";
                String audioDir = sdcardPath+"file/"+context.getPackageName()+"/audio";
                String downloadDir ;
                //文件名叫 title+视频id+.mp4, 本想用视频地址的名字，但是，名字太长，导致有些打不开，所以还是自己命名
                String fileName = download.getTitle()+download.getId()+".mp4";
                if(download.getType() == 1){
                    fileName = download.getTitle()+download.getId()+".mp3";
                }
                if (download.getType() == 2) {
                    //2是视频， 1 是音频
                    downloadDir = videoDir;
                } else if (download.getType() == 1) {
                    // 1 是音频
                    downloadDir = audioDir;
                } else {
                    downloadDir = cacheDir;
                }
                File dir = new File(downloadDir);
                if(!dir.exists()){
                    if(!dir.getParentFile().getParentFile().exists()){
                        dir.getParentFile().getParentFile().mkdir();
                    }
                    if(!dir.getParentFile().exists()){
                        dir.getParentFile().mkdir();
                    }
                    dir.mkdir();
                }
                add(download, downloadDir, fileName, downloadListners[index]);
                download((int)download.getId());
            }
            index++;
        }
    }

    class DefaultDownloadListener implements DownloadListner {
        private Download download;
        private long lastNotifyTime;
        private long currentSize;
        private int currentProgress;
        private long lastSize;
        DefaultDownloadListener(Download download){
            this.download = download;
        }
        @Override
        public void onFinished() {

            download.setBreakPoint(download.getTotalBytes());
            download.setDownloadFinish(true);
            download.setPercentage(100);
            LogUtil.e("下载完成");
            aiidbManager.save(download);
            if(context != null){
                Intent intent = new Intent(ACTION_DOWNLOAD_UPDATE);
                intent.putExtra(ARG_DOWNLOAD_ID, download.getId());
                intent.putExtra(ARG_TOTAL, download.getTotalBytes());
                intent.putExtra(ARG_CURRENT, download.getTotalBytes());
                intent.putExtra(ARG_TYPE, ARG_TYPE_FINISH);
                context.sendBroadcast(intent);
            }
            mDownloadTasks.remove((int) download.getId());
        }

        @Override
        public void onProgress(long current, long total, int progress) {
            long currentTime = System.currentTimeMillis();
            currentSize = current;
            currentProgress = progress;
            //短时间内刷新，容易出问题，所以刷新频率限制一下
            if(currentTime - lastNotifyTime < 1000){
                return ;
            }
            long speed = ((current - lastSize) * 1000 / (currentTime - lastNotifyTime))/1024;
            if(speed < 0){
                speed = 0;
            }
            String speedStr = "";
//            if (speed == 0)
//                speedStr =  "0.00";
//            DecimalFormat df = new DecimalFormat("#0.00");
//            return df.format(money);
            if(speed > 1024){
                speedStr = AiiUtil.formatString((speed/1000.0))+"M/s";
            } else {
                speedStr = AiiUtil.formatString((speed/1000.0))+"K/s";
            }
            lastSize = current;
            lastNotifyTime = currentTime;
            download.setBreakPoint(current);
            download.setTotalBytes(total);
            download.setPercentage(progress);
            LogUtil.e("下载中"+download.getTitle()+"  "+progress+"%"+"    current:"+current+" total:"+total);
            if(context != null){
                Intent intent = new Intent(ACTION_DOWNLOAD_UPDATE);
                intent.putExtra(ARG_DOWNLOAD_ID, download.getId());
                intent.putExtra(ARG_TOTAL, total);
                intent.putExtra(ARG_CURRENT, current);
                intent.putExtra(ARG_PERCENTAGE, progress);
                intent.putExtra(ARG_SPPED, speedStr);
                intent.putExtra(ARG_TYPE, ARG_TYPE_UPDATE);
                context.sendBroadcast(intent);
            }

        }

        @Override
        public void onPause() {
            if(context != null){
                Intent intent = new Intent(ACTION_DOWNLOAD_UPDATE);
                intent.putExtra(ARG_DOWNLOAD_ID, download.getId());
                intent.putExtra(ARG_TOTAL, download.getTotalBytes());
                intent.putExtra(ARG_CURRENT, currentSize);
                intent.putExtra(ARG_PERCENTAGE, currentProgress);
                intent.putExtra(ARG_SPPED, "");
                intent.putExtra(ARG_TYPE, ARG_TYPE_PAUSE);
                context.sendBroadcast(intent);
            }
            download.setBreakPoint(currentSize);
            download.setPercentage(currentProgress);
            aiidbManager.save(download);
        }

        @Override
        public void onCancel() {
            if(context != null){
                Intent intent = new Intent(ACTION_DOWNLOAD_UPDATE);
                intent.putExtra(ARG_DOWNLOAD_ID, download.getId());
                intent.putExtra(ARG_TOTAL, 0);
                intent.putExtra(ARG_CURRENT, 0);
                intent.putExtra(ARG_PERCENTAGE, 0);
                intent.putExtra(ARG_SPPED, "");
                intent.putExtra(ARG_TYPE, ARG_TYPE_CANCEL);
                context.sendBroadcast(intent);
            }
            aiidbManager.deleteById(Download.class, download.getId());
        }
    }


    /** 获取下载文件的名称*/
    public static String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    /**
     * 暂停
     */
    public void pause(int... ids) {
        //单任务暂停或多任务暂停下载
        for (int id : ids) {
            if (mDownloadTasks.get(id) != null) {
                mDownloadTasks.get(id).pause();
            }
        }
    }

    /**
     * 取消下载
     */
    public void cancel(int... ids) {
        //单任务取消或多任务取消下载
        for (int id : ids) {
            if (mDownloadTasks.get(id) != null) {
                mDownloadTasks.get(id).cancel();
                mDownloadTasks.remove(id);
            } else {
//                DownloadTasks.get(id) 虽然是空的，但是文件可能已经下载完成了，所以本地文件还是要删除
                try {
                    Download download = aiidbManager.findObjectFromId(Download.class, id);
                    if(download != null && !TextUtils.isEmpty(download.getLocalPath())){
                        File file = new File(download.getLocalPath());
                        if(file.exists()){
                            file.delete();
                        }
                        aiidbManager.deleteById(Download.class, id);
                    }

                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加下载任务
     */
    public void add(Download download, DownloadListner l) {
        add(download, null, null, l);
    }

    /**
     * 添加下载任务
     */
    public void add(Download download, String filePath, DownloadListner l) {
        add(download, filePath, null, l);
    }

    /**
     * 添加下载任务
     */
    public void add(Download download, String filePath, String fileName, DownloadListner l) {
        if (TextUtils.isEmpty(filePath)) {//没有指定下载目录,使用默认目录
            filePath = getDefaultDirectory();
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = getFileName(download.getPath());
        }
        download.setLocalPath(filePath+ File.separator+fileName);
        aiidbManager.save(download);
        mDownloadTasks.put((int) download.getId(), new DownloadTask(new FilePoint(download.getPath(), filePath, fileName), l));
    }

    /**
     * 默认下载目录
     * @return
     */
    private String getDefaultDirectory() {
        if (TextUtils.isEmpty(DEFAULT_FILE_DIR)) {
            DEFAULT_FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "moreschool" + File.separator;
        }
        return DEFAULT_FILE_DIR;
    }

    public static DownloadManager getInstance(Context context) {//管理器初始化
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager(context);
                }
            }
        }
        return mInstance;
    }

    private DownloadManager(Context context) {
        mDownloadTasks = new SparseArray<>();
        this.context = context;

        aiidbManager = new AIIDBManager(context.getApplicationContext());

        try {
            List<Download> downloads = aiidbManager.findAll(Download.class);
            if(downloads != null && downloads.size() > 0){
                for(Download download : downloads){
                    if(!download.isDownloadFinish()){
                        if(mDownloadTasks.get((int) download.getId()) == null){
                            add(download,  new DefaultDownloadListener(download));
                        }
                    }
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


    /**
     * 是否正在下载
     */
    public boolean isDownloading(int... ids) {
        //这里传一个url就是判断一个下载任务
        //多个url数组适合下载管理器判断是否作操作全部下载或全部取消下载
        boolean result = false;
        for (int id : ids) {
            if (mDownloadTasks.get(id) != null) {
                result = mDownloadTasks.get(id).isDownloading();
            }
        }
        return result;
    }
//    /**
//     * 取消下载
//     */
//    public boolean isPause(int... ids) {
//        //这里传一个url就是判断一个下载任务
//        //多个url数组适合下载管理器判断是否作操作全部下载或全部取消下载
//        boolean result = false;
//        for (int id : ids) {
//            if (mDownloadTasks.get(id) != null) {
//                result = mDownloadTasks.get(id).isPause();
//            }
//        }
//        return result;
//    }
}
