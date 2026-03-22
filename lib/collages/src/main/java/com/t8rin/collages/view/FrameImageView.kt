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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.withClip
import androidx.core.graphics.withSave
import androidx.core.net.toUri
import com.t8rin.collages.utils.GeometryUtils
import com.t8rin.collages.utils.ImageDecoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.min

@SuppressLint("ViewConstructor")
internal class FrameImageView(
    private val context: Context,
    val photoItem: PhotoItem
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
    private var corner = 0f
    private var space = 0f
    private val mPath = Path()
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
        snapToBordersEnabled = enabled
        if (enabled) {
            // Snap immediately
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
    }

    private var viewState: Bundle = Bundle.EMPTY

    init {
        reloadImageFromPhotoItem()

        mPaint.isFilterBitmap = true
        mPaint.isAntiAlias = true
        scaleType = ScaleType.MATRIX
        setLayerType(LAYER_TYPE_SOFTWARE, mPaint)
    }

    fun saveInstanceState(outState: Bundle) {
        val index = photoItem.index
        val values = FloatArray(9)
        mImageMatrix.getValues(values)
        outState.putFloatArray("mImageMatrix_$index", values)
        outState.putFloat("mViewWidth_$index", viewWidth)
        outState.putFloat("mViewHeight_$index", viewHeight)
        outState.putFloat("mCorner_$index", corner)
        outState.putFloat("mSpace_$index", space)
        outState.putInt("mBackgroundColor_$index", mBackgroundColor)
    }

    /**
     * Called after init() function
     *
     * @param savedInstanceState
     */
    fun restoreInstanceState(savedInstanceState: Bundle) {
        viewState = savedInstanceState
        val index = photoItem.index
        val values = savedInstanceState.getFloatArray("mImageMatrix_$index")
        if (values != null) {
            mImageMatrix.setValues(values)
        }
        viewWidth = savedInstanceState.getFloat("mViewWidth_$index", 1f)
        viewHeight = savedInstanceState.getFloat("mViewHeight_$index", 1f)
        corner = savedInstanceState.getFloat("mCorner_$index", 0f)
        space = savedInstanceState.getFloat("mSpace_$index", 0f)
        mBackgroundColor = savedInstanceState.getInt("mBackgroundColor_$index", Color.WHITE)
        mTouchHandler!!.matrix = mImageMatrix
        setSpace(space, corner)
    }

    fun swapImage(view: FrameImageView) {
        if (image != null && view.image != null) {
            val temp = view.image
            view.image = image
            image = temp

            val tmpPath = view.photoItem.imagePath
            view.photoItem.imagePath = photoItem.imagePath
            photoItem.imagePath = tmpPath
            resetImageMatrix()
            view.resetImageMatrix()
        }
    }

    fun reloadImageFromPhotoItem() {
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
                            pathName = t.toUri()
                        )
                    }.getOrNull()
                }
            }

            // If another reload changed the token while we were decoding, bail out
            if (token != imageLoadToken) return@launch

            if (bmp == null) {
                image = null
                invalidate()
            } else {
                image = bmp
                resetImageMatrix()
                if (viewState != Bundle.EMPTY) {
                    restoreInstanceState(viewState)
                    viewState = Bundle.EMPTY
                }
            }

            // Clear the job if we're still the latest load for this token
            if (token == imageLoadToken) imageLoadJob = null
        }
    }

    fun setOnImageClickListener(onImageClickListener: OnImageClickListener) {
        mOnImageClickListener = onImageClickListener
    }

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
        this.space = space
        this.corner = corner
        setSpace(
            viewWidth, viewHeight, photoItem,
            mConvertedPoints, mConvertedClearPoints,
            mPath, mClearPath, mBackgroundPath, mPolygon, mPathRect, space, corner
        )
        invalidate()
    }

    fun updateFrame(viewWidth: Float, viewHeight: Float) {
        // --- Save previous state we need once
        val oldW = this.viewWidth
        val oldH = this.viewHeight

        // Preserve center point
        mImageMatrix.postTranslate((viewWidth - oldW) * 0.5f, (viewHeight - oldH) * 0.5f)

        if (oldW > 0 && oldH > 0 && abs(viewWidth / oldW - viewHeight / oldH) < 0.00001f) {
            // Simple center rescale

            val scale = (viewWidth / oldW + viewHeight / oldH) / 2
            mImageMatrix.postScale(scale, scale, viewWidth / 2, viewHeight / 2)
        } else {
            if (image != null) {
                val hasEmptyAfter = hasEmptySpace(mImageMatrix, viewWidth, viewHeight)

                // If empty space disappeared naturally due to frame change, clear the pin
                if (!hasEmptyAfter) {
                    userAllowedEmptySpace = false
                }

                // If empty space would appear and the user didn't pin it, zoom just enough to cover.
                if (hasEmptyAfter && !userAllowedEmptySpace) {
                    snapToBorders(onlyMatrixUpdate = true)
                }
            }
        }

        mTouchHandler?.matrix = mImageMatrix

        // --- Apply new bounds and rebuild geometry
        this.viewWidth = viewWidth
        this.viewHeight = viewHeight

        mConvertedPoints.clear()
        mConvertedClearPoints.clear()
        mPolygon.clear()
        mPath.reset()
        mClearPath.reset()
        mBackgroundPath.reset()

        // Invalidates the view
        setSpace(this.space, this.corner)
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
        mTouchHandler?.matrix = mImageMatrix
        invalidate()
    }

    fun isSelected(x: Float, y: Float): Boolean {
        return GeometryUtils.contains(mPolygon, PointF(x, y))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawImage(
            canvas, mPath, mPaint, mPathRect, image, mImageMatrix,
            width.toFloat(), height.toFloat(), mBackgroundColor, mBackgroundPath,
            mClearPath, mPolygon
        )
    }

    fun drawOutputImage(canvas: Canvas, outputScale: Float) {
        val viewWidth = this.viewWidth * outputScale
        val viewHeight = this.viewHeight * outputScale
        val path = Path()
        val clearPath = Path()
        val backgroundPath = Path()
        val pathRect = Rect()
        val polygon = ArrayList<PointF>()
        setSpace(
            viewWidth, viewHeight, photoItem, ArrayList(),
            ArrayList(), path, clearPath, backgroundPath, polygon, pathRect,
            space * outputScale, corner * outputScale
        )
        val exportMatrix = Matrix(mImageMatrix).apply { postScale(outputScale, outputScale) }
        drawImage(
            canvas, path, mPaint, pathRect, image, exportMatrix,
            viewWidth, viewHeight, mBackgroundColor, backgroundPath, clearPath, polygon
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mEnableTouch) {
            return super.onTouchEvent(event)
        } else {
            if (event.action == MotionEvent.ACTION_DOWN) {
                mSelected = GeometryUtils.contains(mPolygon, PointF(event.x, event.y))
            }

            if (mSelected) {
                if (event.action == MotionEvent.ACTION_UP) {
                    mSelected = false
                }

                mGestureDetector.onTouchEvent(event)
                if (mTouchHandler != null && image != null && !image!!.isRecycled) {
                    mTouchHandler!!.touch(event)
                    mImageMatrix.set(mTouchHandler!!.matrix)

                    if (event.action == MotionEvent.ACTION_UP) {
                        if (snapToBordersEnabled) {
                            snapToBorders()
                        }
                        // Update weather user allowed empty space based on current transform
                        userAllowedEmptySpace = hasEmptySpace(mImageMatrix, viewWidth, viewHeight)
                    }
                    invalidate()
                }
                return true
            } else {
                return super.onTouchEvent(event)
            }
        }
    }

    private fun snapToBorders(onlyMatrixUpdate: Boolean = false) {
        val img = image ?: return
        if (viewWidth <= 0f || viewHeight <= 0f) return

        fun mappedRect(): RectF =
            RectF(0f, 0f, img.width.toFloat(), img.height.toFloat()).also {
                mImageMatrix.mapRect(it)
            }

        var rect = mappedRect()
        var changed = false

        // 1) Minimal translation to reduce single-edge gaps (always apply if it reduces gap)
        var dx = 0f
        var dy = 0f
        val leftGap = rect.left - space
        val rightGap = (viewWidth - space) - rect.right
        val topGap = rect.top - space
        val bottomGap = (viewHeight - space) - rect.bottom

        val leftNeeds = leftGap > 0f
        val rightNeeds = rightGap > 0f
        if (leftNeeds.xor(rightNeeds)) {
            dx = if (leftNeeds) -leftGap else rightGap
        }

        val topNeeds = topGap > 0f
        val bottomNeeds = bottomGap > 0f
        if (topNeeds.xor(bottomNeeds)) {
            dy = if (topNeeds) -topGap else bottomGap
        }

        if (dx != 0f || dy != 0f) {
            mImageMatrix.postTranslate(dx, dy)
            rect = mappedRect()
            changed = true
        }

        // 2) Minimal uniform scale about view center to cover remaining gaps (capped)
        val cx = viewWidth * 0.5f
        val cy = viewHeight * 0.5f
        var needed = 1f
        if (rect.left > space) {
            val denom = (cx - rect.left)
            if (denom > 0f) needed = kotlin.math.max(needed, (cx - space) / denom)
        }
        if (rect.top > space) {
            val denom = (cy - rect.top)
            if (denom > 0f) needed = kotlin.math.max(needed, (cy - space) / denom)
        }
        if (rect.right < viewWidth - space) {
            val denom = (rect.right - cx)
            if (denom > 0f) needed = kotlin.math.max(needed, (cx - space) / denom)
        }
        if (rect.bottom < viewHeight - space) {
            val denom = (rect.bottom - cy)
            if (denom > 0f) needed = kotlin.math.max(needed, (cy - space) / denom)
        }

        val maxStep = 4f
        val scale = min(needed, maxStep)
        if (scale > 1.0005f) {
            mImageMatrix.postScale(scale, scale, cx, cy)
            rect = mappedRect()
            changed = true
        }

        // 3) Final clamp to remove any residual tiny gaps
        var cdx = 0f
        var cdy = 0f
        if (rect.left > space) cdx = space - rect.left
        if (rect.top > space) cdy = space - rect.top
        if (rect.right < viewWidth - space) cdx = (viewWidth - space) - rect.right
        if (rect.bottom < viewHeight - space) cdy = (viewHeight - space) - rect.bottom
        if (cdx != 0f || cdy != 0f) {
            mImageMatrix.postTranslate(cdx, cdy)
            changed = true
        }

        if (onlyMatrixUpdate) return

        if (changed) {
            mTouchHandler?.matrix = mImageMatrix
            invalidate()
        }
    }

    private fun hasEmptySpace(matrix: Matrix, vw: Float, vh: Float): Boolean {
        if (image == null || vw <= 0f || vh <= 0f) return false
        val rect = RectF(0f, 0f, image!!.width.toFloat(), image!!.height.toFloat())
        matrix.mapRect(rect)
        // if image rect does not fully cover the view rect, there is empty space
        return rect.left > space || rect.top > space || rect.right < vw - space || rect.bottom < vh - space
    }

    companion object {
        private fun setSpace(
            viewWidth: Float, viewHeight: Float, photoItem: PhotoItem,
            convertedPoints: MutableList<PointF>,
            convertedClearPoints: MutableList<PointF>,
            path: Path,
            clearPath: Path,
            backgroundPath: Path,
            polygon: MutableList<PointF>,
            pathRect: Rect,
            space: Float, corner: Float
        ) {
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
                val shrunkPoints: List<PointF>
                when (photoItem.shrinkMethod) {
                    PhotoItem.SHRINK_METHOD_3_3 -> {
                        val centerPointIdx = findCenterPointIndex(photoItem)
                        shrunkPoints = GeometryUtils.shrinkPathCollage_3_3(
                            convertedPoints,
                            centerPointIdx,
                            space,
                            photoItem.bound
                        )
                    }

                    PhotoItem.SHRINK_METHOD_USING_MAP if photoItem.shrinkMap != null -> {
                        shrunkPoints = GeometryUtils.shrinkPathCollageUsingMap(
                            convertedPoints,
                            space,
                            photoItem.shrinkMap!!
                        )
                    }

                    PhotoItem.SHRINK_METHOD_COMMON if photoItem.shrinkMap != null -> {
                        shrunkPoints =
                            GeometryUtils.commonShrinkPath(
                                convertedPoints,
                                space,
                                photoItem.shrinkMap!!
                            )
                    }

                    else -> {
                        shrunkPoints = if (photoItem.disableShrink) {
                            GeometryUtils.shrinkPath(convertedPoints, 0f, photoItem.bound)
                        } else {
                            GeometryUtils.shrinkPath(convertedPoints, space, photoItem.bound)
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

        private fun drawImage(
            canvas: Canvas,
            path: Path,
            paint: Paint,
            pathRect: Rect,
            image: Bitmap?,
            imageMatrix: Matrix,
            viewWidth: Float,
            viewHeight: Float,
            color: Int,
            backgroundPath: Path?,
            clearPath: Path?,
            touchPolygon: MutableList<PointF>?
        ) {
            if (image != null && !image.isRecycled) {
                canvas.drawBitmap(image, imageMatrix, paint)
            }
            //clip outside
            if (pathRect.left == pathRect.right) {
                canvas.withClip(path) {
                    pathRect.set(clipBounds)
                }
            }

            canvas.save()
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            canvas.drawARGB(0x00, 0x00, 0x00, 0x00)
            paint.color = Color.BLACK
            paint.style = Paint.Style.FILL
            canvas.drawRect(0f, 0f, viewWidth, pathRect.top.toFloat(), paint)
            canvas.drawRect(0f, 0f, pathRect.left.toFloat(), viewHeight, paint)
            canvas.drawRect(pathRect.right.toFloat(), 0f, viewWidth, viewHeight, paint)
            canvas.drawRect(0f, pathRect.bottom.toFloat(), viewWidth, viewHeight, paint)
            paint.xfermode = null
            canvas.restore()
            //clip inside
            canvas.save()
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            canvas.drawARGB(0x00, 0x00, 0x00, 0x00)
            paint.color = Color.BLACK
            paint.style = Paint.Style.FILL
            val currentFillType = path.fillType
            path.fillType = Path.FillType.INVERSE_WINDING
            canvas.drawPath(path, paint)
            paint.xfermode = null
            canvas.restore()
            path.fillType = currentFillType
            //clear area
            if (clearPath != null) {
                canvas.withSave {
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    drawARGB(0x00, 0x00, 0x00, 0x00)
                    paint.color = Color.BLACK
                    paint.style = Paint.Style.FILL
                    drawPath(clearPath, paint)
                    paint.xfermode = null
                }
            }
            //draw out side
            if (backgroundPath != null) {
                canvas.withSave {
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
                    drawARGB(0x00, 0x00, 0x00, 0x00)
                    paint.color = color
                    paint.style = Paint.Style.FILL
                    drawPath(backgroundPath, paint)
                    paint.xfermode = null
                }
            }
            //touch polygon
            if (touchPolygon != null && touchPolygon.isEmpty()) {
                touchPolygon.add(PointF(pathRect.left.toFloat(), pathRect.top.toFloat()))
                touchPolygon.add(PointF(pathRect.right.toFloat(), pathRect.top.toFloat()))
                touchPolygon.add(PointF(pathRect.right.toFloat(), pathRect.bottom.toFloat()))
                touchPolygon.add(PointF(pathRect.left.toFloat(), pathRect.bottom.toFloat()))
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
