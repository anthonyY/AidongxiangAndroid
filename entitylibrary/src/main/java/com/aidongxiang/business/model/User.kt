package com.aidongxiang.business.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/12/9.
 * @version 1.0
 */
class User() : Entity(), Parcelable {
    var id : Long = -1
    var name : String ?= null
    var imagePath : String ?= null

    var nickName : String ?= null
    var sex = -1
    var description : String ?= null
    var backImagePath : String ?= null
    var regionId : String ?= null
    var regionInfo : String ?= null
    var fansNum : String ?= null
    var focusNum : String ?= null
    var microblogNum : String ?= null
    var mobile : String ?= null
    var isFocus = -1

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString()
        imagePath = parcel.readString()
        nickName = parcel.readString()
        sex = parcel.readInt()
        description = parcel.readString()
        backImagePath = parcel.readString()
        regionId = parcel.readString()
        regionInfo = parcel.readString()
        fansNum = parcel.readString()
        focusNum = parcel.readString()
        microblogNum = parcel.readString()
        mobile = parcel.readString()
        isFocus = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(imagePath)
        parcel.writeString(nickName)
        parcel.writeInt(sex)
        parcel.writeString(description)
        parcel.writeString(backImagePath)
        parcel.writeString(regionId)
        parcel.writeString(regionInfo)
        parcel.writeString(fansNum)
        parcel.writeString(focusNum)
        parcel.writeString(microblogNum)
        parcel.writeString(mobile)
        parcel.writeInt(isFocus)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}