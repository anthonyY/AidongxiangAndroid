package com.aidongxiang.app.receiver

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.view.View
import android.widget.RemoteViews
import com.aidongxiang.app.R
import com.aidongxiang.app.service.MusicService
import com.aidongxiang.app.service.MusicService.Companion.TYPE_PLAY
import com.aiitec.openapi.utils.LogUtil


/**
 *
 * @author Anthony
 * createTime 2017/12/17.
 * @version 1.0
 */
class MusicPlayReceiver : BroadcastReceiver() {


    val MUSIC_PLAY_NOTIFY_ID = 9
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getIntExtra(MusicService.ARG_TYPE, TYPE_PLAY)
        val title = intent.getStringExtra(MusicService.ARG_TITLE)
        LogUtil.e("MusicPlayReceiver  type:$type    title:$title")
        val mNotificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        val builder = NotificationCompat.Builder(context, "music")
//
//        val notification = builder.build()


        val notify: Notification

        val contentView = RemoteViews(context.packageName, R.layout.notification_music_control)
        //新建意图，用服务播放音乐
        val intentPlay = Intent(context, MusicService::class.java)
        intentPlay.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_START)
        val pIntentPlay = PendingIntent.getService(context, 0, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT)
        contentView.setOnClickPendingIntent(R.id.ivPlay, pIntentPlay)

        //新建意图，用服务暂停音乐
        val intentPause = Intent(context, MusicService::class.java)
        intentPause.putExtra(MusicService.ARG_TYPE, MusicService.TYPE_PAUSE)
        val pIntentPause = PendingIntent.getService(context, 0, intentPause, PendingIntent.FLAG_UPDATE_CURRENT)
        contentView.setOnClickPendingIntent(R.id.ivStop, pIntentPause);//为play控件注册事件


        if(!TextUtils.isEmpty(title)){
            contentView.setTextViewText(R.id.tvTitle, title)
        }


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {

//            val notificationIntent1 = Intent(context, Main2Activity::class.java)
//            notificationIntent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            // addflag设置跳转类型
//            val contentIntent1 = PendingIntent.getActivity(context,
//                    0, notificationIntent1, 0)
            notify = Notification()
            notify.`when` = System.currentTimeMillis()
            notify.number = 1
            notify.contentView = contentView
            notify.flags = notify.flags or Notification.FLAG_NO_CLEAR // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
            notify.sound = null
            notify.vibrate = null
            ;//设置通知点击或滑动时不被清除
            notify.flags = Notification.FLAG_NO_CLEAR
//            notify.contentIntent = contentIntent1

        } else {

            val builder = Notification.Builder(context)
                    .setContent(contentView)
                    .setWhen(System.currentTimeMillis())// 设置时间发生时间
                    .setAutoCancel(false)// 设置可以清除
                    .setSmallIcon(R.mipmap.ic_launcher)


            notify = builder.build()
            //设置通知点击或滑动时不被清除
            notify?.flags = Notification.FLAG_NO_CLEAR
        }

        if(type == MusicService.TYPE_PLAY){

            contentView.setViewVisibility(R.id.ivPlay, View.GONE)
            contentView.setViewVisibility(R.id.ivStop, View.VISIBLE)
            mNotificationManager.notify(MUSIC_PLAY_NOTIFY_ID, notify)
        } else if(type == MusicService.TYPE_PAUSE){

            contentView.setViewVisibility(R.id.ivPlay, View.VISIBLE)
            contentView.setViewVisibility(R.id.ivStop, View.GONE)
            mNotificationManager.notify(MUSIC_PLAY_NOTIFY_ID, notify)
        } else if(type == MusicService.TYPE_STOP){
            mNotificationManager.cancel(MUSIC_PLAY_NOTIFY_ID)
        }
    }

}