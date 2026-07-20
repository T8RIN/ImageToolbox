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
import com.t8rin.imagetoolbox.core.resources.icons.QualityHigh
import com.t8rin.imagetoolbox.core.settings.domain.model.RawDemosaicQuality
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun RawDemosaicQualitySettingItem(
    onValueChange: (RawDemosaicQuality) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
) {
    DataSelector(
        value = LocalSettingsState.current.rawDevelopSettings.quality,
        onValueChange = onValueChange,
        spanCount = 1,
        entries = RawDemosaicQuality.entries,
        title = stringResource(R.string.raw_demosaic_quality),
        titleIcon = Icons.Outlined.QualityHigh,
        itemContentText = { it.title },
        containerColor = Color.Unspecified,
        shape = shape,
        modifier = modifier,
        selectedItemColor = MaterialTheme.colorScheme.secondary
    )
}

private val RawDemosaicQuality.title: String
    @Composable
    get() = when (this) {
        RawDemosaicQuality.Linear -> stringResource(R.string.raw_demosaic_linear)
        else -> name.uppercase()
    }
