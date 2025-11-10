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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.components.model.NamedPalette
import com.t8rin.imagetoolbox.feature.palette_tools.presentation.screenLogic.PaletteToolsComponent

@Composable
internal fun PaletteToolsScreenControls(
    component: PaletteToolsComponent
) {
    val paletteType = component.paletteType ?: return

    val bitmap = component.bitmap

    AnimatedContent(
        targetState = paletteType
    ) { type ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (type) {
                PaletteType.Default -> {
                    DefaultPaletteControls(
                        bitmap = bitmap ?: return@AnimatedContent,
                        onOpenExport = { colors ->
                            component.setPaletteType(PaletteType.Edit)
                            component.updatePalette(
                                NamedPalette(
                                    name = "",
                                    colors = colors
                                )
                            )
                        }
                    )
                }

                PaletteType.MaterialYou -> {
                    MaterialYouPaletteControls(
                        bitmap = bitmap ?: return@AnimatedContent
                    )
                }

                PaletteType.Edit -> {
                    EditPaletteControls(
                        paletteFormat = component.paletteFormat,
                        onPaletteFormatChange = component::updatePaletteFormat,
                        palette = component.palette,
                        onPaletteChange = component::updatePalette
                    )
                }
            }
        }
    }
}