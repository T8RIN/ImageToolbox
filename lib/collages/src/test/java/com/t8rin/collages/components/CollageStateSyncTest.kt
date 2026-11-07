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

package com.t8rin.collages.components

import com.t8rin.collages.model.CollageImageState
import com.t8rin.collages.model.CollageState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CollageStateSyncTest {
    private val image = CollageImageState(
        index = 0,
        uri = "test",
        matrixValues = listOf(2f, 0f, -100f, 0f, 2f, -100f, 0f, 0f, 1f),
        viewWidth = 400f,
        viewHeight = 400f,
        userAllowedEmptySpace = false
    )
    private val initial = CollageState("test", listOf(image), emptyList())
    private val moved = initial.copy(
        images = listOf(image.copy(matrixValues = listOf(2f, 0f, -150f, 0f, 2f, -100f, 0f, 0f, 1f)))
    )

    @Test
    fun delayedUiUpdateDoesNotRestoreStateFromBeforeGesture() {
        val sync = CollageStateSync()
        assertEquals(initial, sync.update(initial))
        sync.current = initial
        sync.current = moved
        assertNull(sync.update(initial))
        assertEquals(moved, sync.current)
        assertNull(sync.update(moved))
    }

    @Test
    fun undoAndRedoStillRestoreTheirRequestedState() {
        val sync = CollageStateSync()
        sync.update(initial)
        sync.current = moved
        sync.update(moved)
        assertEquals(initial, sync.update(initial))
        sync.current = initial
        assertEquals(moved, sync.update(moved))
    }
}