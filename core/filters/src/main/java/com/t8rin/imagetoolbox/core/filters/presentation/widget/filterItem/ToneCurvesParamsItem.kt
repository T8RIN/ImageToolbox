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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.dp
import com.t8rin.curves.ImageCurvesEditor
import com.t8rin.curves.ImageCurvesEditorLayout
import com.t8rin.curves.ImageCurvesEditorState
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ToneCurvesParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter

@Composable
internal fun ToneCurvesParamsItem(
    value: ToneCurvesParams,
    filter: UiFilter<ToneCurvesParams>,
    onFilterChange: (value: ToneCurvesParams) -> Unit,
    previewOnly: Boolean
) {
    var editorState by remember {
        mutableStateOf(ImageCurvesEditorState(value.controlPoints))
    }

    LaunchedEffect(value.controlPoints) {
        if (editorState.controlPoints != value.controlPoints) {
            editorState = editorState.copy(controlPoints = value.controlPoints)
        }
    }

    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        ImageCurvesEditor(
            bitmap = remember { ImageBitmap(1, 1).asAndroidBitmap() },
            state = editorState,
            imageObtainingTrigger = false,
            onImageObtained = { },
            layout = ImageCurvesEditorLayout.Separate,
            showImagePreview = false,
            containerModifier = Modifier.fillMaxWidth(),
            onStateChange = {
                onFilterChange(
                    ToneCurvesParams(
                        controlPoints = it.controlPoints
                    )
                )
            },
            showHistogram = false
        )

        if (previewOnly) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = Color.Transparent,
                content = {}
            )
        }
    }
}