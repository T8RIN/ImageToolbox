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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.rememberImageColorPaletteState
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FileExport
import com.t8rin.imagetoolbox.core.ui.theme.mixedContainer
import com.t8rin.imagetoolbox.core.ui.theme.onMixedContainer
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.NamedColor


@Composable
internal fun DefaultPaletteControls(
    bitmap: Bitmap,
    onOpenExport: (List<NamedColor>) -> Unit
) {
    val essentials = rememberLocalEssentials()

    var count by rememberSaveable { mutableIntStateOf(32) }

    val state = rememberImageColorPaletteState(
        imageBitmap = bitmap.asImageBitmap(),
        maximumColorCount = count
    )

    PaletteColorsCountSelector(
        value = count,
        onValueChange = { count = it }
    )
    Spacer(modifier = Modifier.height(16.dp))

    PreferenceItem(
        title = stringResource(R.string.export),
        subtitle = stringResource(R.string.export_palette_sub),
        onClick = {
            onOpenExport(
                state.paletteData.map {
                    NamedColor(
                        color = it.colorData.color,
                        name = it.colorData.name
                    )
                }
            )
        },
        endIcon = Icons.Outlined.FileExport,
        shape = ShapeDefaults.top,
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.mixedContainer.copy(0.5f),
        contentColor = MaterialTheme.colorScheme.onMixedContainer
    )
    Spacer(modifier = Modifier.height(4.dp))
    ImageColorPalette(
        paletteDataList = state.paletteData,
        modifier = Modifier
            .fillMaxSize()
            .container(ShapeDefaults.bottom)
            .padding(4.dp),
        onColorClick = {
            essentials.copyToClipboard(
                text = it.color.toHex(),
                message = R.string.color_copied
            )
        }
    )
}