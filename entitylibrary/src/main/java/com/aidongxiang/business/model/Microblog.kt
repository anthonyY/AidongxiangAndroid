package com.aidongxiang.business.model

import android.os.Parcel
import android.os.Parcelable
import com.aiitec.openapi.model.Entity

/**
 *
 * @author Anthony
 * createTime 2017/12/10.
 * @version 1.0
 */
class Microblog() : Entity(), Parcelable {


    var id : Long = -1
    var content : String ?= null
    var timestamp : String ?= null
    var address : String ?= null
    var accessUrl : String ?= null
    var praiseNum = -1
    var commentNum = -1
    var repeatNum = -1
    var videoPath : String ?= null
    var isFocus = -1
    var isPraise = -1
    var user : User ?= null
    var videoId : Long = -1
    var regionId : Int = -1
    var parentId : Long = -1
    var longitude : Double = -1.0
    var latitude : Double = -1.0
    var images  : ArrayList<String> ?= null
    var imageIds  : ArrayList<Long> ?= null
    var originMicroblog  : Microblog ?= null

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        content = parcel.readString()
        timestamp = parcel.readString()
        address = parcel.readString()
        accessUrl = parcel.readString()
        praiseNum = parcel.readInt()
        commentNum = parcel.readInt()
        repeatNum = parcel.readInt()
        videoPath = parcel.readString()
        isFocus = parcel.readInt()
        isPraise = parcel.readInt()
        user = parcel.readParcelable(User::class.java.classLoader)
        videoId = parcel.readLong()
        regionId = parcel.readInt()
        parentId = parcel.readLong()
        longitude = parcel.readDouble()
        latitude = parcel.readDouble()
        images = parcel.createStringArrayList()
        imageIds = ArrayList()
        parcel.readList(this.imageIds, Long::class.java.classLoader)
        originMicroblog = parcel.readParcelable(Microblog::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(content)
        parcel.writeString(timestamp)
        parcel.writeString(address)
        parcel.writeString(accessUrl)
        parcel.writeInt(praiseNum)
        parcel.writeInt(commentNum)
        parcel.writeInt(repeatNum)
        parcel.writeString(videoPath)
        parcel.writeInt(isFocus)
        parcel.writeInt(isPraise)
        parcel.writeParcelable(user, flags)
        parcel.writeLong(videoId)
        parcel.writeInt(regionId)
        parcel.writeLong(parentId)
        parcel.writeDouble(longitude)
        parcel.writeDouble(latitude)
        parcel.writeStringList(images)
        parcel.writeList(imageIds)
        parcel.writeParcelable(originMicroblog, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Microblog> {
        override fun createFromParcel(parcel: Parcel): Microblog {
            return Microblog(parcel)
        }

        override fun newArray(size: Int): Array<Microblog?> {
            return arrayOfNulls(size)
        }
    }

}