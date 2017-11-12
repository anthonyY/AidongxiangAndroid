package com.aidongxiang.app.ui

import android.content.Intent
import android.media.AudioFormat
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.ui.audio.AudioFragment
import com.aidongxiang.app.ui.home.HomeFragment
import com.aidongxiang.app.ui.mine.MineFragment
import com.aidongxiang.app.ui.square.SquareFragment
import com.aidongxiang.app.ui.video.VideoFragment
import com.aidongxiang.app.utils.BottomNavigationViewHelper
import kotlinx.android.synthetic.main.activity_main2.*

/**
 * 主页
 * @author Anthony
 * createTime 2017-11-04
 */
@ContentView(R.layout.activity_main2)
class Main2Activity : BaseKtActivity() {

    var currentFragment : Fragment ?= null
    private val homeFragment = HomeFragment()
    private val mineFragment = MineFragment()
    private val videoFragment = VideoFragment()
    private val audioFragment = AudioFragment()
    private val squareFragment = SquareFragment()

    override fun init(savedInstanceState: Bundle?) {

        BottomNavigationViewHelper.disableShiftMode(navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        switchFragment(homeFragment)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                switchFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_video -> {
                switchFragment(videoFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_square -> {
                switchFragment(squareFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_audio -> {
                switchFragment(audioFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_mine -> {
                switchFragment(mineFragment)
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
}
