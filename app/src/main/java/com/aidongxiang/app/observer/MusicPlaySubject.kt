package com.aidongxiang.app.observer

import android.media.MediaPlayer

/**
 * 音频播放订阅者
 * @author Anthony
 * createTime 2017/12/17.
 * @version 1.0
 */
class MusicPlaySubject private constructor() : IMusicPlayObserver {


    companion object {
        private var musicSubject: MusicPlaySubject? = null
        fun getInstance(): MusicPlaySubject {
            if (musicSubject == null) {
                musicSubject = MusicPlaySubject()
            }
            return musicSubject!!
        }
    }

    private val observers = ArrayList<IMusicPlayObserver>()

    fun registerObserver(o: IMusicPlayObserver) {
        observers.add(o)
    }

    fun removeObserver(o: IMusicPlayObserver) {
        if (observers.indexOf(o) >= 0) {
            observers.remove(o)
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        for (o in observers) {
            o.onCompletion(mediaPlayer)
        }
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int) {
        for (o in observers) {
            o.onError(mediaPlayer, what, extra)
        }
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, p1: Int) {
        for (o in observers) {
            o.onBufferingUpdate(mediaPlayer, p1)
        }
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        for (o in observers) {
            o.onPrepared(mediaPlayer)
        }
    }

    override fun updatePosition(mediaPlayer: MediaPlayer, current: Int, duration: Int) {
        for (o in observers) {
            o.updatePosition(mediaPlayer, current, duration)
        }
    }
}