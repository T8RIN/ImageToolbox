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

package com.t8rin.imagetoolbox.feature.shader_studio.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.presentation.utils.localizedMessage
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.EvShadow
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic.ShaderStudioComponent

@Composable
internal fun ShaderPresetEditor(component: ShaderStudioComponent) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .container(MaterialTheme.shapes.extraLarge)
            .padding(16.dp)
    ) {
        TitleItem(
            text = stringResource(R.string.shader),
            icon = Icons.Rounded.EvShadow,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RoundedTextField(
            value = component.name,
            onValueChange = component::updateName,
            label = stringResource(R.string.name),
            shape = ShapeDefaults.large,
            modifier = Modifier.fillMaxWidth()
        )
        RoundedTextField(
            value = component.shaderSource,
            onValueChange = component::updateShaderSource,
            hint = stringResource(R.string.shader_main_body_info),
            shape = ShapeDefaults.large,
            singleLine = false,
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Default
            ),
            modifier = Modifier.fillMaxWidth(),
            supportingText = component.validationErrors.takeIf { it.isNotEmpty() }?.let {
                {
                    Text(
                        text = remember(component.validationErrors) {
                            component.validationErrors.joinToString("\n") { it.localizedMessage() }
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
    }
}
