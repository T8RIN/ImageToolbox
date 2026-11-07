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

package com.t8rin.collages.gesture

import android.graphics.Matrix
import android.view.MotionEvent
import kotlin.math.atan2
import kotlin.math.hypot

internal class MultiTouchHandler {

    private val transform = Matrix()
    private var firstPointer = -1
    private var secondPointer = -1
    private var focusX = 0f
    private var focusY = 0f
    private var span = 0f
    private var angle = 0f
    private var rotationEnabled = false
    private var minScale = 0.01f
    private var maxScale = 100f

    var matrix: Matrix
        get() = transform
        set(value) {
            transform.set(value)
        }

    fun setEnableRotation(enabled: Boolean) {
        rotationEnabled = enabled
    }

    fun setScaleRange(min: Float, max: Float) {
        minScale = min
        maxScale = max
    }

    fun touch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> rebase(event)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                firstPointer = -1
                secondPointer = -1
            }

            MotionEvent.ACTION_MOVE -> {
                val first = event.findPointerIndex(firstPointer)
                if (first < 0) return
                val second = event.findPointerIndex(secondPointer)
                val x =
                    if (second >= 0) (event.getX(first) + event.getX(second)) / 2f else event.getX(
                        first
                    )
                val y =
                    if (second >= 0) (event.getY(first) + event.getY(second)) / 2f else event.getY(
                        first
                    )
                if (second >= 0) {
                    val dx = event.getX(second) - event.getX(first)
                    val dy = event.getY(second) - event.getY(first)
                    val distance = hypot(dx, dy)
                    val rotation = atan2(dy, dx)
                    if (span > 1f && distance > 1f) {
                        val values = FloatArray(9)
                        transform.getValues(values)
                        val currentScale = hypot(values[0], values[3]).coerceAtLeast(0.0001f)
                        val scale = (currentScale * distance / span)
                            .coerceIn(minScale, maxScale) / currentScale
                        transform.postScale(scale, scale, focusX, focusY)
                        if (rotationEnabled) {
                            transform.postRotate(
                                Math.toDegrees((rotation - angle).toDouble()).toFloat(),
                                focusX, focusY
                            )
                        }
                    }
                    span = distance
                    angle = rotation
                }
                transform.postTranslate(x - focusX, y - focusY)
                focusX = x
                focusY = y
            }
        }
    }

    private fun rebase(event: MotionEvent) {
        val pointers = (0 until event.pointerCount).filter {
            event.actionMasked != MotionEvent.ACTION_POINTER_UP || it != event.actionIndex
        }
        val first = pointers.firstOrNull() ?: return
        val second = pointers.getOrNull(1)
        firstPointer = event.getPointerId(first)
        secondPointer = second?.let(event::getPointerId) ?: -1
        focusX = event.getX(first)
        focusY = event.getY(first)
        span = 0f
        if (second != null) {
            val dx = event.getX(second) - focusX
            val dy = event.getY(second) - focusY
            focusX += dx / 2f
            focusY += dy / 2f
            span = hypot(dx, dy)
            angle = atan2(dy, dx)
        }
    }
}