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
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.text.TextPaint
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min


internal class PhotoFilterCurvesControl @JvmOverloads constructor(
    context: Context?,
    value: CurvesToolValue = CurvesToolValue()
) : View(context) {
    private var activeSegment = CurvesSegmentNone
    private var isMoving = false
    private var checkForMoving = true
    private var lastX = 0f
    private var lastY = 0f
    private val actualArea = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintDash = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintCurve = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintNotActiveCurve = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private var delegate: PhotoFilterCurvesControlDelegate? = null
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

    init {
        setWillNotDraw(false)

        curveValue = value

        paint.color = guidelinesColor
        paint.strokeWidth = dp(1f).toFloat()
        paint.style = Paint.Style.STROKE

        paintDash.color = defaultCurveColor
        paintDash.strokeWidth = dp(2f).toFloat()
        paintDash.style = Paint.Style.STROKE
        paintDash.strokeCap = Paint.Cap.ROUND

        paintCurve.color = lumaCurveColor
        paintCurve.strokeWidth = dp(2f).toFloat()
        paintCurve.style = Paint.Style.STROKE
        paintCurve.strokeCap = Paint.Cap.ROUND
        paintCurve.setShadowLayer(2f, 0f, 0f, Color.Black.copy(0.5f).toArgb())

        paintNotActiveCurve.color = lumaCurveColor
        paintNotActiveCurve.strokeWidth = dp(1f).toFloat()
        paintNotActiveCurve.style = Paint.Style.STROKE
        paintNotActiveCurve.strokeCap = Paint.Cap.ROUND
        paintNotActiveCurve.setShadowLayer(1f, 0f, 0f, Color.Black.copy(0.5f).toArgb())

        textPaint.color = Color(defaultCurveColor).copy(1f).toArgb()
        textPaint.textSize = dp(13f).toFloat()
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
        guidelinesColor: Int
    ) {
        this.lumaCurveColor = lumaCurveColor
        this.redCurveColor = redCurveColor
        this.greenCurveColor = greenCurveColor
        this.blueCurveColor = blueCurveColor
        this.guidelinesColor = guidelinesColor
        this.defaultCurveColor = defaultCurveColor
        paint.color = guidelinesColor
        paintDash.color = defaultCurveColor
        textPaint.color = Color(defaultCurveColor).copy(1f).toArgb()

        invalidate()
    }

    fun setDelegate(photoFilterCurvesControlDelegate: PhotoFilterCurvesControlDelegate?) {
        delegate = photoFilterCurvesControlDelegate
    }

    fun setActualArea(x: Float, y: Float, width: Float, height: Float) {
        actualArea.x = x
        actualArea.y = y
        actualArea.width = width
        actualArea.height = height
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked

        when (action) {
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_DOWN -> {
                if (event.pointerCount == 1) {
                    if (checkForMoving && !isMoving) {
                        val locationX = event.x
                        val locationY = event.y
                        lastX = locationX
                        lastY = locationY
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
                    handlePan(GestureStateEnded, event)
                    isMoving = false
                }
                checkForMoving = true
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
                selectSegmentWithPoint(locationX)
            }

            GestureStateChanged -> {
                val delta = min(2.0, ((lastY - locationY) / 8.0f).toDouble())
                    .toFloat()

                var curveValue: CurvesValue? = null
                when (this.curveValue.activeType) {
                    CurvesToolValue.CurvesTypeLuminance -> curveValue =
                        this.curveValue.luminanceCurve

                    CurvesToolValue.CurvesTypeRed -> curveValue = this.curveValue.redCurve
                    CurvesToolValue.CurvesTypeGreen -> curveValue = this.curveValue.greenCurve
                    CurvesToolValue.CurvesTypeBlue -> curveValue = this.curveValue.blueCurve
                    else -> {}
                }
                checkNotNull(curveValue)
                when (activeSegment) {
                    CurvesSegmentBlacks -> curveValue.blacksLevel =
                        max(0.0, min(100.0, (curveValue.blacksLevel + delta).toDouble()))
                            .toFloat()

                    CurvesSegmentShadows -> curveValue.shadowsLevel =
                        max(0.0, min(100.0, (curveValue.shadowsLevel + delta).toDouble()))
                            .toFloat()

                    CurvesSegmentMidtones -> curveValue.midtonesLevel =
                        max(0.0, min(100.0, (curveValue.midtonesLevel + delta).toDouble()))
                            .toFloat()

                    CurvesSegmentHighlights -> curveValue.highlightsLevel =
                        max(0.0, min(100.0, (curveValue.highlightsLevel + delta).toDouble()))
                            .toFloat()

                    CurvesSegmentWhites -> curveValue.whitesLevel =
                        max(0.0, min(100.0, (curveValue.whitesLevel + delta).toDouble()))
                            .toFloat()

                    else -> {}
                }
                invalidate()

                if (delegate != null) {
                    delegate!!.valueChanged()
                }

                lastX = locationX
                lastY = locationY
            }

            GestureStateEnded, GestureStateCancelled, GestureStateFailed -> {
                unselectSegments()
            }

            else -> {}
        }
    }

    private fun selectSegmentWithPoint(pointX: Float) {
        var pointx = pointX
        if (activeSegment != CurvesSegmentNone) {
            return
        }
        val segmentWidth = actualArea.width / 5.0f
        pointx -= actualArea.x
        activeSegment = floor(((pointx / segmentWidth) + 1).toDouble()).toInt()
    }

    private fun unselectSegments() {
        if (activeSegment == CurvesSegmentNone) {
            return
        }
        activeSegment = CurvesSegmentNone
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val segmentWidth = actualArea.width / 5.0f

        canvas.drawRect(
            RectF(
                actualArea.x,
                actualArea.y + actualArea.height - dp(20f),
                actualArea.x + actualArea.width,
                actualArea.y + actualArea.height
            ),
            Paint().apply {
                color = Color.Black.copy(0.5f).toArgb()
            }
        )

        for (i in 0..3) {
            canvas.drawLine(
                actualArea.x + segmentWidth + i * segmentWidth,
                actualArea.y,
                actualArea.x + segmentWidth + i * segmentWidth,
                actualArea.y + actualArea.height,
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
        for (a in 0..4) {
            checkNotNull(curvesValue)
            val str = when (a) {
                0 -> String.format(Locale.US, "%.2f", curvesValue.blacksLevel / 100.0f)
                1 -> String.format(Locale.US, "%.2f", curvesValue.shadowsLevel / 100.0f)
                2 -> String.format(Locale.US, "%.2f", curvesValue.midtonesLevel / 100.0f)
                3 -> String.format(Locale.US, "%.2f", curvesValue.highlightsLevel / 100.0f)
                4 -> String.format(Locale.US, "%.2f", curvesValue.whitesLevel / 100.0f)
                else -> ""
            }
            val width = textPaint.measureText(str)
            canvas.drawText(
                str,
                actualArea.x + (segmentWidth - width) / 2 + segmentWidth * a,
                actualArea.y + actualArea.height - dp(4f),
                textPaint
            )
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
                invalidate()
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
        invalidate()
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

    fun interface PhotoFilterCurvesControlDelegate {
        fun valueChanged()
    }

    internal class CurvesValue {
        var blacksLevel: Float = 0.0f
        var shadowsLevel: Float = 25.0f
        var midtonesLevel: Float = 50.0f
        var highlightsLevel: Float = 75.0f
        var whitesLevel: Float = 100.0f

        private var cachedDataPoints: FloatArray? = null

        val dataPoints: FloatArray?
            get() {
                if (cachedDataPoints == null) {
                    interpolateCurve()
                }
                return cachedDataPoints
            }

        fun interpolateCurve(): FloatArray {
            val points = floatArrayOf(
                -0.001f, blacksLevel / 100.0f,
                0.0f, blacksLevel / 100.0f,
                0.25f, shadowsLevel / 100.0f,
                0.5f, midtonesLevel / 100.0f,
                0.75f, highlightsLevel / 100.0f,
                1f, whitesLevel / 100.0f,
                1.001f, whitesLevel / 100.0f
            )

            val dataPoints = ArrayList<Float>(100)
            val interpolatedPoints = ArrayList<Float>(100)

            interpolatedPoints.add(points[0])
            interpolatedPoints.add(points[1])

            for (index in 1 until points.size / 2 - 2) {
                val point0x = points[(index - 1) * 2]
                val point0y = points[(index - 1) * 2 + 1]
                val point1x = points[index * 2]
                val point1y = points[index * 2 + 1]
                val point2x = points[(index + 1) * 2]
                val point2y = points[(index + 1) * 2 + 1]
                val point3x = points[(index + 2) * 2]
                val point3y = points[(index + 2) * 2 + 1]


                for (i in 1 until curveGranularity) {
                    val t = i.toFloat() * (1.0f / curveGranularity.toFloat())
                    val tt = t * t
                    val ttt = tt * t

                    val pix =
                        0.5f * (2 * point1x + (point2x - point0x) * t + (2 * point0x - 5 * point1x + 4 * point2x - point3x) * tt + (3 * point1x - point0x - 3 * point2x + point3x) * ttt)
                    var piy =
                        0.5f * (2 * point1y + (point2y - point0y) * t + (2 * point0y - 5 * point1y + 4 * point2y - point3y) * tt + (3 * point1y - point0y - 3 * point2y + point3y) * ttt)

                    piy = max(0.0, min(1.0, piy.toDouble())).toFloat()

                    if (pix > point0x) {
                        interpolatedPoints.add(pix)
                        interpolatedPoints.add(piy)
                    }

                    if ((i - 1) % curveDataStep == 0) {
                        dataPoints.add(piy)
                    }
                }
                interpolatedPoints.add(point2x)
                interpolatedPoints.add(point2y)
            }
            interpolatedPoints.add(points[12])
            interpolatedPoints.add(points[13])

            cachedDataPoints = FloatArray(dataPoints.size)
            for (a in cachedDataPoints!!.indices) {
                cachedDataPoints!![a] = dataPoints[a]
            }
            val retValue = FloatArray(interpolatedPoints.size)
            for (a in retValue.indices) {
                retValue[a] = interpolatedPoints[a]
            }
            return retValue
        }

        val isDefault: Boolean
            get() = abs((blacksLevel - 0).toDouble()) < 0.00001 && abs((shadowsLevel - 25).toDouble()) < 0.00001 && abs(
                (midtonesLevel - 50).toDouble()
            ) < 0.00001 && abs((highlightsLevel - 75).toDouble()) < 0.00001 && abs(
                (whitesLevel - 100).toDouble()
            ) < 0.00001
    }

    internal class CurvesToolValue {
        var luminanceCurve: CurvesValue = CurvesValue()
        var redCurve: CurvesValue = CurvesValue()
        var greenCurve: CurvesValue = CurvesValue()
        var blueCurve: CurvesValue = CurvesValue()
        var activeType: Int = CurvesTypeLuminance

        companion object {
            const val CurvesTypeLuminance: Int = 0
            const val CurvesTypeRed: Int = 1
            const val CurvesTypeGreen: Int = 2
            const val CurvesTypeBlue: Int = 3
        }
    }

    companion object {
        private const val curveGranularity = 100
        private const val curveDataStep = 2
        private val density = Resources.getSystem().displayMetrics.density
        private const val CurvesSegmentNone = 0
        private const val CurvesSegmentBlacks = 1
        private const val CurvesSegmentShadows = 2
        private const val CurvesSegmentMidtones = 3
        private const val CurvesSegmentHighlights = 4
        private const val CurvesSegmentWhites = 5
        private const val GestureStateBegan = 1
        private const val GestureStateChanged = 2
        private const val GestureStateEnded = 3
        private const val GestureStateCancelled = 4
        private const val GestureStateFailed = 5
        fun dp(value: Float): Int {
            if (value == 0f) {
                return 0
            }
            return ceil((density * value).toDouble())
                .toInt()
        }
    }
}