package com.aiitec.openapi.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.aiitec.openapi.db.annotation.Unique;

/**
 * @author Anthony
 * @version 1.0
 *          createTime 2018/3/27.
 */

public class Download extends Entity implements Parcelable {

    @Unique
    private long id = -1;
    private String title;
    private String path;
    private String localPath;
    private String imagePath;
    /**
     * 1 音频， 2 视频， 3 其他
     */
    private int type;
    /**
     * 百分比
     */
    private int percentage;
    private long breakPoint;
    private long totalBytes;
    private String playLength;

    private int cacheNum;
//    @JSONField(notCombination = true)
//    private Call call;
    private boolean downloadFinish;
    private boolean isSelect;
    private String speed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getType() {
        return type;
    }

    /**
     * 下载类型
     * @param type 1 音频， 2 视频， 3 其他
     */
    public void setType(int type) {
        this.type = type;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public long getBreakPoint() {
        return breakPoint;
    }

    public void setBreakPoint(long breakPoint) {
        this.breakPoint = breakPoint;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public String getPlayLength() {
        return playLength;
    }

    public void setPlayLength(String playLength) {
        this.playLength = playLength;
    }

    public int getCacheNum() {
        return cacheNum;
    }

    public void setCacheNum(int cacheNum) {
        this.cacheNum = cacheNum;
    }


//    public Call getCall() {
//        return call;
//    }
//
//    public void setCall(Call call) {
//        this.call = call;
//    }

    public boolean isDownloadFinish() {
        return downloadFinish;
    }

    public void setDownloadFinish(boolean downloadFinish) {
        this.downloadFinish = downloadFinish;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.path);
        dest.writeString(this.localPath);
        dest.writeString(this.imagePath);
        dest.writeInt(this.type);
        dest.writeInt(this.percentage);
        dest.writeLong(this.breakPoint);
        dest.writeLong(this.totalBytes);
        dest.writeString(this.playLength);
        dest.writeInt(this.cacheNum);
        dest.writeByte(this.downloadFinish ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
        dest.writeString(this.speed);
    }

    public Download() {
    }

    protected Download(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.path = in.readString();
        this.localPath = in.readString();
        this.imagePath = in.readString();
        this.type = in.readInt();
        this.percentage = in.readInt();
        this.breakPoint = in.readLong();
        this.totalBytes = in.readLong();
        this.playLength = in.readString();
        this.cacheNum = in.readInt();
        this.downloadFinish = in.readByte() != 0;
        this.isSelect = in.readByte() != 0;
        this.speed = in.readString();
    }

    public static final Parcelable.Creator<Download> CREATOR = new Parcelable.Creator<Download>() {
        @Override
        public Download createFromParcel(Parcel source) {
            return new Download(source);
        }

        @Override
        public Download[] newArray(int size) {
            return new Download[size];
        }
    };
}
