package com.aidongxiang.app.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.Api
import com.aidongxiang.app.base.App
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.ui.audio.AudioDetailsActivity
import com.aidongxiang.app.ui.audio.AudioFragment
import com.aidongxiang.app.ui.home.ArticleDetailsActivity
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.ui.mine.MineFragment
import com.aidongxiang.app.ui.square.SquareFragment
import com.aidongxiang.app.ui.video.VideoDetails2Activity
import com.aidongxiang.app.ui.video.VideoFragment
import com.aidongxiang.app.utils.BottomNavigationViewHelper
import com.aidongxiang.app.utils.LocationUtils
import com.aidongxiang.app.utils.PermissionsUtils
import com.aidongxiang.app.utils.VersionCheck
import com.aidongxiang.app.widgets.CommonDialog
import com.aiitec.openapi.utils.AiiUtil
import kotlinx.android.synthetic.main.activity_main2.*

/**
 * 主页
 * @author Anthony
 * createTime 2017-11-04
 */
@ContentView(R.layout.activity_main2)
class Main2Activity : BaseKtActivity() {

    var lastItemId = -1
    var currentFragment : Fragment ?= null
    private val homeFragment = HomeFragment()
    private val mineFragment = MineFragment()
    private val videoFragment = VideoFragment()
    private val audioFragment = AudioFragment()
    private val squareFragment = SquareFragment()
    lateinit var permissionUtils : PermissionsUtils
    lateinit var exitDialog : CommonDialog
    override fun init(savedInstanceState: Bundle?) {

        BottomNavigationViewHelper.disableShiftMode(navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        lastItemId = R.id.navigation_square
        switchFragment(homeFragment)

        permissionUtils = PermissionsUtils(this)
        permissionUtils.setOnPermissionsListener(object : PermissionsUtils.OnPermissionsListener{
            override fun onPermissionsSuccess(requestCode: Int) {
                LocationUtils.startLocation()

            }

            override fun onPermissionsFailure(requestCode: Int) {
            }

        })
        permissionUtils.requestPermissions(1, android.Manifest.permission.ACCESS_FINE_LOCATION)
        VersionCheck(this).startCheck(Api.VERSION_URL, false)

        val videoId = AiiUtil.getLong(this, Constants.ARG_VIDEO_ID, -1)
        val audioId = AiiUtil.getLong(this, Constants.ARG_AUDIO_ID, -1)
        val newsId = AiiUtil.getLong(this, Constants.ARG_NEWS_ID, -1)
        val microblogId = AiiUtil.getLong(this, Constants.ARG_MICROBLOG_ID, -1)
        val abstract = AiiUtil.getLong(this, Constants.ARG_ABSTRACT)
        when {
            videoId > 0 -> switchToActivity(VideoDetails2Activity::class.java, ARG_ID to videoId)
            audioId > 0 -> switchToActivity(AudioDetailsActivity::class.java, ARG_ID to audioId)
            newsId > 0 -> switchToActivity(ArticleDetailsActivity::class.java, Constants.ARG_TITLE to "资讯详情", Constants.ARG_ABSTRACT to abstract, Constants.ARG_ID to newsId)
            microblogId > 0 -> swicthFragment(2)
        }
        exitDialog = CommonDialog(this)
        exitDialog.setTitle("确认退出爱侗乡？")
        exitDialog.setOnConfirmClickListener {
            (application as App).exit()
        }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(homeFragment)
                lastItemId = R.id.navigation_home
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_video -> {
                switchFragment(videoFragment)
                lastItemId = R.id.navigation_video
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_square -> {
                switchFragment(squareFragment)
                lastItemId = R.id.navigation_square
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_audio -> {
                switchFragment(audioFragment)
                lastItemId = R.id.navigation_audio
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mine -> {
                if(Constants.user == null){
                    //需要延时一点来处理，否则不起作用
                    Handler().postDelayed({  navigation.selectedItemId = lastItemId }, 100)

                    switchToActivity(LoginActivity::class.java)
                    return@OnNavigationItemSelectedListener true
                }
                switchFragment(mineFragment)
                lastItemId = R.id.navigation_mine
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    /**
     * 切换Fragment
     */
    private fun switchFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }
        if (!fragment.isAdded) {
            transaction.add(R.id.container, fragment).commitAllowingStateLoss()
        } else {
            transaction.show(fragment).commitAllowingStateLoss()
        }
        currentFragment = fragment
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    fun swicthFragment(index : Int){
        when(index){

            0-> navigation.selectedItemId = R.id.navigation_home
            1-> navigation.selectedItemId = R.id.navigation_video
            2-> navigation.selectedItemId = R.id.navigation_square
            3-> navigation.selectedItemId = R.id.navigation_audio
            4-> navigation.selectedItemId = R.id.navigation_mine
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        if(exitDialog.isShowing){
            exitDialog.dismiss()
        } else {
            exitDialog.show()
        }
    }
}
