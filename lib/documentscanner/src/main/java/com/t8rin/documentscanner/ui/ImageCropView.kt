/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.documentscanner.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.withClip
import com.t8rin.documentscanner.R
import com.t8rin.documentscanner.enums.QuadCorner
import com.t8rin.documentscanner.extensions.changeByteCountByResizing
import com.t8rin.documentscanner.extensions.distance
import com.t8rin.documentscanner.extensions.drawQuad
import com.t8rin.documentscanner.models.Quad
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * This class contains the original document photo, and a cropper. The user can drag the corners
 * to make adjustments to the detected corners.
 *
 * @param context view context
 * @param attrs view attributes
 * @constructor creates image crop view
 */
class ImageCropView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    /**
     * @property quad the 4 document corners
     */
    private var quad: Quad? = null

    /**
     * @property prevTouchPoint keep track of where the user touches, so we know how much
     * to move corners on drag
     */
    private var prevTouchPoint: PointF? = null

    /**
     * @property dragTarget corner or edge that should move on drag
     */
    private var dragTarget: CropperDragTarget? = null

    /**
     * @property cropperLineStyle paint style for connecting lines
     */
    private val cropperLineStyle = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * @property cropperCornerStyle paint style for 4 corners
     */
    private val cropperCornerStyle = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * @property cropperCornerOutlineStyle paint style for the outer ring around corner handles
     */
    private val cropperCornerOutlineStyle = Paint(Paint.ANTI_ALIAS_FLAG)

    private val magnifierPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val magnifierMatrix = Matrix()
    private val magnifierClip = Path()
    private val magnifierDotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var magnifierDotRadius = 0f
    private var magnifierBelowTouch = false
    private var magnifierSideProgress = -1f
    private var magnifierSideAnimator: ValueAnimator? = null
    private val magnifierRadius: Float
        get() {
            val inset = MagnifierInsetDp.dpToPx()
            val halfHeight = height / 2f - MagnifierGapDp.dpToPx() - inset
            return minOf(MagnifierRadiusDp.dpToPx(), width / 2f - inset, halfHeight / 2f)
        }

    /**
     * @property imagePreviewHeight this is needed because height doesn't update immediately
     * after we set the image
     */
    private var imagePreviewHeight = height

    /**
     * @property imagePreviewWidth this is needed because width doesn't update immediately
     * after we set the image
     */
    private var imagePreviewWidth = width

    /**
     * @property ratio image container height to image height ratio used to map container
     * to image coordinates and vice versa
     */
    private val ratio: Float get() = imagePreviewBounds.height() / drawable.intrinsicHeight

    /**
     * @property corners document corners in image preview coordinates
     */
    val corners: Quad get() = quad!!

    /**
     * @property imagePreviewMaxSizeInBytes if the photo is too big, we need to scale it down
     * before we display it
     */
    private val imagePreviewMaxSizeInBytes = 100 * 1024 * 1024

    init {
        val defaultCropperStrokeWidth = DefaultCropperStrokeWidthDp.dpToPx()

        // set cropper style
        cropperLineStyle.color = Color.WHITE
        cropperLineStyle.style = Paint.Style.STROKE
        cropperLineStyle.strokeWidth = defaultCropperStrokeWidth

        cropperCornerStyle.color = Color.WHITE
        cropperCornerStyle.style = Paint.Style.STROKE
        cropperCornerStyle.strokeWidth = defaultCropperStrokeWidth

        cropperCornerOutlineStyle.color = Color.argb(128, 255, 255, 255)
        cropperCornerOutlineStyle.style = Paint.Style.STROKE
        cropperCornerOutlineStyle.strokeWidth = defaultCropperStrokeWidth
    }

    /**
     * Initially the image preview height is 0. This calculates the height by using the photo
     * dimensions. It maintains the photo aspect ratio (we likely need to scale the photo down
     * to fit the preview container).
     *
     * @param photo the original photo with a rectangular document
     * @param screenWidth the device width
     */
    fun setImagePreviewBounds(photo: Bitmap, screenWidth: Int, screenHeight: Int) {
        // image width to height aspect ratio
        val imageRatio = photo.width.toFloat() / photo.height.toFloat()
        val buttonsViewMinHeight = context.resources.getDimension(
            R.dimen.buttons_container_min_height
        ).toInt()

        imagePreviewHeight = if (photo.height > photo.width) {
            // if user takes the photo in portrait
            (screenWidth.toFloat() / imageRatio).toInt()
        } else {
            // if user takes the photo in landscape
            (screenWidth.toFloat() * imageRatio).toInt()
        }

        // set a cap on imagePreviewHeight, so that the bottom buttons container isn't hidden
        imagePreviewHeight = Integer.min(
            imagePreviewHeight,
            screenHeight - buttonsViewMinHeight
        )

        imagePreviewWidth = screenWidth

        // image container initially has a 0 width and 0 height, calculate both and set them
        layoutParams.height = imagePreviewHeight
        layoutParams.width = imagePreviewWidth

        // refresh layout after we change height
        requestLayout()
    }

    /**
     * Insert bitmap in image view, and trigger onSetImage event handler
     */
    fun setImage(photo: Bitmap) {
        var previewImagePhoto = photo
        // if the image is too large, we need to scale it down before displaying it
        if (photo.byteCount > imagePreviewMaxSizeInBytes) {
            previewImagePhoto = photo.changeByteCountByResizing(imagePreviewMaxSizeInBytes)
        }
        this.setImageBitmap(previewImagePhoto)
        this.onSetImage()
    }

    /**
     * Once the user takes a photo, we try to detect corners. This function stores them as quad.
     *
     * @param cropperCorners 4 corner points in original photo coordinates
     */
    fun setCropper(cropperCorners: Quad) {
        quad = cropperCorners
    }

    /**
     * Set cropper colors from the app theme.
     */
    fun setCropperColors(handleColor: Int, frameColor: Int) {
        cropperCornerStyle.color = handleColor
        cropperLineStyle.color = frameColor
        invalidate()
    }

    /**
     * Set cropper colors and line width from the app theme.
     */
    fun setCropperStyle(
        handleColor: Int,
        frameColor: Int,
        handleOutlineColor: Int,
        strokeWidthDp: Float
    ) {
        val strokeWidth = strokeWidthDp.dpToPx()

        cropperCornerStyle.color = handleColor
        cropperCornerOutlineStyle.color = handleOutlineColor
        cropperLineStyle.color = frameColor
        cropperCornerStyle.strokeWidth = strokeWidth
        cropperCornerOutlineStyle.strokeWidth = strokeWidth
        cropperLineStyle.strokeWidth = strokeWidth
        invalidate()
    }

    internal fun setMagnifierDot(color: Int, sizeDp: Float) {
        magnifierDotPaint.color = color
        magnifierDotRadius = sizeDp.dpToPx() / 2f
        invalidate()
    }

    /**
     * @property imagePreviewBounds image coordinates - if the image ratio is different than
     * the image container ratio then there's blank space either at the top and bottom of the
     * image or the left and right of the image
     */
    val imagePreviewBounds: RectF
        get() {
            // image container width to height ratio
            val imageViewRatio: Float = imagePreviewWidth.toFloat() / imagePreviewHeight.toFloat()

            // image width to height ratio
            val imageRatio = drawable.intrinsicWidth.toFloat() / drawable.intrinsicHeight.toFloat()

            var left = 0f
            var top = 0f
            var right = imagePreviewWidth.toFloat()
            var bottom = imagePreviewHeight.toFloat()

            if (imageRatio > imageViewRatio) {
                // if the image is really wide, there's blank space at the top and bottom
                val offset = (imagePreviewHeight - (imagePreviewWidth / imageRatio)) / 2
                top += offset
                bottom -= offset
            } else {
                // if the image is really tall, there's blank space at the left and right
                // it's also possible that the image ratio matches the image container ratio
                // in which case there's no blank space
                val offset = (imagePreviewWidth - (imagePreviewHeight * imageRatio)) / 2
                left += offset
                right -= offset
            }

            return RectF(left, top, right, bottom)
        }

    /**
     * This gets called once we insert an image in this image view
     */
    private fun onSetImage() {
        magnifierPaint.shader = BitmapShader(
            drawable.toBitmap(),
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
    }

    private fun findDragTarget(touchPoint: PointF): CropperDragTarget {
        val touchRadius = resources.getDimension(R.dimen.cropper_corner_radius) * 2f
        val touchedCorner = quad!!.corners
            .mapNotNull { (corner, point) ->
                val distance = point.distance(touchPoint)
                if (distance <= touchRadius) {
                    corner to distance
                } else null
            }
            .minByOrNull { it.second }
            ?.first

        if (touchedCorner != null) return CropperDragTarget.Corner(touchedCorner)

        val touchedEdge = cropperEdges
            .mapNotNull { (firstCorner, secondCorner) ->
                val firstPoint = quad!!.corners[firstCorner] ?: return@mapNotNull null
                val secondPoint = quad!!.corners[secondCorner] ?: return@mapNotNull null
                val distance = touchPoint.closestPointOnSegment(firstPoint, secondPoint)
                    .distance(touchPoint)

                if (distance <= touchRadius) {
                    CropperDragTarget.Edge(firstCorner, secondCorner) to distance
                } else null
            }
            .minByOrNull { it.second }
            ?.first

        return touchedEdge ?: CropperDragTarget.Corner(quad!!.getCornerClosestToPoint(touchPoint))
    }

    private fun PointF.coerceDragAmountFor(corners: Set<QuadCorner>): PointF {
        val bounds = imagePreviewBounds
        var minX = Float.NEGATIVE_INFINITY
        var maxX = Float.POSITIVE_INFINITY
        var minY = Float.NEGATIVE_INFINITY
        var maxY = Float.POSITIVE_INFINITY

        corners.forEach { corner ->
            val point = quad!!.corners[corner] ?: return@forEach
            minX = minX.coerceAtLeast(bounds.left - point.x)
            maxX = maxX.coerceAtMost(bounds.right - point.x)
            minY = minY.coerceAtLeast(bounds.top - point.y)
            maxY = maxY.coerceAtMost(bounds.bottom - point.y)
        }

        return PointF(
            x.coerceIn(minX, maxX),
            y.coerceIn(minY, maxY)
        )
    }

    /**
     * This gets called constantly, and we use it to update the cropper corners
     *
     * @param canvas the image preview canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val quad = quad ?: return
        canvas.drawQuad(
            quad,
            resources.getDimension(R.dimen.cropper_corner_radius),
            cropperLineStyle,
            cropperCornerStyle,
            cropperCornerOutlineStyle
        )

        val touchPoint = prevTouchPoint ?: return
        val source = when (val target = dragTarget) {
            is CropperDragTarget.Corner -> quad.corners.getValue(target.corner)
            is CropperDragTarget.Edge -> touchPoint.closestPointOnSegment(
                quad.corners.getValue(target.firstCorner),
                quad.corners.getValue(target.secondCorner)
            )

            null -> return
        }
        drawMagnifier(canvas, quad, touchPoint, source)
    }

    private fun drawMagnifier(canvas: Canvas, quad: Quad, touchPoint: PointF, source: PointF) {
        val shader = magnifierPaint.shader ?: return
        val radius = magnifierRadius
        val alpha = (abs(magnifierSideProgress) * 255).roundToInt()
        if (radius <= 0f || alpha == 0) return

        val margin = radius + MagnifierInsetDp.dpToPx()
        val centerX = touchPoint.x.coerceIn(margin, width - margin)
        val offsetY = radius + MagnifierGapDp.dpToPx()
        val preferredY = touchPoint.y + if (magnifierSideProgress < 0f) -offsetY else offsetY
        val centerY = preferredY.coerceIn(margin, height - margin)
        val layer = if (alpha < 255) {
            canvas.saveLayerAlpha(
                centerX - margin, centerY - margin, centerX + margin, centerY + margin, alpha
            )
        } else null

        val bounds = imagePreviewBounds
        val scale = bounds.height() / drawable.intrinsicHeight * MagnifierZoom
        magnifierMatrix.setScale(scale, scale)
        magnifierMatrix.postTranslate(
            centerX - (source.x - bounds.left) * MagnifierZoom,
            centerY - (source.y - bounds.top) * MagnifierZoom
        )
        shader.setLocalMatrix(magnifierMatrix)
        canvas.drawCircle(centerX, centerY, radius, magnifierPaint)

        magnifierClip.rewind()
        magnifierClip.addCircle(centerX, centerY, radius, Path.Direction.CW)
        canvas.withClip(magnifierClip) {
            for (edge in quad.edges) {
                drawLine(
                    centerX + (edge.from.x - source.x) * MagnifierZoom,
                    centerY + (edge.from.y - source.y) * MagnifierZoom,
                    centerX + (edge.to.x - source.x) * MagnifierZoom,
                    centerY + (edge.to.y - source.y) * MagnifierZoom,
                    cropperLineStyle
                )
            }
        }
        canvas.drawCircle(centerX, centerY, radius, cropperCornerStyle)
        canvas.drawCircle(
            centerX,
            centerY,
            radius + cropperCornerOutlineStyle.strokeWidth,
            cropperCornerOutlineStyle
        )

        if (magnifierDotRadius > 0f) {
            canvas.drawCircle(
                centerX,
                centerY,
                magnifierDotRadius.coerceAtMost(radius),
                magnifierDotPaint
            )
        }
        layer?.let(canvas::restoreToCount)
    }

    private fun updateMagnifierSide(touchY: Float, animate: Boolean) {
        val radius = magnifierRadius
        if (radius <= 0f) return

        val lensSpace = radius * 2f + MagnifierInsetDp.dpToPx()
        val minimumSpace = lensSpace + 8f.dpToPx()
        val showBelow = when {
            !animate -> touchY < lensSpace + MagnifierGapDp.dpToPx()
            magnifierBelowTouch -> height - touchY >= minimumSpace
            else -> touchY < minimumSpace
        }
        if (animate && showBelow == magnifierBelowTouch) return

        magnifierBelowTouch = showBelow
        magnifierSideAnimator?.cancel()
        val target = if (showBelow) 1f else -1f
        if (animate && isAttachedToWindow) {
            // Fade out before drawing the magnifier on the other side of the finger.
            magnifierSideAnimator = ValueAnimator.ofFloat(magnifierSideProgress, target).apply {
                duration = 140L
                addUpdateListener {
                    magnifierSideProgress = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        } else {
            magnifierSideProgress = target
        }
    }

    /**
     * This gets called when the user touches, drags, and stops touching screen. We use this
     * to figure out which corner we need to move, and how far we need to move it.
     *
     * @param event the touch event
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val quad = quad ?: return false
        // keep track of the touched point
        val touchPoint = PointF(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // when the user touches the screen record the point, and find the closest
                // corner or edge to the touch point
                prevTouchPoint = touchPoint
                dragTarget = findDragTarget(touchPoint)
                updateMagnifierSide(touchPoint.y, animate = false)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                clearDrag()
            }

            MotionEvent.ACTION_MOVE -> {
                // when the user drags their finger, update the selected corner or edge position
                val target = dragTarget ?: return true
                val touchMoveDistance = PointF(
                    touchPoint.x - prevTouchPoint!!.x,
                    touchPoint.y - prevTouchPoint!!.y
                ).coerceDragAmountFor(target.corners)

                target.corners.forEach { corner ->
                    quad.moveCorner(corner, touchMoveDistance.x, touchMoveDistance.y)
                }

                // record the point touched, so we can use it to calculate how far to move corner
                // next time the user drags (assuming they don't stop touching the screen)
                prevTouchPoint = touchPoint
                updateMagnifierSide(touchPoint.y, animate = true)
            }
        }

        // force refresh view
        invalidate()

        return true
    }

    override fun onDetachedFromWindow() {
        clearDrag()
        super.onDetachedFromWindow()
    }

    private fun clearDrag() {
        prevTouchPoint = null
        dragTarget = null
        magnifierSideAnimator?.cancel()
        magnifierSideAnimator = null
    }

    private fun Float.dpToPx(): Float = this * resources.displayMetrics.density

    private sealed class CropperDragTarget {
        data class Corner(val corner: QuadCorner) : CropperDragTarget()
        data class Edge(
            val firstCorner: QuadCorner,
            val secondCorner: QuadCorner
        ) : CropperDragTarget()

        val corners: Set<QuadCorner>
            get() = when (this) {
                is Corner -> setOf(corner)
                is Edge -> setOf(firstCorner, secondCorner)
            }
    }

    private fun PointF.closestPointOnSegment(start: PointF, end: PointF): PointF {
        val segmentX = end.x - start.x
        val segmentY = end.y - start.y
        val lengthSquared = segmentX * segmentX + segmentY * segmentY

        if (lengthSquared == 0f) return start

        val projection = (((x - start.x) * segmentX + (y - start.y) * segmentY) / lengthSquared)
            .coerceIn(0f, 1f)
        return PointF(start.x + projection * segmentX, start.y + projection * segmentY)
    }

    private companion object {
        private const val DefaultCropperStrokeWidthDp = 1.2f
        private const val MagnifierRadiusDp = 50f
        private const val MagnifierInsetDp = 4f
        private const val MagnifierGapDp = 24f
        private const val MagnifierZoom = 5f

        val cropperEdges = listOf(
            QuadCorner.TOP_LEFT to QuadCorner.TOP_RIGHT,
            QuadCorner.TOP_RIGHT to QuadCorner.BOTTOM_RIGHT,
            QuadCorner.BOTTOM_RIGHT to QuadCorner.BOTTOM_LEFT,
            QuadCorner.BOTTOM_LEFT to QuadCorner.TOP_LEFT
        )
    }
}
