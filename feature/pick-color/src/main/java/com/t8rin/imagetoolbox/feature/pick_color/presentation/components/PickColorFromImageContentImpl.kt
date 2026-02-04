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
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.colordetector.ImageColorDetector
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker

@Composable
internal fun ColumnScope.PickColorFromImageContentImpl(
    bitmap: Bitmap?,
    isPortrait: Boolean,
    panEnabled: Boolean,
    onColorChange: (Color) -> Unit,
    onPickImage: () -> Unit,
    magnifierButton: @Composable () -> Unit,
    switch: @Composable () -> Unit,
    onOneTimePickImage: () -> Unit,
    color: Color
) {
    val settingsState = LocalSettingsState.current

    Box(
        modifier = Modifier.weight(1f)
    ) {
        bitmap?.let {
            if (isPortrait) {
                AnimatedContent(
                    targetState = it
                ) { bitmap ->
                    ImageColorDetector(
                        panEnabled = panEnabled,
                        imageBitmap = bitmap.asImageBitmap(),
                        color = color,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .windowInsetsPadding(
                                WindowInsets.navigationBars
                                    .only(WindowInsetsSides.Bottom)
                                    .union(WindowInsets.displayCutout)
                            )
                            .container(resultPadding = 8.dp)
                            .clip(
                                ShapeDefaults.small
                            )
                            .transparencyChecker(),
                        isMagnifierEnabled = settingsState.magnifierEnabled,
                        onColorChange = onColorChange
                    )
                }
            } else {
                Row {
                    Box(
                        modifier = Modifier.weight(0.8f)
                    ) {
                        Box(Modifier.align(Alignment.Center)) {
                            AnimatedContent(
                                targetState = it
                            ) { bitmap ->
                                val direction = LocalLayoutDirection.current
                                ImageColorDetector(
                                    panEnabled = panEnabled,
                                    imageBitmap = bitmap.asImageBitmap(),
                                    color = color,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(20.dp)
                                        .windowInsetsPadding(
                                            WindowInsets.navigationBars
                                                .only(WindowInsetsSides.Bottom)
                                                .union(WindowInsets.displayCutout)
                                        )
                                        .padding(
                                            start = WindowInsets
                                                .displayCutout
                                                .asPaddingValues()
                                                .calculateStartPadding(direction)
                                        )
                                        .container(resultPadding = 8.dp)
                                        .clip(ShapeDefaults.small)
                                        .transparencyChecker(),
                                    isMagnifierEnabled = settingsState.magnifierEnabled,
                                    onColorChange = onColorChange
                                )
                            }
                        }
                    }
                    val direction = LocalLayoutDirection.current
                    Column(
                        modifier = Modifier
                            .container(
                                shape = RectangleShape,
                                resultPadding = 0.dp
                            )
                            .fillMaxHeight()
                            .padding(horizontal = 20.dp)
                            .padding(
                                end = WindowInsets.displayCutout
                                    .asPaddingValues()
                                    .calculateEndPadding(direction)
                            )
                            .navigationBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        magnifierButton()
                        Spacer(modifier = Modifier.height(8.dp))
                        switch()
                        Spacer(modifier = Modifier.height(8.dp))
                        EnhancedFloatingActionButton(
                            onClick = onPickImage,
                            onLongClick = onOneTimePickImage
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlt,
                                contentDescription = stringResource(R.string.pick_image_alt)
                            )
                        }
                    }
                }
            }
        } ?: Column(Modifier.enhancedVerticalScroll(rememberScrollState())) {
            ImageNotPickedWidget(
                onPickImage = onPickImage,
                modifier = Modifier
                    .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                    .navigationBarsPadding()
            )
        }
    }
}