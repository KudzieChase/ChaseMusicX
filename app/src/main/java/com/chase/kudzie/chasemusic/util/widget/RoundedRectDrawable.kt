package com.chase.kudzie.chasemusic.util.widget

import android.graphics.drawable.PaintDrawable

class RoundedRectDrawable(color: Int, radius: Float) : PaintDrawable(color) {
    init {
        setCornerRadius(radius)
    }
}