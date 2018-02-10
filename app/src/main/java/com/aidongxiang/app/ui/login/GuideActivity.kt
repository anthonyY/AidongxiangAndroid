package com.aidongxiang.app.ui.login

import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.Constants
import com.aiitec.openapi.utils.AiiUtil
import kotlinx.android.synthetic.main.activity_guide.*

/**
 * 引导页
 * @author Anthony
 * createTime 2018/1/28.
 * @version 1.0
 */

@ContentView(R.layout.activity_guide)
class GuideActivity : BaseKtActivity() {



    /**
     *  把需要滑动的页卡添加到这个list中
     */
    private var viewList = ArrayList<View>()
    private lateinit var myPagerAdapter: MyPagerAdapter

    /**
     * 过渡图片
     */
    private val drawableIds = intArrayOf(R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4)

    override fun init(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AiiUtil.putBoolean(this, Constants.ARG_FIRST_LAUNCHER, false)
        init()
    }

    private fun init() {

        for(drawableId in drawableIds){
            val imageView = ImageView(this)
            imageView.setImageResource(drawableId)
            val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            imageView.layoutParams = params
            viewList.add(imageView)
        }
        myPagerAdapter = MyPagerAdapter(viewList)
        viewpager.adapter = myPagerAdapter
        viewpager.currentItem = 0
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
//                if (position == viewList.size - 1) {
//                    btn_immediate_experience.visibility = View.VISIBLE
//                } else {
//                    btn_immediate_experience.visibility = View.GONE
//                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        btn_immediate_experience.setOnClickListener {
            switchToActivity(LoginActivity::class.java)
            finish()
        }
    }

    inner class MyPagerAdapter(private val mListViews: List<View>)// 构造方法，参数是我们的页卡，这样比较方便。
        : PagerAdapter() {

        override fun getCount(): Int {
            return mListViews.size
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            return arg0 === arg1
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any { // 这个方法用来实例化页卡
            val guidePictureView = mListViews[position]
            if (position == mListViews.size - 1) {
                guidePictureView.setOnClickListener {
                    guidePictureView.isEnabled = false
                    switchToActivity(LoginActivity::class.java)
                    finish()
                }
            }
            container.addView(mListViews[position], 0)// 添加页卡
            return mListViews[position]
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(mListViews[position])// 删除页卡
        }
    }
}
