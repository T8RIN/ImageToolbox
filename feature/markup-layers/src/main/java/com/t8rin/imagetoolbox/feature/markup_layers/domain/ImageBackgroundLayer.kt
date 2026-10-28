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

package com.t8rin.imagetoolbox.feature.markup_layers.domain

import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize

internal fun ProjectBackground.Image.asLayer(): MarkupLayer = MarkupLayer(
    type = LayerType.Picture.Image(uri.toUri()),
    position = LayerPosition(
        currentCanvasSize = IntegerSize.Zero,
        coerceToBounds = true,
        isVisible = true
    ),
    isLocked = true,
    isBackground = true
)

internal fun MarkupProject.withImageBackgroundLayer(): MarkupProject = copy(
    layers = layers.withImageBackgroundLayer(background),
    lastLayers = lastLayers.withImageBackgroundLayer(background),
    undoneLayers = undoneLayers.withImageBackgroundLayer(background),
    history = history.map { it.withImageBackgroundLayer() },
    redoHistory = redoHistory.map { it.withImageBackgroundLayer() }
)

private fun MarkupProjectHistorySnapshot.withImageBackgroundLayer() = copy(
    layers = layers.withImageBackgroundLayer(background)
)

private fun List<MarkupLayer>.withImageBackgroundLayer(
    background: ProjectBackground
): List<MarkupLayer> =
    if (background is ProjectBackground.Image && none(MarkupLayer::isBackground)) {
        listOf(background.asLayer()) + this
    } else this