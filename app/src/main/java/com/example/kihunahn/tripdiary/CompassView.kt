package org.androidtown.tripdiary

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import com.example.kihunahn.tripdiary.R

class CompassView(ctx: Context) : View(ctx) {
    private val mCompass: Drawable
    private var mAzimuth = 0f
    private val PADDING = 2

    init {

        this.mCompass = ctx.resources.getDrawable(R.drawable.arrow_n)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()

        canvas.rotate(360 - mAzimuth, (PADDING + mCompass.minimumWidth / 2).toFloat(), (PADDING + mCompass.minimumHeight / 2).toFloat())
        mCompass.setBounds(PADDING, PADDING, PADDING + mCompass.minimumWidth, PADDING + mCompass.minimumHeight)

        mCompass.draw(canvas)
        canvas.restore()

        super.onDraw(canvas)
    }

    fun setAzimuth(aAzimuth: Float) {
        mAzimuth = aAzimuth
    }


  
  
  
  
  

}