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

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.settings.domain.model.RawOutputColorSpace
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun RawOutputColorSpaceSettingItem(
    onValueChange: (RawOutputColorSpace) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
) {
    DataSelector(
        value = LocalSettingsState.current.rawDevelopSettings.outputColorSpace,
        onValueChange = onValueChange,
        initialExpanded = true,
        spanCount = 2,
        entries = RawOutputColorSpace.entries,
        title = stringResource(R.string.raw_output_color_space),
        titleIcon = Icons.Outlined.Palette,
        itemContentText = { it.title },
        containerColor = Color.Unspecified,
        shape = shape,
        modifier = modifier,
        selectedItemColor = MaterialTheme.colorScheme.secondary
    )
}

private val RawOutputColorSpace.title: String
    @Composable
    get() = when (this) {
        RawOutputColorSpace.SRgb -> "sRGB"
        RawOutputColorSpace.AdobeRgb -> stringResource(R.string.raw_color_space_adobe_rgb)
        RawOutputColorSpace.WideGamutRgb -> stringResource(R.string.raw_color_space_wide_gamut_rgb)
        RawOutputColorSpace.ProPhotoRgb -> stringResource(R.string.raw_color_space_pro_photo_rgb)
        RawOutputColorSpace.Xyz -> "CIE XYZ"
        RawOutputColorSpace.Aces -> "ACES"
    }
