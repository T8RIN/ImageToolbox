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

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.getSystemService
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withTranslation
import com.t8rin.collages.geometry.collageOutputDimensions
import com.t8rin.collages.geometry.collagePreviewSize
import com.t8rin.collages.handles.CollageHandleBounds
import com.t8rin.collages.handles.Handle
import com.t8rin.collages.handles.ParamsManager
import com.t8rin.collages.handles.collageHandleBounds
import com.t8rin.collages.image.FrameImageRender
import com.t8rin.collages.image.ImageDecoder
import com.t8rin.collages.model.CollageDesign
import com.t8rin.collages.model.CollageSlotStyle
import com.t8rin.collages.model.CollageState
import com.t8rin.collages.model.PhotoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import androidx.compose.ui.graphics.Color as ComposeColor

@SuppressLint("ViewConstructor")
internal class FramePhotoLayout(
    context: Context,
    var mPhotoItems: List<PhotoItem>,
    maxImageZoom: Float
) : RelativeLayout(context), FrameImageView.OnImageClickListener {

    private data class FrameMetrics(
        val leftMargin: Int,
        val topMargin: Int,
        val width: Int,
        val height: Int
    )

    private fun computeFrameMetrics(
        bound: RectF,
        width: Int = mViewWidth,
        height: Int = mViewHeight,
        padding: Float = min(outerPadding * 2, min(width, height) * .24f)
    ): FrameMetrics {
        val left = (padding + (width - padding * 2) * bound.left).roundToInt()
        val top = (padding + (height - padding * 2) * bound.top).roundToInt()
        val right = (padding + (width - padding * 2) * bound.right).roundToInt()
        val bottom = (padding + (height - padding * 2) * bound.bottom).roundToInt()
        return FrameMetrics(
            left,
            top,
            (right - left).coerceAtLeast(1),
            (bottom - top).coerceAtLeast(1)
        )
    }

    private class ImageDrag(
        val source: FrameImageView,
        val startX: Float,
        val startY: Float,
        val before: CollageState,
        var target: FrameImageView? = null,
        var settling: Boolean = false
    )

    private var imageDrag: ImageDrag? = null
    private val handleTouchOffset = PointF()
    private var touchX = 0f
    private var touchY = 0f

    private val outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
    }
    private var outlineColor = Color.WHITE
    private var outlineAccent = Color.BLACK
    private var canvasScale = 1f
    private var outlinePhase = 0f
    private var outlineAnimator: ValueAnimator? = null

    fun setOutlineStyle(color: Int, accent: Int, scale: Float) {
        if (outlineColor == color && outlineAccent == accent && canvasScale == scale) return
        outlineColor = color
        outlineAccent = accent
        canvasScale = scale.coerceAtLeast(0.1f)
        invalidate()
    }

    private fun updateOutlineAnimation() {
        val visible = isAttachedToWindow && hasWindowFocus() &&
                (selectedItemIndex != null || imageDrag != null)
        if (!visible) {
            outlineAnimator?.cancel()
            outlineAnimator = null
        } else if (outlineAnimator == null) {
            outlineAnimator = ValueAnimator.ofFloat(0f, 80f).apply {
                duration = 1000L
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
                addUpdateListener {
                    outlinePhase = it.animatedValue as Float
                    invalidate()
                }
                start()
            }
        }
    }

    private fun drawOutline(canvas: Canvas, view: FrameImageView, selected: Boolean) {
        outlinePaint.strokeWidth = 3f * resources.displayMetrics.density / canvasScale
        canvas.withTranslation(view.left.toFloat(), view.top.toFloat()) {
            if (selected) {
                outlinePaint.color = outlineColor
                outlinePaint.pathEffect = null
                view.drawOutline(this, outlinePaint)
            }
            outlinePaint.color = if (selected) outlineAccent else outlineColor
            outlinePaint.pathEffect = DashPathEffect(
                floatArrayOf(20f / canvasScale, 20f / canvasScale),
                outlinePhase / canvasScale
            )
            view.drawOutline(this, outlinePaint)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateOutlineAnimation()
    }

    override fun onDetachedFromWindow() {
        resetImageDrag()
        outlineAnimator?.cancel()
        outlineAnimator = null
        super.onDetachedFromWindow()
    }

    private val mItemImageViews: MutableList<FrameImageView> = ArrayList()
    var outputAspectRatio: Float = 1f
    var maxImageZoom: Float = maxImageZoom
        set(value) {
            if (field == value) return
            field = value
            mItemImageViews.forEach { it.maxImageZoom = value }
        }

    var layoutDesign: CollageDesign? = null
    var slotStyles: List<CollageSlotStyle> = emptyList()
    var slotBorderShaders: List<((Float, Float) -> Shader)?> = emptyList()
    var outerPadding: Float = 0f
        set(value) {
            if (field == value) return
            field = value
            if (mViewWidth > 0 && mViewHeight > 0) {
                mItemImageViews.indices.forEach { resizeItem(it) }
            }
        }

    private val framePadding get() = min(outerPadding * 2, min(mViewWidth, mViewHeight) * .24f)

    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private val sourceDimensions = mutableMapOf<Uri, Int>()
    private var backgroundColor: ComposeColor = ComposeColor.White
    private var backgroundShader: ((Float, Float) -> Shader)? = null
    private var onItemTapListener: ((index: Int) -> Unit)? = null
    var onImagesLoaded: (() -> Unit)? = null
    val imagesReady: Boolean
        get() = mItemImageViews.size == mPhotoItems.size && mItemImageViews.all(FrameImageView::isStateReady)

    private var onStateReadyListener: ((CollageState) -> Unit)? = null
    private var onUserStateChangeListener: ((CollageState, CollageState, Pair<Int, Int>?) -> Unit)? =
        null
    private var interactionStartState: CollageState? = null
    private var isRestoringState: Boolean = false
    private var layoutId: String = ""

    // Handle overlay state
    private var selectedItemIndex: Int? = null
    private var activeHandle: Handle? = null
    private var paramsManager: ParamsManager? = null
    private var handleDrawable: Drawable? = null

    // Flags propagated from upper layers
    private var disableRotation: Boolean = false
    private var enableSnapToBorders: Boolean = false

    private val isNotLargeThan1Gb: Boolean
        get() = getMemoryInfo().run {
            totalMem > 0 && totalMem / 1048576.0 <= 1024
        }

    init {
        isMotionEventSplittingEnabled = false
        setWillNotDraw(false)
        // Enable focus so we can detect focus loss and clear selection
        isFocusable = true
        isFocusableInTouchMode = true
    }

    fun setParamsManager(manager: ParamsManager?) {
        paramsManager = manager
        // Update item views whenever params change
        paramsManager?.onItemUpdated = { itemIndex -> resizeItem(itemIndex) }
    }

    fun setDisableRotation(disable: Boolean) {
        disableRotation = disable
        // Update existing children
        for (v in mItemImageViews) {
            v.setRotationEnabled(!disableRotation)
        }
    }

    fun setEnableSnapToBorders(enable: Boolean) {
        if (enableSnapToBorders == enable) return
        enableSnapToBorders = enable
        // Update existing children
        for (v in mItemImageViews) {
            v.setSnapToBordersEnabled(enableSnapToBorders)
        }
        onStateReadyListener?.invoke(snapshotState())
    }

    fun build(
        viewWidth: Int,
        viewHeight: Int,
        space: Float = 0f,
        corner: Float = 0f
    ) {
        resetImageDrag()
        isRestoringState = false
        selectedItemIndex = null
        activeHandle = null
        mItemImageViews.forEach(FrameImageView::release)
        mItemImageViews.clear()
        removeAllViews()
        if (viewWidth < 1 || viewHeight < 1) {
            return
        }

        setBackgroundColor(if (backgroundShader == null) backgroundColor.toArgb() else Color.TRANSPARENT)

        //add children views
        mViewWidth = viewWidth
        mViewHeight = viewHeight
        mItemImageViews.clear()
        //A circle view always is on top
        for (item in mPhotoItems) {
            val imageView = addPhotoItemView(item, space, corner)
            mItemImageViews.add(imageView)
        }
    }

    fun resize(width: Int, height: Int) {
        if (mViewWidth == width && mViewHeight == height) return
        resetImageDrag()
        mViewWidth = width
        mViewHeight = height
        for (i in mItemImageViews.indices) {
            resizeItem(i, scaleImage = true)
        }
    }

    fun release() {
        selectedItemIndex = null
        resetImageDrag()
        updateOutlineAnimation()
        mItemImageViews.forEach(FrameImageView::release)
        onStateReadyListener = null
        onImagesLoaded = null
        onUserStateChangeListener = null
    }

    private fun resizeItem(index: Int, scaleImage: Boolean = false) {
        val view = mItemImageViews[index]
        val item = mPhotoItems[index]
        val metrics = computeFrameMetrics(item.bound)
        val params = view.layoutParams as LayoutParams
        params.leftMargin = metrics.leftMargin
        params.topMargin = metrics.topMargin
        params.width = metrics.width
        params.height = metrics.height
        view.layoutParams = params
        view.updateFrame(metrics.width.toFloat(), metrics.height.toFloat(), scaleImage)
    }

    fun setBackgroundColor(color: ComposeColor) {
        backgroundColor = color
        setBackgroundColor(if (backgroundShader == null) backgroundColor.toArgb() else Color.TRANSPARENT)
        invalidate()
    }

    fun setBackgroundShader(shader: ((Float, Float) -> Shader)?) {
        backgroundShader = shader
        setBackgroundColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        backgroundShader?.let {
            drawBackgroundGradient(
                canvas,
                width.toFloat(),
                height.toFloat(),
                it
            )
        }
    }

    private fun drawBackgroundGradient(
        canvas: Canvas,
        width: Float,
        height: Float,
        factory: (Float, Float) -> Shader
    ) {
        canvas.drawRect(
            0f,
            0f,
            width,
            height,
            Paint(Paint.DITHER_FLAG).apply {
                shader = factory(width, height)
            }
        )
    }

    fun setOnItemTapListener(listener: ((index: Int) -> Unit)?) {
        onItemTapListener = listener
    }

    fun setStateListeners(
        layoutId: String,
        onStateReady: ((CollageState) -> Unit)?,
        onUserStateChange: ((CollageState, CollageState, Pair<Int, Int>?) -> Unit)?
    ) {
        this.layoutId = layoutId
        onStateReadyListener = onStateReady
        onUserStateChangeListener = onUserStateChange
    }

    fun snapshotState(): CollageState = CollageState(
        layoutId = layoutId,
        images = mItemImageViews.map(FrameImageView::snapshotState),
        layoutParams = paramsManager?.snapshotValues()?.toList().orEmpty(),
        design = layoutDesign
    )

    fun restoreState(state: CollageState) {
        if (state.layoutId != layoutId) return
        isRestoringState = true
        paramsManager?.takeIf { it.snapshotValues().toList() != state.layoutParams }
            ?.restoreValues(state.layoutParams)
        var restored = true
        state.images.forEach { imageState ->
            val imageRestored = mItemImageViews
                .firstOrNull { it.photoItem.index == imageState.index }
                ?.restoreState(imageState) != false
            restored = restored && imageRestored
        }
        if (restored) {
            finishStateRestoration()
        }
    }

    private fun beginUserInteraction() {
        if (interactionStartState == null) {
            interactionStartState = snapshotState()
        }
    }

    private fun finishUserInteraction() {
        val before = interactionStartState ?: return
        interactionStartState = null
        dispatchUserStateChange(before)
    }

    private fun dispatchUserStateChange(
        before: CollageState,
        swappedImages: Pair<Int, Int>? = null
    ) {
        val after = snapshotState()
        if (before != after || swappedImages != null) {
            onUserStateChangeListener?.invoke(before, after, swappedImages)
        }
        onStateReadyListener?.invoke(after)
    }

    fun setSpace(space: Float, corner: Float) {
        for (img in mItemImageViews) {
            img.setStyle(slotStyles.getOrNull(img.photoItem.index) ?: CollageSlotStyle())
            img.setBorderShader(slotBorderShaders.getOrNull(img.photoItem.index))
            img.outerPadding = 0f
            img.setSpace(space, corner)
        }
        invalidate()
    }

    fun setHandleDrawable(drawable: Drawable?) {
        handleDrawable = drawable ?: createDefaultHandleDrawable()
        invalidate()
    }

    fun updateImages(images: List<Uri>) {
        val minSize = min(images.size, mPhotoItems.size)
        for (i in 0 until minSize) {
            val newUri = images[i]
            val item = mPhotoItems[i]
            val oldUri = item.imagePath
            val changed = (oldUri?.toString() ?: "") != (newUri.toString())
            if (changed) {
                item.imagePath = newUri
                if (i < mItemImageViews.size) {
                    mItemImageViews[i].reloadImageFromPhotoItem()
                }
            }
        }
    }

    private fun createDefaultHandleDrawable(): Drawable {
        val diameterPx = 72 // equals previous 36px radius circle
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.rgb(255, 165, 0))
            setSize(diameterPx, diameterPx)
        }
    }

    private fun addPhotoItemView(
        item: PhotoItem,
        space: Float,
        corner: Float
    ): FrameImageView {
        val imageView = FrameImageView(
            context = context,
            photoItem = item,
            maxImageZoom = maxImageZoom,
            previewSize = collagePreviewSize(
                imageCount = mPhotoItems.size,
                relativeArea = item.bound.width() * item.bound.height() /
                        mPhotoItems.sumOf { (it.bound.width() * it.bound.height()).toDouble() }
                            .toFloat(),
                lowMemory = isNotLargeThan1Gb
            )
        )
        val metrics = computeFrameMetrics(item.bound)
        imageView.setRotationEnabled(!disableRotation)
        imageView.setSnapToBordersEnabled(enableSnapToBorders)
        imageView.init(metrics.width.toFloat(), metrics.height.toFloat(), space, corner)
        imageView.setOnImageClickListener(this)
        val params = LayoutParams(metrics.width, metrics.height)
        params.leftMargin = metrics.leftMargin
        params.topMargin = metrics.topMargin
        imageView.originalLayoutParams = params
        addView(imageView, params)
        return imageView
    }

    internal val hasValidDimensions: Boolean
        get() = mViewWidth > 0 && mViewHeight > 0

    suspend fun outputDimensions(outputScaleRatio: Float): Pair<Int, Int> {
        if (!hasValidDimensions) return 0 to 0
        val sourceMaxDimension = mItemImageViews.maxOfOrNull { view ->
            view.photoItem.imagePath?.let { uri ->
                sourceDimensions[uri] ?: ImageDecoder.sourceMaxDimension(context, uri)
                    .also { size ->
                        if (size > 0) sourceDimensions[uri] = size
                    }
            } ?: 0
        }?.takeIf { it > 0 } ?: mItemImageViews.maxOfOrNull { view ->
            max(view.image?.width ?: 0, view.image?.height ?: 0)
        }?.takeIf { it > 0 } ?: 1600
        return collageOutputDimensions(
            sourceSize = sourceMaxDimension,
            aspectRatio = outputAspectRatio,
            scale = outputScaleRatio,
            maxPixels = Runtime.getRuntime().maxMemory() / 16
        )
    }

    suspend fun createImage(outputScaleRatio: Float): Bitmap {
        mItemImageViews.forEach(FrameImageView::finishTransform)
        val (outputWidth, outputHeight) = outputDimensions(outputScaleRatio)
        val decorationScale =
            min(outputWidth, outputHeight).toFloat() / min(mViewWidth, mViewHeight)
        val frames = mItemImageViews.mapNotNull { view ->
            val (left, top, width, height) = computeFrameMetrics(
                view.photoItem.bound, outputWidth, outputHeight,
                min(outerPadding * 2 * decorationScale, min(outputWidth, outputHeight) * .24f)
            )
            view.prepareOutputImage(width.toFloat(), height.toFloat(), decorationScale)?.let {
                OutputFrame(
                    left = left,
                    top = top,
                    width = width,
                    height = height,
                    sourceSize = sourceDimensions[view.photoItem.imagePath]
                        ?: max(outputWidth, outputHeight),
                    image = it
                )
            }
        }
        val color = backgroundColor.toArgb()
        val shader = backgroundShader
        var rendered: Bitmap? = null
        try {
            return withContext(Dispatchers.Default) {
                val output = createBitmap(outputWidth, outputHeight).also { rendered = it }
                val canvas = Canvas(output)
                if (shader == null) {
                    canvas.drawColor(color)
                } else {
                    drawBackgroundGradient(
                        canvas,
                        outputWidth.toFloat(),
                        outputHeight.toFloat(),
                        shader
                    )
                }
                for (frame in frames) {
                    currentCoroutineContext().ensureActive()
                    canvas.withTranslation(frame.left.toFloat(), frame.top.toFloat()) {
                        frame.image.drawShadow(this)
                    }
                }
                for (frame in frames) {
                    currentCoroutineContext().ensureActive()
                    val save = canvas.save()
                    try {
                        canvas.translate(frame.left.toFloat(), frame.top.toFloat())
                        canvas.clipRect(0, 0, frame.width, frame.height)
                        frame.image.draw(canvas, frame.sourceSize)
                    } finally {
                        canvas.restoreToCount(save)
                    }
                }
                output
            }
        } catch (throwable: Throwable) {
            rendered?.recycle()
            throw throwable
        }
    }

    private data class OutputFrame(
        val left: Int,
        val top: Int,
        val width: Int,
        val height: Int,
        val sourceSize: Int,
        val image: FrameImageRender
    )

    override fun onLongClickImage(view: FrameImageView) {
        startImageDrag(view, touchX, touchY)
    }

    fun startImageDragAt(x: Float, y: Float) {
        imageAt(x, y)?.let { startImageDrag(it, x, y) }
    }

    private fun startImageDrag(source: FrameImageView, x: Float, y: Float) {
        if (imageDrag != null || mPhotoItems.size < 2 ||
            source.image == null || !source.isStateReady()
        ) return
        source.finishTransform()
        imageDrag = ImageDrag(source, x, y, snapshotState())
        updateOutlineAnimation()
        parent?.requestDisallowInterceptTouchEvent(true)
        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        source.translationZ = 12f * resources.displayMetrics.density
        source.animate().scaleX(1.04f).scaleY(1.04f).alpha(0.92f)
            .setDuration(140L).setInterpolator(DecelerateInterpolator()).start()
        invalidate()
    }

    fun moveImageDrag(x: Float, y: Float) {
        val drag = imageDrag?.takeUnless { it.settling } ?: return
        drag.source.translationX = x - drag.startX
        drag.source.translationY = y - drag.startY
        val target =
            imageAt(x, y)?.takeIf { it !== drag.source && it.image != null && it.isStateReady() }
        if (drag.target !== target) {
            drag.target?.animate()?.alpha(1f)?.setDuration(120L)?.start()
            drag.target = target
            target?.animate()?.alpha(0.55f)?.setDuration(120L)?.start()
        }
    }

    fun finishImageDrag(cancelled: Boolean = false) {
        val drag = imageDrag?.takeUnless { it.settling } ?: return
        drag.settling = true
        parent?.requestDisallowInterceptTouchEvent(false)
        val source = drag.source
        val target = drag.target.takeUnless { cancelled }
        if (target == null) {
            drag.target?.animate()?.alpha(1f)?.setDuration(120L)?.start()
            source.animate()
                .translationX(0f).translationY(0f)
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(200L).setInterpolator(DecelerateInterpolator())
                .withEndAction { if (imageDrag === drag) resetImageDrag() }
                .start()
            return
        }

        val targetAlpha = target.alpha
        resetImageDrag()
        target.swapImage(source)
        val sourceIndex = source.photoItem.index
        val targetIndex = target.photoItem.index
        slotStyles = List(mItemImageViews.size) { index ->
            slotStyles.getOrNull(if (index == sourceIndex) targetIndex else if (index == targetIndex) sourceIndex else index)
                ?: CollageSlotStyle()
        }
        slotBorderShaders = List(mItemImageViews.size) { index ->
            slotBorderShaders.getOrNull(if (index == sourceIndex) targetIndex else if (index == targetIndex) sourceIndex else index)
        }
        target.alpha = targetAlpha
        target.animate().alpha(1f).setDuration(220L).start()
        when (selectedItemIndex) {
            source.photoItem.index -> onSingleTapImage(target)
            target.photoItem.index -> onSingleTapImage(source)
        }
        dispatchUserStateChange(drag.before, sourceIndex to targetIndex)
    }

    private fun resetImageDrag() {
        val drag = imageDrag ?: return
        imageDrag = null
        updateOutlineAnimation()
        listOfNotNull(drag.source, drag.target).forEach {
            it.animate().withEndAction(null).cancel()
            it.translationX = 0f
            it.translationY = 0f
            it.translationZ = 0f
            it.scaleX = 1f
            it.scaleY = 1f
            it.alpha = 1f
        }
        parent?.requestDisallowInterceptTouchEvent(false)
        invalidate()
    }

    private fun imageAt(x: Float, y: Float): FrameImageView? =
        mItemImageViews.asReversed().firstOrNull {
            it.isSelected(x - it.left, y - it.top)
        }

    override fun onDoubleClickImage(view: FrameImageView) {

    }

    fun clearSelection() {
        selectedItemIndex = null
        activeHandle = null
        onItemTapListener?.invoke(-1)
        updateOutlineAnimation()
        invalidate()
    }

    fun selectImageAt(x: Float, y: Float) {
        imageAt(x, y)?.let(::onSingleTapImage)
    }

    override fun onSingleTapImage(view: FrameImageView) {
        if (selectedItemIndex == view.photoItem.index) {
            clearSelection()
        } else {
            onItemTapListener?.invoke(view.photoItem.index)
            selectedItemIndex = view.photoItem.index
            updateOutlineAnimation()
            requestFocus()
            invalidate()
        }
    }

    override fun onImageTransformStart() {
        beginUserInteraction()
    }

    override fun onImageTransformEnd() {
        if (mItemImageViews.none { it.isTransforming }) finishUserInteraction()
    }

    override fun onImageStateReady() {
        if (imagesReady && interactionStartState == null) {
            onImagesLoaded?.invoke()
            if (isRestoringState) {
                finishStateRestoration()
            } else {
                onStateReadyListener?.invoke(snapshotState())
            }
        }
    }

    private fun finishStateRestoration() {
        isRestoringState = false
        onStateReadyListener?.invoke(snapshotState())
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        updateOutlineAnimation()
        if (!hasWindowFocus) {
            resetImageDrag()
            // Clear selection when window focus is lost
            if (selectedItemIndex != null) {
                clearSelection()
            }
        }
    }

    override fun onFocusChanged(
        gainFocus: Boolean,
        direction: Int,
        previouslyFocusedRect: android.graphics.Rect?
    ) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (!gainFocus) {
            // Clear selection when view focus is lost
            if (selectedItemIndex != null) {
                clearSelection()
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        mItemImageViews.forEach { view ->
            canvas.withTranslation(view.left.toFloat(), view.top.toFloat()) {
                concat(view.matrix)
                view.drawShadow(this)
            }
        }
        imageDrag?.source?.let { drawOutline(canvas, it, selected = false) }
        super.dispatchDraw(canvas)
        if (imageDrag != null) {
            imageDrag?.target?.let { drawOutline(canvas, it, selected = true) }
            return
        }
        mItemImageViews.firstOrNull { it.photoItem.index == selectedItemIndex }
            ?.let { drawOutline(canvas, it, selected = true) }
        val drawable = handleDrawable ?: return
        val diameter = handleDiameter()
        val half = (diameter / 2f).toInt()
        drawable.setBounds(-half, -half, half, half)
        selectedHandles().forEach { (handle, bounds) ->
            canvas.withTranslation(bounds.centerX, bounds.centerY) {
                canvas.rotate(handle.getAngle() + 90f)
                val scale = bounds.diameter / diameter
                canvas.scale(scale, scale)
                drawable.draw(canvas)
            }
        }
    }

    private fun handleDiameter(): Float = handleDrawable?.let {
        max(it.intrinsicWidth, it.intrinsicHeight).takeIf { size -> size > 0 }?.toFloat()
    } ?: 72f

    private fun selectedHandles(): List<Pair<Handle, CollageHandleBounds>> {
        val manager = paramsManager ?: return emptyList()
        val index = selectedItemIndex ?: return emptyList()
        val handles = manager.getHandles(index)
        val positions = handles.map { handle ->
            val point = handle.draggablePoint(manager)
            (framePadding + (mViewWidth - framePadding * 2) * point.x) to
                    (framePadding + (mViewHeight - framePadding * 2) * point.y)
        }
        return handles.zip(
            collageHandleBounds(
                positions = positions,
                diameter = handleDiameter(),
                touchRadius = 24f * resources.displayMetrics.density,
                scale = canvasScale
            )
        )
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        touchX = event.x
        touchY = event.y
        imageDrag?.let { drag ->
            if (!drag.settling) {
                when (event.actionMasked) {
                    MotionEvent.ACTION_MOVE -> moveImageDrag(event.x, event.y)
                    MotionEvent.ACTION_UP -> finishImageDrag()
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_DOWN ->
                        finishImageDrag(cancelled = true)
                }
            }
            return true
        }
        if (activeHandle != null || event.actionMasked == MotionEvent.ACTION_DOWN) {
            if (onTouchEvent(event)) return true
        }
        return super.dispatchTouchEvent(event)
    }

    fun startHandleDragAt(x: Float, y: Float): Boolean {
        val nearest = selectedHandles().filter { (_, bounds) ->
            kotlin.math.hypot(x - bounds.centerX, y - bounds.centerY) <= bounds.touchRadius
        }.minByOrNull { (_, bounds) ->
            kotlin.math.hypot(x - bounds.centerX, y - bounds.centerY)
        } ?: return false
        activeHandle = nearest.first
        handleTouchOffset.set(x - nearest.second.centerX, y - nearest.second.centerY)
        parent?.requestDisallowInterceptTouchEvent(true)
        beginUserInteraction()
        return true
    }

    fun moveHandleDrag(x: Float, y: Float) {
        val handle = activeHandle ?: return
        val manager = paramsManager ?: return
        val nx = ((x - handleTouchOffset.x - framePadding) / (mViewWidth - framePadding * 2))
            .coerceIn(0f, 1f)
        val ny = ((y - handleTouchOffset.y - framePadding) / (mViewHeight - framePadding * 2))
            .coerceIn(0f, 1f)
        handle.tryDrag(PointF(nx, ny), manager) ?: return
        invalidate()
    }

    fun finishHandleDrag() {
        if (activeHandle == null) return
        activeHandle = null
        parent?.requestDisallowInterceptTouchEvent(false)
        finishUserInteraction()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            if (startHandleDragAt(event.x, event.y)) return true
        } else if (activeHandle != null) {
            when (event.actionMasked) {
                MotionEvent.ACTION_MOVE -> moveHandleDrag(event.x, event.y)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_POINTER_DOWN ->
                    finishHandleDrag()
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun getMemoryInfo(): MemoryInfo = MemoryInfo().apply {
        context.getSystemService<ActivityManager>()?.getMemoryInfo(this)
    }

}