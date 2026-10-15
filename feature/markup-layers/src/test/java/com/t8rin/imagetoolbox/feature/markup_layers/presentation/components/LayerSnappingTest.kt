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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.LayerBounds
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class LayerSnappingTest {

    private val canvasSize = IntegerSize(400, 200)
    private val canvasBounds = LayerBounds(-200f, -100f, 200f, 100f)

    @Test
    fun snapsToBothCanvasCenterAxes() {
        val result = calculateLayerSnapping(
            bounds = LayerBounds(-17f, -34f, 23f, 26f),
            targets = listOf(canvasBounds),
            threshold = 6f
        )

        assertEquals(Offset(-3f, 4f), result.offset)
        assertEquals(0f, result.verticalGuide)
        assertEquals(0f, result.horizontalGuide)
    }

    @Test
    fun snapsToCanvasEdges() {
        val result = calculateLayerSnapping(
            bounds = LayerBounds(-197f, 55f, -157f, 95f),
            targets = listOf(canvasBounds),
            threshold = 6f
        )

        assertEquals(Offset(-3f, 5f), result.offset)
        assertEquals(-200f, result.verticalGuide)
        assertEquals(100f, result.horizontalGuide)
    }

    @Test
    fun choosesNearestLayerGuideIndependentlyForEachAxis() {
        val result = calculateLayerSnapping(
            bounds = LayerBounds(35f, 12f, 55f, 32f),
            targets = listOf(canvasBounds, LayerBounds(56f, -30f, 86f, 30f)),
            threshold = 6f
        )

        assertEquals(Offset(1f, -2f), result.offset)
        assertEquals(56f, result.verticalGuide)
        assertEquals(30f, result.horizontalGuide)
    }

    @Test
    fun leavesPositionUnchangedOutsideThreshold() {
        val result = calculateLayerSnapping(
            bounds = LayerBounds(20f, 20f, 40f, 40f),
            targets = listOf(canvasBounds),
            threshold = 6f
        )

        assertEquals(Offset.Zero, result.offset)
        assertNull(result.verticalGuide)
        assertNull(result.horizontalGuide)
    }

    @Test
    fun doesNotSnapToItselfOrHiddenLayers() {
        val layer = layer(offset = Offset(50f, 40f))
        val hidden = layer(offset = Offset(52f, 42f)).also { it.state.isVisible = false }
        val transparent = layer(offset = Offset(51f, 41f)).also { it.state.alpha = 0f }
        val result = layer.snapToLayers(listOf(layer, hidden, transparent), canvasSize, 6f)

        assertEquals(Offset(50f, 40f), layer.state.offset)
        assertNull(result?.verticalGuide)
        assertNull(result?.horizontalGuide)
    }

    @Test
    fun usesRotatedScaledBounds() {
        val layer = layer(offset = Offset(157f, 40f)).also {
            it.state.contentSize = IntSize(20, 40)
            it.state.rotation = 90f
            it.state.scale = 2f
        }
        val result = layer.snapToLayers(listOf(layer), canvasSize, 6f)

        assertEquals(160f, layer.state.offset.x, 0.001f)
        assertEquals(200f, result?.verticalGuide)
    }

    @Test
    fun snapsGroupUsingVisibleChildrenBounds() {
        val group = layer(offset = Offset(0f, 4f)).copy(
            groupedLayers = listOf(
                layer(offset = Offset(10f, 0f)),
                layer(offset = Offset(24f, 0f)),
                layer(offset = Offset(100f, 60f)).also { it.state.isVisible = false }
            )
        )
        val result = group.snapToLayers(listOf(group), canvasSize, 6f)

        assertEquals(Offset(0f, 0f), group.state.offset)
        assertEquals(0f, result?.horizontalGuide)
        assertNull(result?.verticalGuide)
    }

    @Test
    fun hidesGuideWhenCanvasBoundsPreventAlignment() {
        val layer = layer(offset = Offset(185f, 40f))
        val outside = layer(offset = Offset(226f, 40f))
        val result = layer.snapToLayers(listOf(layer, outside), canvasSize, 6f)

        assertEquals(180f, layer.state.offset.x, 0.001f)
        assertNull(result?.verticalGuide)
    }

    @Test
    fun accumulatedSmallMovesReleaseSnapping() {
        val layer = layer(offset = Offset(0f, 40f))
        var correction = Offset.Zero
        repeat(7) {
            layer.state.applyGlobalChanges(
                parentMaxWidth = canvasSize.width,
                parentMaxHeight = canvasSize.height,
                contentSize = layer.state.contentSize,
                cornerRadiusPercent = 0,
                zoomChange = 1f,
                offsetChange = Offset(1f, 0f) - correction,
                rotationChange = 0f
            )
            val result = layer.snapToLayers(listOf(layer), canvasSize, 6f)
            assertNotNull(result)
            correction = result!!.offset
            if (it < 6) assertEquals(0f, layer.state.offset.x, 0.001f)
        }

        assertEquals(7f, layer.state.offset.x, 0.001f)
    }

    private fun layer(offset: Offset) = UiMarkupLayer(
        type = LayerType.Shape.Default,
        state = EditBoxState(
            offset = offset,
            canvasSize = canvasSize,
            contentSize = IntSize(40, 40)
        )
    )
}