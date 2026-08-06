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

package com.t8rin.imagetoolbox.feature.crop.presentation.components

import com.t8rin.crop.advanced.compose.AdvancedCropperState
import com.t8rin.cropper.ImageCropperState
import com.t8rin.opencv_tools.free_corners_crop.compose.FreeCornersCropperState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CropperStateTest {

    @Test
    fun aspectRatioActionSupportsRepeatedUndoRedo() {
        val state = CropperState(
            advancedCropperState = AdvancedCropperState(),
            imageCropperState = ImageCropperState(),
            freeCornersCropperState = FreeCornersCropperState()
        )
        var aspectRatio = 1

        state.setActiveCropType(CropType.NoRotation)
        state.recordExternalAction(
            previousCropType = CropType.NoRotation,
            currentCropType = CropType.NoRotation,
            undo = { aspectRatio = 0 },
            redo = { aspectRatio = 1 }
        )

        repeat(3) {
            assertTrue(state.canUndo)
            assertFalse(state.canRedo)
            state.undo()
            assertEquals(0, aspectRatio)
            assertFalse(state.canUndo)
            assertTrue(state.canRedo)

            state.redo()
            assertEquals(1, aspectRatio)
        }
    }
}
