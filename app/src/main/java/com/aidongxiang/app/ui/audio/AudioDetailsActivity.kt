package com.aidongxiang.app.ui.audio

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.model.Ad
import com.aidongxiang.app.observer.IMusicPlayObserver
import com.aidongxiang.app.observer.MusicPlaySubject
import com.aidongxiang.app.service.MusicService
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.ui.mine.MyDownloadActivity
import com.aiitec.openapi.utils.LogUtil
import kotlinx.android.synthetic.main.activity_audio_details.*
import java.util.*

/**
 * 音频详情
 * @author Anthony
 * createTime 2017-11-26
 * @version 1.0
 */
@ContentView(R.layout.activity_audio_details)
class AudioDetailsActivity : BaseKtActivity(), IMusicPlayObserver {



    var audioPath : String ?= null
    private var mediaPlayer : MediaPlayer ?= null
    var isPlaying = false
    override fun init(savedInstanceState: Bundle?) {
//        audioPath = "http://192.168.31.7:8080/audio/程璧 - Loving You.mp3"
        audioPath = "http://192.168.31.7:8080/audio/theYear.mp3"

        ivAudioDetailsPlay.setOnClickListener {
            switchPlayStatus(!isPlaying)
        }

        seekbar_audio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2){
                    val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
                    intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_SEEK)
                    intent.putExtra(MusicService.ARG_SEEK_POSITION, p1)
                    startService(intent)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        MusicPlaySubject.getInstance().registerObserver(this)

        val random = Random()
        val ads = ArrayList<Ad>()
        for(i in 0..5){
            val ad = Ad()
            ad.imagePath = HomeFragment.imgs[random.nextInt(HomeFragment.imgs.size)]
            ad.name = "广告"
            ads.add(ad)
        }

        adAudioDetails.startAD(ads.size, 4, true, ads)
    }

    override fun onDestroy() {
        super.onDestroy()

        MusicPlaySubject.getInstance().removeObserver(this)
    }
    override fun onStart() {
        super.onStart()
        ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
        val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
        intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_PLAY)
        intent.putExtra(MusicService.ARG_TITLE, "许巍-故乡")
        intent.putExtra(MusicService.ARG_URL, audioPath)
        startService(intent)

    }

    private fun switchPlayStatus(playing: Boolean) {

        if(!playing){
            ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
            val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
            intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_PAUSE)
            startService(intent)

        } else {
            ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
            val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
            intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_START)
            startService(intent)
        }

        isPlaying = playing
    }


    var oldPosition = 0
    var currentPosition = 0
    var duration = 0
    private fun getCurrentValue(mediaPlayer: MediaPlayer, current: Int, duration: Int) {
        if(isFinishing || supportFragmentManager.isDestroyed){
            return
        }
        if(isPlaying != mediaPlayer.isPlaying){
            isPlaying = mediaPlayer.isPlaying
            if(isPlaying){
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_stop2)
            } else {
                ivAudioDetailsPlay.setImageResource(R.drawable.video_btn_play2)
            }
        }

        this.currentPosition = current
        this.duration = duration
        if(duration == 0){
            seekbar_audio.progress = 0
        } else {
            seekbar_audio.progress = currentPosition*100/duration
        }
        tvCurrentTime.text = formatTime(currentPosition)
        tvDuration.text = formatTime(duration)

        //根据上次正在播放的时间和当前的时间对比，如果一致，则说明进度没有变化，进度没有变化就肯定是缓冲了
        if (oldPosition == currentPosition && mediaPlayer.isPlaying) {
//            loading.visibility = View.VISIBLE
        } else {
//            loading.visibility = View.GONE
        }
        oldPosition = currentPosition

    }

    private fun formatTime(time : Int) : String{
        val second = time/1000%60
        val minute = time/1000/60%60
        val hour = time/1000/60/60
        return if(hour > 0){
            "${formatNum(hour)}:${formatNum(minute)}:${formatNum(second)}"
        } else {
            "${formatNum(minute)}:${formatNum(second)}"
        }
    }
    fun formatNum(value : Int) : String{
        return if(value < 10){
            "0$value"
        } else {
            value.toString()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_audio_details, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === R.id.action_download) {
            switchToActivity(MyDownloadActivity::class.java, MyDownloadActivity.ARG_POSITION to 1)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int) {
        LogUtil.e("播放错误  what:$what  extra:$extra ")
        toast("播放错误！")
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, bufferingProgress: Int) {
//        LogUtil.e("OnBuffering ${bufferingProgress}")
        seekbar_audio.secondaryProgress = bufferingProgress
        val currentProgress=seekbar_audio.max *mediaPlayer.currentPosition /mediaPlayer.duration
//        LogUtil.e("$currentProgress % play",  "$bufferingProgress% buffer")
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        this.mediaPlayer = mediaPlayer
        switchPlayStatus(true)
        tvDuration.text = formatTime(duration)
        val intent = Intent(this@AudioDetailsActivity, MusicService::class.java)
        intent.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_START)
        intent.putExtra(MusicService.ARG_TITLE, "许巍-故乡")
        startService(intent)

    }
    override fun updatePosition(mediaPlayer: MediaPlayer, current: Int, duration: Int) {
        getCurrentValue(mediaPlayer, current, duration)
    }
}
