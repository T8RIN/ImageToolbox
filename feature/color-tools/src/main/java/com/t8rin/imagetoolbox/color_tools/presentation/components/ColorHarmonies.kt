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

package com.t8rin.imagetoolbox.color_tools.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.ColorWithNameItem
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
internal fun ColorHarmonies(
    selectedColor: Color,
) {
    val essentials = rememberLocalEssentials()

    var selectedHarmony by rememberSaveable {
        mutableStateOf(HarmonyType.COMPLEMENTARY)
    }
    val harmonies by remember(selectedColor, selectedHarmony) {
        derivedStateOf {
            selectedColor.applyHarmony(selectedHarmony)
        }
    }

    ExpandableItem(
        visibleContent = {
            TitleItem(
                text = stringResource(R.string.color_harmonies),
                icon = Icons.Rounded.BarChart,
                modifier = Modifier.padding(12.dp)
            )
        },
        expandableContent = {
            Column(
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HarmonyType.entries.forEach {
                        EnhancedChip(
                            selected = it == selectedHarmony,
                            onClick = { selectedHarmony = it },
                            selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = it.icon(),
                                contentDescription = null
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                AnimatedContent(
                    targetState = selectedHarmony,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) {
                    Text(it.title())
                }
                Spacer(Modifier.height(8.dp))
                AnimatedContent(
                    targetState = harmonies,
                    modifier = Modifier.fillMaxWidth(),
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    }
                ) { harmonies ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        harmonies.forEachIndexed { index, color ->
                            val shape = ShapeDefaults.byIndex(
                                index = index,
                                size = harmonies.size,
                                vertical = false
                            )
                            ColorWithNameItem(
                                color = color,
                                containerShape = shape,
                                onCopy = {
                                    essentials.copyToClipboard(
                                        text = getFormattedColor(color),
                                        message = R.string.color_copied
                                    )
                                },
                                modifier = Modifier
                                    .heightIn(min = 120.dp)
                                    .weight(1f)
                            )
                        }
                    }
                }
            }
        },
        shape = ShapeDefaults.extraLarge,
        initialState = false
    )
}