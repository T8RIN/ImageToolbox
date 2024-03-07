/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.compare.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.RotateLeft
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreenTopAppBar(
    imageNotPicked: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    onNavigationIconClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onSwapImagesClick: () -> Unit,
    onRotateImagesClick: () -> Unit,
    isShareButtonVisible: Boolean,
    isImagesRotated: Boolean,
    titleWhenBitmapsPicked: String
) {
    if (imageNotPicked) {
        LargeTopAppBar(
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = onNavigationIconClick
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                }
            },
            title = {
                Marquee {
                    Text(stringResource(R.string.compare))
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            actions = {
                TopAppBarEmoji()
            },
            modifier = Modifier.drawHorizontalStroke()
        )
    } else {
        TopAppBar(
            navigationIcon = {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = onNavigationIconClick
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                }
            },
            actions = {
                AnimatedVisibility(visible = isShareButtonVisible) {
                    EnhancedIconButton(
                        containerColor = Color.Transparent,
                        contentColor = LocalContentColor.current,
                        enableAutoShadowAndBorder = false,
                        onClick = onShareButtonClick
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = null
                        )
                    }
                }
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = onSwapImagesClick
                ) {
                    Icon(Icons.Rounded.SwapHoriz, null)
                }
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = onRotateImagesClick
                ) {
                    AnimatedContent(isImagesRotated) { rotated ->
                        Icon(
                            imageVector = if (rotated) Icons.AutoMirrored.Rounded.RotateLeft
                            else Icons.AutoMirrored.Rounded.RotateRight,
                            contentDescription = null
                        )
                    }
                }
            },
            title = {
                Marquee {
                    AnimatedContent(
                        targetState = titleWhenBitmapsPicked
                    ) { text ->
                        Text(text)
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.drawHorizontalStroke()
        )
    }
}