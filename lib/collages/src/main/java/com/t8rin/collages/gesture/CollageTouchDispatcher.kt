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

import android.view.InputDevice
import android.view.MotionEvent
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerId
import com.t8rin.collages.view.FramePhotoLayout

internal class CollageTouchDispatcher(private val view: FramePhotoLayout) {
    private val pointers = linkedMapOf<PointerId, Int>()
    private var downTime = 0L
    private var lastEvent: MotionEvent? = null

    fun dispatch(event: PointerEvent) {
        val changes = event.changes
        if (pointers.isEmpty()) downTime = changes.first().uptimeMillis
        changes.filter { it.pressed && !it.previousPressed }.forEach {
            pointers[it.id] = (0..31).first { id -> id !in pointers.values }
        }
        val active = changes.filter { it.id in pointers }
        if (active.isEmpty()) return
        val down = active.indexOfFirst { it.pressed && !it.previousPressed }
        val up = active.indexOfFirst { !it.pressed && it.previousPressed }
        val action = when {
            down >= 0 -> if (active.size == 1) MotionEvent.ACTION_DOWN
            else MotionEvent.ACTION_POINTER_DOWN or (down shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)

            up >= 0 -> if (active.size == 1) MotionEvent.ACTION_UP
            else MotionEvent.ACTION_POINTER_UP or (up shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)

            else -> MotionEvent.ACTION_MOVE
        }
        val motion = MotionEvent.obtain(
            downTime, active.first().uptimeMillis, action, active.size,
            Array(active.size) { index ->
                MotionEvent.PointerProperties().apply {
                    id = pointers.getValue(active[index].id)
                    toolType = MotionEvent.TOOL_TYPE_FINGER
                }
            },
            Array(active.size) { index ->
                MotionEvent.PointerCoords().apply {
                    x = active[index].position.x
                    y = active[index].position.y
                    pressure = if (active[index].pressed) 1f else 0f
                    size = 1f
                }
            },
            0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0
        )
        try {
            view.dispatchTouchEvent(motion)
            lastEvent?.recycle()
            lastEvent = if (action == MotionEvent.ACTION_UP) null else MotionEvent.obtain(motion)
        } finally {
            motion.recycle()
        }
        active.filterNot { it.pressed }.forEach { pointers.remove(it.id) }
    }

    fun cancel() {
        lastEvent?.let {
            it.action = MotionEvent.ACTION_CANCEL
            view.dispatchTouchEvent(it)
            it.recycle()
        }
        lastEvent = null
        pointers.clear()
    }
}