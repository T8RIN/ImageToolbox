@file:Suppress("UNCHECKED_CAST", "unused", "MemberVisibilityCanBePrivate")

package com.t8rin.drawbox.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.t8rin.drawbox.domain.DrawController
import com.t8rin.drawbox.presentation.model.DrawPath
import com.t8rin.drawbox.presentation.model.PaintOptions

class DrawView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), DrawController {
    override var paths = LinkedHashMap<DrawPath, PaintOptions>()

    override var lastPaths = LinkedHashMap<DrawPath, PaintOptions>()
    override var undonePaths = LinkedHashMap<DrawPath, PaintOptions>()

    override var paint = Paint()
    override var drawPath = DrawPath()
    override var paintOptions = PaintOptions()

    override var curX = 0f
    override var curY = 0f
    override var startX = 0f
    override var startY = 0f
    override var isSaving = false
    override var isStrokeWidthBarEnabled = false

    override var isEraserOn = false

    init {
        paint.apply {
            color = paintOptions.color
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = paintOptions.strokeWidth
            isAntiAlias = true
        }
    }

    override fun undo() {
        if (paths.isEmpty() && lastPaths.isNotEmpty()) {
            paths = lastPaths.clone() as LinkedHashMap<DrawPath, PaintOptions>
            lastPaths.clear()
            invalidate()
            return
        }
        if (paths.isEmpty()) {
            return
        }
        val lastPath = paths.values.lastOrNull()
        val lastKey = paths.keys.lastOrNull()

        paths.remove(lastKey)
        if (lastPath != null && lastKey != null) {
            undonePaths[lastKey] = lastPath
        }
        invalidate()
    }

    override fun redo() {
        if (undonePaths.keys.isEmpty()) {
            return
        }

        val lastKey = undonePaths.keys.last()
        addPath(lastKey, undonePaths.values.last())
        undonePaths.remove(lastKey)
        invalidate()
    }

    override fun setColor(newColor: Int) {
        @ColorInt
        val alphaColor = ColorUtils.setAlphaComponent(newColor, paintOptions.alpha)
        paintOptions.color = alphaColor
        if (isStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    override fun setAlpha(newAlpha: Int) {
        val alpha = (newAlpha * 255) / 100
        paintOptions.alpha = alpha
        setColor(paintOptions.color)
    }

    override fun setStrokeWidth(newStrokeWidth: Float) {
        paintOptions.strokeWidth = newStrokeWidth
        if (isStrokeWidthBarEnabled) {
            invalidate()
        }
    }

    override fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        isSaving = true
        draw(canvas)
        isSaving = false
        return bitmap
    }

    override fun addPath(path: DrawPath, options: PaintOptions) {
        paths[path] = options
    }

    override fun changePaint(paintOptions: PaintOptions) {
        paint.color = if (paintOptions.isEraserOn) Color.WHITE else paintOptions.color
        paint.strokeWidth = paintOptions.strokeWidth
    }

    override fun toggleEraser() {
        isEraserOn = !isEraserOn
        paintOptions.isEraserOn = isEraserOn
        invalidate()
    }

    fun clearCanvas() {
        lastPaths = paths.clone() as LinkedHashMap<DrawPath, PaintOptions>
        drawPath.reset()
        paths.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for ((key, value) in paths) {
            changePaint(value)
            canvas.drawPath(key, paint)
        }

        changePaint(paintOptions)
        canvas.drawPath(drawPath, paint)
    }

    private fun actionDown(x: Float, y: Float) {
        drawPath.reset()
        drawPath.moveTo(x, y)
        curX = x
        curY = y
    }

    private fun actionMove(x: Float, y: Float) {
        drawPath.quadTo(curX, curY, (x + curX) / 2, (y + curY) / 2)
        curX = x
        curY = y
    }

    private fun actionUp() {
        drawPath.lineTo(curX, curY)

        // draw a dot on click
        if (startX == curX && startY == curY) {
            drawPath.lineTo(curX, curY + 2)
            drawPath.lineTo(curX + 1, curY + 2)
            drawPath.lineTo(curX + 1, curY)
        }

        paths[drawPath] = paintOptions
        drawPath = DrawPath()
        paintOptions = PaintOptions(
            paintOptions.color,
            paintOptions.strokeWidth,
            paintOptions.alpha,
            paintOptions.isEraserOn
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = x
                startY = y
                actionDown(x, y)
                undonePaths.clear()
            }

            MotionEvent.ACTION_MOVE -> actionMove(x, y)
            MotionEvent.ACTION_UP -> actionUp()
        }

        invalidate()
        return true
    }
}