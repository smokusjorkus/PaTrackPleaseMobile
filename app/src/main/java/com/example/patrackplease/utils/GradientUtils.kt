package com.example.patrackplease.utils

import android.graphics.Color

object GradientUtils {

    fun blend(color1: Int, color2: Int, value: Float): Int {
        val inverse = 1f - value

        val a = (Color.alpha(color1) * inverse + Color.alpha(color2) * value).toInt()
        val r = (Color.red(color1) * inverse + Color.red(color2) * value).toInt()
        val g = (Color.green(color1) * inverse + Color.green(color2) * value).toInt()
        val b = (Color.blue(color1) * inverse + Color.blue(color2) * value).toInt()

        return Color.argb(a, r, g, b)
    }
}