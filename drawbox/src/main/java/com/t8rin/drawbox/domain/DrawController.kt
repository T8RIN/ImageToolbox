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
}