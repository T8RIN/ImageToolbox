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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiFont
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.FontSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode

@Composable
internal fun TextParamsSelector(
    value: DrawMode,
    onValueChange: (DrawMode) -> Unit
) {
    AnimatedVisibility(
        visible = value is DrawMode.Text,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            RoundedTextField(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .container(
                        shape = ShapeDefaults.top,
                        color = MaterialTheme.colorScheme.surface,
                        resultPadding = 8.dp
                    ),
                value = (value as? DrawMode.Text)?.text ?: "",
                singleLine = false,
                onValueChange = {
                    onValueChange(
                        (value as? DrawMode.Text)?.copy(
                            text = it
                        ) ?: value
                    )
                },
                label = {
                    Text(stringResource(R.string.text))
                }
            )
            Spacer(modifier = Modifier.height(4.dp))
            FontSelector(
                value = (value as? DrawMode.Text)?.font.toUiFont(),
                onValueChange = {
                    onValueChange(
                        (value as? DrawMode.Text)?.copy(
                            font = it.type
                        ) ?: value
                    )
                },
                modifier = Modifier
                    .padding(horizontal = 8.dp),
                shape = ShapeDefaults.center
            )
            Spacer(modifier = Modifier.height(4.dp))
            val isDashSizeControlVisible = (value as? DrawMode.Text)?.isRepeated == true
            PreferenceRowSwitch(
                title = stringResource(R.string.repeat_text),
                subtitle = stringResource(R.string.repeat_text_sub),
                checked = (value as? DrawMode.Text)?.isRepeated == true,
                onClick = {
                    onValueChange(
                        (value as? DrawMode.Text)?.copy(
                            isRepeated = it
                        ) ?: value
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = animateShape(
                    if (isDashSizeControlVisible) ShapeDefaults.center
                    else ShapeDefaults.bottom
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                resultModifier = Modifier.padding(16.dp),
                applyHorizontalPadding = false
            )
            Spacer(
                modifier = Modifier.height(
                    if (isDashSizeControlVisible) 4.dp else 8.dp
                )
            )
            AnimatedVisibility(
                visible = isDashSizeControlVisible,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                EnhancedSliderItem(
                    value = (value as? DrawMode.Text)?.repeatingInterval?.value ?: 0f,
                    title = stringResource(R.string.dash_size),
                    valueRange = 0f..100f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        onValueChange(
                            (value as? DrawMode.Text)?.copy(
                                repeatingInterval = it.pt
                            ) ?: value
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    valueSuffix = " Pt",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .padding(bottom = 8.dp),
                    shape = ShapeDefaults.bottom
                )
            }
        }
    }
}