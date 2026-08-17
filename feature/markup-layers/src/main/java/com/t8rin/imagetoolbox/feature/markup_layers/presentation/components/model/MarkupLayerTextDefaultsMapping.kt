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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model

import com.t8rin.imagetoolbox.core.domain.model.Outline
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.MarkupLayerTextDefaults
import com.t8rin.imagetoolbox.core.settings.presentation.model.asFontType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.DropShadow
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.TextGeometricTransform

internal fun MarkupLayerTextDefaults.toLayerType(text: String): LayerType.Text = LayerType.Text(
    color = color,
    size = size,
    font = DomainFontFamily.fromString(font)?.asFontType(),
    backgroundColor = backgroundColor,
    text = text,
    decorations = decorations.map {
        LayerType.Text.Decoration.valueOf(it.name)
    },
    outline = outline?.let {
        Outline(
            color = it.color,
            width = it.width
        )
    },
    alignment = LayerType.Text.Alignment.valueOf(alignment.name),
    geometricTransform = geometricTransform?.let {
        TextGeometricTransform(
            scaleX = it.scaleX,
            skewX = it.skewX
        )
    },
    shadow = shadow?.let {
        DropShadow(
            color = it.color,
            offsetX = it.offsetX,
            offsetY = it.offsetY,
            blurRadius = it.blurRadius
        )
    }
)
