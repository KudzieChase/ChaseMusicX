package com.chase.kudzie.chasemusic.util.transition

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import androidx.transition.Transition
import androidx.transition.TransitionValues
import com.chase.kudzie.chasemusic.R

/**
 * A transition which sets a specified [Animatable] `drawable` on a target
 * [ImageView] and [starts][Animatable.start] it when the transition begins.
 *
 * Source: Plaid::: https://github.com/nickbutcher/plaid/
 */
class StartAnimatable : Transition {
    private val animatable: Animatable?

    constructor(animatable: Animatable?) : super() {
        require(animatable is Drawable) { "Non-Drawable resource provided." }
        this.animatable = animatable
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.StartAnimatable)
        val drawable = a.getDrawable(R.styleable.StartAnimatable_android_src)
        a.recycle()
        animatable = if (drawable is Animatable) {
            drawable
        } else {
            throw IllegalArgumentException("Non-Animatable resource provided.")
        }
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        // no-op
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        // no-op
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {
        if (animatable == null || endValues == null || endValues.view !is ImageView) return null
        val iv = endValues.view as ImageView
        iv.setImageDrawable(animatable as Drawable?)

        // need to return a non-null Animator even though we just want to listen for the start
        val transition = ValueAnimator.ofInt(0, 1)
        transition.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                animatable.start()
            }
        })
        return transition
    }
}