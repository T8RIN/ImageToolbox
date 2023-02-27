package ru.tech.imageresizershrinker.resize_screen.components

import androidx.annotation.FloatRange
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    label: String = "",
    hint: String = "",
    shape: Shape = RoundedCornerShape(4.dp),
    startIcon: ImageVector? = null,
    value: String,
    isError: Boolean = false,
    loading: Boolean = false,
    supportingText: (@Composable (isError: Boolean) -> Unit)? = null,
    supportingTextVisible: Boolean = true,
    endIcon: (@Composable () -> Unit)? = null,
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
    minLines: Int = 1
) {
    val labelImpl = @Composable {
        Text(
            text = label,
            modifier = if (singleLine) Modifier else Modifier.offset(y = 4.dp)
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
        minLines = minLines
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    label: (@Composable () -> Unit)? = null,
    hint: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(4.dp),
    startIcon: (@Composable () -> Unit)? = null,
    value: String,
    isError: Boolean = false,
    loading: Boolean = false,
    supportingText: (@Composable (isError: Boolean) -> Unit)? = null,
    supportingTextVisible: Boolean = true,
    endIcon: (@Composable () -> Unit)? = null,
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
    minLines: Int = 1
) {
    val focus = LocalFocusManager.current
    val focused = interactionSource.collectIsFocusedAsState().value

    val colorScheme = MaterialTheme.colorScheme
    val borderColor by remember(colorScheme) {
        derivedStateOf {
            Animatable(
                initialValue = if (!focused) colorScheme.outline.blend(Color.Black, 0.2f)
                else colorScheme.primary
            )
        }
    }

    val scope = rememberCoroutineScope()
    LaunchedEffect(isError) {
        borderColor.animateTo(
            if (isError && focused) colorScheme.error else if (focused) colorScheme.primary else colorScheme.outline.blend(
                Color.Black,
                0.2f
            )
        )
    }

    val mergedModifier = Modifier
        .fillMaxWidth()
        .border(
            width = 2.dp,
            color = borderColor.value,
            shape = shape
        )
        .onFocusChanged {
            scope.launch {
                if (readOnly) {
                    focus.clearFocus()
                    cancel()
                }
                if (it.isFocused) borderColor.animateTo(if (isError) colorScheme.error else colorScheme.primary)
                else {
                    if (!isError) borderColor.animateTo(
                        colorScheme.outline.blend(
                            Color.Black,
                            0.2f
                        )
                    )
                    onValueChange(value.onLoseFocusTransformation())
                }
                cancel()
            }
        }
        .animateContentSize()

    val supportingTextImpl = @Composable {
        if (!loading && supportingText != null) {
            supportingText(isError)
        }
    }

    Column(
        modifier = modifier
            .animateContentSize()
            .clip(shape)
    ) {
        TextField(
            modifier = mergedModifier,
            value = value,
            onValueChange = { onValueChange(it.formatText()) },
            textStyle = textStyle,
            colors = colors,
            shape = shape,
            singleLine = singleLine,
            readOnly = readOnly,
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            trailingIcon = endIcon,
            leadingIcon = startIcon,
            label = label,
            placeholder = hint,
            keyboardActions = keyboardActions,
            enabled = enabled,
            maxLines = maxLines,
            interactionSource = interactionSource,
            minLines = minLines,
        )
        if (isError && !loading && supportingText != null && supportingTextVisible) {
            Spacer(Modifier.height(6.dp))
            ProvideTextStyle(
                LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            ) {
                Row {
                    Spacer(modifier = Modifier.width(15.dp))
                    supportingTextImpl()
                    Spacer(modifier = Modifier.width(15.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedTextFieldColors(isError: Boolean): TextFieldColors =
    MaterialTheme.colorScheme.run {
        TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = if (isError) error else primary,
            focusedLabelColor = if (isError) error else primary,
            focusedLeadingIconColor = if (isError) error else onSurfaceVariant,
            unfocusedLeadingIconColor = if (isError) error else onSurfaceVariant,
            focusedTrailingIconColor = if (isError) error else onSurfaceVariant,
            unfocusedTrailingIconColor = if (isError) error else onSurfaceVariant,
            unfocusedLabelColor = if (isError) error else onSurfaceVariant,
            containerColor = if (isError) surfaceVariant.blend(error, 0.2f) else surfaceVariant,
        )
    }

fun Color.blend(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.2f
): Color = Color(ColorUtils.blendARGB(this.toArgb(), color.toArgb(), fraction))


fun Int.blend(
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.2f
): Int = ColorUtils.blendARGB(this, color.toArgb(), fraction)
