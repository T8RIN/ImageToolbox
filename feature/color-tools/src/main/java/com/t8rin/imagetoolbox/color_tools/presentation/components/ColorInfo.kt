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

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
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
import com.smarttoolfactory.colordetector.parser.ColorNameParser
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun ColorInfo(
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
) {
    val essentials = rememberLocalEssentials()

    ExpandableItem(
        visibleContent = {
            TitleItem(
                text = stringResource(R.string.color_info),
                icon = Icons.Rounded.Info
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
                val boxColor by animateColorAsState(selectedColor)
                val contentColor = boxColor.inverse(
                    fraction = { cond ->
                        if (cond) 0.8f
                        else 0.5f
                    },
                    darkMode = boxColor.luminance() < 0.3f
                )
                Box(
                    modifier = Modifier
                        .heightIn(min = 80.dp)
                        .fillMaxWidth()
                        .clip(ShapeDefaults.default)
                        .transparencyChecker()
                        .background(boxColor)
                        .hapticsClickable {
                            essentials.copyToClipboard(
                                text = getFormattedColor(selectedColor),
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
                        text = selectedColor.toHex(),
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

                    Text(
                        text = remember(selectedColor) {
                            derivedStateOf {
                                ColorNameParser.parseColorName(selectedColor)
                            }
                        }.value,
                        color = contentColor,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .background(
                                color = boxColor.copy(alpha = 1f),
                                shape = ShapeDefaults.mini
                            )
                            .padding(horizontal = 4.dp),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                var wasNull by rememberSaveable {
                    mutableStateOf(false)
                }
                var resetJob by remember {
                    mutableStateOf<Job?>(null)
                }
                ColorInfoDisplay(
                    value = selectedColor,
                    onValueChange = {
                        wasNull = it == null

                        onColorChange(it ?: selectedColor)
                    },
                    onCopy = {
                        essentials.copyToClipboard(
                            text = it,
                            message = R.string.color_copied
                        )
                    },
                    onLoseFocus = {
                        resetJob?.cancel()
                        resetJob = essentials.launch {
                            delay(100)
                            if (wasNull) {
                                selectedColor.let {
                                    onColorChange(Color.White)
                                    delay(100)
                                    onColorChange(it)
                                }
                            }
                        }
                    }
                )
            }
        },
        initialState = true
    )
}