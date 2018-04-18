package com.aidongxiang.app.ui.mine

import android.text.TextUtils
import android.view.View
import com.aidongxiang.app.R
import com.aidongxiang.app.annotation.ContentView
import com.aidongxiang.app.base.BaseKtActivity
import com.aidongxiang.app.base.BaseKtFragment
import com.aidongxiang.app.base.Constants
import com.aidongxiang.app.base.Constants.ARG_ID
import com.aidongxiang.app.observer.IUserInfoChangeObserver
import com.aidongxiang.app.observer.UserInfoSubject
import com.aidongxiang.app.utils.GlideImgManager
import com.aidongxiang.business.model.User
import kotlinx.android.synthetic.main.fragment_mine.*

/**
 * 主页 - 我的
 */
@ContentView(R.layout.fragment_mine)
class MineFragment : BaseKtFragment(), IUserInfoChangeObserver {



    override fun init(view: View) {

        setListener()

        if(Constants.user != null){
            setUserData()
        } else {
            (activity as BaseKtActivity).requestUserDetails()
        }


    }

    private fun setListener() {

        llSetting.setOnClickListener{
            switchToActivity(SettingActivity::class.java)
        }
        llMyDownload.setOnClickListener {
            switchToActivity(MyDownloadActivity::class.java)
        }
        llMyCollection.setOnClickListener {
            switchToActivity(MyCollectionActivity::class.java)
        }
        llWatchHistory.setOnClickListener {
            switchToActivity(WatchHistoryActivity::class.java)
        }
        llMineUserInfo.setOnClickListener {
            Constants.user?.let { switchToActivity(PersonCenterActivity::class.java, ARG_ID to it.id) }

        }
        ivMineAvatar.setOnClickListener { switchToActivity(UserInfoActivity::class.java) }
        ll_mine_fans.setOnClickListener { switchToActivity(FansListActivity::class.java, Constants.ARG_TYPE to 2) }
        ll_mine_follow.setOnClickListener { switchToActivity(FansListActivity::class.java, Constants.ARG_TYPE to 1) }
        ll_mine_microblog.setOnClickListener { switchToActivity(MyMicroblogActivity::class.java) }

        UserInfoSubject.getInstance().registerObserver(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        UserInfoSubject.getInstance().removeObserver(this)
    }
    private fun setUserData() {
        if(Constants.user != null){
            val user = Constants.user!!
            if(TextUtils.isEmpty(user.description)){
                tvMineSignature.text = "快点设置签名一个个性吧"
            } else {
                tvMineSignature.text = user.description
            }
            tvMineName.text = user.nickName
            tvMineSignature.visibility = View.VISIBLE
            tvMineMicroblogNum.text = user.microblogNum
            tvMineFocusNum.text = user.focusNum
            tvMineFansNum.text = user.fansNum
            GlideImgManager.load(activity, user.imagePath, R.drawable.ic_avatar_default, ivMineAvatar, GlideImgManager.GlideType.TYPE_CIRCLE)
        } else {
            tvMineSignature.text = ""
            tvMineName.text = "未登录"
            tvMineSignature.visibility = View.GONE
            tvMineMicroblogNum.text = "0"
            tvMineFocusNum.text = "0"
            tvMineFansNum.text = "0"
            ivMineAvatar.setImageResource(R.drawable.ic_avatar_default)

        }
    }
    override fun update(user: User) {
        setUserData()
    }

    override fun logout() {
        Constants.user = null
        setUserData()
    }

}