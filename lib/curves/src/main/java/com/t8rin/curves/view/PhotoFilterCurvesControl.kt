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

@file:Suppress("ConstPropertyName")

package com.t8rin.curves.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt
import android.graphics.Color as AndroidColor


internal class PhotoFilterCurvesControl @JvmOverloads constructor(
    context: Context?,
    value: CurvesToolValue = CurvesToolValue()
) : View(context) {
    private var activePointIndex = NoPoint
    private val selectedPointIndices = IntArray(CurvesToolValue.CurveTypeCount) { NoPoint }
    private var isMoving = false
    private var checkForMoving = true
    private var downX = 0f
    private var downY = 0f
    private var pointWasCreated = false
    private var disallowInterceptTouchEvents = true
    private val actualArea = Rect()
    private val areaRect = RectF()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintDash = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCurve = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintNotActiveCurve = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pointHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private var delegate: PhotoFilterCurvesControlDelegate? = null
    private var selectionDelegate: ((Boolean) -> Unit)? = null
    private var curveValue: CurvesToolValue

    fun updateValue(
        value: CurvesToolValue
    ) {
        curveValue = value
        invalidate()
    }

    private var drawNotActiveCurves: Boolean = true
    private var lumaCurveColor = -0x1
    private var redCurveColor = -0x12c2b4
    private var greenCurveColor = -0xef1163
    private var blueCurveColor = -0xcc8805
    private var defaultCurveColor = -0x66000001
    private var guidelinesColor = -0x66000001
    private var editorBackgroundColor = Color.Black.copy(alpha = 0.18f).toArgb()

    private val activeCurve: CurvesValue
        get() = when (curveValue.activeType) {
            CurvesToolValue.CurvesTypeRed -> curveValue.redCurve
            CurvesToolValue.CurvesTypeGreen -> curveValue.greenCurve
            CurvesToolValue.CurvesTypeBlue -> curveValue.blueCurve
            else -> curveValue.luminanceCurve
        }

    private var selectedPointIndex: Int
        get() = selectedPointIndices[curveValue.activeType]
        set(value) {
            selectedPointIndices[curveValue.activeType] = value
            selectionDelegate?.invoke(canDeleteSelectedPoint)
        }

    val canDeleteSelectedPoint: Boolean
        get() = selectedPointIndex in 1 until activeCurve.points.lastIndex

    init {
        setWillNotDraw(false)

        curveValue = value

        paint.color = Color(guidelinesColor).copy(alpha = GridAlpha).toArgb()
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = editorBackgroundColor

        paint.strokeWidth = dp(0.75f).toFloat()
        paint.style = Paint.Style.STROKE

        paintDash.color = Color(defaultCurveColor).copy(alpha = DiagonalAlpha).toArgb()
        paintDash.strokeWidth = dp(1f).toFloat()
        paintDash.style = Paint.Style.STROKE
        paintDash.strokeCap = Paint.Cap.ROUND
        paintDash.pathEffect = DashPathEffect(
            floatArrayOf(dp(3f).toFloat(), dp(3f).toFloat()),
            0f
        )

        paintCurve.color = lumaCurveColor
        paintCurve.strokeWidth = dp(2.5f).toFloat()
        paintCurve.style = Paint.Style.STROKE
        paintCurve.strokeCap = Paint.Cap.ROUND
        paintCurve.strokeJoin = Paint.Join.ROUND
        paintCurve.setShadowLayer(2f, 0f, 0f, Color.Black.copy(0.5f).toArgb())

        paintNotActiveCurve.color = lumaCurveColor
        paintNotActiveCurve.strokeWidth = dp(1f).toFloat()
        paintNotActiveCurve.style = Paint.Style.STROKE
        paintNotActiveCurve.strokeCap = Paint.Cap.ROUND
        paintNotActiveCurve.setShadowLayer(1f, 0f, 0f, Color.Black.copy(0.5f).toArgb())

        pointPaint.style = Paint.Style.FILL
        pointHaloPaint.style = Paint.Style.FILL
        pointHaloPaint.color = Color.Black.copy(alpha = 0.55f).toArgb()
    }

    fun setDrawNotActiveCurves(
        drawNotActiveCurves: Boolean
    ) {
        this.drawNotActiveCurves = drawNotActiveCurves
        invalidate()
    }

    fun setColors(
        lumaCurveColor: Int,
        redCurveColor: Int,
        greenCurveColor: Int,
        blueCurveColor: Int,
        defaultCurveColor: Int,
        guidelinesColor: Int,
        editorBackgroundColor: Int
    ) {
        this.lumaCurveColor = lumaCurveColor
        this.redCurveColor = redCurveColor
        this.greenCurveColor = greenCurveColor
        this.blueCurveColor = blueCurveColor
        this.guidelinesColor = guidelinesColor
        this.defaultCurveColor = defaultCurveColor
        this.editorBackgroundColor = editorBackgroundColor
        backgroundPaint.color = editorBackgroundColor
        paint.color = Color(guidelinesColor).copy(alpha = GridAlpha).toArgb()
        paintDash.color = Color(defaultCurveColor).copy(alpha = DiagonalAlpha).toArgb()

        invalidate()
    }

    fun setDelegate(photoFilterCurvesControlDelegate: PhotoFilterCurvesControlDelegate?) {
        delegate = photoFilterCurvesControlDelegate
    }

    fun setSelectionDelegate(delegate: ((Boolean) -> Unit)?) {
        selectionDelegate = delegate
        delegate?.invoke(canDeleteSelectedPoint)
    }

    fun setActualArea(x: Float, y: Float, width: Float, height: Float) {
        actualArea.x = x
        actualArea.y = y
        actualArea.width = width
        actualArea.height = height
        areaRect.set(x, y, x + width, y + height)
        invalidate()
    }

    fun setActiveCurveType(type: Int) {
        if (curveValue.activeType != type) {
            curveValue.activeType = type
            activePointIndex = NoPoint
            selectionDelegate?.invoke(canDeleteSelectedPoint)
            invalidate()
        }
    }

    fun deleteSelectedPoint() {
        if (canDeleteSelectedPoint) {
            activeCurve.removePoint(selectedPointIndex)
            selectedPointIndex = NoPoint
            delegate?.valueChanged()
            invalidate()
        }
    }

    fun setDisallowInterceptTouchEvents(disallow: Boolean) {
        disallowInterceptTouchEvents = disallow
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (actualArea.x == 0f && actualArea.y == 0f) {
            setActualArea(0f, 0f, w.toFloat(), h.toFloat())
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_DOWN) {
            parent?.requestDisallowInterceptTouchEvent(disallowInterceptTouchEvents)
        }

        when (action) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount == 1) {
                    if (checkForMoving && !isMoving) {
                        val locationX = event.x
                        val locationY = event.y
                        downX = locationX
                        downY = locationY
                        if (locationX >= actualArea.x && locationX <= actualArea.x + actualArea.width && locationY >= actualArea.y && locationY <= actualArea.y + actualArea.height) {
                            isMoving = true
                        }
                        checkForMoving = false
                        if (isMoving) {
                            handlePan(GestureStateBegan, event)
                        }
                    }
                } else {
                    if (isMoving) {
                        handlePan(GestureStateEnded, event)
                        checkForMoving = true
                        isMoving = false
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (isMoving) {
                    handlePan(
                        if (action == MotionEvent.ACTION_CANCEL) GestureStateCancelled
                        else GestureStateEnded,
                        event
                    )
                    isMoving = false
                }
                checkForMoving = true
                parent?.requestDisallowInterceptTouchEvent(false)
            }

            MotionEvent.ACTION_MOVE -> {
                if (isMoving) {
                    handlePan(GestureStateChanged, event)
                }
            }
        }
        return true
    }

    private fun handlePan(state: Int, event: MotionEvent) {
        val locationX = event.x
        val locationY = event.y

        when (state) {
            GestureStateBegan -> {
                selectOrCreatePoint(locationX, locationY)
            }

            GestureStateChanged -> {
                moveActivePoint(locationX, locationY)
                invalidate()
                delegate?.valueChanged()
            }

            GestureStateEnded, GestureStateCancelled, GestureStateFailed -> {
                if (
                    state == GestureStateEnded &&
                    !pointWasCreated &&
                    activePointIndex in 1 until activeCurve.points.lastIndex &&
                    distance(downX, downY, locationX, locationY) < dp(3f)
                ) {
                    val now = event.eventTime
                    if (
                        activePointIndex == lastTappedPointIndex &&
                        now - lastTapTime <= DoubleTapTimeout
                    ) {
                        activeCurve.removePoint(activePointIndex)
                        selectedPointIndex = NoPoint
                        delegate?.valueChanged()
                        lastTappedPointIndex = NoPoint
                    } else {
                        lastTappedPointIndex = activePointIndex
                        lastTapTime = now
                    }
                }
                unselectPoint()
            }

            else -> {}
        }
    }

    private fun selectOrCreatePoint(x: Float, y: Float) {
        if (activePointIndex != NoPoint || actualArea.width <= 0f || actualArea.height <= 0f) {
            return
        }
        val touchRadius = dp(22f).toFloat()
        activePointIndex = activeCurve.points.indices.minByOrNull { index ->
            val point = activeCurve.points[index]
            distance(
                x,
                y,
                actualArea.x + point.x * actualArea.width,
                actualArea.y + (1f - point.y) * actualArea.height
            )
        }?.takeIf { index ->
            val point = activeCurve.points[index]
            distance(
                x,
                y,
                actualArea.x + point.x * actualArea.width,
                actualArea.y + (1f - point.y) * actualArea.height
            ) <= touchRadius
        } ?: run {
            val pointCount = activeCurve.points.size
            activeCurve.addPoint(
                x = ((x - actualArea.x) / actualArea.width).coerceIn(
                    MinimumPointDistance,
                    1f - MinimumPointDistance
                ),
                y = (1f - (y - actualArea.y) / actualArea.height).coerceIn(0f, 1f)
            ).also {
                pointWasCreated = activeCurve.points.size > pointCount
                if (pointWasCreated) delegate?.valueChanged()
            }
        }
        selectedPointIndex = activePointIndex
        invalidate()
    }

    private fun moveActivePoint(x: Float, y: Float) {
        val index = activePointIndex
        if (index == NoPoint) return

        val point = activeCurve.points[index]
        val normalizedY = (1f - (y - actualArea.y) / actualArea.height).coerceIn(0f, 1f)
        point.y = normalizedY
        if (index != 0 && index != activeCurve.points.lastIndex) {
            val previousX = activeCurve.points[index - 1].x
            val nextX = activeCurve.points[index + 1].x
            val availableSpacing = (nextX - previousX).coerceAtLeast(0f)
            val pointSpacing = minOf(MinimumPointDistance, availableSpacing / 2f)
            val minX = previousX + pointSpacing
            val maxX = nextX - pointSpacing
            point.x = ((x - actualArea.x) / actualArea.width).coerceIn(minX, maxX)
        }
        activeCurve.invalidateCache()
    }

    private fun unselectPoint() {
        activePointIndex = NoPoint
        pointWasCreated = false
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (actualArea.width <= 0f || actualArea.height <= 0f) return

        canvas.drawRect(areaRect, backgroundPaint)

        for (i in 1..3) {
            canvas.drawLine(
                actualArea.x + actualArea.width * i / 4f,
                actualArea.y,
                actualArea.x + actualArea.width * i / 4f,
                actualArea.y + actualArea.height,
                paint
            )
            canvas.drawLine(
                actualArea.x,
                actualArea.y + actualArea.height * i / 4f,
                actualArea.x + actualArea.width,
                actualArea.y + actualArea.height * i / 4f,
                paint
            )
        }

        canvas.drawLine(
            actualArea.x,
            actualArea.y + actualArea.height,
            actualArea.x + actualArea.width,
            actualArea.y,
            paintDash
        )

        var curvesValue: CurvesValue? = null
        when (curveValue.activeType) {
            CurvesToolValue.CurvesTypeLuminance -> {
                paintCurve.color = lumaCurveColor
                curvesValue = curveValue.luminanceCurve
            }

            CurvesToolValue.CurvesTypeRed -> {
                paintCurve.color = redCurveColor
                curvesValue = curveValue.redCurve
            }

            CurvesToolValue.CurvesTypeGreen -> {
                paintCurve.color = greenCurveColor
                curvesValue = curveValue.greenCurve
            }

            CurvesToolValue.CurvesTypeBlue -> {
                paintCurve.color = blueCurveColor
                curvesValue = curveValue.blueCurve
            }

            else -> Unit
        }
        var points: FloatArray

        if (drawNotActiveCurves) {
            listOf(
                curveValue.luminanceCurve to lumaCurveColor,
                curveValue.redCurve to redCurveColor,
                curveValue.greenCurve to greenCurveColor,
                curveValue.blueCurve to blueCurveColor
            ).filter { it.first != curvesValue && !it.first.isDefault }.forEach { (curve, color) ->
                paintNotActiveCurve.color = Color(color).copy(0.7f).toArgb()
                points = curve.interpolateCurve()
                path.reset()
                for (a in 0 until points.size / 2) {
                    if (a == 0) {
                        path.moveTo(
                            actualArea.x + points[0] * actualArea.width,
                            actualArea.y + (1.0f - points[1]) * actualArea.height
                        )
                    } else {
                        path.lineTo(
                            actualArea.x + points[a * 2] * actualArea.width,
                            actualArea.y + (1.0f - points[a * 2 + 1]) * actualArea.height
                        )
                    }
                }

                canvas.drawPath(path, paintNotActiveCurve)
            }
        }

        points = curvesValue!!.interpolateCurve()
        path.reset()
        for (a in 0 until points.size / 2) {
            if (a == 0) {
                path.moveTo(
                    actualArea.x + points[0] * actualArea.width,
                    actualArea.y + (1.0f - points[1]) * actualArea.height
                )
            } else {
                path.lineTo(
                    actualArea.x + points[a * 2] * actualArea.width,
                    actualArea.y + (1.0f - points[a * 2 + 1]) * actualArea.height
                )
            }
        }

        canvas.drawPath(path, paintCurve)

        pointPaint.color = paintCurve.color
        curvesValue.points.forEachIndexed { index, point ->
            val x = actualArea.x + point.x * actualArea.width
            val y = actualArea.y + (1f - point.y) * actualArea.height
            val isActive = index == activePointIndex || index == selectedPointIndex
            canvas.drawCircle(
                x,
                y,
                dp(if (isActive) 8f else 6f).toFloat(),
                pointHaloPaint
            )
            canvas.drawCircle(
                x,
                y,
                dp(if (isActive) 5.5f else 3.5f).toFloat(),
                pointPaint
            )
            if (isActive) {
                pointPaint.style = Paint.Style.STROKE
                pointPaint.strokeWidth = dp(1.5f).toFloat()
                pointPaint.color = AndroidColor.WHITE
                canvas.drawCircle(x, y, dp(8f).toFloat(), pointPaint)
                pointPaint.style = Paint.Style.FILL
                pointPaint.color = paintCurve.color
            }
        }
    }

    fun interface PhotoFilterCurvesControlDelegate {
        fun valueChanged()
    }

    internal class CurvesValue {
        val points: MutableList<PointF> = mutableListOf(
            PointF(0f, 0f),
            PointF(1f, 1f)
        )

        private var cachedDataPoints: FloatArray? = null

        val dataPoints: FloatArray?
            get() {
                if (cachedDataPoints == null) {
                    interpolateCurve()
                }
                return cachedDataPoints
            }

        fun interpolateCurve(): FloatArray {
            val dataPoints = ArrayList<Float>(100)
            val interpolatedPoints = ArrayList<Float>(100)

            points.forEachIndexed { index, point ->
                if (index == 0) {
                    interpolatedPoints.add(point.x)
                    interpolatedPoints.add(point.y)
                }
                if (index == points.lastIndex) return@forEachIndexed

                val point0 = points.getOrElse(index - 1) { point }
                val point1 = point
                val point2 = points[index + 1]
                val point3 = points.getOrElse(index + 2) { point2 }
                for (i in 1..curveGranularity) {
                    val t = i.toFloat() * (1.0f / curveGranularity.toFloat())
                    val tt = t * t
                    val ttt = tt * t

                    val pix = 0.5f * (
                            2 * point1.x +
                                    (point2.x - point0.x) * t +
                                    (2 * point0.x - 5 * point1.x + 4 * point2.x - point3.x) * tt +
                                    (3 * point1.x - point0.x - 3 * point2.x + point3.x) * ttt
                            )
                    var piy =
                        0.5f * (
                                2 * point1.y +
                                        (point2.y - point0.y) * t +
                                        (2 * point0.y - 5 * point1.y + 4 * point2.y - point3.y) * tt +
                                        (3 * point1.y - point0.y - 3 * point2.y + point3.y) * ttt
                                )

                    piy = piy.coerceIn(0f, 1f)
                    interpolatedPoints.add(pix.coerceIn(point1.x, point2.x))
                    interpolatedPoints.add(piy)
                    if (dataPoints.size < 256) {
                        dataPoints.add(piy)
                    }
                }
            }

            cachedDataPoints = dataPoints.toFloatArray()
            return interpolatedPoints.toFloatArray()
        }

        fun addPoint(x: Float, y: Float): Int {
            if (points.size >= MaxPointCount) {
                return points.indices.minBy { abs(points[it].x - x) }
            }
            val index = points.indexOfFirst { it.x > x }
                .let { if (it == -1) points.lastIndex else it }
                .coerceAtLeast(1)
            val minX = points[index - 1].x + MinimumPointDistance
            val maxX = points[index].x - MinimumPointDistance
            if (minX > maxX) {
                return if (abs(points[index - 1].x - x) <= abs(points[index].x - x)) {
                    index - 1
                } else {
                    index
                }
            }
            points.add(index, PointF(x.coerceIn(minX, maxX), y))
            invalidateCache()
            return index
        }

        fun removePoint(index: Int) {
            if (index in 1 until points.lastIndex) {
                points.removeAt(index)
                invalidateCache()
            }
        }

        fun replacePoints(newPoints: List<PointF>) {
            val normalized = newPoints
                .map { PointF(it.x.coerceIn(0f, 1f), it.y.coerceIn(0f, 1f)) }
                .sortedBy { it.x }
                .distinctBy { it.x }
                .toMutableList()
            if (normalized.size < 2) return
            normalized.first().x = 0f
            normalized.last().x = 1f
            points.clear()
            points.addAll(
                if (normalized.size <= MaxPointCount) {
                    normalized
                } else {
                    normalized.take(MaxPointCount - 1) + normalized.last()
                }
            )
            invalidateCache()
        }

        fun invalidateCache() {
            cachedDataPoints = null
        }

        fun copy(): CurvesValue = CurvesValue().also { copy ->
            copy.replacePoints(points)
        }

        val isDefault: Boolean
            get() = points.all { point -> abs(point.x - point.y) < 0.00001f }
    }

    internal class CurvesToolValue {
        var luminanceCurve: CurvesValue = CurvesValue()
        var redCurve: CurvesValue = CurvesValue()
        var greenCurve: CurvesValue = CurvesValue()
        var blueCurve: CurvesValue = CurvesValue()
        var activeType: Int = CurvesTypeLuminance

        fun copy(): CurvesToolValue = CurvesToolValue().also {
            it.luminanceCurve = luminanceCurve.copy()
            it.redCurve = redCurve.copy()
            it.greenCurve = greenCurve.copy()
            it.blueCurve = blueCurve.copy()
            it.activeType = activeType
        }

        companion object {
            const val CurvesTypeLuminance: Int = 0
            const val CurvesTypeRed: Int = 1
            const val CurvesTypeGreen: Int = 2
            const val CurvesTypeBlue: Int = 3
            const val CurveTypeCount: Int = 4
        }
    }

    companion object {
        private const val curveGranularity = 100
        private val density = Resources.getSystem().displayMetrics.density
        private const val NoPoint = -1
        private const val MaxPointCount = 16
        private const val MinimumPointDistance = 0.015f
        private const val DoubleTapTimeout = 300L
        private const val GridAlpha = 0.32f
        private const val DiagonalAlpha = 0.85f
        private const val GestureStateBegan = 1
        private const val GestureStateChanged = 2
        private const val GestureStateEnded = 3
        private const val GestureStateCancelled = 4
        private const val GestureStateFailed = 5
        private var lastTapTime = 0L
        private var lastTappedPointIndex = NoPoint

        private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            return sqrt((x1 - x2).pow(2) + (y1 - y2).pow(2))
        }
        fun dp(value: Float): Int {
            if (value == 0f) {
                return 0
            }
            return ceil((density * value).toDouble())
                .toInt()
        }
    }
}
