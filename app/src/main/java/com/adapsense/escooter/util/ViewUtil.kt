package com.adapsense.escooter.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object ViewUtil {

    fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        )
            .toInt()
    }

    fun vectorDrawableToBitmapDescriptor(
        context: Context?,
        @DrawableRes id: Int
    ): BitmapDescriptor? {
        val vectorDrawable = ContextCompat.getDrawable(context!!, id)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}