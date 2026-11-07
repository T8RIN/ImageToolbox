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

package com.t8rin.imagetoolbox.collage_maker.domain

import com.t8rin.collages.model.CollageSlotStyle
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CollageStyleTest {
    private val gradient = GradientFill(angle = 45f, type = GradientType.Radial)
    private val photo = CollageSlotStyle(
        cornerRadius = 27f, borderWidth = 9f, borderColor = 0xff456789.toInt(),
        fit = true, shadowRadius = 12f, shadowColor = 0x80556677.toInt()
    )
    private val style = CollageStyle(
        spacing = 4f, outerPadding = 18f, cornerRadius = 3f,
        borderWidth = 2f, borderColor = -1, backgroundColor = -1,
        borderGradient = gradient, slotStyles = listOf(photo),
        slotBorderGradients = listOf(null)
    )

    @Test
    fun individualSettingsAndSolidOverrideSurviveApplyingStyle() {
        val result = style.applyTo(CollageParams(), 1)
        assertEquals(photo, result.slotStyles.single())
        assertNull(result.slotBorderGradients.single())
        assertEquals(gradient, result.borderGradient)
    }

    @Test
    fun additionalPhotosUseGlobalStyleAndKeepExportSettings() {
        val params = CollageParams(outputScaleRatio = .8f, disableRotation = false)
        val result = style.applyTo(params, 3)
        assertEquals(listOf(null, gradient, gradient), result.slotBorderGradients)
        assertEquals(2f, result.slotStyles[2].borderWidth)
        assertNull(result.slotStyles[2].cornerRadius)
        assertEquals(.8f, result.outputScaleRatio)
        assertEquals(false, result.disableRotation)
    }

    @Test
    fun smallerCollageDropsSettingsForMissingSlots() {
        val result = style.copy(
            slotStyles = listOf(photo, photo.copy(fit = false)),
            slotBorderGradients = listOf(gradient, null)
        ).applyTo(CollageParams(), 1)
        assertEquals(listOf(photo), result.slotStyles)
        assertEquals(listOf(gradient), result.slotBorderGradients)
    }

    @Test
    fun movingPhotoTransfersIndividualStyleAndExplicitSolidBorder() {
        val params = style.applyTo(CollageParams(), 3)
        val moved = params.swapPhotos(0, 2, 3)
        assertEquals(photo, moved.slotStyles[2])
        assertNull(moved.slotBorderGradients[2])
        assertEquals(params.slotStyles[2], moved.slotStyles[0])
        assertEquals(gradient, moved.slotBorderGradients[0])
        assertEquals(params, moved.swapPhotos(2, 0, 3))
    }

    @Test
    fun movingPhotoWithPartialOverridesKeepsGlobalDefaults() {
        val params = CollageParams(
            borderWidth = 6f, borderGradient = gradient,
            slotStyles = listOf(photo), slotBorderGradients = listOf(null)
        )
        val moved = params.swapPhotos(0, 2, 3)
        assertEquals(photo, moved.slotStyles[2])
        assertEquals(6f, moved.slotStyles[0].borderWidth)
        assertEquals(listOf(gradient, gradient, null), moved.slotBorderGradients)
        assertEquals(params, params.swapPhotos(-1, 2, 3))
    }
}