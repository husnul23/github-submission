package com.husnul23.githubsubmission
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Github (var username: String = "", var avatar: String = "", ) : Parcelable

