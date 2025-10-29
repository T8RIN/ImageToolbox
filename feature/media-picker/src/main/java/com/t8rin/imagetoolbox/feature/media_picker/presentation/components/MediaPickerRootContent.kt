/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import android.content.Intent
import androidx.compose.runtime.Composable
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.ui.utils.provider.ImageToolboxCompositionLocals
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent

@Composable
internal fun MediaPickerRootContent(component: MediaPickerComponent) {
    val context = LocalComponentActivity.current

    ImageToolboxCompositionLocals(
        settingsState = component.settingsState.toUiState()
    ) {
        MediaPickerRootContentEmbeddable(
            component = component,
            allowedMedia = context.intent.type.allowedMedia,
            allowMultiple = context.intent.allowMultiple,
            onPicked = context::sendMediaAsResult,
            onBack = context::finish
        )

        ObserveColorSchemeExtra()
    }
}

private val Intent.allowMultiple get() = getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
private val String?.pickImage: Boolean get() = this?.startsWith("image") == true
private val String?.pickVideo: Boolean get() = this?.startsWith("video") == true
private val String?.allowedMedia: AllowedMedia
    get() = if (pickImage) AllowedMedia.Photos(this?.takeLastWhile { it != '/' })
    else if (pickVideo) AllowedMedia.Videos
    else AllowedMedia.Both