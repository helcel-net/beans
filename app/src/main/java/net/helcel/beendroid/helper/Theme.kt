package net.helcel.beendroid.helper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils

fun colorWrapper(ctx : Context, res: Int): ColorDrawable {
    val colorPrimaryTyped = TypedValue()
    ctx.theme.resolveAttribute(res, colorPrimaryTyped, true)
    return ColorDrawable(colorPrimaryTyped.data)
}

fun colorToHex6(c: ColorDrawable): String {
    return '#'+colorToHex8(c).substring(3)
}

@OptIn(ExperimentalStdlibApi::class)
fun colorToHex8(c: ColorDrawable): String {
    return '#'+c.color.toHexString()
}

fun createActionBar(ctx: AppCompatActivity, title: String) {
    ctx.supportActionBar?.setBackgroundDrawable(colorWrapper(ctx, android.R.attr.colorPrimary))
    ctx.supportActionBar?.title = title
    ctx.supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

fun getContrastColor(color: Int): Int {
    val whiteContrast = ColorUtils.calculateContrast(Color.WHITE, color)
    val blackContrast = ColorUtils.calculateContrast(Color.BLACK, color)
    return if (whiteContrast > blackContrast) Color.WHITE else Color.BLACK
}