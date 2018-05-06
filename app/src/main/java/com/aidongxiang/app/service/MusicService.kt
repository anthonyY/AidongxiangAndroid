package com.aidongxiang.app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.text.TextUtils
import com.aidongxiang.app.observer.MusicPlaySubject
import com.aidongxiang.app.receiver.MusicPlayReceiver
import com.aiitec.openapi.utils.LogUtil


/**
 *
 * @author Anthony
 * createTime 2017/12/17.
 * @version 1.0
 */
class MusicService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    override fun onBufferingUpdate(p0: MediaPlayer, p1: Int) {
        MusicPlaySubject.getInstance().onBufferingUpdate(p0, p1)
    }

    override fun onCompletion(p0: MediaPlayer) {
        MusicPlaySubject.getInstance().onCompletion(p0)
    }

    override fun onError(p0: MediaPlayer, p1: Int, p2: Int): Boolean {
        MusicPlaySubject.getInstance().onError(p0, p1, p2)
        return true
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        MusicPlaySubject.getInstance().onPrepared(mediaPlayer)
    }

    companion object {
        val ARG_URL = "url"
        val ARG_TYPE = "type"
        val ARG_SEEK_POSITION = "position"
        val ARG_TITLE = "title"
        val TYPE_PLAY = 1
        val TYPE_PAUSE = 2
        val TYPE_START = 3
        val TYPE_STOP = 4
        val TYPE_RELEASE = 5
        val TYPE_SEEK = 6
        var isPlaying = false
        var playPath : String ?= null

    }
    val TAG = "MusicService"
    private var player = MediaPlayer()

    lateinit var audioManager: AudioManager
    private var mListener: MyOnAudioFocusChangeListener? = null


    override fun onCreate() {

        player.isLooping = false
        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mListener = MyOnAudioFocusChangeListener()
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        LogUtil.i(TAG, "onStart")

        // Request audio focus for playback
        val result = audioManager.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        val type = intent.getIntExtra(ARG_TYPE, TYPE_PLAY)
        val title = intent.getStringExtra(ARG_TITLE)
        LogUtil.i(TAG, "title:"+title)
        when(type){
            TYPE_PLAY->{

                val newPlayPath = intent.getStringExtra(ARG_URL)
                if(!TextUtils.isEmpty(newPlayPath)) {
                    if(newPlayPath != playPath){
                        player.stop()
                        player = MediaPlayer()
                        playPath = newPlayPath
                        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        player.setDataSource(playPath)
                        player.setOnPreparedListener(this)
                        player.setOnErrorListener(this)
                        player.setOnCompletionListener(this)
                        player.setOnBufferingUpdateListener(this)
                        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            LogUtil.w(TAG, "requestAudioFocus successfully.")
                            player.prepareAsync()
                        } else {
                            LogUtil.w(TAG, "requestAudioFocus failed.")
                        }
                    } else {
                        if(!player.isPlaying){
                            startMusic(title)
                        }
                    }

                }
            }
            TYPE_PAUSE->{
                onMusicPause()
            }
            TYPE_START->{
                startMusic(title)
            }
            TYPE_STOP->{
                stopMusic()
            }
            TYPE_SEEK->{
                val value = intent.getIntExtra(ARG_SEEK_POSITION, 0)
                val position = value*player.duration/100
                player.seekTo(position)
            }
            TYPE_RELEASE->{
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)

    }

    private fun stopMusic() {

        val intent = Intent(this, MusicPlayReceiver::class.java)
        intent.putExtra(ARG_TYPE, TYPE_STOP)
        sendBroadcast(intent)
        player.stop()
        isPlaying = false
    }

    private fun startMusic(title : String?) {

        player.start()
        val intent = Intent(this, MusicPlayReceiver::class.java)
        intent.putExtra(ARG_TYPE, TYPE_PLAY)
        intent.putExtra(ARG_TITLE, title)
        sendBroadcast(intent)
        getCurrentValue()
        isPlaying = true
    }


    override fun onDestroy() {
        LogUtil.i(TAG, "onDestroy")
        player.stop()
        player.release()
        audioManager.abandonAudioFocus(mListener)
    }


    private inner class MyOnAudioFocusChangeListener : OnAudioFocusChangeListener {
        override fun onAudioFocusChange(focusChange: Int) {
            LogUtil.i(TAG, "focusChange=" + focusChange)

            if(focusChange == AudioManager.AUDIOFOCUS_LOSS || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                onMusicPause()
            }
        }
    }

    /**
     * 暂停音乐
     */
    private fun onMusicPause() {
        player.pause()
        val intent = Intent(this, MusicPlayReceiver::class.java)
        intent.putExtra(ARG_TYPE, TYPE_PAUSE)
        sendBroadcast(intent)
        isPlaying = false
    }


    private fun getCurrentValue() {
        val currentPosition = player.currentPosition
        val duration = player.duration
        MusicPlaySubject.getInstance().updatePosition(player, currentPosition, duration)
        Handler().postDelayed({

            if(player.isPlaying){
                getCurrentValue()
            }
        }, 500)
    }
}