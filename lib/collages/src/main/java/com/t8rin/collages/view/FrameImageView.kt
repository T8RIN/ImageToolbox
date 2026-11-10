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

package com.t8rin.collages.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.graphics.Shader
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.net.toUri
import com.t8rin.collages.geometry.GeometryUtils
import com.t8rin.collages.geometry.ImageTransform
import com.t8rin.collages.geometry.collageSpacingEdges
import com.t8rin.collages.geometry.insetMask
import com.t8rin.collages.gesture.MultiTouchHandler
import com.t8rin.collages.image.FrameImageRender
import com.t8rin.collages.image.ImageDecoder
import com.t8rin.collages.image.drawCollageImage
import com.t8rin.collages.image.drawCollageShadow
import com.t8rin.collages.model.CollageImageState
import com.t8rin.collages.model.CollageSlotStyle
import com.t8rin.collages.model.PhotoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@SuppressLint("ViewConstructor")
internal class FrameImageView(
    private val context: Context,
    val photoItem: PhotoItem,
    maxImageZoom: Float,
    private val previewSize: Int
) : AppCompatImageView(context) {

    private val mGestureDetector: GestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener!!.onLongClickImage(this@FrameImageView)
                }
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener!!.onDoubleClickImage(this@FrameImageView)
                }
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (mOnImageClickListener != null) {
                    mOnImageClickListener!!.onSingleTapImage(this@FrameImageView)
                }
                return super.onSingleTapUp(e)
            }
        }
    )
    private var mTouchHandler: MultiTouchHandler? = null
    var image: Bitmap? = null
    private val mPaint: Paint = Paint()
    private val mImageMatrix: Matrix = Matrix()
    private var viewWidth: Float = 0.toFloat()
    private var viewHeight: Float = 0.toFloat()
    private var mOnImageClickListener: OnImageClickListener? = null
    private var mOriginalLayoutParams: RelativeLayout.LayoutParams? = null
    private var mEnableTouch = true
    private var style = CollageSlotStyle()
    private var borderShaderFactory: ((Float, Float) -> Shader)? = null
    private var borderShader: Shader? = null
    private var borderShaderWidth = 0f
    private var borderShaderHeight = 0f

    fun setBorderShader(factory: ((Float, Float) -> Shader)?) {
        if (borderShaderFactory === factory && borderShaderWidth == viewWidth &&
            borderShaderHeight == viewHeight
        ) return
        borderShaderFactory = factory
        borderShaderWidth = viewWidth
        borderShaderHeight = viewHeight
        borderShader = factory?.invoke(viewWidth, viewHeight)
        invalidate()
    }

    var maxImageZoom: Float = maxImageZoom
        set(value) {
            if (field == value) return
            field = value
            updateScaleRange()
        }

    var outerPadding: Float = 0f
    fun setStyle(value: CollageSlotStyle) {
        if (style == value) return
        val fitChanged = style.fit != value.fit
        style = value
        if (fitChanged) {
            resetImageMatrix()
            userAllowedEmptySpace = value.fit
        }
    }

    private var corner = 0f
    private var space = 0f
    private val mPath = Path()
    private val hitRegion = Region()
    private val outlinePath = Path()
    private var previousImage: Bitmap? = null
    private val previousImageMatrix = Matrix()
    private var imageProgress = 1f
    private var imageAnimator: ValueAnimator? = null
    private val mBackgroundPath = Path()
    private val mPolygon = ArrayList<PointF>()
    private val mPathRect = Rect(0, 0, 0, 0)
    private var mSelected = true
    private val mConvertedPoints = ArrayList<PointF>()

    private var mBackgroundColor = Color.WHITE

    //Clear area
    private val mClearPath = Path()
    private val mConvertedClearPoints = ArrayList<PointF>()

    // If true, user allowed empty space via gestures; don't auto-zoom it away on frame updates
    private var userAllowedEmptySpace: Boolean = false

    // Feature flags
    private var rotationEnabled: Boolean = true
    private var snapToBordersEnabled: Boolean = false
    private var imageLoadJob: Job? = null
    private var imageLoadToken: String? = null

    fun setRotationEnabled(enabled: Boolean) {
        rotationEnabled = enabled
        mTouchHandler?.setEnableRotation(enabled)
    }

    fun setSnapToBordersEnabled(enabled: Boolean) {
        if (snapToBordersEnabled == enabled) return
        snapToBordersEnabled = enabled
        if (enabled) {
            finishTransform()
            snapToBorders()
        }
    }

    var originalLayoutParams: RelativeLayout.LayoutParams
        get() {
            if (mOriginalLayoutParams != null) {
                val params = RelativeLayout.LayoutParams(
                    mOriginalLayoutParams!!.width,
                    mOriginalLayoutParams!!.height
                )
                params.leftMargin = mOriginalLayoutParams!!.leftMargin
                params.topMargin = mOriginalLayoutParams!!.topMargin
                return params
            } else {
                return layoutParams as RelativeLayout.LayoutParams
            }
        }
        set(originalLayoutParams) {
            mOriginalLayoutParams =
                RelativeLayout.LayoutParams(originalLayoutParams.width, originalLayoutParams.height)
            mOriginalLayoutParams!!.leftMargin = originalLayoutParams.leftMargin
            mOriginalLayoutParams!!.topMargin = originalLayoutParams.topMargin
        }

    interface OnImageClickListener {
        fun onLongClickImage(view: FrameImageView)

        fun onDoubleClickImage(view: FrameImageView)

        fun onSingleTapImage(view: FrameImageView)

        fun onImageTransformStart()

        fun onImageTransformEnd()

        fun onImageStateReady()
    }

    private var settleAnimator: ValueAnimator? = null
    var isTransforming = false
        private set
    private var pendingState: CollageImageState? = null

    init {
        reloadImageFromPhotoItem()

        mPaint.isFilterBitmap = true
        mPaint.isAntiAlias = true
        scaleType = ScaleType.MATRIX
    }

    fun swapImage(view: FrameImageView) {
        if (
            image != null &&
            view.image != null &&
            imageLoadJob?.isActive != true &&
            view.imageLoadJob?.isActive != true &&
            pendingState == null &&
            view.pendingState == null
        ) {
            finishTransform()
            view.finishTransform()
            finishImageTransition()
            view.finishImageTransition()
            val oldImage = image
            val oldMatrix = Matrix(mImageMatrix)
            val previousState = snapshotState()
            val otherState = view.snapshotState()
            val temp = view.image
            view.image = image
            image = temp

            val tmpPath = view.photoItem.imagePath
            view.photoItem.imagePath = photoItem.imagePath
            photoItem.imagePath = tmpPath
            imageLoadToken = photoItem.imagePath?.toString()
            view.imageLoadToken = view.photoItem.imagePath?.toString()
            val previousStyle = style
            style = view.style
            view.style = previousStyle
            val previousShader = borderShaderFactory
            setBorderShader(view.borderShaderFactory)
            view.setBorderShader(previousShader)
            setSpace(space, corner)
            view.setSpace(view.space, view.corner)
            applyState(otherState)
            view.applyState(previousState)
            if (snapToBordersEnabled) snapToBorders()
            if (view.snapToBordersEnabled) view.snapToBorders()
            animateImageTransition(oldImage, oldMatrix)
            view.animateImageTransition(null)
        }
    }

    fun reloadImageFromPhotoItem() {
        finishImageTransition()
        val token = photoItem.imagePath?.toString()?.takeIf { it.isNotEmpty() }

        // Skip if we're already loading the same image
        if (token == imageLoadToken && imageLoadJob?.isActive == true) return

        imageLoadJob?.cancel()
        imageLoadToken = token

        imageLoadJob = CoroutineScope(Dispatchers.Main.immediate).launch {
            // Decode on IO if we have a token/uri; null otherwise
            val bmp = withContext(Dispatchers.IO) {
                token?.let { t ->
                    runCatching {
                        ImageDecoder.decodeFileToBitmap(
                            context = context,
                            pathName = t.toUri(),
                            size = previewSize
                        )
                    }.getOrNull()
                }
            }

            // If another reload changed the token while we were decoding, bail out
            if (token != imageLoadToken) return@launch

            if (bmp == null) {
                image = null
                pendingState?.let(::applyState)
                pendingState = null
                invalidate()
            } else {
                image = bmp
                resetImageMatrix()
                if (snapToBordersEnabled) snapToBorders()
                val state = pendingState
                if (state != null) {
                    pendingState = null
                    applyState(state)
                }
            }

            // Clear the job if we're still the latest load for this token
            if (token == imageLoadToken) imageLoadJob = null
            mOnImageClickListener?.onImageStateReady()
        }
    }

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        mOnImageClickListener = onImageClickListener
    }

    fun snapshotState(): CollageImageState {
        val matrixValues = FloatArray(9)
        mImageMatrix.getValues(matrixValues)
        return CollageImageState(
            index = photoItem.index,
            uri = photoItem.imagePath?.toString(),
            matrixValues = matrixValues.toList(),
            viewWidth = viewWidth,
            viewHeight = viewHeight,
            userAllowedEmptySpace = userAllowedEmptySpace,
            imageWidth = image?.width ?: 0,
            imageHeight = image?.height ?: 0
        )
    }

    fun restoreState(state: CollageImageState): Boolean {
        val currentUri = photoItem.imagePath?.toString()
        if (currentUri != state.uri) {
            photoItem.imagePath = state.uri?.toUri()
            pendingState = state
            reloadImageFromPhotoItem()
            return false
        }
        if (image == null || imageLoadJob?.isActive == true) {
            pendingState = state
            return false
        }

        pendingState = null
        applyState(state)
        return true
    }

    private fun applyState(state: CollageImageState) {
        finishImageTransition()
        stopSettling()
        if (state.matrixValues.size != 9) {
            resetImageMatrix()
            return
        }
        val values = ImageTransform.resize(
            state.matrixValues.toFloatArray(),
            state.viewWidth,
            state.viewHeight,
            viewWidth,
            viewHeight
        )
        mImageMatrix.setValues(values)
        image?.let { bitmap ->
            if (state.imageWidth > 0 && state.imageHeight > 0) {
                mImageMatrix.preScale(
                    state.imageWidth.toFloat() / bitmap.width,
                    state.imageHeight.toFloat() / bitmap.height
                )
            }
        }
        userAllowedEmptySpace = state.userAllowedEmptySpace
        if (snapToBordersEnabled) snapToBorders()
        mTouchHandler?.matrix = mImageMatrix
        invalidate()
    }

    fun isStateReady(): Boolean = pendingState == null && imageLoadJob?.isActive != true

    override fun setBackgroundColor(backgroundColor: Int) {
        mBackgroundColor = backgroundColor
        invalidate()
    }

    override fun getImageMatrix(): Matrix {
        return mImageMatrix
    }

    @JvmOverloads
    fun init(
        viewWidth: Float,
        viewHeight: Float,
        space: Float = 0f,
        corner: Float = 0f
    ) {
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight
        this.space = space
        this.corner = corner

        if (image != null) {
            mImageMatrix.set(
                createMatrixToDrawImageInCenterView(
                    viewWidth = viewWidth,
                    viewHeight = viewHeight,
                    imageWidth = image!!.width.toFloat(),
                    imageHeight = image!!.height.toFloat()
                )
            )
        }

        mTouchHandler = MultiTouchHandler()
        mTouchHandler!!.matrix = mImageMatrix
        mTouchHandler!!.setEnableRotation(rotationEnabled)

        setSpace(this.space, this.corner)
    }

    fun setSpace(space: Float, corner: Float) {
        val previousBounds = imageBounds()
        this.space = space
        this.corner = corner
        setSpace(
            viewWidth, viewHeight, photoItem,
            mConvertedPoints, mConvertedClearPoints,
            mPath, mClearPath, mBackgroundPath, mPolygon, mPathRect, space,
            (style.cornerRadius?.let {
                it * min(
                    viewWidth / photoItem.bound.width(),
                    viewHeight / photoItem.bound.height()
                ) / 1000f
            } ?: corner), outerPadding
        )
        setBorderShader(borderShaderFactory)
        outlinePath.rewind()
        outlinePath.addRect(0f, 0f, viewWidth, viewHeight, Path.Direction.CW)
        outlinePath.op(mPath, Path.Op.INTERSECT)
        if (!mClearPath.isEmpty) outlinePath.op(mClearPath, Path.Op.DIFFERENCE)
        hitRegion.setPath(outlinePath, Region(0, 0, viewWidth.toInt(), viewHeight.toInt()))
        if (snapToBordersEnabled && !previousBounds.contentEquals(imageBounds())) {
            stopSettling()
            snapToBorders()
        }
        invalidate()
    }

    fun updateFrame(viewWidth: Float, viewHeight: Float, scaleImage: Boolean) {
        finishImageTransition()
        finishTransform()
        val values = FloatArray(9).also(mImageMatrix::getValues)
        mImageMatrix.setValues(
            ImageTransform.resize(
                values, this.viewWidth, this.viewHeight, viewWidth, viewHeight, scaleImage
            )
        )
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight
        mConvertedPoints.clear()
        mConvertedClearPoints.clear()
        mPolygon.clear()
        mPath.reset()
        mClearPath.reset()
        mBackgroundPath.reset()
        setSpace(space, corner)
        if (snapToBordersEnabled || scaleImage && !userAllowedEmptySpace) snapToBorders()
        mTouchHandler?.matrix = mImageMatrix
        updateScaleRange()
    }

    private fun updateScaleRange() {
        image?.let {
            val cover = max(viewWidth / it.width, viewHeight / it.height).coerceAtLeast(0.001f)
            mTouchHandler?.setScaleRange(cover * 0.2f, cover * maxImageZoom)
        }
    }

    private fun resetImageMatrix() {
        if (image != null) {
            mImageMatrix.set(
                createMatrixToDrawImageInCenterView(
                    viewWidth = viewWidth,
                    viewHeight = viewHeight,
                    imageWidth = image!!.width.toFloat(),
                    imageHeight = image!!.height.toFloat()
                )
            )
        }
        if (style.fit) image?.let { bitmap ->
            val scale = min(viewWidth / bitmap.width, viewHeight / bitmap.height)
            mImageMatrix.setScale(scale, scale)
            mImageMatrix.postTranslate(
                (viewWidth - bitmap.width * scale) / 2,
                (viewHeight - bitmap.height * scale) / 2
            )
            userAllowedEmptySpace = true
        }
        mTouchHandler?.matrix = mImageMatrix
        updateScaleRange()
        invalidate()
    }

    fun isSelected(x: Float, y: Float): Boolean {
        return hitRegion.contains(x.toInt(), y.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawCollageImage(
            canvas, mPath, mBackgroundPath, mClearPath,
            image, mImageMatrix, mBackgroundColor, mPaint,
            previousImage, previousImageMatrix, imageProgress,
            style.borderWidth * min(
                viewWidth / photoItem.bound.width(),
                viewHeight / photoItem.bound.height()
            ) / 1000f, style.borderColor, borderShader
        )
    }

    fun drawShadow(canvas: Canvas) {
        drawCollageShadow(
            canvas, mPath, mClearPath,
            style.shadowRadius * min(
                viewWidth / photoItem.bound.width(),
                viewHeight / photoItem.bound.height()
            ) / 1000f,
            style.shadowColor,
            alpha,
            mPaint
        )
    }

    fun drawOutline(canvas: Canvas, paint: Paint) {
        canvas.drawPath(outlinePath, paint)
    }

    private fun animateImageTransition(bitmap: Bitmap?, matrix: Matrix? = null) {
        previousImage = bitmap
        matrix?.let(previousImageMatrix::set)
        imageProgress = 0f
        imageAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 220L
            addUpdateListener {
                imageProgress = it.animatedValue as Float
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    finishImageTransition()
                }
            })
            start()
        }
    }

    private fun finishImageTransition() {
        imageAnimator?.removeAllListeners()
        imageAnimator?.cancel()
        imageAnimator = null
        previousImage = null
        imageProgress = 1f
        invalidate()
    }

    fun prepareOutputImage(
        viewWidth: Float,
        viewHeight: Float,
        decorationScale: Float
    ): FrameImageRender? {
        val preview = image?.takeUnless(Bitmap::isRecycled) ?: return null
        val path = Path()
        val clearPath = Path()
        val backgroundPath = Path()
        setSpace(
            viewWidth, viewHeight, photoItem, ArrayList(), ArrayList(),
            path, clearPath, backgroundPath, ArrayList(), Rect(),
            space * decorationScale,
            style.cornerRadius?.let {
                it * min(
                    viewWidth / photoItem.bound.width(),
                    viewHeight / photoItem.bound.height()
                ) / 1000f
            } ?: (corner * decorationScale),
            outerPadding * decorationScale
        )
        val matrix = Matrix().apply {
            setValues(
                ImageTransform.resize(
                    FloatArray(9).also(mImageMatrix::getValues),
                    this@FrameImageView.viewWidth,
                    this@FrameImageView.viewHeight,
                    viewWidth,
                    viewHeight
                )
            )
        }
        return FrameImageRender(
            context, photoItem.imagePath, preview, matrix,
            path, backgroundPath, clearPath, mBackgroundColor,
            style.borderWidth * min(
                viewWidth / photoItem.bound.width(),
                viewHeight / photoItem.bound.height()
            ) / 1000f, style.borderColor,
            style.shadowRadius * min(
                viewWidth / photoItem.bound.width(),
                viewHeight / photoItem.bound.height()
            ) / 1000f, style.shadowColor,
            borderShaderFactory?.invoke(viewWidth, viewHeight)
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mEnableTouch) return false
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            finishImageTransition()
            parent?.requestDisallowInterceptTouchEvent(true)
            mSelected = isSelected(event.x, event.y)
            if (!mSelected) return false
            stopSettling()
            if (!isTransforming) {
                isTransforming = true
                mOnImageClickListener?.onImageTransformStart()
            }
        }
        if (!mSelected) return false
        mGestureDetector.onTouchEvent(event)
        if (image?.isRecycled == false) {
            mTouchHandler?.touch(event)
            mTouchHandler?.matrix?.let(mImageMatrix::set)
            invalidate()
        }
        if (event.actionMasked == MotionEvent.ACTION_UP || event.actionMasked == MotionEvent.ACTION_CANCEL) {
            mSelected = false
            parent?.requestDisallowInterceptTouchEvent(false)
            if (snapToBordersEnabled) settleToBorders() else finishTransform()
        }
        return true
    }

    private fun imageBounds(): FloatArray {
        val bounds = RectF()
        mPath.computeBounds(bounds, true)
        if (bounds.isEmpty) bounds.set(0f, 0f, viewWidth, viewHeight)
        return floatArrayOf(bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    private fun fittedMatrix(): FloatArray {
        val values = FloatArray(9).also(mImageMatrix::getValues)
        val bitmap = image ?: return values
        return ImageTransform.fit(
            values,
            bitmap.width.toFloat(),
            bitmap.height.toFloat(),
            imageBounds()
        )
    }

    private fun snapToBorders() {
        if (style.fit) return
        mImageMatrix.setValues(fittedMatrix())
        if (image != null) userAllowedEmptySpace = false
        mTouchHandler?.matrix = mImageMatrix
        invalidate()
    }

    private fun settleToBorders() {
        if (style.fit) {
            finishTransform(); return
        }
        stopSettling()
        val start = FloatArray(9).also(mImageMatrix::getValues)
        val end = fittedMatrix()
        if (start.indices.all { abs(start[it] - end[it]) < 0.001f }) {
            finishTransform()
            return
        }
        settleAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 240L
            interpolator = DecelerateInterpolator()
            val values = FloatArray(9)
            addUpdateListener { animator ->
                val progress = animator.animatedValue as Float
                for (i in values.indices) values[i] = start[i] + (end[i] - start[i]) * progress
                mImageMatrix.setValues(values)
                mTouchHandler?.matrix = mImageMatrix
                invalidate()
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    settleAnimator = null
                    mImageMatrix.setValues(end)
                    mTouchHandler?.matrix = mImageMatrix
                    finishTransform()
                }
            })
            start()
        }
    }

    private fun stopSettling() {
        settleAnimator?.removeAllListeners()
        settleAnimator?.cancel()
        settleAnimator = null
    }

    fun finishTransform() {
        stopSettling()
        if (!isTransforming) return
        isTransforming = false
        val bitmap = image
        userAllowedEmptySpace = bitmap != null && !ImageTransform.covers(
            FloatArray(9).also(mImageMatrix::getValues),
            bitmap.width.toFloat(), bitmap.height.toFloat(), imageBounds()
        )
        mOnImageClickListener?.onImageTransformEnd()
    }

    fun release() {
        finishImageTransition()
        finishTransform()
        imageLoadJob?.cancel()
        imageLoadJob = null
        mOnImageClickListener = null
    }

    companion object {
        fun shapePath(item: PhotoItem, width: Float, height: Float, spacing: Float = 0f): Path {
            val path = Path()
            val clear = Path()
            setSpace(
                width,
                height,
                item,
                ArrayList(),
                ArrayList(),
                path,
                clear,
                Path(),
                ArrayList(),
                Rect(),
                spacing,
                0f
            )
            if (!clear.isEmpty) path.op(clear, Path.Op.DIFFERENCE)
            return path
        }

        private fun Path.scaleForSpacing(
            distance: Float,
            width: Float,
            height: Float,
            bounds: RectF
        ) {
            val inset = min(distance, min(width, height) * .45f)
            transform(Matrix().apply {
                setRectToRect(
                    RectF(0f, 0f, width, height),
                    RectF(
                        if (bounds.left == 0f) 0f else inset,
                        if (bounds.top == 0f) 0f else inset,
                        if (bounds.right == 1f) width else width - inset,
                        if (bounds.bottom == 1f) height else height - inset
                    ),
                    Matrix.ScaleToFit.FILL
                )
            })
        }

        private fun setSpace(
            viewWidth: Float, viewHeight: Float, photoItem: PhotoItem,
            convertedPoints: MutableList<PointF>,
            convertedClearPoints: MutableList<PointF>,
            path: Path,
            clearPath: Path,
            backgroundPath: Path,
            polygon: MutableList<PointF>,
            pathRect: Rect,
            space: Float, corner: Float, outerPadding: Float = 0f
        ) {
            val canvasWidth = viewWidth / photoItem.bound.width()
            val canvasHeight = viewHeight / photoItem.bound.height()
            val outerBounds = RectF(
                -photoItem.bound.left * canvasWidth,
                -photoItem.bound.top * canvasHeight,
                (1f - photoItem.bound.left) * canvasWidth,
                (1f - photoItem.bound.top) * canvasHeight
            )
            photoItem.normalizedPath?.let { outline ->
                val scale = min(canvasWidth, canvasHeight)
                val inset = min(space, photoItem.maximumMaskInset * scale)
                    .takeIf { it >= .5f } ?: 0f
                val preserveShape = photoItem.mask?.preserveShape == true
                path.set(
                    if (inset > 0f && !preserveShape) photoItem.normalizedInsetPath ?: outline
                    else outline
                )
                path.transform(Matrix().apply { setScale(viewWidth, viewHeight) })
                if (inset > 0f) {
                    if (preserveShape) {
                        path.scaleForSpacing(inset, viewWidth, viewHeight, photoItem.bound)
                    } else {
                        path.insetMask(inset, path.collageSpacingEdges(outerBounds))
                    }
                }
                return
            }
            photoItem.shape?.let { points ->
                path.reset()
                val local = points.map {
                    PointF(
                        (it.x - photoItem.bound.left) / photoItem.bound.width() * viewWidth,
                        (it.y - photoItem.bound.top) / photoItem.bound.height() * viewHeight
                    )
                }
                local.forEachIndexed { index, point ->
                    if (index == 0) path.moveTo(point.x, point.y)
                    else path.lineTo(point.x, point.y)
                }
                path.close()
                if (space > 0f) {
                    var doubleArea = 0f
                    var perimeter = 0f
                    local.forEachIndexed { index, point ->
                        val next = local[(index + 1) % local.size]
                        doubleArea += point.x * next.y - next.x * point.y
                        perimeter += kotlin.math.hypot(next.x - point.x, next.y - point.y)
                    }
                    val maxInset = abs(doubleArea) * .375f / perimeter
                    if (photoItem.preserveShape) {
                        path.scaleForSpacing(
                            min(space, maxInset),
                            viewWidth,
                            viewHeight,
                            photoItem.bound
                        )
                    } else {
                        path.insetMask(
                            min(space, maxInset),
                            path.collageSpacingEdges(outerBounds),
                            keepLargest = true
                        )
                    }
                }
                if (corner > 0f) {
                    val rounded = Path()
                    Paint().apply {
                        pathEffect = CornerPathEffect(min(corner, min(viewWidth, viewHeight) / 2))
                    }
                        .getFillPath(path, rounded)
                    path.set(rounded)
                }
                return
            }

            if (convertedPoints.isEmpty()) {
                for (p in photoItem.pointList) {
                    val convertedPoint = PointF(p.x * viewWidth, p.y * viewHeight)
                    convertedPoints.add(convertedPoint)
                    if (photoItem.shrinkMap != null) {

                        photoItem.shrinkMap!![convertedPoint] = photoItem.shrinkMap!![p]!!

                    }
                }
            }

            if (photoItem.clearAreaPoints != null && photoItem.clearAreaPoints!!.isNotEmpty()) {
                clearPath.reset()
                if (convertedClearPoints.isEmpty())
                    for (p in photoItem.clearAreaPoints!!) {
                        convertedClearPoints.add(PointF(p.x * viewWidth, p.y * viewHeight))
                    }
                GeometryUtils.createPathWithCircleCorner(clearPath, convertedClearPoints, corner)
            } else if (photoItem.clearPath != null) {
                clearPath.reset()
                buildRealClearPath(viewWidth, viewHeight, photoItem, clearPath, corner)
            }

            if (photoItem.path != null) {
                buildRealPath(viewWidth, viewHeight, photoItem, path, space, corner)
                polygon.clear()
            } else {
                val inset = min(space, min(viewWidth, viewHeight) * .35f)
                val shrunkPoints: List<PointF>
                when (photoItem.shrinkMethod) {
                    PhotoItem.SHRINK_METHOD_3_3 -> {
                        val centerPointIdx = findCenterPointIndex(photoItem)
                        shrunkPoints = GeometryUtils.shrinkPathCollage_3_3(
                            convertedPoints,
                            centerPointIdx,
                            inset,
                            photoItem.bound
                        )
                    }

                    PhotoItem.SHRINK_METHOD_USING_MAP if photoItem.shrinkMap != null -> {
                        shrunkPoints = GeometryUtils.shrinkPathCollageUsingMap(
                            convertedPoints,
                            inset,
                            photoItem.shrinkMap!!,
                            outerBounds
                        )
                    }

                    PhotoItem.SHRINK_METHOD_COMMON if photoItem.shrinkMap != null -> {
                        shrunkPoints =
                            GeometryUtils.commonShrinkPath(
                                convertedPoints,
                                inset,
                                photoItem.shrinkMap!!,
                                outerBounds
                            )
                    }

                    else -> {
                        shrunkPoints = if (photoItem.disableShrink) {
                            GeometryUtils.shrinkPath(convertedPoints, 0f, photoItem.bound)
                        } else {
                            GeometryUtils.shrinkPath(
                                convertedPoints,
                                inset,
                                photoItem.bound,
                                outerPadding
                            )
                        }
                    }
                }
                polygon.clear()
                polygon.addAll(shrunkPoints)
                GeometryUtils.createPathWithCircleCorner(path, shrunkPoints, corner)
                if (photoItem.hasBackground) {
                    backgroundPath.reset()
                    GeometryUtils.createPathWithCircleCorner(
                        backgroundPath,
                        convertedPoints,
                        corner
                    )
                }
            }

            pathRect.set(0, 0, 0, 0)
        }

        private fun findCenterPointIndex(photoItem: PhotoItem): Int {
            var centerPointIdx = 0
            if (photoItem.bound.left == 0f && photoItem.bound.top == 0f) {
                var minX = 1f
                for (idx in photoItem.pointList.indices) {
                    val p = photoItem.pointList[idx]
                    if (p.x > 0 && p.x < 1 && p.y > 0 && p.y < 1 && p.x < minX) {
                        centerPointIdx = idx
                        minX = p.x
                    }
                }
            } else {
                var maxX = 0f
                for (idx in photoItem.pointList.indices) {
                    val p = photoItem.pointList[idx]
                    if (p.x > 0 && p.x < 1 && p.y > 0 && p.y < 1 && p.x > maxX) {
                        centerPointIdx = idx
                        maxX = p.x
                    }
                }
            }

            return centerPointIdx
        }

        private fun buildRealPath(
            viewWidth: Float, viewHeight: Float,
            photoItem: PhotoItem, outPath: Path,
            space: Float, corner: Float
        ) {
            var newSpace = space
            if (photoItem.path != null) {
                val rect = RectF()
                photoItem.path!!.computeBounds(rect, true)
                val pathWidthPixels = rect.width()
                val pathHeightPixels = rect.height()
                newSpace *= 2
                outPath.set(photoItem.path!!)
                val m = Matrix()
                val ratioX: Float
                val ratioY: Float
                if (photoItem.fitBound) {
                    ratioX =
                        photoItem.pathScaleRatio * (viewWidth * photoItem.pathRatioBound!!.width() - 2 * newSpace) / pathWidthPixels
                    ratioY =
                        photoItem.pathScaleRatio * (viewHeight * photoItem.pathRatioBound!!.height() - 2 * newSpace) / pathHeightPixels
                } else {
                    val ratio = min(
                        photoItem.pathScaleRatio * (viewHeight - 2 * newSpace) / pathHeightPixels,
                        photoItem.pathScaleRatio * (viewWidth - 2 * newSpace) / pathWidthPixels
                    )
                    ratioX = ratio
                    ratioY = ratio
                }
                m.postScale(ratioX, ratioY)
                outPath.transform(m)
                val bound = RectF()
                when (photoItem.cornerMethod) {
                    PhotoItem.CORNER_METHOD_3_6 -> {
                        outPath.computeBounds(bound, true)
                        GeometryUtils.createRegularPolygonPath(
                            outPath,
                            min(bound.width(), bound.height()),
                            6,
                            corner
                        )
                        outPath.computeBounds(bound, true)
                    }

                    PhotoItem.CORNER_METHOD_3_13 -> {
                        outPath.computeBounds(bound, true)
                        GeometryUtils.createRectanglePath(
                            outPath,
                            bound.width(),
                            bound.height(),
                            corner
                        )
                        outPath.computeBounds(bound, true)
                    }

                    else -> {
                        outPath.computeBounds(bound, true)
                    }
                }

                var x: Float
                var y: Float
                if (photoItem.shrinkMethod == PhotoItem.SHRINK_METHOD_3_6 || photoItem.shrinkMethod == PhotoItem.SHRINK_METHOD_3_8) {
                    x = viewWidth / 2 - bound.width() / 2
                    y = viewHeight / 2 - bound.height() / 2
                    m.reset()
                    m.postTranslate(x, y)
                    outPath.transform(m)
                } else {
                    if (photoItem.pathAlignParentRight) {
                        x =
                            photoItem.pathRatioBound!!.right * viewWidth - bound.width() - newSpace / ratioX
                        y = photoItem.pathRatioBound!!.top * viewHeight + newSpace / ratioY
                    } else {
                        x = photoItem.pathRatioBound!!.left * viewWidth + newSpace / ratioX
                        y = photoItem.pathRatioBound!!.top * viewHeight + newSpace / ratioY
                    }

                    if (photoItem.pathInCenterHorizontal) {
                        x = viewWidth / 2.0f - bound.width() / 2.0f
                    }

                    if (photoItem.pathInCenterVertical) {
                        y = viewHeight / 2.0f - bound.height() / 2.0f
                    }

                    m.reset()
                    m.postTranslate(x, y)
                    outPath.transform(m)
                }
            }
        }

        private fun buildRealClearPath(
            viewWidth: Float,
            viewHeight: Float,
            photoItem: PhotoItem,
            clearPath: Path,
            corner: Float
        ): Path? {
            if (photoItem.clearPath != null) {
                val rect = RectF()
                photoItem.clearPath!!.computeBounds(rect, true)
                val clearPathWidthPixels = rect.width()
                val clearPathHeightPixels = rect.height()

                clearPath.set(photoItem.clearPath!!)
                val m = Matrix()
                val ratioX: Float
                val ratioY: Float
                if (photoItem.fitBound) {
                    ratioX =
                        photoItem.clearPathScaleRatio * viewWidth * photoItem.clearPathRatioBound!!.width() / clearPathWidthPixels
                    ratioY =
                        photoItem.clearPathScaleRatio * viewHeight * photoItem.clearPathRatioBound!!.height() / clearPathHeightPixels
                } else {
                    val ratio = min(
                        photoItem.clearPathScaleRatio * viewHeight / clearPathHeightPixels,
                        photoItem.clearPathScaleRatio * viewWidth / clearPathWidthPixels
                    )
                    ratioX = ratio
                    ratioY = ratio
                }
                m.postScale(ratioX, ratioY)
                clearPath.transform(m)
                val bound = RectF()
                when (photoItem.cornerMethod) {
                    PhotoItem.CORNER_METHOD_3_6 -> {
                        clearPath.computeBounds(bound, true)
                        GeometryUtils.createRegularPolygonPath(
                            clearPath,
                            min(bound.width(), bound.height()),
                            6,
                            corner
                        )
                        clearPath.computeBounds(bound, true)
                    }

                    PhotoItem.CORNER_METHOD_3_13 -> {
                        clearPath.computeBounds(bound, true)
                        GeometryUtils.createRectanglePath(
                            clearPath,
                            bound.width(),
                            bound.height(),
                            corner
                        )
                        clearPath.computeBounds(bound, true)
                    }

                    else -> {
                        clearPath.computeBounds(bound, true)
                    }
                }

                var x: Float
                var y: Float
                if (photoItem.shrinkMethod == PhotoItem.SHRINK_METHOD_3_6) {
                    x = if (photoItem.clearPathRatioBound!!.left > 0) {
                        viewWidth - bound.width() / 2
                    } else {
                        -bound.width() / 2
                    }
                    y = viewHeight / 2 - bound.height() / 2
                } else {
                    if (photoItem.centerInClearBound) {
                        x =
                            photoItem.clearPathRatioBound!!.left * viewWidth + (viewWidth / 2 - bound.width() / 2)
                        y =
                            photoItem.clearPathRatioBound!!.top * viewHeight + (viewHeight / 2 - bound.height() / 2)
                    } else {
                        x = photoItem.clearPathRatioBound!!.left * viewWidth
                        y = photoItem.clearPathRatioBound!!.top * viewHeight
                        if (photoItem.clearPathInCenterHorizontal) {
                            x = viewWidth / 2.0f - bound.width() / 2.0f
                        }
                        if (photoItem.clearPathInCenterVertical) {
                            y = viewHeight / 2.0f - bound.height() / 2.0f
                        }
                    }
                }

                m.reset()
                m.postTranslate(x, y)
                clearPath.transform(m)
                return clearPath
            } else {
                return null
            }
        }

    }

    private fun createMatrixToDrawImageInCenterView(
        viewWidth: Float,
        viewHeight: Float,
        imageWidth: Float,
        imageHeight: Float
    ): Matrix {
        val ratioWidth = viewWidth / imageWidth
        val ratioHeight = viewHeight / imageHeight
        val ratio = ratioWidth.coerceAtLeast(ratioHeight)
        val dx = (viewWidth - imageWidth) / 2.0f
        val dy = (viewHeight - imageHeight) / 2.0f
        val result = Matrix()
        result.postTranslate(dx, dy)
        result.postScale(ratio, ratio, viewWidth / 2, viewHeight / 2)
        return result
    }
}