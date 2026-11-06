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
import com.t8rin.collages.CollageState
import com.t8rin.collages.utils.Handle
import com.t8rin.collages.utils.ImageDecoder
import com.t8rin.collages.utils.ParamsManager
import com.t8rin.collages.utils.collageOutputDimensions
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
    var mPhotoItems: List<PhotoItem>
) : RelativeLayout(context), FrameImageView.OnImageClickListener {

    private data class FrameMetrics(
        val leftMargin: Int,
        val topMargin: Int,
        val width: Int,
        val height: Int
    )

    private fun computeFrameMetrics(bound: RectF): FrameMetrics {
        val leftMargin = (mViewWidth * bound.left).toInt()
        val topMargin = (mViewHeight * bound.top).toInt()
        val frameWidth: Int = if (bound.right == 1f) {
            mViewWidth - leftMargin
        } else {
            (mViewWidth * bound.width() + 0.5f).toInt()
        }

        val frameHeight: Int = if (bound.bottom == 1f) {
            mViewHeight - topMargin
        } else {
            (mViewHeight * bound.height() + 0.5f).toInt()
        }

        return FrameMetrics(
            leftMargin = leftMargin,
            topMargin = topMargin,
            width = frameWidth,
            height = frameHeight
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

    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private val sourceDimensions = mutableMapOf<Uri, Int>()
    private var backgroundColor: ComposeColor = ComposeColor.White
    private var backgroundShader: ((Float, Float) -> Shader)? = null
    private var onItemTapListener: ((index: Int) -> Unit)? = null
    private var onStateReadyListener: ((CollageState) -> Unit)? = null
    private var onUserStateChangeListener: ((CollageState, CollageState) -> Unit)? = null
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
            resizeItem(i)
        }
    }

    fun release() {
        selectedItemIndex = null
        resetImageDrag()
        updateOutlineAnimation()
        mItemImageViews.forEach(FrameImageView::release)
        onStateReadyListener = null
        onUserStateChangeListener = null
    }

    private fun resizeItem(index: Int) {
        val view = mItemImageViews[index]
        val item = mPhotoItems[index]
        val metrics = computeFrameMetrics(item.bound)
        val params = view.layoutParams as LayoutParams
        params.leftMargin = metrics.leftMargin
        params.topMargin = metrics.topMargin
        params.width = metrics.width
        params.height = metrics.height
        view.layoutParams = params
        view.updateFrame(metrics.width.toFloat(), metrics.height.toFloat())
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
        drawBackgroundGradient(canvas, width.toFloat(), height.toFloat())
    }

    private fun drawBackgroundGradient(canvas: Canvas, width: Float, height: Float) {
        backgroundShader?.let { factory ->
            canvas.drawRect(0f, 0f, width, height, Paint(Paint.DITHER_FLAG).apply {
                shader = factory(width, height)
            })
        }
    }

    fun setOnItemTapListener(listener: ((index: Int) -> Unit)?) {
        onItemTapListener = listener
    }

    fun setStateListeners(
        layoutId: String,
        onStateReady: ((CollageState) -> Unit)?,
        onUserStateChange: ((CollageState, CollageState) -> Unit)?
    ) {
        this.layoutId = layoutId
        onStateReadyListener = onStateReady
        onUserStateChangeListener = onUserStateChange
    }

    fun snapshotState(): CollageState = CollageState(
        layoutId = layoutId,
        images = mItemImageViews.map(FrameImageView::snapshotState),
        layoutParams = paramsManager?.snapshotValues()?.toList().orEmpty()
    )

    fun restoreState(state: CollageState) {
        if (state.layoutId != layoutId) return
        isRestoringState = true
        paramsManager?.restoreValues(state.layoutParams)
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

    private fun dispatchUserStateChange(before: CollageState) {
        val after = snapshotState()
        if (before != after) {
            onUserStateChangeListener?.invoke(before, after)
        }
        onStateReadyListener?.invoke(after)
    }

    fun setSpace(space: Float, corner: Float) {
        for (img in mItemImageViews)
            img.setSpace(space, corner)
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
            previewSize = if (mPhotoItems.size > 4 || isNotLargeThan1Gb) 1024 else 1600
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
            val bounds = view.photoItem.bound
            val left = (bounds.left * outputWidth).roundToInt()
            val top = (bounds.top * outputHeight).roundToInt()
            val width = (bounds.right * outputWidth).roundToInt() - left
            val height = (bounds.bottom * outputHeight).roundToInt() - top
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
                canvas.drawColor(color)
                shader?.let {
                    canvas.drawRect(
                        0f,
                        0f,
                        outputWidth.toFloat(),
                        outputHeight.toFloat(),
                        Paint().apply {
                            this.shader = it(outputWidth.toFloat(), outputHeight.toFloat())
                        })
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

        target.animate().alpha(1f).setDuration(100L).start()
        source.animate().alpha(0f).setDuration(100L).withEndAction {
            if (imageDrag === drag) {
                source.translationX = 0f
                source.translationY = 0f
                source.translationZ = 0f
                source.scaleX = 1f
                source.scaleY = 1f
                resetImageDrag()
                target.swapImage(source)
                when (selectedItemIndex) {
                    source.photoItem.index -> onSingleTapImage(target)
                    target.photoItem.index -> onSingleTapImage(source)
                }
                dispatchUserStateChange(drag.before)
            }
        }.start()
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
        finishUserInteraction()
    }

    override fun onImageStateReady() {
        if (mItemImageViews.all(FrameImageView::isStateReady)) {
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
        imageDrag?.source?.let { drawOutline(canvas, it, selected = false) }
        super.dispatchDraw(canvas)
        if (imageDrag != null) {
            imageDrag?.target?.let { drawOutline(canvas, it, selected = true) }
            return
        }
        mItemImageViews.firstOrNull { it.photoItem.index == selectedItemIndex }
            ?.let { drawOutline(canvas, it, selected = true) }
        // draw handles for selected item
        val index = selectedItemIndex ?: return
        val handles = paramsManager?.getHandles(index) ?: emptyList()
        if (handles.isEmpty()) return
        val drawable = handleDrawable ?: return
        for (handle in handles) {
            val dp = handle.draggablePoint(paramsManager!!)
            val cx = mViewWidth * dp.x
            val cy = mViewHeight * dp.y
            val angle = handle.getAngle() + 90f
            val diameter = max(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight
            ).takeIf { it > 0 }?.toFloat() ?: 72f
            val half = diameter / 2f
            canvas.withTranslation(cx, cy) {
                canvas.rotate(angle)
                drawable.setBounds((-half).toInt(), (-half).toInt(), half.toInt(), half.toInt())
                drawable.draw(canvas)
            }
        }
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val index = selectedItemIndex ?: return super.onTouchEvent(event)
        mItemImageViews.firstOrNull { it.photoItem.index == index } ?: return super.onTouchEvent(
            event
        )
        val manager = paramsManager ?: return super.onTouchEvent(event)
        val handles = manager.getHandles(index)
        if (handles.isEmpty()) return super.onTouchEvent(event)

        val globalX = event.x
        val globalY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val drawable = handleDrawable
                if (drawable == null) {
                    activeHandle = null
                    return super.onTouchEvent(event)
                }
                val diameter = max(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight
                ).takeIf { it > 0 }?.toFloat() ?: 72f
                val radius = diameter / 2f
                activeHandle = handles.firstOrNull { handle ->
                    val dp = handle.draggablePoint(manager)
                    val hx = mViewWidth * dp.x
                    val hy = mViewHeight * dp.y
                    val dx = globalX - hx
                    val dy = globalY - hy
                    dx * dx + dy * dy <= radius * radius
                }
                if (activeHandle != null) {
                    parent?.requestDisallowInterceptTouchEvent(true)
                    beginUserInteraction()
                }
                return activeHandle != null || super.onTouchEvent(event)
            }

            MotionEvent.ACTION_MOVE -> {
                val handle = activeHandle ?: return super.onTouchEvent(event)
                val nx = (globalX / mViewWidth).coerceIn(0f, 1f)
                val ny = (globalY / mViewHeight).coerceIn(0f, 1f)
                handle.tryDrag(PointF(nx, ny), manager) ?: return true  // Drag failed
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (activeHandle == null) return false
                activeHandle = null
                parent?.requestDisallowInterceptTouchEvent(false)
                finishUserInteraction()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getMemoryInfo(): MemoryInfo = MemoryInfo().apply {
        context.getSystemService<ActivityManager>()?.getMemoryInfo(this)
    }

}