package com.example.canvasdodofrost

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs

/*
 @JvmOverloads - генерирует несколько видов констркуторов
 */
class RimeCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        // интересный флаг, отключающий аппаратное ускорение (не совсем понимаю, как и зачем)
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    private lateinit var backgroundSnow: Bitmap
    private lateinit var snow: Bitmap
    private lateinit var scratchBitmap: Bitmap
    private lateinit var scratchCanvas: Canvas

    private var mLastTouchX = 0f
    private var mLastTouchY = 0f

    private val paint by lazy {
        Paint()
    }
    private val innerPaintForDrawing by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 50f
        }
    }
    private val path by lazy {
        Path()
    }

    // про разные модификаторы при наложении изображений -
    // https://developer.android.com/reference/android/graphics/PorterDuff.Mode
    private val srcOverPorterDuffMode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    private val dstOutPorterDuffMode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // buffer bitmap for erasure
        scratchBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        scratchCanvas = Canvas(scratchBitmap)

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

        paint.xfermode = srcOverPorterDuffMode
        canvas.drawBitmap(backgroundSnow, 0f, 0f, paint)
        canvas.drawBitmap(snow, 0f, 0f, paint)

        paint.xfermode = dstOutPorterDuffMode
        canvas.drawBitmap(scratchBitmap, 0f, 0f, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val currentTouchX = event.x
        val currentTouchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.reset()
                path.moveTo(currentTouchX, currentTouchY)
            }

            MotionEvent.ACTION_UP -> {
                path.lineTo(currentTouchX, currentTouchY)
            }

            // рисуем квадратичную кривую Безье, если палец прошёл более 4 пикселей в одну из сторон (значение получено опытным путём), для того чтобы был эффект закругления.
            MotionEvent.ACTION_MOVE -> {
                val dx = abs(currentTouchX - mLastTouchX)
                val dy = abs(currentTouchY - mLastTouchY)
                if (dx >= 4 || dy >= 4) {
                    val x1 = mLastTouchX
                    val y1 = mLastTouchY
                    val x2 = (currentTouchX + mLastTouchX) / 2
                    val y2 = (currentTouchY + mLastTouchY) / 2
                    path.quadTo(x1, y1, x2, y2)
                }
            }
        }

        scratchCanvas.drawPath(path, innerPaintForDrawing)
        mLastTouchX = currentTouchX
        mLastTouchY = currentTouchY
        invalidate()
        return true
    }
}
