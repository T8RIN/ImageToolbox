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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BackgroundColor
import com.t8rin.imagetoolbox.core.resources.icons.ImageTooltip
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.restrict
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.draw.presentation.screenLogic.DrawComponent

@Composable
internal fun DrawContentNoDataControls(
    component: DrawComponent,
    onPickImage: () -> Unit
) {
    var showBackgroundDrawingSetup by rememberSaveable { mutableStateOf(false) }

    val cutout = WindowInsets.displayCutout.asPaddingValues()
    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxHeight(),
        columns = StaggeredGridCells.Adaptive(300.dp),
        horizontalArrangement = Arrangement.spacedBy(
            space = 12.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalItemSpacing = 12.dp,
        contentPadding = PaddingValues(
            bottom = 12.dp + WindowInsets
                .navigationBars
                .asPaddingValues()
                .calculateBottomPadding(),
            top = 12.dp,
            end = 12.dp + cutout.calculateEndPadding(
                LocalLayoutDirection.current
            ),
            start = 12.dp + cutout.calculateStartPadding(
                LocalLayoutDirection.current
            )
        ),
    ) {
        item {
            PreferenceItem(
                onClick = onPickImage,
                startIcon = Icons.Outlined.ImageTooltip,
                title = stringResource(R.string.draw_on_image),
                subtitle = stringResource(R.string.draw_on_image_sub),
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            PreferenceItem(
                onClick = { showBackgroundDrawingSetup = true },
                startIcon = Icons.Outlined.FormatPaint,
                title = stringResource(R.string.draw_on_background),
                subtitle = stringResource(R.string.draw_on_background_sub),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    val drawOnBackgroundParams = component.drawOnBackgroundParams
    val screenSize = LocalScreenSize.current
    val screenWidth = screenSize.widthPx
    val screenHeight = screenSize.heightPx

    var width by remember(
        showBackgroundDrawingSetup,
        screenWidth,
        drawOnBackgroundParams
    ) {
        mutableIntStateOf(drawOnBackgroundParams.width.takeIf { it > -1 } ?: screenWidth)
    }
    var height by remember(
        showBackgroundDrawingSetup,
        screenHeight,
        drawOnBackgroundParams
    ) {
        mutableIntStateOf(drawOnBackgroundParams.height.takeIf { it > -1 } ?: screenHeight)
    }
    var sheetBackgroundColor by rememberSaveable(
        showBackgroundDrawingSetup,
        drawOnBackgroundParams,
        stateSaver = ColorSaver
    ) {
        mutableStateOf(drawOnBackgroundParams.color?.toColor() ?: Color.White)
    }
    EnhancedModalBottomSheet(
        title = {
            TitleItem(
                text = stringResource(R.string.draw),
                icon = Icons.Rounded.FormatPaint
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showBackgroundDrawingSetup = false
                    component.startDrawOnBackground(
                        reqWidth = width,
                        reqHeight = height,
                        color = sheetBackgroundColor
                    )
                }
            ) {
                AutoSizeText(stringResource(R.string.ok))
            }
        },
        sheetContent = {
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .container(shape = ShapeDefaults.extraLarge)
                    ) {
                        RoundedTextField(
                            value = width.takeIf { it != 0 }?.toString() ?: "",
                            onValueChange = {
                                width = it.restrict(8192).toIntOrNull() ?: 0
                            },
                            shape = ShapeDefaults.smallStart,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            label = {
                                Text(stringResource(R.string.width, " "))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    start = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    end = 2.dp
                                )
                        )
                        RoundedTextField(
                            value = height.takeIf { it != 0 }?.toString() ?: "",
                            onValueChange = {
                                height = it.restrict(8192).toIntOrNull() ?: 0
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            shape = ShapeDefaults.smallEnd,
                            label = {
                                Text(stringResource(R.string.height, " "))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    start = 2.dp,
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    end = 8.dp
                                ),
                        )
                    }
                    ColorRowSelector(
                        value = sheetBackgroundColor,
                        onValueChange = { sheetBackgroundColor = it },
                        icon = Icons.Outlined.BackgroundColor,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                            .container(ShapeDefaults.extraLarge)
                    )
                }
            }
        },
        visible = showBackgroundDrawingSetup,
        onDismiss = {
            showBackgroundDrawingSetup = it
        }
    )
}