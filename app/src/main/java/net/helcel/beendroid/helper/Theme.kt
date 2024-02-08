package net.helcel.beendroid.helper

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity

fun colorWrapper(ctx : Context, res: Int): ColorDrawable {
    val colorPrimaryTyped = TypedValue()
    ctx.theme.resolveAttribute(res, colorPrimaryTyped, true)
    return ColorDrawable(colorPrimaryTyped.data)
}

fun colorPrimary(ctx : Context): ColorDrawable {
    return colorWrapper(ctx, android.R.attr.colorPrimary)
}

fun colorBackground(ctx : Context): ColorDrawable {
    return colorWrapper(ctx, android.R.attr.colorBackground)
}

fun colorPanelBackground(ctx: Context): ColorDrawable {
    return colorWrapper(ctx, android.R.attr.panelColorBackground)
}

fun createActionBar(ctx: AppCompatActivity, title: String) {
    ctx.supportActionBar?.setBackgroundDrawable(colorPrimary(ctx))
    ctx.supportActionBar?.title = title
    ctx.supportActionBar?.setDisplayHomeAsUpEnabled(true)
}