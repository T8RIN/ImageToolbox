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
import com.t8rin.colors.util.ColorUtil.hex
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.HashTag
import com.t8rin.imagetoolbox.core.resources.icons.ShortText
import com.t8rin.imagetoolbox.core.resources.icons.TagText
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import kotlinx.coroutines.delay

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

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                formats.forEachIndexed { index, format ->
                    val textToCopy = remember(format, color, colorName) {
                        format.textToCopy(
                            hex = color.hex(),
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
    HexAndName;

    @Composable
    fun title(): String = when (this) {
        Hex -> "HEX"
        Name -> stringResource(R.string.name)
        HexAndName -> "HEX + ${stringResource(R.string.name)}"
    }

    fun icon(): ImageVector = when (this) {
        Hex -> Icons.Rounded.HashTag
        Name -> Icons.Outlined.TagText
        HexAndName -> Icons.Rounded.ShortText
    }

    fun textToCopy(
        hex: String,
        colorName: String
    ): String = when (this) {
        Hex -> hex
        Name -> colorName
        HexAndName -> "$colorName - $hex"
    }
}