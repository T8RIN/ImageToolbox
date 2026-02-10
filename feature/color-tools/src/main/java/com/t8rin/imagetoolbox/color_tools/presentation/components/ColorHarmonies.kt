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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.ContentCopy
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    harmonies.forEach { color ->
                        val boxColor by animateColorAsState(color)
                        val contentColor = boxColor.inverse(
                            fraction = { cond ->
                                if (cond) 0.8f
                                else 0.5f
                            },
                            darkMode = boxColor.luminance() < 0.3f
                        )
                        Box(
                            modifier = Modifier
                                .heightIn(min = 120.dp)
                                .weight(1f)
                                .clip(ShapeDefaults.mini)
                                .transparencyChecker()
                                .background(boxColor)
                                .hapticsClickable {
                                    essentials.copyToClipboard(
                                        text = getFormattedColor(color),
                                        message = R.string.color_copied
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ContentCopy,
                                contentDescription = stringResource(R.string.copy),
                                tint = contentColor,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(28.dp)
                                    .background(
                                        color = boxColor.copy(alpha = 1f),
                                        shape = ShapeDefaults.mini
                                    )
                                    .padding(2.dp)
                            )

                            Text(
                                text = color.toHex(),
                                color = contentColor,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(4.dp)
                                    .background(
                                        color = boxColor.copy(alpha = 1f),
                                        shape = ShapeDefaults.mini
                                    )
                                    .padding(horizontal = 4.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        initialState = false
    )
}