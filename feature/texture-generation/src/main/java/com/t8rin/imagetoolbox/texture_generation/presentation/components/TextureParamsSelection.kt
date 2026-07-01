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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.withDefaultsFor

@Composable
fun TextureParamsSelection(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit
) {
    Column(
        modifier = Modifier.container(
            shape = ShapeDefaults.large,
            resultPadding = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleItem(
            text = stringResource(R.string.params),
            icon = Icons.Rounded.Build,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DataSelector(
                value = value.textureFilterType,
                onValueChange = {
                    onValueChange(value.withDefaultsFor(it))
                },
                entries = TextureFilterType.entries,
                title = stringResource(R.string.texture_type),
                titleIcon = null,
                itemContentText = {
                    stringResource(it.titleRes())
                },
                spanCount = 1,
                containerColor = MaterialTheme.colorScheme.surface,
                shape = ShapeDefaults.top
            )

            AnimatedContent(
                targetState = value.textureFilterType,
                modifier = Modifier.fillMaxWidth()
            ) { textureFilterType ->
                when (textureFilterType) {
                TextureFilterType.BrushedMetal -> {
                    BrushedMetalParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }

                TextureFilterType.Caustics -> {
                    CausticsParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }

                TextureFilterType.Cellular -> {
                    CellularParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }

                TextureFilterType.Check -> {
                    CheckParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }

                TextureFilterType.FBM -> {
                    FbmParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }

                TextureFilterType.Marble -> {
                    MarbleParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }

                TextureFilterType.Plasma -> {
                    PlasmaParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }

                TextureFilterType.Quilt -> {
                    QuiltParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }

                TextureFilterType.Wood -> {
                    WoodParams(
                        value = value,
                        onValueChange = onValueChange
                    )
                }
                }
            }
        }
    }
}