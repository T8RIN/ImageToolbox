/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.ui.widget.controls.ResizeImageField
import kotlinx.coroutines.delay

@Composable
internal fun IntegerSizeParamsItem(
    value: IntegerSize,
    filter: UiFilter<IntegerSize>,
    onFilterChange: (value: IntegerSize) -> Unit,
    previewOnly: Boolean
) {
    var width by remember(value) { mutableIntStateOf(value.width) }
    var height by remember(value) { mutableIntStateOf(value.height) }

    val imageInfo by remember(width, height) {
        derivedStateOf {
            ImageInfo(
                width = width,
                height = height
            )
        }
    }

    LaunchedEffect(imageInfo) {
        delay(500)
        onFilterChange(
            IntegerSize(
                width = imageInfo.width,
                height = imageInfo.height
            )
        )
    }

    ResizeImageField(
        modifier = Modifier.padding(8.dp),
        imageInfo = imageInfo,
        originalSize = null,
        onWidthChange = {
            width = it
        },
        onHeightChange = {
            height = it
        },
        enabled = !previewOnly
    )
}