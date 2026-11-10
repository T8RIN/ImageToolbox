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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.TextSearch
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.GalleryOcrState
import com.t8rin.imagetoolbox.feature.settings.presentation.GalleryIndexSettingItem

@Composable
internal fun GalleryOcrSettingItem(
    state: GalleryOcrState,
    onValueChange: (Boolean) -> Unit
) {
    val enabled = LocalSettingsState.current.isGalleryOcrEnabled
    GalleryIndexSettingItem(
        title = stringResource(R.string.gallery_ocr),
        subtitle = stringResource(R.string.gallery_ocr_sub) + if (!state.modelsReady) {
            "\n" + stringResource(R.string.gallery_ocr_download_hint)
        } else "",
        checked = enabled,
        progress = state.downloadProgress.takeIf { enabled && state.isDownloading },
        permissionMessage = R.string.gallery_ocr_permission,
        onValueChange = onValueChange,
        shape = ShapeDefaults.bottom,
        icon = Icons.Outlined.TextSearch
    )
}