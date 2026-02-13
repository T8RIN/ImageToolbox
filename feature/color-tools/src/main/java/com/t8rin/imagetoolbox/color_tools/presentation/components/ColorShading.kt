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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Swatch
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.ColorWithNameItem
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlin.math.roundToInt

@Composable
internal fun ColorShading(
    selectedColor: Color
) {
    val essentials = rememberLocalEssentials()

    var shadingVariation by rememberSaveable {
        mutableIntStateOf(5)
    }
    val shades by remember(selectedColor, shadingVariation) {
        derivedStateOf {
            selectedColor.mixWith(
                color = Color.Black,
                variations = shadingVariation,
                maxPercent = 0.9f
            )
        }
    }
    val tones by remember(selectedColor, shadingVariation) {
        derivedStateOf {
            selectedColor.mixWith(
                color = Color(0xff8e918f),
                variations = shadingVariation,
                maxPercent = 0.9f
            )
        }
    }
    val tints by remember(selectedColor, shadingVariation) {
        derivedStateOf {
            selectedColor.mixWith(
                color = Color.White,
                variations = shadingVariation,
                maxPercent = 0.8f
            )
        }
    }

    ExpandableItem(
        visibleContent = {
            TitleItem(
                text = stringResource(R.string.color_shading),
                icon = Icons.Rounded.Swatch,
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
                EnhancedSliderItem(
                    value = shadingVariation,
                    title = stringResource(R.string.variation),
                    valueRange = 2f..20f,
                    onValueChange = { shadingVariation = it.roundToInt() },
                    internalStateTransformation = { it.roundToInt() },
                    behaveAsContainer = true,
                    containerColor = MaterialTheme.colorScheme.surface,
                    steps = 17
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        tints to R.string.tints,
                        tones to R.string.tones,
                        shades to R.string.shades
                    ).forEach { (data, title) ->
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = stringResource(title))
                            data.forEachIndexed { index, color ->
                                ColorWithNameItem(
                                    color = color,
                                    containerShape = ShapeDefaults.byIndex(
                                        index = index,
                                        size = data.size
                                    ),
                                    onCopy = {
                                        essentials.copyToClipboard(
                                            text = getFormattedColor(color),
                                            message = R.string.color_copied
                                        )
                                    },
                                    modifier = Modifier.heightIn(min = 100.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        shape = ShapeDefaults.extraLarge,
        initialState = false
    )
}