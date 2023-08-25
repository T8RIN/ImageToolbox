package com.smarttoolfactory.colorpicker.widget

import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colorpicker.ui.Grey400
import com.smarttoolfactory.colorpicker.ui.Red400
import com.smarttoolfactory.colorpicker.util.HexVisualTransformation
import com.smarttoolfactory.colorpicker.util.hexRegex
import com.smarttoolfactory.colorpicker.util.hexRegexSingleChar
import com.smarttoolfactory.colorpicker.util.hexWithAlphaRegex
import com.smarttoolfactory.extendedcolors.util.HexUtil


/**
 * [TextField] that displays color in hex representation either with #RRGGBB or #AARRGGBB
 * depending on [useAlpha] flag.
 *
 * @param colors [TextFieldColors] that will be used to resolve color of the text and content
 * (including label, placeholder, leading and trailing icons, border) for this text field in
 * different states. See [TextFieldDefaults.outlinedTextFieldColors]
 * @param textStyle the style to be applied to the input text. The default [textStyle] uses the
 * [LocalTextStyle] defined by the theme
 * @param label the optional label to be displayed inside the text field container. The default
 * text style for internal [Text] is [Typography.caption] when the text field is in focus and
 * [Typography.subtitle1] when the text field is not in focus
 * @param placeholder the optional placeholder to be displayed when the text field is in focus and
 * the input text is empty. The default text style for internal [Text] is [Typography.subtitle1]
 * @param shape of this [TextField].
 * @param hexString string in hex format.
 * @param useAlpha when set to true returns colors in #AARRGGBB format.
 * @param onTextChange this callback returns when the last char typed by user is an acceptable
 * HEX char between 0-9,a-f, or A-F.
 * @param onColorChange when user type valid 6 or 8 char hex returns a [Color] associated
 * with the hex string.
 */
@Composable
fun HexTextField(
    modifier: Modifier = Modifier,
    hexString: String,
    textStyle: TextStyle = TextStyle(fontSize = 24.sp),
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    ),
    label: @Composable () -> Unit = {
        Text("Hex", color = Grey400)
    },
    placeholder: @Composable () -> Unit = {
        Text("Enter a color", fontSize = textStyle.fontSize)
    },
    shape: Shape = RoundedCornerShape(25),
    useAlpha: Boolean = false,
    onTextChange: (String) -> Unit,
    onColorChange: (Color) -> Unit
) {
    val currentRegex = if (useAlpha) hexWithAlphaRegex else hexRegex
    OutlinedTextField(
        modifier = modifier
            .widthIn(min = 80.dp)
            .drawBehind {
                drawLine(
                    if (currentRegex.matches(hexString)) Grey400 else Red400,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 5f
                )
            },
        visualTransformation = HexVisualTransformation(useAlpha),
        textStyle = textStyle,
        colors = colors,
        shape = shape,
        label = label,
        placeholder = placeholder,
        value = hexString.removePrefix("#"),
        onValueChange = {

            if (it.length <= if (useAlpha) 8 else 6) {
                var validHex = true

                for (index in it.indices) {
                    validHex = hexRegexSingleChar.matches(it[index].toString())
                    if (!validHex) break
                }

                if (validHex) {
                    onTextChange("#$it")
                    // Hex String with 6 or 8 chars matches a Color
                    if (currentRegex.matches(it)) {
                        onColorChange(HexUtil.hexToColor(it))
                    }
                }
            }
        }
    )
}
