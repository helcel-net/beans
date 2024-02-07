package net.helcel.beendroid.helper

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue



fun colorPrimary(ctx : Context): ColorDrawable {
    val colorPrimaryTyped = TypedValue()
    ctx.theme.resolveAttribute(android.R.attr.colorPrimary, colorPrimaryTyped, true)
    return ColorDrawable(colorPrimaryTyped.data)
}
