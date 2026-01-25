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

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.t8rin.curves.ImageCurvesEditor
import com.t8rin.curves.ImageCurvesEditorState
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ToneCurvesParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.LocalFilterPreviewModelProvider
import com.t8rin.imagetoolbox.core.utils.appContext

@Composable
internal fun ToneCurvesParamsItem(
    value: ToneCurvesParams,
    filter: UiFilter<ToneCurvesParams>,
    onFilterChange: (value: ToneCurvesParams) -> Unit,
    previewOnly: Boolean
) {
    val editorState: MutableState<ImageCurvesEditorState> =
        remember { mutableStateOf(ImageCurvesEditorState(value.controlPoints)) }

    Box(
        modifier = Modifier.padding(8.dp)
    ) {
        var bitmap by remember {
            mutableStateOf<Bitmap?>(null)
        }

        val previewModel = LocalFilterPreviewModelProvider.current.preview

        LaunchedEffect(previewModel) {
            bitmap = appContext.imageLoader.execute(
                ImageRequest.Builder(appContext)
                    .data(previewModel.data)
                    .build()
            ).image?.toBitmap()
        }

        ImageCurvesEditor(
            bitmap = bitmap,
            state = editorState.value,
            curvesSelectionText = {
                Text(
                    text = when (it) {
                        0 -> stringResource(R.string.all)
                        1 -> stringResource(R.string.color_red)
                        2 -> stringResource(R.string.color_green)
                        3 -> stringResource(R.string.color_blue)
                        else -> ""
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            },
            imageObtainingTrigger = false,
            onImageObtained = { },
            //shape = ShapeDefaults.extraSmall,
            containerModifier = Modifier.fillMaxWidth(),
            onStateChange = {
                onFilterChange(
                    ToneCurvesParams(
                        controlPoints = it.controlPoints
                    )
                )
            }
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