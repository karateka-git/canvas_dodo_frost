package com.example.canvasdodofrost

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/*
 @JvmOverloads - генерирует несколько видов констркуторов
 */
class RimeCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var backgroundSnow: Bitmap
    private lateinit var snow: Bitmap
    private val paint by lazy {
        Paint()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // background for snow
        backgroundSnow = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val backgroundSnowCanvas = Canvas(backgroundSnow)
        val backgroundSnowDrawable =
            ContextCompat.getDrawable(context, R.drawable.background_for_snow)
        backgroundSnowDrawable?.setBounds(0, 0, w, h)
        backgroundSnowDrawable?.draw(backgroundSnowCanvas)

        // snow
        snow = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val snowCanvas = Canvas(snow)
        val snowDrawable = ContextCompat.getDrawable(context, R.drawable.snow)
        snowDrawable?.setBounds(0, 0, w, h)
        snowDrawable?.draw(snowCanvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(backgroundSnow, 0f, 0f, paint)
        canvas.drawBitmap(snow, 0f, 0f, paint)
    }
}
