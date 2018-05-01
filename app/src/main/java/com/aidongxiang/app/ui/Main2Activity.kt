package com.aidongxiang.app.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.ui.audio.AudioFragment
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.ui.login.LoginActivity
import com.aidongxiang.app.ui.mine.MineFragment
import com.aidongxiang.app.ui.square.SquareFragment
import com.aidongxiang.app.ui.video.VideoFragment
import com.aidongxiang.app.utils.BottomNavigationViewHelper
import com.aidongxiang.app.utils.LocationUtils
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

    override fun init(savedInstanceState: Bundle?) {

        BottomNavigationViewHelper.disableShiftMode(navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        lastItemId = R.id.navigation_square
        switchFragment(homeFragment)

        LocationUtils.startLocation()
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
            transaction.add(R.id.container, fragment).commit()
        } else {
            transaction.show(fragment).commit()
        }
        currentFragment = fragment
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    fun swicthFragment(index : Int){
        when(index){
//            0-> switchFragment(homeFragment)
//            1-> switchFragment(videoFragment)
//            2-> switchFragment(squareFragment)
//            3-> switchFragment(audioFragment)
//            4-> switchFragment(mineFragment)

            0-> navigation.selectedItemId = R.id.navigation_home
            1-> navigation.selectedItemId = R.id.navigation_video
            2-> navigation.selectedItemId = R.id.navigation_square
            3-> navigation.selectedItemId = R.id.navigation_audio
            4-> navigation.selectedItemId = R.id.navigation_mine
        }
    }
}
