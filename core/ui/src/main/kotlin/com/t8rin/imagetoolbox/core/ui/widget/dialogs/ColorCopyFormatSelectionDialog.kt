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

package com.t8rin.imagetoolbox.core.ui.widget.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.colors.parser.ColorWithName
import com.t8rin.colors.util.ColorUtil
import com.t8rin.colors.util.ColorUtil.hex
import com.t8rin.imagetoolbox.core.domain.utils.trimTrailingZero
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.resources.icons.ShortText
import com.t8rin.imagetoolbox.core.resources.icons.TagText
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun ColorCopyFormatSelectionDialog(
    target: ColorWithName?,
    onDismiss: () -> Unit
) {
    val colorWithName by produceState(
        target ?: ColorWithName(
            color = Color.Transparent,
            name = ""
        ),
        key1 = target
    ) {
        if (target == null) {
            delay(600)
            value = target ?: ColorWithName(
                color = Color.Transparent,
                name = ""
            )
        } else {
            value = target
        }
    }

    ColorCopyFormatSelectionDialog(
        visible = target != null,
        onDismiss = onDismiss,
        color = colorWithName.color,
        colorName = colorWithName.name
    )
}

@Composable
fun ColorCopyFormatSelectionDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    color: Color,
    colorName: String
) {
    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        placeAboveAll = true,
        icon = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .container(
                        shape = ShapeDefaults.circle,
                        color = color,
                        resultPadding = 0.dp
                    )
            )
        },
        title = {
            Text(stringResource(R.string.copy_color_as))
        },
        text = {
            val formats = remember { ColorCopyFormat.entries }
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fadingEdges(
                        scrollableState = scrollState,
                        isVertical = true
                    )
                    .enhancedVerticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                formats.forEachIndexed { index, format ->
                    val textToCopy = remember(format, color, colorName) {
                        format.textToCopy(
                            color = color,
                            colorName = colorName
                        )
                    }

                    PreferenceItem(
                        title = format.title(),
                        subtitle = textToCopy,
                        startIcon = format.icon(),
                        shape = ShapeDefaults.byIndex(
                            index = index,
                            size = formats.size
                        ),
                        titleFontStyle = PreferenceItemDefaults.TitleFontStyle.copy(
                            textAlign = TextAlign.Start
                        ),
                        onClick = {
                            Clipboard.copy(
                                text = textToCopy,
                                message = R.string.color_copied
                            )
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

private enum class ColorCopyFormat {
    Hex,
    Name,
    HexAndName,
    RGB,
    RGBA,
    HSL,
    HSV,
    CMYK;

    @Composable
    fun title(): String = when (this) {
        Hex -> "HEX"
        Name -> stringResource(R.string.name)
        HexAndName -> "HEX + ${stringResource(R.string.name)}"
        RGB -> "RGB"
        RGBA -> "RGBA"
        HSL -> "HSL"
        HSV -> "HSV"
        CMYK -> "CMYK"
    }

    fun icon(): ImageVector = when (this) {
        Hex -> Icons.Rounded.HashTag
        Name -> Icons.Outlined.TagText
        HexAndName,
        RGB,
        RGBA,
        HSL,
        HSV,
        CMYK -> Icons.Rounded.ShortText
    }

    fun textToCopy(
        color: Color,
        colorName: String
    ): String {
        val rgb = ColorUtil.colorToRGBArray(color)
        val hsl = ColorUtil.colorToHSL(color)
        val hsv = ColorUtil.colorToHSV(color)
        val alpha = color.alpha.round(2)

        return when (this) {
            Hex -> color.hex()
            Name -> colorName
            HexAndName -> "$colorName - ${color.hex()}"
            RGB -> "rgb(${rgb[0]}, ${rgb[1]}, ${rgb[2]})"
            RGBA -> "rgba(${rgb[0]}, ${rgb[1]}, ${rgb[2]}, $alpha)"
            HSL -> "hsl(${hsl[0].roundToInt()}, ${(hsl[1] * 100).roundToInt()}%, ${(hsl[2] * 100).roundToInt()}%)"
            HSV -> "hsv(${hsv[0].roundToInt()}, ${(hsv[1] * 100).roundToInt()}%, ${(hsv[2] * 100).roundToInt()}%)"
            CMYK -> color.toCmykString()
        }
    }
}

private fun Color.toCmykString(): String {
    val black = 1f - maxOf(red, green, blue)
    val denominator = 1f - black
    val cyan = if (denominator == 0f) 0f else (1f - red - black) / denominator
    val magenta = if (denominator == 0f) 0f else (1f - green - black) / denominator
    val yellow = if (denominator == 0f) 0f else (1f - blue - black) / denominator

    return "cmyk(${(cyan * 100).roundToInt()}%, ${(magenta * 100).roundToInt()}%, ${(yellow * 100).roundToInt()}%, ${(black * 100).roundToInt()}%)"
}

private fun Float.round(digits: Int): String {
    val factor = 10f.pow(digits)
    return ((this * factor).roundToInt() / factor).toString().trimTrailingZero()
}
