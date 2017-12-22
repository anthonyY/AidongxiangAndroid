package com.aidongxiang.app.observer

import android.media.MediaPlayer

/**
 * 音乐播放接口
 * @author Anthony
 * createTime 2017/12/17.
 * @version 1.0
 */
interface IMusicPlayObserver {
//    fun onPlay()
//    fun onPause()
    fun onCompletion(mediaPlayer: MediaPlayer)
    fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int)
    fun onBufferingUpdate(mediaPlayer: MediaPlayer, p1: Int)
    fun onPrepared(mediaPlayer: MediaPlayer)
    fun updatePosition(mediaPlayer: MediaPlayer, current : Int, duration : Int)

}