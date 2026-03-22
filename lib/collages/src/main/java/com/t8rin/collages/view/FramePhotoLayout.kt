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
import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.getSystemService
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withTranslation
import com.t8rin.collages.utils.Handle
import com.t8rin.collages.utils.ImageDecoder
import com.t8rin.collages.utils.ParamsManager
import kotlin.math.max
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

    private var mOnDragListener: OnDragListener = OnDragListener { v, event ->
        if (event.action == DragEvent.ACTION_DROP) {
            var target: FrameImageView? = v as FrameImageView
            val selectedView = getSelectedFrameImageView(target!!, event)
            if (selectedView != null) {
                target = selectedView
                val dragged = event.localState as FrameImageView
                var targetPath: Uri? = target.photoItem.imagePath
                var draggedPath: Uri? = dragged.photoItem.imagePath
                if (targetPath == null) targetPath = Uri.EMPTY
                if (draggedPath == null) draggedPath = Uri.EMPTY
                if (targetPath != draggedPath) target.swapImage(dragged)
            }
        }

        true
    }
    private val mItemImageViews: MutableList<FrameImageView> = ArrayList()
    private var mViewWidth: Int = 0
    private var mViewHeight: Int = 0
    private var backgroundColor: ComposeColor = ComposeColor.White
    private var onItemTapListener: ((index: Int) -> Unit)? = null

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
        setLayerType(LAYER_TYPE_HARDWARE, null)
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
        enableSnapToBorders = enable
        // Update existing children
        for (v in mItemImageViews) {
            v.setSnapToBordersEnabled(enableSnapToBorders)
        }
    }

    private fun getSelectedFrameImageView(
        target: FrameImageView,
        event: DragEvent
    ): FrameImageView? {
        val dragged = event.localState as FrameImageView
        val leftMargin = (mViewWidth * target.photoItem.bound.left).toInt()
        val topMargin = (mViewHeight * target.photoItem.bound.top).toInt()
        val globalX = leftMargin + event.x
        val globalY = topMargin + event.y
        for (idx in mItemImageViews.indices.reversed()) {
            val view = mItemImageViews[idx]
            val x = globalX - mViewWidth * view.photoItem.bound.left
            val y = globalY - mViewHeight * view.photoItem.bound.top
            if (view.isSelected(x, y)) {
                return if (view === dragged) {
                    null
                } else {
                    view
                }
            }
        }
        return null
    }

    fun saveInstanceState(outState: Bundle) {
        paramsManager?.saveInstanceState(outState)
        for (view in mItemImageViews)
            view.saveInstanceState(outState)
    }

    fun restoreInstanceState(savedInstanceState: Bundle) {
        paramsManager?.restoreInstanceState(savedInstanceState)
        for (view in mItemImageViews)
            view.restoreInstanceState(savedInstanceState)
    }

    @JvmOverloads
    fun build(
        viewWidth: Int,
        viewHeight: Int,
        space: Float = 0f,
        corner: Float = 0f
    ) {
        mItemImageViews.clear()
        removeAllViews()
        if (viewWidth < 1 || viewHeight < 1) {
            return
        }

        setBackgroundColor(backgroundColor.toArgb())

        //add children views
        mViewWidth = viewWidth
        mViewHeight = viewHeight
        mItemImageViews.clear()
        //A circle view always is on top
        if (mPhotoItems.size > 4 || isNotLargeThan1Gb) {
            ImageDecoder.SAMPLER_SIZE = 1024
        } else {
            ImageDecoder.SAMPLER_SIZE = 1600
        }
        for (item in mPhotoItems) {
            val imageView = addPhotoItemView(item, space, corner)
            mItemImageViews.add(imageView)
        }
    }

    fun resize(width: Int, height: Int) {
        mViewWidth = width
        mViewHeight = height
        for (i in mItemImageViews.indices) {
            resizeItem(i)
        }
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
        setBackgroundColor(backgroundColor.toArgb())
        invalidate()
    }

    fun setOnItemTapListener(listener: ((index: Int) -> Unit)?) {
        onItemTapListener = listener
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
        val minSize = kotlin.math.min(images.size, mPhotoItems.size)
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
            setColor(android.graphics.Color.rgb(255, 165, 0))
            setSize(diameterPx, diameterPx)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addPhotoItemView(
        item: PhotoItem,
        space: Float,
        corner: Float
    ): FrameImageView {
        val imageView = FrameImageView(context, item)
        val metrics = computeFrameMetrics(item.bound)
        imageView.setRotationEnabled(!disableRotation)
        imageView.setSnapToBordersEnabled(enableSnapToBorders)
        imageView.init(metrics.width.toFloat(), metrics.height.toFloat(), space, corner)
        imageView.setOnImageClickListener(this)
        imageView.setOnTouchListener { _, event ->
            // Intercept to support handle dragging overlay
            onTouchEvent(event)
        }
        if (mPhotoItems.size > 1)
            imageView.setOnDragListener(mOnDragListener)

        val params = LayoutParams(metrics.width, metrics.height)
        params.leftMargin = metrics.leftMargin
        params.topMargin = metrics.topMargin
        imageView.originalLayoutParams = params
        addView(imageView, params)
        return imageView
    }

    @Throws(OutOfMemoryError::class)
    fun createImage(outputScaleRatio: Float): Bitmap {
        try {
            val template = createBitmap(
                (outputScaleRatio * mViewWidth).toInt(),
                (outputScaleRatio * mViewHeight).toInt()
            )
            val canvas = Canvas(template)
            canvas.drawColor(backgroundColor.toArgb())
            for (view in mItemImageViews)
                if (view.image != null && !view.image!!.isRecycled) {
                    val left = (view.left * outputScaleRatio).toInt()
                    val top = (view.top * outputScaleRatio).toInt()
                    val width = (view.width * outputScaleRatio).toInt()
                    val height = (view.height * outputScaleRatio).toInt()
                    //draw image
                    canvas.saveLayer(
                        left.toFloat(),
                        top.toFloat(),
                        (left + width).toFloat(),
                        (top + height).toFloat(),
                        Paint()
                    )
                    canvas.translate(left.toFloat(), top.toFloat())
                    canvas.clipRect(0, 0, width, height)
                    view.drawOutputImage(canvas, outputScaleRatio)
                    canvas.restore()
                }

            return template
        } catch (error: OutOfMemoryError) {
            throw error
        }
    }

    override fun onLongClickImage(view: FrameImageView) {
        if (mPhotoItems.size > 1) {
            view.tag = """x=${0f},y=${0f},path=${view.photoItem.imagePath}"""
            val item = ClipData.Item(view.tag as CharSequence)
            val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
            val dragData = ClipData(view.tag.toString(), mimeTypes, item)
            val myShadow = DragShadowBuilder(view)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(dragData, myShadow, view, 0)
            } else {
                @Suppress("DEPRECATION")
                view.startDrag(dragData, myShadow, view, 0)
            }
        }
    }

    override fun onDoubleClickImage(view: FrameImageView) {

    }

    fun clearSelection() {
        selectedItemIndex = null
        activeHandle = null
        onItemTapListener?.invoke(-1)
        invalidate()
    }

    override fun onSingleTapImage(view: FrameImageView) {
        if (selectedItemIndex == view.photoItem.index) {
            clearSelection()
        } else {
            onItemTapListener?.invoke(view.photoItem.index)
            selectedItemIndex = view.photoItem.index
            requestFocus()
            invalidate()
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (!hasWindowFocus) {
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
        super.dispatchDraw(canvas)
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val index = selectedItemIndex ?: return super.onTouchEvent(event)
        mItemImageViews.firstOrNull { it.photoItem.index == index } ?: return super.onTouchEvent(
            event
        )
        val manager = paramsManager ?: return super.onTouchEvent(event)
        val handles = manager.getHandles(index)
        if (handles.isEmpty()) return super.onTouchEvent(event)

        // Compute coordinates in this layout's local space regardless of source view
        val screenPos = IntArray(2)
        getLocationOnScreen(screenPos)
        val globalX = event.rawX - screenPos[0]
        val globalY = event.rawY - screenPos[1]

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
                activeHandle = null
                return super.onTouchEvent(event)
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getMemoryInfo(): MemoryInfo = MemoryInfo().apply {
        context.getSystemService<ActivityManager>()?.getMemoryInfo(this)
    }

}
