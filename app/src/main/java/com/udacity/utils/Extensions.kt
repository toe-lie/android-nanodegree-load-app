package com.udacity.utils

import android.content.res.Resources
import android.util.TypedValue

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

val Int.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics
    )