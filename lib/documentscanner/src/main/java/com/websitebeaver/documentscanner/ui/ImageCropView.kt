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

package com.websitebeaver.documentscanner.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import com.websitebeaver.documentscanner.R
import com.websitebeaver.documentscanner.enums.QuadCorner
import com.websitebeaver.documentscanner.extensions.changeByteCountByResizing
import com.websitebeaver.documentscanner.extensions.drawQuad
import com.websitebeaver.documentscanner.models.Quad

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
     * @property closestCornerToTouch if the user touches close to the top left corner for
     * example, that corner should move on drag
     */
    private var closestCornerToTouch: QuadCorner? = null

    /**
     * @property cropperLinesAndCornersStyles paint style for 4 corners and connecting lines
     */
    private val cropperLinesAndCornersStyles = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * @property cropperSelectedCornerFillStyles when you tap and drag a cropper corner the circle
     * acts like a magnifying glass
     */
    private val cropperSelectedCornerFillStyles = Paint()

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
        // set cropper style
        cropperLinesAndCornersStyles.color = Color.WHITE
        cropperLinesAndCornersStyles.style = Paint.Style.STROKE
        cropperLinesAndCornersStyles.strokeWidth = 3f
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
     * This ensures that the user doesn't drag a corner outside the image
     *
     * @param point a point
     * @return true if the point is inside the image preview container, false it's not
     */
    private fun isPointInsideImage(point: PointF): Boolean {
        return (point.x >= imagePreviewBounds.left
                && point.y >= imagePreviewBounds.top
                && point.x <= imagePreviewBounds.right
                && point.y <= imagePreviewBounds.bottom)
    }

    /**
     * This gets called once we insert an image in this image view
     */
    private fun onSetImage() {
        cropperSelectedCornerFillStyles.style = Paint.Style.FILL
        cropperSelectedCornerFillStyles.shader = BitmapShader(
            drawable.toBitmap(),
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
    }

    /**
     * This gets called constantly, and we use it to update the cropper corners
     *
     * @param canvas the image preview canvas
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (quad !== null) {
            // draw 4 corners and connecting lines
            canvas.drawQuad(
                quad!!,
                resources.getDimension(R.dimen.cropper_corner_radius),
                cropperLinesAndCornersStyles,
                cropperSelectedCornerFillStyles,
                closestCornerToTouch,
                imagePreviewBounds,
                ratio,
                resources.getDimension(R.dimen.cropper_selected_corner_radius_magnification),
                resources.getDimension(R.dimen.cropper_selected_corner_background_magnification)
            )
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
        // keep track of the touched point
        val touchPoint = PointF(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // when the user touches the screen record the point, and find the closest
                // corner to the touch point
                prevTouchPoint = touchPoint
                closestCornerToTouch = quad!!.getCornerClosestToPoint(touchPoint)
            }

            MotionEvent.ACTION_UP -> {
                // when the user stops touching the screen reset these values
                prevTouchPoint = null
                closestCornerToTouch = null
            }

            MotionEvent.ACTION_MOVE -> {
                // when the user drags their finger, update the closest corner position
                val touchMoveXDistance = touchPoint.x - prevTouchPoint!!.x
                val touchMoveYDistance = touchPoint.y - prevTouchPoint!!.y
                val cornerNewPosition = PointF(
                    quad!!.corners[closestCornerToTouch]!!.x + touchMoveXDistance,
                    quad!!.corners[closestCornerToTouch]!!.y + touchMoveYDistance
                )

                // make sure the user doesn't drag the corner outside the image preview container
                if (isPointInsideImage(cornerNewPosition)) {
                    quad!!.moveCorner(
                        closestCornerToTouch!!,
                        touchMoveXDistance,
                        touchMoveYDistance
                    )
                }

                // record the point touched, so we can use it to calculate how far to move corner
                // next time the user drags (assuming they don't stop touching the screen)
                prevTouchPoint = touchPoint
            }
        }

        // force refresh view
        invalidate()

        return true
    }
}