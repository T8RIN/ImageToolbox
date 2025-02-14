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
import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.automirrored.rounded.RotateLeft
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee

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
    titleWhenBitmapsPicked: String,
    onToggleLabelsEnabled: (Boolean) -> Unit,
    isLabelsEnabled: Boolean,
    isLabelsButtonVisible: Boolean
) {
    if (imageNotPicked) {
        EnhancedTopAppBar(
            type = EnhancedTopAppBarType.Large,
            scrollBehavior = scrollBehavior,
            navigationIcon = {
                EnhancedIconButton(
                    onClick = onNavigationIconClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.exit)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.compare),
                    modifier = Modifier.marquee()
                )
            },
            actions = {
                TopAppBarEmoji()
            }
        )
    } else {
        EnhancedTopAppBar(
            navigationIcon = {
                EnhancedIconButton(
                    onClick = onNavigationIconClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.exit)
                    )
                }
            },
            actions = {
                AnimatedVisibility(visible = isShareButtonVisible) {
                    ShareButton(onShare = onShareButtonClick)
                }
                EnhancedIconButton(
                    onClick = onSwapImagesClick
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SwapHoriz,
                        contentDescription = "Swap"
                    )
                }
                EnhancedIconButton(
                    onClick = onRotateImagesClick
                ) {
                    AnimatedContent(isImagesRotated) { rotated ->
                        Icon(
                            imageVector = if (rotated) Icons.AutoMirrored.Rounded.RotateLeft
                            else Icons.AutoMirrored.Rounded.RotateRight,
                            contentDescription = "Rotate"
                        )
                    }
                }
                AnimatedVisibility(visible = isLabelsButtonVisible) {
                    EnhancedIconButton(
                        onClick = {
                            onToggleLabelsEnabled(!isLabelsEnabled)
                        },
                        containerColor = animateColorAsState(
                            if (isLabelsEnabled) MaterialTheme.colorScheme.secondary
                            else Color.Transparent
                        ).value
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Label,
                            contentDescription = "Label"
                        )
                    }
                }
            },
            title = {
                AnimatedContent(
                    targetState = titleWhenBitmapsPicked,
                    modifier = Modifier.marquee()
                ) { text ->
                    Text(text)
                }
            }
        )
    }
}