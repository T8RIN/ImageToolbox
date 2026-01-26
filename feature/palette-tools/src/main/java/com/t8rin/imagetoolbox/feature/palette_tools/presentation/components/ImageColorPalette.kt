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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.PaletteData
import com.smarttoolfactory.colordetector.model.ColorData
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.PaletteSwatch
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.SourceNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction

@Composable
internal fun ImageColorPalette(
    modifier: Modifier,
    paletteDataList: List<PaletteData>,
    onColorClick: (ColorData) -> Unit,
) {
    AnimatedContent(
        targetState = paletteDataList.isEmpty(),
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { isEmpty ->
        if (isEmpty) {
            SourceNotPickedWidget(
                onClick = null,
                text = stringResource(R.string.no_palette),
                icon = Icons.Rounded.PaletteSwatch,
                containerColor = MaterialTheme.colorScheme.surface
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                paletteDataList.forEachIndexed { index, paletteData: PaletteData ->
                    val colorData = paletteData.colorData
                    val baseShape = ShapeDefaults.byIndex(
                        index = index,
                        size = paletteDataList.size
                    )
                    val interactionSource = remember { MutableInteractionSource() }
                    val shape = shapeByInteraction(
                        shape = baseShape,
                        pressedShape = ShapeDefaults.pressed,
                        interactionSource = interactionSource
                    )

                    Row(
                        modifier = Modifier
                            .container(
                                shape = shape,
                                color = MaterialTheme.colorScheme.surface,
                                resultPadding = 0.dp
                            )
                            .fillMaxWidth()
                            .hapticsClickable(
                                indication = LocalIndication.current,
                                interactionSource = interactionSource
                            ) {
                                onColorClick(colorData)
                            }
                            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .container(
                                    shape = ShapeDefaults.circle,
                                    color = colorData.color
                                )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = colorData.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = colorData.hexText.uppercase(),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}