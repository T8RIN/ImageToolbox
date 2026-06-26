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

package com.t8rin.imagetoolbox.feature.pick_color.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.colors.ImageColorDetector
import com.t8rin.colors.parser.ColorNameParser
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.utils.animation.animateColorAsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.toHex
import com.t8rin.imagetoolbox.core.ui.widget.buttons.PanModeButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ColorCopyFormatSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.derivative.EnhancedZoomableModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer

@Composable
fun PickColorFromImageSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    bitmap: Bitmap?,
    onColorChange: (Color) -> Unit,
    color: Color
) {
    val settingsState = LocalSettingsState.current
    var panEnabled by rememberSaveable { mutableStateOf(false) }
    var showColorCopyDialog by rememberSaveable(visible) { mutableStateOf(false) }
    val displayColor = color.takeOrElse { Color.Transparent }
    val displayColorName = remember(displayColor) {
        ColorNameParser.parseColorName(displayColor)
    }

    EnhancedZoomableModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        confirmButton = {
            PanModeButton(
                selected = panEnabled,
                onClick = { panEnabled = !panEnabled }
            )
        },
        title = {
            val interactionSource = remember { MutableInteractionSource() }

            val shape = shapeByInteraction(
                shape = ShapeDefaults.default,
                pressedShape = ShapeDefaults.pressed,
                interactionSource = interactionSource
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 8.dp, end = 16.dp)
                    .container(
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        shape = shape,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = { showColorCopyDialog = true }
                    )
                    .padding(8.dp)
                    .height(IntrinsicSize.Max)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            color = animateColorAsState(displayColor).value,
                            shape = ShapeDefaults.small
                        )
                        .size(40.dp)
                        .border(
                            width = settingsState.borderWidth,
                            color = MaterialTheme.colorScheme.outlineVariant(
                                onTopOf = animateColorAsState(displayColor).value
                            ),
                            shape = ShapeDefaults.small
                        )
                        .clip(ShapeDefaults.small),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = stringResource(R.string.copy),
                        tint = animateColorAsState(
                            displayColor.inverse(
                                fraction = { cond ->
                                    if (cond) 0.8f
                                    else 0.5f
                                },
                                darkMode = displayColor.luminance() < 0.3f
                            )
                        ).value,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Text(
                        modifier = Modifier
                            .clip(ShapeDefaults.mini)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .border(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(
                                    onTopOf = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                shape = ShapeDefaults.mini
                            )
                            .padding(horizontal = 8.dp),
                        text = displayColor.toHex(),
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = displayColorName,
                        style = LocalTextStyle.current.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            lineHeight = 14.sp
                        )
                    )
                }
            }
        }
    ) {
        remember(bitmap) { bitmap?.asImageBitmap() }?.let {
            ImageColorDetector(
                panEnabled = panEnabled,
                color = displayColor,
                imageBitmap = it,
                onColorChange = onColorChange,
                isMagnifierEnabled = settingsState.magnifierEnabled,
                boxModifier = Modifier.fillMaxSize()
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .shimmer(true)
        )
    }

    ColorCopyFormatSelectionDialog(
        visible = showColorCopyDialog,
        onDismiss = { showColorCopyDialog = false },
        color = displayColor,
        colorName = displayColorName
    )
}