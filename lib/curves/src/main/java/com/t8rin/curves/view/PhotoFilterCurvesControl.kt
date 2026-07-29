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
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.curves.ImageCurvesEditorType
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
    private val selectedPointIndices = IntArray(CurvesToolValue.CurveCount) { NoPoint }
    private var isMoving = false
    private var checkForMoving = true
    private var downX = 0f
    private var downY = 0f
    private var pointWasCreated = false
    private var disallowInterceptTouchEvents = true
    private var drawEditorContent = true
    private var drawControlPoints = true
    private var actualAreaInset = 0f
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
    private var hueCurveShader: Shader? = null
    private var luminanceCurveShader: Shader? = null
    private var labACurveShader: Shader? = null
    private var labBCurveShader: Shader? = null
    private var saturationCurveShader: Shader? = null
    private var delegate: PhotoFilterCurvesControlDelegate? = null
    private var actionAvailabilityDelegate: ((Boolean, Boolean) -> Unit)? = null
    private var curveValue: CurvesToolValue
    private var displayedCurveType = value.activeType
    private var displayedEditorType = value.activeEditorType

    fun updateValue(
        value: CurvesToolValue
    ) {
        curveValue = value
        notifyActionAvailability()
        invalidate()
    }

    private var drawNotActiveCurves: Boolean = true
    private var lumaCurveColor = -0x1
    private var redCurveColor = -0x12c2b4
    private var greenCurveColor = -0xef1163
    private var blueCurveColor = -0xcc8805
    private var cyanCurveColor = AndroidColor.rgb(0, 188, 212)
    private var magentaCurveColor = AndroidColor.rgb(236, 64, 122)
    private var yellowCurveColor = AndroidColor.rgb(255, 193, 7)
    private var hueCurveColors = intArrayOf(
        AndroidColor.rgb(255, 82, 82),
        AndroidColor.rgb(255, 171, 64),
        AndroidColor.rgb(255, 224, 51),
        AndroidColor.rgb(76, 217, 100),
        AndroidColor.rgb(56, 214, 210),
        AndroidColor.rgb(85, 150, 255),
        AndroidColor.rgb(166, 107, 255),
        AndroidColor.rgb(245, 92, 170),
        AndroidColor.rgb(255, 82, 82)
    )
    private var lumaGradientStartColor = AndroidColor.rgb(116, 116, 116)
    private var lumaGradientEndColor = AndroidColor.WHITE
    private var saturationGradientStartColor = AndroidColor.WHITE
    private var saturationGradientEndColor = AndroidColor.rgb(255, 209, 102)
    private var defaultCurveColor = -0x66000001
    private var guidelinesColor = -0x66000001
    private var editorBackgroundColor = Color.Black.copy(alpha = 0.18f).toArgb()
    private var gridLineAlpha = 0.5f
    private var referenceLineAlpha = 0.85f

    private val activeCurve: CurvesValue
        get() = curveValue.activeCurve

    private var selectedPointIndex: Int
        get() = selectedPointIndices[curveValue.activeCurveIndex]
        set(value) {
            selectedPointIndices[curveValue.activeCurveIndex] = value
            notifyActionAvailability()
        }

    val canDeleteSelectedPoint: Boolean
        get() = selectedPointIndex in 1 until activeCurve.points.lastIndex

    val canResetActiveCurve: Boolean
        get() = !activeCurve.isDefault

    init {
        setWillNotDraw(false)

        curveValue = value

        paint.color = Color(guidelinesColor).copy(alpha = gridLineAlpha).toArgb()
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = editorBackgroundColor

        paint.strokeWidth = dp(0.75f).toFloat()
        paint.style = Paint.Style.STROKE

        paintDash.color = Color(defaultCurveColor).copy(alpha = referenceLineAlpha).toArgb()
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

    fun setDrawEditorContent(drawEditorContent: Boolean) {
        this.drawEditorContent = drawEditorContent
        invalidate()
    }

    fun setDrawControlPoints(drawControlPoints: Boolean) {
        this.drawControlPoints = drawControlPoints
        invalidate()
    }

    fun setColors(
        lumaCurveColor: Int,
        redCurveColor: Int,
        greenCurveColor: Int,
        blueCurveColor: Int,
        cyanCurveColor: Int,
        magentaCurveColor: Int,
        yellowCurveColor: Int,
        hueCurveColors: IntArray,
        lumaGradientStartColor: Int,
        lumaGradientEndColor: Int,
        saturationGradientStartColor: Int,
        saturationGradientEndColor: Int,
        defaultCurveColor: Int,
        guidelinesColor: Int,
        editorBackgroundColor: Int,
        gridLineAlpha: Float,
        referenceLineAlpha: Float
    ) {
        this.lumaCurveColor = lumaCurveColor
        this.redCurveColor = redCurveColor
        this.greenCurveColor = greenCurveColor
        this.blueCurveColor = blueCurveColor
        this.cyanCurveColor = cyanCurveColor
        this.magentaCurveColor = magentaCurveColor
        this.yellowCurveColor = yellowCurveColor
        this.hueCurveColors = hueCurveColors.takeIf { it.size >= 2 }?.copyOf()
            ?: this.hueCurveColors
        this.lumaGradientStartColor = lumaGradientStartColor
        this.lumaGradientEndColor = lumaGradientEndColor
        this.saturationGradientStartColor = saturationGradientStartColor
        this.saturationGradientEndColor = saturationGradientEndColor
        this.guidelinesColor = guidelinesColor
        this.defaultCurveColor = defaultCurveColor
        this.editorBackgroundColor = editorBackgroundColor
        this.gridLineAlpha = gridLineAlpha.coerceIn(0f, 1f)
        this.referenceLineAlpha = referenceLineAlpha.coerceIn(0f, 1f)
        backgroundPaint.color = editorBackgroundColor
        paint.color = Color(guidelinesColor).copy(alpha = this.gridLineAlpha).toArgb()
        paintDash.color = Color(defaultCurveColor)
            .copy(alpha = this.referenceLineAlpha)
            .toArgb()

        updateCurveShaders()
        invalidate()
    }

    fun setDelegate(photoFilterCurvesControlDelegate: PhotoFilterCurvesControlDelegate?) {
        delegate = photoFilterCurvesControlDelegate
    }

    fun setActionAvailabilityDelegate(delegate: ((Boolean, Boolean) -> Unit)?) {
        actionAvailabilityDelegate = delegate
        notifyActionAvailability()
    }

    fun setActualAreaInset(inset: Float) {
        actualAreaInset = inset
        updateActualArea(width, height)
    }

    private fun updateActualArea(width: Int, height: Int) {
        actualArea.x = actualAreaInset
        actualArea.y = actualAreaInset
        actualArea.width = (width - actualAreaInset * 2f).coerceAtLeast(0f)
        actualArea.height = (height - actualAreaInset * 2f).coerceAtLeast(0f)
        areaRect.set(
            actualArea.x,
            actualArea.y,
            actualArea.x + actualArea.width,
            actualArea.y + actualArea.height
        )
        updateCurveShaders()
        invalidate()
    }

    private fun updateCurveShaders() {
        hueCurveShader = if (actualArea.width > 0f) {
            LinearGradient(
                actualArea.x,
                0f,
                actualArea.x + actualArea.width,
                0f,
                hueCurveColors,
                null,
                Shader.TileMode.CLAMP
            )
        } else {
            null
        }
        luminanceCurveShader = gradient(lumaGradientStartColor, lumaGradientEndColor)
        labACurveShader = gradient(greenCurveColor, magentaCurveColor)
        labBCurveShader = gradient(blueCurveColor, yellowCurveColor)
        saturationCurveShader = gradient(
            saturationGradientStartColor,
            saturationGradientEndColor
        )
    }

    private fun gradient(
        startColor: Int,
        endColor: Int
    ): Shader? = if (actualArea.width > 0f) {
        LinearGradient(
            actualArea.x,
            0f,
            actualArea.x + actualArea.width,
            0f,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    } else {
        null
    }

    fun setActiveCurveType(type: Int) {
        val safeType = type.coerceIn(0, curveValue.activeEditorType.channelCount - 1)
        curveValue.activeType = safeType
        if (displayedCurveType != safeType) {
            displayedCurveType = safeType
            activePointIndex = NoPoint
            notifyActionAvailability()
        }
        invalidate()
    }

    fun setActiveEditorType(type: ImageCurvesEditorType) {
        curveValue.activeEditorType = type
        curveValue.activeType = curveValue.activeType.coerceIn(0, type.channelCount - 1)
        if (displayedEditorType != type) {
            displayedEditorType = type
            displayedCurveType = curveValue.activeType
            activePointIndex = NoPoint
            notifyActionAvailability()
        }
        invalidate()
    }

    fun deleteSelectedPoint() {
        if (canDeleteSelectedPoint) {
            activeCurve.removePoint(selectedPointIndex)
            normalizeCenteredCurveAfterRemoval()
            selectedPointIndex = NoPoint
            delegate?.valueChanged()
            notifyActionAvailability()
            invalidate()
        }
    }

    fun resetActiveCurve() {
        if (!canResetActiveCurve) return

        activeCurve.reset()
        selectedPointIndex = NoPoint
        delegate?.valueChanged()
        notifyActionAvailability()
        invalidate()
    }

    fun setDisallowInterceptTouchEvents(disallow: Boolean) {
        disallowInterceptTouchEvents = disallow
    }

    fun isControlPointHit(x: Float, y: Float): Boolean {
        if (actualArea.width <= 0f || actualArea.height <= 0f) return false

        val touchRadius = dp(ControlPointTouchRadius).toFloat()
        return editablePointIndices.any { index ->
            val point = activeCurve.points[index]
            distance(
                x,
                y,
                actualArea.x + point.x * actualArea.width,
                actualArea.y + (1f - point.y) * actualArea.height
            ) <= touchRadius
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateActualArea(w, h)
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
                        val isInsideEditor = locationX >= actualArea.x &&
                                locationX <= actualArea.x + actualArea.width &&
                                locationY >= actualArea.y &&
                                locationY <= actualArea.y + actualArea.height
                        if (isInsideEditor || isControlPointHit(locationX, locationY)) {
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
                        normalizeCenteredCurveAfterRemoval()
                        selectedPointIndex = NoPoint
                        delegate?.valueChanged()
                        notifyActionAvailability()
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
        val touchRadius = dp(ControlPointTouchRadius).toFloat()
        activePointIndex = editablePointIndices.minByOrNull { index ->
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
                x = ((x - actualArea.x) / actualArea.width).let { position ->
                    if (curveValue.activeEditorType.centeredCurve) {
                        position.coerceIn(0f, 1f)
                    } else {
                        position.coerceIn(
                            MinimumPointDistance,
                            1f - MinimumPointDistance
                        )
                    }
                },
                y = (1f - (y - actualArea.y) / actualArea.height).coerceIn(0f, 1f),
                minimumDistance = minimumPointDistance
            ).also {
                pointWasCreated = activeCurve.points.size > pointCount
                if (pointWasCreated) {
                    syncCenteredEndpoints()
                    delegate?.valueChanged()
                    notifyActionAvailability()
                }
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
        val isOnlyCenteredPoint = curveValue.activeEditorType.centeredCurve &&
                activeCurve.points.size == 3 &&
                index == 1
        if (isOnlyCenteredPoint) {
            point.x = ((x - actualArea.x) / actualArea.width).coerceIn(0f, 1f)
        }
        if (
            curveValue.activeEditorType.hasCircularInput &&
            (index == 0 || index == activeCurve.points.lastIndex)
        ) {
            activeCurve.points.first().y = normalizedY
            activeCurve.points.last().y = normalizedY
        }
        if (
            !isOnlyCenteredPoint &&
            index != 0 &&
            index != activeCurve.points.lastIndex
        ) {
            val pointSpacing = minimumPointDistance
            val previousX = activeCurve.points
                .getOrNull(index - 1)
                ?.takeUnless { index - 1 == 0 && curveValue.activeEditorType.centeredCurve }
                ?.x
            val nextX = activeCurve.points
                .getOrNull(index + 1)
                ?.takeUnless {
                    index + 1 == activeCurve.points.lastIndex &&
                            curveValue.activeEditorType.centeredCurve
                }
                ?.x
            var minX = previousX?.plus(pointSpacing) ?: 0f
            var maxX = nextX?.minus(pointSpacing) ?: 1f
            if (
                curveValue.activeEditorType.hasCircularInput &&
                activeCurve.points.size > 3
            ) {
                val firstVisibleX = activeCurve.points[1].x
                val lastVisibleX = activeCurve.points[activeCurve.points.lastIndex - 1].x
                if (index == 1) {
                    minX = maxOf(minX, lastVisibleX + pointSpacing - 1f)
                }
                if (index == activeCurve.points.lastIndex - 1) {
                    maxX = minOf(maxX, firstVisibleX + 1f - pointSpacing)
                }
            }
            if (minX <= maxX) {
                point.x = ((x - actualArea.x) / actualArea.width).coerceIn(minX, maxX)
            }
        }
        syncCenteredEndpoints()
        activeCurve.invalidateCache()
        notifyActionAvailability()
    }

    private val editablePointIndices: IntRange
        get() = if (curveValue.activeEditorType.centeredCurve) {
            1 until activeCurve.points.lastIndex
        } else {
            activeCurve.points.indices
        }

    private fun syncCenteredEndpoints() {
        if (curveValue.activeEditorType.centeredCurve && activeCurve.points.size > 2) {
            activeCurve.points.first().y = activeCurve.points[1].y
            activeCurve.points.last().y = activeCurve.points[activeCurve.points.lastIndex - 1].y
            activeCurve.invalidateCache()
        }
    }

    private fun normalizeCenteredCurveAfterRemoval() {
        if (!curveValue.activeEditorType.centeredCurve) return
        if (activeCurve.points.size > 2) {
            syncCenteredEndpoints()
        } else {
            activeCurve.points.first().y = 0.5f
            activeCurve.points.last().y = 0.5f
            activeCurve.invalidateCache()
        }
    }

    private fun unselectPoint() {
        activePointIndex = NoPoint
        pointWasCreated = false
        invalidate()
    }

    private val minimumPointDistance: Float
        get() = maxOf(
            MinimumPointDistance,
            dp(MinimumPointDistanceDp).toFloat() / actualArea.width.coerceAtLeast(1f)
        )

    private fun notifyActionAvailability() {
        actionAvailabilityDelegate?.invoke(
            canDeleteSelectedPoint,
            canResetActiveCurve
        )
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        if (actualArea.width <= 0f || actualArea.height <= 0f) return

        if (drawEditorContent) {
            canvas.drawRect(areaRect, backgroundPaint)

            for (i in 1 until GridSectionCount) {
                canvas.drawLine(
                    actualArea.x + actualArea.width * i / GridSectionCount,
                    actualArea.y,
                    actualArea.x + actualArea.width * i / GridSectionCount,
                    actualArea.y + actualArea.height,
                    paint
                )
                canvas.drawLine(
                    actualArea.x,
                    actualArea.y + actualArea.height * i / GridSectionCount,
                    actualArea.x + actualArea.width,
                    actualArea.y + actualArea.height * i / GridSectionCount,
                    paint
                )
            }

            if (curveValue.activeEditorType.centeredCurve) {
                canvas.drawLine(
                    actualArea.x,
                    actualArea.y + actualArea.height / 2f,
                    actualArea.x + actualArea.width,
                    actualArea.y + actualArea.height / 2f,
                    paintDash
                )
            } else {
                canvas.drawLine(
                    actualArea.x,
                    actualArea.y + actualArea.height,
                    actualArea.x + actualArea.width,
                    actualArea.y,
                    paintDash
                )
            }
        }

        val activeCurvesValue = curveValue.activeCurve
        paintCurve.color = curveColor(
            editorType = curveValue.activeEditorType,
            channel = curveValue.activeType
        )
        paintCurve.shader = when (curveValue.activeEditorType) {
            ImageCurvesEditorType.HueVsSat,
            ImageCurvesEditorType.HueVsHue,
            ImageCurvesEditorType.HueVsLuma -> hueCurveShader

            ImageCurvesEditorType.LumaVsSat,
            ImageCurvesEditorType.LumaVsHue -> luminanceCurveShader

            ImageCurvesEditorType.SatVsSat -> saturationCurveShader
            ImageCurvesEditorType.Lab -> when (curveValue.activeType) {
                1 -> labACurveShader
                2 -> labBCurveShader
                else -> null
            }

            else -> null
        }
        var points: FloatArray

        if (drawEditorContent) {
            if (drawNotActiveCurves) {
                curveValue.curvesFor(curveValue.activeEditorType).mapIndexed { index, curve ->
                    curve to curveColor(curveValue.activeEditorType, index)
                }.filter {
                    it.first != activeCurvesValue && !it.first.isDefault
                }.forEach { (curve, color) ->
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

            points = activeCurvesValue.interpolateCurve()
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
        }

        if (!drawControlPoints) return
        activeCurvesValue.points.forEachIndexed { index, point ->
            if (
                curveValue.activeEditorType.centeredCurve &&
                (index == 0 || index == activeCurvesValue.points.lastIndex)
            ) {
                return@forEachIndexed
            }
            val x = actualArea.x + point.x * actualArea.width
            val y = actualArea.y + (1f - point.y) * actualArea.height
            val isActive = index == activePointIndex || index == selectedPointIndex
            pointPaint.color = pointColor(point.x)
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

    private fun curveColor(
        editorType: ImageCurvesEditorType,
        channel: Int
    ): Int = when (editorType) {
        ImageCurvesEditorType.RGB -> when (channel) {
            CurvesToolValue.CurvesTypeRed -> redCurveColor
            CurvesToolValue.CurvesTypeGreen -> greenCurveColor
            CurvesToolValue.CurvesTypeBlue -> blueCurveColor
            else -> lumaCurveColor
        }

        ImageCurvesEditorType.CMYK -> when (channel) {
            0 -> cyanCurveColor
            1 -> magentaCurveColor
            2 -> yellowCurveColor
            else -> AndroidColor.LTGRAY
        }

        ImageCurvesEditorType.Lab -> when (channel) {
            1 -> magentaCurveColor
            2 -> blueCurveColor
            else -> AndroidColor.WHITE
        }

        else -> AndroidColor.WHITE
    }

    private fun pointColor(position: Float): Int = when (curveValue.activeEditorType) {
        ImageCurvesEditorType.HueVsSat,
        ImageCurvesEditorType.HueVsHue,
        ImageCurvesEditorType.HueVsLuma -> gradientColorAt(hueCurveColors, position)

        ImageCurvesEditorType.LumaVsSat,
        ImageCurvesEditorType.LumaVsHue -> blendColor(
            lumaGradientStartColor,
            lumaGradientEndColor,
            position
        )

        ImageCurvesEditorType.SatVsSat -> blendColor(
            saturationGradientStartColor,
            saturationGradientEndColor,
            position
        )

        ImageCurvesEditorType.Lab -> when (curveValue.activeType) {
            1 -> blendColor(greenCurveColor, magentaCurveColor, position)
            2 -> blendColor(blueCurveColor, yellowCurveColor, position)
            else -> blendColor(defaultCurveColor, lumaCurveColor, position)
        }

        else -> paintCurve.color
    }

    private fun gradientColorAt(colors: IntArray, position: Float): Int {
        if (colors.isEmpty()) return lumaCurveColor
        if (colors.size == 1) return colors.first()

        val scaledPosition = position.coerceIn(0f, 1f) * colors.lastIndex
        val startIndex = scaledPosition.toInt().coerceAtMost(colors.lastIndex - 1)
        return blendColor(
            start = colors[startIndex],
            end = colors[startIndex + 1],
            fraction = scaledPosition - startIndex
        )
    }

    private fun blendColor(
        start: Int,
        end: Int,
        fraction: Float
    ): Int {
        val amount = fraction.coerceIn(0f, 1f)
        val alpha = AndroidColor.alpha(start) +
                (AndroidColor.alpha(end) - AndroidColor.alpha(start)) * amount
        val red = AndroidColor.red(start) +
                (AndroidColor.red(end) - AndroidColor.red(start)) * amount
        val green = AndroidColor.green(start) +
                (AndroidColor.green(end) - AndroidColor.green(start)) * amount
        val blue = AndroidColor.blue(start) +
                (AndroidColor.blue(end) - AndroidColor.blue(start)) * amount
        return AndroidColor.argb(
            alpha.toInt(),
            red.toInt(),
            green.toInt(),
            blue.toInt()
        )
    }

    private val ImageCurvesEditorType.hasCircularInput: Boolean
        get() = this == ImageCurvesEditorType.HueVsSat ||
                this == ImageCurvesEditorType.HueVsHue ||
                this == ImageCurvesEditorType.HueVsLuma

    fun interface PhotoFilterCurvesControlDelegate {
        fun valueChanged()
    }

    internal class CurvesValue(
        private val neutralValue: Float? = null,
        private val circularInput: Boolean = false
    ) {
        val points: MutableList<PointF> = mutableListOf(
            PointF(0f, neutralValue ?: 0f),
            PointF(1f, neutralValue ?: 1f)
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
            if (circularInput) {
                return interpolateCircularCurve()
            }
            if (neutralValue != null) {
                return interpolateCenteredCurve()
            }

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

        private fun interpolateCircularCurve(): FloatArray {
            val controlPoints = points.subList(1, points.lastIndex)
            val neutral = neutralValue ?: 0.5f
            if (controlPoints.isEmpty()) {
                return floatArrayOf(0f, neutral, 1f, neutral)
            }
            if (controlPoints.size == 1) {
                val value = controlPoints.first().y
                cachedDataPoints = FloatArray(256) { value }
                return floatArrayOf(0f, value, 1f, value)
            }

            fun periodicPoint(index: Int): PointF {
                val size = controlPoints.size
                val cycle = Math.floorDiv(index, size)
                val point = controlPoints[Math.floorMod(index, size)]
                return PointF(point.x + cycle, point.y)
            }

            val sampledPoints = List(CircularCurveSampleCount + 1) { sample ->
                val x = sample / CircularCurveSampleCount.toFloat()
                val nextPointIndex = controlPoints
                    .indexOfFirst { point -> point.x >= x }
                    .let { index -> if (index == -1) controlPoints.size else index }
                val point0 = periodicPoint(nextPointIndex - 2)
                val point1 = periodicPoint(nextPointIndex - 1)
                val point2 = periodicPoint(nextPointIndex)
                val point3 = periodicPoint(nextPointIndex + 1)
                val segmentWidth = (point2.x - point1.x).coerceAtLeast(0.00001f)
                val t = ((x - point1.x) / segmentWidth).coerceIn(0f, 1f)
                val tt = t * t
                val ttt = tt * t
                val slope1 = (point2.y - point0.y) /
                        (point2.x - point0.x).coerceAtLeast(0.00001f)
                val slope2 = (point3.y - point1.y) /
                        (point3.x - point1.x).coerceAtLeast(0.00001f)
                val y = (2f * ttt - 3f * tt + 1f) * point1.y +
                        (ttt - 2f * tt + t) * segmentWidth * slope1 +
                        (-2f * ttt + 3f * tt) * point2.y +
                        (ttt - tt) * segmentWidth * slope2
                PointF(x, y.coerceIn(0f, 1f))
            }

            cachedDataPoints = sampledPoints.dropLast(1).map { it.y }.toFloatArray()
            return sampledPoints.flatMap { listOf(it.x, it.y) }.toFloatArray()
        }

        private fun interpolateCenteredCurve(): FloatArray {
            val controlPoints = points
                .subList(1, points.lastIndex)
                .fold(mutableListOf<PointF>()) { result, point ->
                    if (result.lastOrNull()?.x == point.x) {
                        result[result.lastIndex] = point
                    } else {
                        result += point
                    }
                    result
                }
            val neutral = neutralValue ?: 0.5f
            if (controlPoints.isEmpty()) {
                cachedDataPoints = FloatArray(CurveSampleCount) { neutral }
                return floatArrayOf(0f, neutral, 1f, neutral)
            }
            if (controlPoints.size == 1) {
                val value = controlPoints.first().y
                cachedDataPoints = FloatArray(CurveSampleCount) { value }
                return floatArrayOf(0f, value, 1f, value)
            }

            fun slope(index: Int): Float {
                if (index == 0 || index == controlPoints.lastIndex) return 0f
                val previous = controlPoints[(index - 1).coerceAtLeast(0)]
                val next = controlPoints[(index + 1).coerceAtMost(controlPoints.lastIndex)]
                return (next.y - previous.y) /
                        (next.x - previous.x).coerceAtLeast(0.00001f)
            }

            var segmentIndex = 0
            val sampledPoints = List(CurveSampleCount + 1) { sample ->
                val x = sample / CurveSampleCount.toFloat()
                if (x <= controlPoints.first().x) {
                    PointF(x, controlPoints.first().y)
                } else if (x >= controlPoints.last().x) {
                    PointF(x, controlPoints.last().y)
                } else {
                    while (
                        segmentIndex < controlPoints.lastIndex - 1 &&
                        controlPoints[segmentIndex + 1].x < x
                    ) {
                        segmentIndex++
                    }
                    val point1 = controlPoints[segmentIndex]
                    val point2 = controlPoints[segmentIndex + 1]
                    val segmentWidth = (point2.x - point1.x).coerceAtLeast(0.00001f)
                    val t = ((x - point1.x) / segmentWidth).coerceIn(0f, 1f)
                    val tt = t * t
                    val ttt = tt * t
                    val y = (2f * ttt - 3f * tt + 1f) * point1.y +
                            (ttt - 2f * tt + t) * segmentWidth * slope(segmentIndex) +
                            (-2f * ttt + 3f * tt) * point2.y +
                            (ttt - tt) * segmentWidth * slope(segmentIndex + 1)
                    PointF(x, y.coerceIn(0f, 1f))
                }
            }

            cachedDataPoints = sampledPoints.dropLast(1).map { it.y }.toFloatArray()
            return sampledPoints.flatMap { listOf(it.x, it.y) }.toFloatArray()
        }

        fun addPoint(
            x: Float,
            y: Float,
            minimumDistance: Float = MinimumPointDistance
        ): Int {
            if (points.size >= MaxPointCount) {
                return points.indices.minBy { abs(points[it].x - x) }
            }
            val index = points.indexOfFirst { it.x > x }
                .let { if (it == -1) points.lastIndex else it }
                .coerceAtLeast(1)
            val previousX = points[index - 1]
                .takeUnless { neutralValue != null && index - 1 == 0 }
                ?.x
            val nextX = points[index]
                .takeUnless { neutralValue != null && index == points.lastIndex }
                ?.x
            val safeMinimumDistance = minimumDistance.coerceIn(0f, 0.5f)
            var minX = previousX?.plus(safeMinimumDistance) ?: 0f
            var maxX = nextX?.minus(safeMinimumDistance) ?: 1f
            if (circularInput && points.size > 2) {
                if (index == 1) {
                    minX = maxOf(
                        minX,
                        points[points.lastIndex - 1].x + safeMinimumDistance - 1f
                    )
                }
                if (index == points.lastIndex) {
                    maxX = minOf(
                        maxX,
                        points[1].x + 1f - safeMinimumDistance
                    )
                }
            }
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

        fun reset() {
            points.clear()
            points += PointF(0f, neutralValue ?: 0f)
            points += PointF(1f, neutralValue ?: 1f)
            invalidateCache()
        }

        fun removePoint(index: Int) {
            if (index in 1 until points.lastIndex) {
                points.removeAt(index)
                invalidateCache()
            }
        }

        fun replacePoints(newPoints: List<PointF>) {
            val sortedPoints = newPoints
                .map { PointF(it.x.coerceIn(0f, 1f), it.y.coerceIn(0f, 1f)) }
                .sortedBy { it.x }
            val normalized = if (neutralValue == null) {
                sortedPoints.distinctBy { it.x }.toMutableList()
            } else {
                sortedPoints.toMutableList()
            }
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
            if (neutralValue != null && !circularInput && points.size > 2) {
                points.first().y = points[1].y
                points.last().y = points[points.lastIndex - 1].y
            }
            invalidateCache()
        }

        fun invalidateCache() {
            cachedDataPoints = null
        }

        fun copy(): CurvesValue = CurvesValue(neutralValue, circularInput).also { copy ->
            copy.replacePoints(points)
        }

        val isDefault: Boolean
            get() = points.all { point ->
                abs(point.y - (neutralValue ?: point.x)) < 0.00001f
            }

        fun toLut(size: Int = 256): FloatArray {
            val interpolated = interpolateCurve()
            if (interpolated.size < 4) return FloatArray(size) { it / (size - 1f) }

            var segment = 0
            return FloatArray(size) { index ->
                val x = index / (size - 1f)
                while (
                    segment < interpolated.size / 2 - 2 &&
                    interpolated[(segment + 1) * 2] < x
                ) {
                    segment++
                }
                val x0 = interpolated[segment * 2]
                val y0 = interpolated[segment * 2 + 1]
                val x1 = interpolated[(segment + 1) * 2]
                val y1 = interpolated[(segment + 1) * 2 + 1]
                val fraction = if (x1 > x0) ((x - x0) / (x1 - x0)).coerceIn(0f, 1f) else 0f
                y0 + (y1 - y0) * fraction
            }
        }
    }

    internal class CurvesToolValue {
        var luminanceCurve: CurvesValue = CurvesValue()
        var redCurve: CurvesValue = CurvesValue()
        var greenCurve: CurvesValue = CurvesValue()
        var blueCurve: CurvesValue = CurvesValue()
        var cyanCurve: CurvesValue = CurvesValue()
        var magentaCurve: CurvesValue = CurvesValue()
        var yellowCurve: CurvesValue = CurvesValue()
        var blackCurve: CurvesValue = CurvesValue()
        var labLuminanceCurve: CurvesValue = CurvesValue()
        var labACurve: CurvesValue = CurvesValue()
        var labBCurve: CurvesValue = CurvesValue()
        var hueVsSatCurve: CurvesValue = CurvesValue(0.5f, circularInput = true)
        var hueVsHueCurve: CurvesValue = CurvesValue(0.5f, circularInput = true)
        var hueVsLumaCurve: CurvesValue = CurvesValue(0.5f, circularInput = true)
        var lumaVsSatCurve: CurvesValue = CurvesValue(0.5f)
        var lumaVsHueCurve: CurvesValue = CurvesValue(0.5f)
        var satVsSatCurve: CurvesValue = CurvesValue(0.5f)
        var activeEditorType: ImageCurvesEditorType = ImageCurvesEditorType.RGB
        var activeType: Int = CurvesTypeLuminance

        val allCurves: List<CurvesValue>
            get() = listOf(
                luminanceCurve,
                redCurve,
                greenCurve,
                blueCurve,
                cyanCurve,
                magentaCurve,
                yellowCurve,
                blackCurve,
                labLuminanceCurve,
                labACurve,
                labBCurve,
                hueVsSatCurve,
                hueVsHueCurve,
                hueVsLumaCurve,
                lumaVsSatCurve,
                lumaVsHueCurve,
                satVsSatCurve
            )

        val activeCurveIndex: Int
            get() = activeEditorType.curveOffset + activeType

        val activeCurve: CurvesValue
            get() = allCurves[activeCurveIndex]

        fun curvesFor(type: ImageCurvesEditorType): List<CurvesValue> {
            return allCurves.subList(type.curveOffset, type.curveOffset + type.channelCount)
        }

        fun copy(): CurvesToolValue = CurvesToolValue().also {
            val copies = allCurves.map(CurvesValue::copy)
            it.luminanceCurve = copies[0]
            it.redCurve = copies[1]
            it.greenCurve = copies[2]
            it.blueCurve = copies[3]
            it.cyanCurve = copies[4]
            it.magentaCurve = copies[5]
            it.yellowCurve = copies[6]
            it.blackCurve = copies[7]
            it.labLuminanceCurve = copies[8]
            it.labACurve = copies[9]
            it.labBCurve = copies[10]
            it.hueVsSatCurve = copies[11]
            it.hueVsHueCurve = copies[12]
            it.hueVsLumaCurve = copies[13]
            it.lumaVsSatCurve = copies[14]
            it.lumaVsHueCurve = copies[15]
            it.satVsSatCurve = copies[16]
            it.activeEditorType = activeEditorType
            it.activeType = activeType
        }

        companion object {
            const val CurvesTypeLuminance: Int = 0
            const val CurvesTypeRed: Int = 1
            const val CurvesTypeGreen: Int = 2
            const val CurvesTypeBlue: Int = 3
            const val CurveCount: Int = 17
        }
    }

    companion object {
        private const val curveGranularity = 100
        private val density = Resources.getSystem().displayMetrics.density
        private const val NoPoint = -1
        private const val MaxPointCount = 16
        private const val CurveSampleCount = 256
        private const val CircularCurveSampleCount = CurveSampleCount
        private const val MinimumPointDistance = 0.04f
        private const val MinimumPointDistanceDp = 16f
        private const val ControlPointTouchRadius = 22f
        private const val DoubleTapTimeout = 300L
        private const val GridSectionCount = 8
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
