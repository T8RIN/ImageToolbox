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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.Composable
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
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.ColorWithNameItem
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
                icon = Icons.Rounded.Info,
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
                ColorWithNameItem(
                    color = selectedColor,
                    onCopy = {
                        essentials.copyToClipboard(
                            text = getFormattedColor(selectedColor),
                            message = R.string.color_copied
                        )
                    }
                )
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
        shape = ShapeDefaults.extraLarge,
        initialState = true
    )
}