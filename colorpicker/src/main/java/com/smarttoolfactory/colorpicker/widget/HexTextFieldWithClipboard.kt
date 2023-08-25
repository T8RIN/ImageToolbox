package com.smarttoolfactory.colorpicker.widget

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colorpicker.R
import com.smarttoolfactory.colorpicker.util.HexVisualTransformation
import com.smarttoolfactory.colorpicker.util.hexRegex
import com.smarttoolfactory.colorpicker.util.hexRegexSingleChar
import com.smarttoolfactory.colorpicker.util.hexWithAlphaRegex
import com.smarttoolfactory.extendedcolors.parser.rememberColorParser
import com.smarttoolfactory.extendedcolors.util.ColorUtil
import com.smarttoolfactory.extendedcolors.util.HexUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

/**
 * [TextField] that displays color in hex representation either with #RRGGBB or #AARRGGBB
 * depending on [useAlpha] flag and with [LocalClipboardManager] to save hex to clipboard.
 *
 * @param hexString string in hex format.
 * @param useAlpha when set to true returns colors in #AARRGGBB format.
 * @param onTextChange this callback returns when the last char typed by user is an acceptable
 * HEX char between 0-9,a-f, or A-F.
 * @param onColorChange when user type valid 6 or 8 char hex returns a [Color] associated
 * with the hex string.
 */
@Composable
fun HexTextFieldWithClipboard(
    modifier: Modifier = Modifier,
    hexString: String,
    useAlpha: Boolean = false,
    onTextChange: (String) -> Unit,
    onColorChange: (Color) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val hexText = rememberUpdatedState(newValue = hexString)

    val currentRegex = if (useAlpha) hexWithAlphaRegex else hexRegex
    val isHexValid = currentRegex.matches(hexText.value)

    val color = remember(hexText.value) {
        derivedStateOf {
            try {
                HexUtil.hexToColor(hexText.value)
            } catch (e: Exception) {
                Color.Unspecified
            }
        }
    }

    val lightness = ColorUtil.colorToHSL(color.value)[2]

    val textColor = if (color.value == Color.Unspecified) {
        Color.Black
    } else {
        if (lightness < .6f) Color.White else Color.Black
    }


    val hexModifier = if (isHexValid) {
        modifier
            .shadow(2.dp, RoundedCornerShape(50))
            .background(color = color.value)
            .padding(start = 20.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    } else {
        modifier
            .shadow(2.dp, RoundedCornerShape(50))
            .background(color = Color.White)
            .padding(start = 20.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    }

    Row(
        modifier = hexModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BasicTextField(
            modifier = Modifier
                .width(140.dp)
                .wrapContentHeight()
                .padding(bottom = 10.dp, top = 12.dp),
            visualTransformation = HexVisualTransformation(useAlpha),
            value = hexString.removePrefix("#"),
            textStyle = TextStyle(
                color = textColor,
                fontSize = 20.sp
            ),

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
            },
            cursorBrush = SolidColor(textColor)
        )


        if (isHexValid) {
            IconButton(
                onClick = {
                    Toast.makeText(context, "Copied $hexString", Toast.LENGTH_SHORT).show()
                    clipboardManager.setText(AnnotatedString(hexString))
                }) {
                Icon(
                    tint = if (isHexValid) textColor else Color.LightGray,
                    painter = painterResource(id = R.drawable.ic_baseline_content_copy_24),
                    contentDescription = "clipboard"
                )
            }
        } else {
            Icon(
                modifier = Modifier.padding(12.dp),
                tint = Color.Red,
                imageVector = Icons.Filled.Error,
                contentDescription = "error"
            )
        }
    }
}

/**
 * [TextField] that displays color in hex representation either with #RRGGBB
 * with [LocalClipboardManager] to save hex to clipboard and displays when 6 characters input
 * displays name closest to R, G, B values in 3D space.
 *
 * @param hexString string in hex format.
 * @param onTextChange this callback returns when the last char typed by user is an acceptable
 * HEX char between 0-9,a-f, or A-F.
 * @param onColorChange when user type valid 6 or 8 char hex returns a [Color] associated
 * with the hex string.
 */
@Composable
fun HexTextFieldWithLabelClipboard(
    modifier: Modifier = Modifier,
    hexString: String,
    onTextChange: (String) -> Unit,
    onColorChange: (Color) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val colorNameParser = rememberColorParser()
    val hexText = rememberUpdatedState(newValue = hexString)
    var colorName by remember { mutableStateOf("") }

    val currentRegex = hexRegex
    val isHexValid = currentRegex.matches(hexText.value)

    val color = remember(hexText.value) {
        derivedStateOf {
            try {
                HexUtil.hexToColor(hexText.value)
            } catch (e: Exception) {
                Color.Unspecified
            }
        }
    }

    val lightness = ColorUtil.colorToHSL(color.value)[2]

    val textColor = if (color.value == Color.Unspecified) {
        Color.Black
    } else {
        if (lightness < .6f) Color.White else Color.Black
    }

    LaunchedEffect(key1 = colorNameParser) {

        snapshotFlow { hexText.value }
            .distinctUntilChanged()
            .mapLatest {
                println("✊ HEX: $it, isHexValid: $isHexValid")
                if (currentRegex.matches(hexText.value)) {
                    colorNameParser.parseColorName(color.value)
                } else {
                    ""
                }
            }
            .flowOn(Dispatchers.Default)
            .collect { name: String ->
                colorName = name
            }
    }

    val hexModifier = if (isHexValid) {
        modifier
            .shadow(2.dp, RoundedCornerShape(50))
            .background(color = color.value)
            .padding(start = 20.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    } else {
        modifier
            .shadow(2.dp, RoundedCornerShape(50))
            .background(color = Color.White)
            .padding(start = 20.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    }

    Row(
        modifier = hexModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {


        Box {
            Text(
                text = colorName,
                fontSize = 10.sp,
                color = textColor,
                modifier = Modifier.padding(2.dp)
            )

            BasicTextField(
                modifier = Modifier
                    .width(140.dp)
                    .wrapContentHeight()
                    .padding(bottom = 10.dp, top = 12.dp),
                visualTransformation = HexVisualTransformation(false),
                value = hexString.removePrefix("#"),
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 20.sp
                ),

                onValueChange = {

                    if (it.length <= 6) {
                        var validHex = true

                        for (index in it.indices) {
                            validHex = hexRegexSingleChar.matches(it[index].toString())
                            if (!validHex) break
                        }

                        if (validHex) {
                            onTextChange("#$it")
                            // Hex String with 6 chars matches a Color
                            if (currentRegex.matches(it)) {
                                onColorChange(HexUtil.hexToColor(it))
                            }
                        }
                    }
                },
                cursorBrush = SolidColor(textColor)
            )
        }

        if (isHexValid) {
            IconButton(
                onClick = {
                    Toast.makeText(context, "Copied $hexString", Toast.LENGTH_SHORT).show()
                    clipboardManager.setText(AnnotatedString(hexString))
                }) {
                Icon(
                    tint = if (isHexValid) textColor else Color.LightGray,
                    painter = painterResource(id = R.drawable.ic_baseline_content_copy_24),
                    contentDescription = "clipboard"
                )
            }
        } else {
            Icon(
                modifier = Modifier.padding(12.dp),
                tint = Color.Red,
                imageVector = Icons.Filled.Error,
                contentDescription = "error"
            )
        }
    }
}