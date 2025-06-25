package com.practicum.playlistmaker.player.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
): View(context, attrs, defStyleAttr, defStyleRes) {

    private var isPlaying = false

    private val imagePlayBitmap: Bitmap?
    private val imagePauseBitmap: Bitmap?

    private var imageRect = RectF(0f, 0f, 0f, 0f)

    init {
         context.theme.obtainStyledAttributes(
             attrs,
             R.styleable.PlaybackButtonView,
             defStyleAttr,
             defStyleRes
         ).apply {
             try {
                 imagePlayBitmap = getDrawable(R.styleable.PlaybackButtonView_imagePlayResId)?.toBitmap()
                 imagePauseBitmap = getDrawable(R.styleable.PlaybackButtonView_imagePauseResId)?.toBitmap()
             } finally {
                 recycle()
             }
         }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                changeButtonState()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    fun changeButtonState() {
        isPlaying = !isPlaying
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (isPlaying) {
            imagePauseBitmap?.let {
                canvas.drawBitmap(imagePauseBitmap, null, imageRect, null)
            }
        } else {
            imagePlayBitmap?.let {
                canvas.drawBitmap(imagePlayBitmap, null, imageRect, null)
            }
        }
    }

}