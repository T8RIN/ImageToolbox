@file:Suppress("UNCHECKED_CAST")

package com.t8rin.drawbox.domain

import android.graphics.Bitmap
import android.graphics.Paint
import com.t8rin.drawbox.presentation.model.DrawPath
import com.t8rin.drawbox.presentation.model.PaintOptions


interface DrawController {
    var paths: LinkedHashMap<DrawPath, PaintOptions>

    var lastPaths: LinkedHashMap<DrawPath, PaintOptions>
    var undonePaths: LinkedHashMap<DrawPath, PaintOptions>

    var paint: Paint
    var drawPath: DrawPath
    var paintOptions: PaintOptions

    var curX: Float
    var curY: Float
    var startX: Float
    var startY: Float
    var isSaving: Boolean
    var isStrokeWidthBarEnabled: Boolean
    var isEraserOn: Boolean

    fun undo()

    fun redo()

    fun setColor(newColor: Int)
    fun setAlpha(newAlpha: Int)

    fun setStrokeWidth(newStrokeWidth: Float)

    fun getBitmap(): Bitmap

    fun addPath(path: DrawPath, options: PaintOptions)

    fun changePaint(paintOptions: PaintOptions)

    fun toggleEraser()
}