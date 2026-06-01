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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.presentation.utils.localizedMessage
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.EvShadow
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.shader_studio.presentation.screenLogic.ShaderStudioComponent
import dev.hossain.highlight.engine.HighlightTheme
import dev.hossain.highlight.ui.rememberHighlightedCode

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
        GlslCodeField(
            value = component.shaderSource,
            onValueChange = component::updateShaderSource,
            hint = stringResource(R.string.shader_main_body_info),
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
        ExpandableItem(
            initialState = component.helperSource.isNotBlank(),
            visibleContent = {
                TitleItem(text = stringResource(R.string.shader_helper_code))
            },
            expandableContent = {
                GlslCodeField(
                    value = component.helperSource,
                    onValueChange = component::updateHelperSource,
                    hint = stringResource(R.string.shader_helper_code_info),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            },
            color = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun GlslCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    supportingText: (@Composable (Boolean) -> Unit)? = null
) {
    val isNightMode = LocalSettingsState.current.isNightMode

    val highlightedCode by rememberHighlightedCode(
        code = value,
        theme = remember(isNightMode) {
            if (isNightMode) {
                HighlightTheme.tomorrowNight()
            } else {
                HighlightTheme.tomorrow()
            }
        },
        language = GLSL_LANGUAGE
    )
    var cachedHighlightedCode by remember(isNightMode) {
        mutableStateOf<AnnotatedString?>(null)
    }
    LaunchedEffect(highlightedCode, value) {
        if (highlightedCode?.text == value) {
            cachedHighlightedCode = highlightedCode
        }
    }

    val displayHighlightedCode = remember(value, cachedHighlightedCode) {
        cachedHighlightedCode
            ?.takeIf { it.text == value }
            ?: cachedHighlightedCode?.applySpanStylesTo(value)
    }
    val visualTransformation = remember(value, displayHighlightedCode) {
        GlslHighlightVisualTransformation(
            highlightedCode = displayHighlightedCode
        )
    }

    RoundedTextField(
        value = value,
        onValueChange = onValueChange,
        hint = hint,
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
        visualTransformation = visualTransformation,
        modifier = modifier,
        supportingText = supportingText
    )
}

private class GlslHighlightVisualTransformation(
    private val highlightedCode: AnnotatedString?
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = highlightedCode
            ?.takeIf { it.text == text.text }
            ?: text

        return TransformedText(
            text = transformedText,
            offsetMapping = OffsetMapping.Identity
        )
    }
}

private fun AnnotatedString.applySpanStylesTo(text: String): AnnotatedString {
    if (text.isEmpty()) return AnnotatedString(text)

    return AnnotatedString.Builder(text).apply {
        spanStyles.forEach { range ->
            val start = range.start.coerceAtMost(text.length)
            val end = range.end.coerceAtMost(text.length)
            if (start < end) {
                addStyle(range.item, start, end)
            }
        }
    }.toAnnotatedString()
}

private const val GLSL_LANGUAGE = "glsl"
