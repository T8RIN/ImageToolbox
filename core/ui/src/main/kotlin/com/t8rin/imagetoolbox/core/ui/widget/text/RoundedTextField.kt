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

package com.t8rin.imagetoolbox.core.ui.widget.text

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    label: String = "",
    hint: String = "",
    shape: Shape = ShapeDefaults.small,
    startIcon: ImageVector? = null,
    value: String,
    isError: Boolean = false,
    loading: Boolean = false,
    supportingText: (@Composable (isError: Boolean) -> Unit)? = null,
    supportingTextVisible: Boolean = true,
    endIcon: (@Composable (Boolean) -> Unit)? = null,
    formatText: String.() -> String = { this },
    textStyle: TextStyle = LocalTextStyle.current,
    onLoseFocusTransformation: String.() -> String = { this },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    colors: TextFieldColors = RoundedTextFieldColors(isError),
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    minLines: Int = 1,
    maxSymbols: Int = Int.MAX_VALUE
) {
    val labelImpl = @Composable {
        Text(
            text = label
        )
    }
    val hintImpl = @Composable {
        Text(text = hint, modifier = Modifier.padding(start = 4.dp))
    }
    val leadingIconImpl = @Composable {
        Icon(
            imageVector = startIcon!!,
            contentDescription = null
        )
    }

    RoundedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { onValueChange(it.formatText()) },
        textStyle = textStyle,
        colors = colors,
        shape = shape,
        singleLine = singleLine,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        endIcon = endIcon,
        startIcon = if (startIcon != null) leadingIconImpl else null,
        label = if (label.isNotEmpty()) labelImpl else null,
        hint = if (hint.isNotEmpty()) hintImpl else null,
        isError = isError,
        loading = loading,
        supportingText = supportingText,
        supportingTextVisible = supportingTextVisible,
        formatText = formatText,
        onLoseFocusTransformation = onLoseFocusTransformation,
        keyboardActions = keyboardActions,
        enabled = enabled,
        maxLines = maxLines,
        interactionSource = interactionSource,
        minLines = minLines,
        maxSymbols = maxSymbols
    )
}

@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    label: (@Composable () -> Unit)? = null,
    hint: (@Composable () -> Unit)? = null,
    shape: Shape = ShapeDefaults.small,
    startIcon: (@Composable () -> Unit)? = null,
    value: String,
    isError: Boolean = false,
    loading: Boolean = false,
    supportingText: (@Composable (isError: Boolean) -> Unit)? = null,
    supportingTextVisible: Boolean = true,
    endIcon: (@Composable (Boolean) -> Unit)? = null,
    formatText: String.() -> String = { this },
    textStyle: TextStyle = LocalTextStyle.current,
    onLoseFocusTransformation: String.() -> String = { this },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    colors: TextFieldColors = RoundedTextFieldColors(isError),
    enabled: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    minLines: Int = 1,
    maxSymbols: Int = Int.MAX_VALUE
) {
    val focus = LocalFocusManager.current
    val focused = interactionSource.collectIsFocusedAsState().value

    val colorScheme = MaterialTheme.colorScheme
    val unfocusedColor = if (!enabled) colorScheme.outlineVariant(
        onTopOf = colors.focusedContainerColor,
        luminance = 0.2f
    ) else colors.unfocusedIndicatorColor

    val focusedColor = if (isError) {
        colors.errorIndicatorColor
    } else colors.focusedIndicatorColor

    val borderColor by remember(focusedColor, enabled, focused) {
        derivedStateOf {
            Animatable(
                initialValue = if (!focused) unfocusedColor
                else focusedColor
            )
        }
    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(isError) {
        borderColor.animateTo(if (focused) focusedColor else unfocusedColor)
    }

    val mergedModifier = Modifier
        .fillMaxWidth()
        .border(
            width = animateDpAsState(
                if (borderColor.value == unfocusedColor) 1.dp
                else 2.dp
            ).value,
            color = borderColor.value,
            shape = shape
        )
        .onFocusChanged {
            scope.launch {
                if (readOnly) {
                    focus.clearFocus()
                    cancel()
                }
                if (it.isFocused) borderColor.animateTo(focusedColor)
                else {
                    if (!isError) borderColor.animateTo(unfocusedColor)
                    onValueChange(value.onLoseFocusTransformation())
                }
                cancel()
            }
        }
        .animateContentSizeNoClip()

    val supportingTextImpl = @Composable {
        if (!loading && supportingText != null) {
            supportingText(isError)
        }
    }

    Column(
        modifier = modifier.animateContentSizeNoClip()
    ) {
        val showSupportingText =
            !loading && (isError || (supportingText != null && supportingTextVisible))
        TextField(
            modifier = mergedModifier.clip(shape),
            value = value,
            onValueChange = { onValueChange(it.take(maxSymbols).formatText()) },
            textStyle = textStyle,
            colors = colors,
            shape = shape,
            singleLine = singleLine,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            trailingIcon = endIcon?.let { { it(focused) } },
            leadingIcon = startIcon,
            label = label,
            placeholder = hint,
            keyboardActions = keyboardActions,
            enabled = enabled,
            maxLines = maxLines,
            interactionSource = interactionSource,
            minLines = minLines,
        )
        val showMaxSymbols = maxSymbols != Int.MAX_VALUE && value.isNotEmpty()

        AnimatedVisibility(
            visible = showSupportingText || showMaxSymbols,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .padding(horizontal = 8.dp)
            ) {
                if (showSupportingText) {
                    ProvideTextStyle(
                        LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                        )
                    ) {
                        Row {
                            supportingTextImpl()
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
                if (showMaxSymbols) {
                    Text(
                        text = "${value.length} / $maxSymbols",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        color = borderColor.value
                    )
                }
            }
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun RoundedTextFieldColors(
    isError: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    focusedIndicatorColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor: Color = MaterialTheme.colorScheme.surfaceVariant.inverse({ 0.2f })
): TextFieldColors =
    MaterialTheme.colorScheme.run {
        val containerColorNew = if (isError) {
            containerColor.blend(error)
        } else containerColor
        TextFieldDefaults.colors(
            focusedContainerColor = containerColorNew,
            unfocusedContainerColor = containerColorNew,
            disabledContainerColor = containerColorNew,
            cursorColor = if (isError) error else focusedIndicatorColor,
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor,
            focusedLeadingIconColor = if (isError) error else focusedIndicatorColor,
            unfocusedLeadingIconColor = if (isError) error else surfaceVariant.inverse(),
            focusedTrailingIconColor = if (isError) error else focusedIndicatorColor,
            unfocusedTrailingIconColor = if (isError) error else surfaceVariant.inverse(),
            focusedLabelColor = if (isError) error else focusedIndicatorColor,
            unfocusedLabelColor = if (isError) error else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                0.9f
            ),
            selectionColors = TextSelectionColors(
                handleColor = focusedIndicatorColor.copy(1f),
                backgroundColor = focusedIndicatorColor.copy(0.4f)
            )
        )
    }