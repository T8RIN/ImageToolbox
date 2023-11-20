package ru.tech.imageresizershrinker.presentation.root.widget.text

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.presentation.root.theme.blend
import ru.tech.imageresizershrinker.presentation.root.theme.inverse

@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    label: String = "",
    hint: String = "",
    shape: Shape = RoundedCornerShape(12.dp),
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

@Composable
fun RoundedTextField(
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    label: (@Composable () -> Unit)? = null,
    hint: (@Composable () -> Unit)? = null,
    shape: Shape = RoundedCornerShape(12.dp),
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
    val unfocusedColor = if (!enabled) colorScheme.outlineVariant
    else colorScheme.surfaceVariant.inverse({ 0.2f })

    val focusedColor = if (isError) colorScheme.error else colorScheme.primary

    val borderColor by remember(focusedColor, enabled, focused) {
        derivedStateOf {
            Animatable(
                initialValue = if (!focused) unfocusedColor
                else focusedColor
            )
        }
    }

    //TODO: Remove when library will be fixed
    KeyboardFocusHandler()

    val scope = rememberCoroutineScope()
    LaunchedEffect(isError) {
        borderColor.animateTo(if (focused) focusedColor else unfocusedColor)
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
                if (it.isFocused) borderColor.animateTo(focusedColor)
                else {
                    if (!isError) borderColor.animateTo(unfocusedColor)
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

@SuppressLint("ComposableNaming")
@Composable
fun RoundedTextFieldColors(isError: Boolean): TextFieldColors =
    MaterialTheme.colorScheme.run {
        val containerColor = if (isError) {
            surfaceColorAtElevation(1.dp).blend(error)
        } else surfaceColorAtElevation(1.dp)
        TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            cursorColor = if (isError) error else primary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLeadingIconColor = if (isError) error else surfaceVariant.inverse(),
            unfocusedLeadingIconColor = if (isError) error else surfaceVariant.inverse(),
            focusedTrailingIconColor = if (isError) error else surfaceVariant.inverse(),
            unfocusedTrailingIconColor = if (isError) error else surfaceVariant.inverse(),
            focusedLabelColor = if (isError) error else primary,
            unfocusedLabelColor = if (isError) error else surfaceVariant.inverse(),
        )
    }