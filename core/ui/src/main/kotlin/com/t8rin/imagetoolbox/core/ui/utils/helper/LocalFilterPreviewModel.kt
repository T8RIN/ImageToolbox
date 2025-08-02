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

package com.t8rin.imagetoolbox.core.ui.utils.helper

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.flexibleScale

val LocalFilterPreviewModelProvider =
    compositionLocalOf<FilterPreviewProvider> { error("FilterPreviewProvider not present") }

@Composable
fun rememberFilterPreviewProvider(
    preview: ImageModel,
    canSetDynamicFilterPreview: Boolean
): FilterPreviewProvider {
    return remember(preview) {
        FilterPreviewProviderImpl(
            default = preview,
            canSetDynamicFilterPreview = canSetDynamicFilterPreview
        )
    }.also {
        LaunchedEffect(it, canSetDynamicFilterPreview) {
            it._canSetDynamicFilterPreview.value = canSetDynamicFilterPreview
        }
    }
}

interface FilterPreviewProvider {
    val canSetDynamicFilterPreview: Boolean

    val preview: ImageModel

    @Composable
    fun ProvidePreview(preview: Any?)
}

private class FilterPreviewProviderImpl(
    private val default: ImageModel,
    canSetDynamicFilterPreview: Boolean
) : FilterPreviewProvider {

    private val _preview = mutableStateOf(default)

    override val preview: ImageModel by _preview

    val _canSetDynamicFilterPreview = mutableStateOf(canSetDynamicFilterPreview)

    override val canSetDynamicFilterPreview by _canSetDynamicFilterPreview

    private var updatesCount: Int = 0

    override fun toString(): String {
        return "FilterPreviewProviderImpl(preview = $preview, canSetDynamicFilterPreview = $canSetDynamicFilterPreview, updatesCount = $updatesCount)"
    }

    @Composable
    override fun ProvidePreview(preview: Any?) {
        DisposableEffect(Unit) {
            onDispose {
                _preview.value = default
            }
        }

        LaunchedEffect(preview, canSetDynamicFilterPreview) {
            updatesCount++

            _preview.value = if (canSetDynamicFilterPreview) {
                when (preview) {
                    is ImageModel -> preview
                    is Bitmap -> ImageModel(preview.flexibleScale(300))
                    is Any -> ImageModel(preview)
                    else -> default
                }
            } else {
                default
            }
        }
    }

}

@Composable
fun ProvideFilterPreview(preview: Any?) {
    LocalFilterPreviewModelProvider.current.ProvidePreview(preview)
}