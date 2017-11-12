package com.aidongxiang.app.ui.square

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.aidongxiang.app.R
import me.nereo.multi_image_selector.MultiImageSelectorFragment
import java.io.File
import android.support.v4.app.Fragment
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import kotlinx.android.synthetic.main.layout_title_bar_with_right_text.*
import me.nereo.multi_image_selector.MultiImageSelectorActivity

/**
 * 图片选择类
 * @author Anthony
 * createTime 2017-11-11
 */
@ContentView(R.layout.activity_image_select)
class ImageSelectActivity : BaseKtActivity() , MultiImageSelectorFragment.Callback{

    val datas = ArrayList<String>()
    companion object {
        val MODE_SINGLE = 0
        val MODE_MULTI = 1
        val EXTRA_SELECT_COUNT = "max_select_count"
        val EXTRA_SELECT_MODE = "select_count_mode"
        val EXTRA_SHOW_CAMERA = "show_camera"
        val EXTRA_RESULT = "select_result"
        private val DEFAULT_IMAGE_SIZE = 9
    }
    var max = DEFAULT_IMAGE_SIZE
    override fun init(savedInstanceState: Bundle?) {

        max = bundle.getInt(EXTRA_SELECT_COUNT, DEFAULT_IMAGE_SIZE)
        val bundle1 = Bundle()
        bundle1.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, max)
        bundle1.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, bundle.getInt(EXTRA_SELECT_MODE, MODE_MULTI))
        bundle1.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, bundle.getBoolean(EXTRA_SHOW_CAMERA, true))
        // Add fragment to your Activity
        supportFragmentManager.beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment::class.java.name, bundle1))
                .commit()
        MultiImageSelectorActivity.EXTRA_RESULT
        btn_title_confirm.setOnClickListener {
            intent.putExtra(EXTRA_RESULT, datas)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onSingleImageSelected(path: String?) {
        if(path == null) return
        datas.clear()
        datas.add(path)
        intent.putExtra(EXTRA_RESULT, datas)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }

    override fun onImageUnselected(path: String?) {
        if(path == null) return
        try {
            datas.remove(path)
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onCameraShot(file: File?) {
        if(file == null) return
        datas.add(file.absolutePath)
    }

    override fun onImageSelected(path: String?) {
        if(path == null) return
        datas.add(path)
    }

}
