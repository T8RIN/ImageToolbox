package com.t8rin.drawbox.domain

import android.graphics.Bitmap
import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import com.t8rin.drawbox.presentation.model.DrawPath
import com.t8rin.drawbox.presentation.model.PaintOptions


interface DrawController {

    val paths: Map<DrawPath, PaintOptions>
    val lastPaths: Map<DrawPath, PaintOptions>
    val undonePaths: Map<DrawPath, PaintOptions>

    var paint: Paint
    var drawPath: DrawPath
    var paintOptions: PaintOptions

    val backgroundColor: Color

    var curX: Float
    var curY: Float
    var startX: Float
    var startY: Float
    var isStrokeWidthBarEnabled: Boolean
    var isEraserOn: Boolean

    fun undo()

    fun redo()

    fun setColor(newColor: Int)

    fun setDrawBackground(color: Color)

    fun setAlpha(newAlpha: Int)

    fun setStrokeWidth(newStrokeWidth: Float)

    suspend fun getBitmap(): Bitmap?

    fun addPath(path: DrawPath, options: PaintOptions)

    fun changePaint(paintOptions: PaintOptions)

    fun toggleEraser()

    fun clearPaths()

    fun clearDrawing()
}

@Suppress("UNUSED_PARAMETER")
abstract class AbstractDrawController : DrawController {
    override val paths: Map<DrawPath, PaintOptions>
        get() = emptyMap()
    override val lastPaths: Map<DrawPath, PaintOptions>
        get() = emptyMap()
    override val undonePaths: Map<DrawPath, PaintOptions>
        get() = emptyMap()
    override var paint: Paint
        get() = Paint()
        set(value) {}
    override var drawPath: DrawPath
        get() = DrawPath()
        set(value) {}
    override var paintOptions: PaintOptions
        get() = PaintOptions()
        set(value) {}
    override val backgroundColor: Color
        get() = Color.Transparent
    override var curX: Float
        get() = 0f
        set(value) {}
    override var curY: Float
        get() = 0f
        set(value) {}
    override var startX: Float
        get() = 0f
        set(value) {}
    override var startY: Float
        get() = 0f
        set(value) {}
    override var isStrokeWidthBarEnabled: Boolean
        get() = false
        set(value) {}
    override var isEraserOn: Boolean
        get() = false
        set(value) {}

    override fun undo() {}

    override fun redo() {}

    override fun setColor(newColor: Int) {}

    override fun setDrawBackground(color: Color) {}

    override fun setAlpha(newAlpha: Int) {}

    override fun setStrokeWidth(newStrokeWidth: Float) {}

    override suspend fun getBitmap(): Bitmap? = null

    override fun addPath(path: DrawPath, options: PaintOptions) {}

    override fun changePaint(paintOptions: PaintOptions) {}

    override fun toggleEraser() {}

    override fun clearPaths() {}

    override fun clearDrawing() {}
}