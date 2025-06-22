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

package com.t8rin.imagetoolbox.core.ui.widget.controls

import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun SaveExifWidget(
    checked: Boolean,
    imageFormat: ImageFormat,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Unspecified
) {
    val settingsState = LocalSettingsState.current
    LaunchedEffect(Unit) {
        onCheckedChange(settingsState.exifWidgetInitialState)
    }
    PreferenceRowSwitch(
        modifier = modifier,
        title = stringResource(R.string.keep_exif),
        subtitle = if (imageFormat.canWriteExif) {
            stringResource(R.string.keep_exif_sub)
        } else {
            stringResource(
                R.string.image_exif_warning,
                imageFormat.title
            )
        },
        checked = checked,
        enabled = imageFormat.canWriteExif,
        shape = ShapeDefaults.extraLarge,
        color = backgroundColor,
        onClick = onCheckedChange,
        startIcon = Icons.Rounded.Exif
    )
}