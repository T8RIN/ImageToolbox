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

package ru.tech.imageresizershrinker.core.ui.widget.buttons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke

@Composable
fun BottomButtonsBlock(
    isNoData: Boolean,
    onSecondaryButtonClick: () -> Unit,
    onSecondaryButtonLongClick: (() -> Unit)? = null,
    secondaryButtonIcon: ImageVector = Icons.Rounded.AddPhotoAlternate,
    secondaryButtonText: String = stringResource(R.string.pick_image_alt),
    onPrimaryButtonClick: () -> Unit,
    onPrimaryButtonLongClick: (() -> Unit)? = null,
    primaryButtonIcon: ImageVector = Icons.Rounded.Save,
    primaryButtonText: String = "",
    isPrimaryButtonVisible: Boolean = true,
    isSecondaryButtonVisible: Boolean = true,
    showNullDataButtonAsContainer: Boolean = false,
    columnarFab: (@Composable ColumnScope.() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit,
    isPrimaryButtonEnabled: Boolean = true,
    showColumnarFabInRow: Boolean = false,
) {
    val isPortrait by isPortraitOrientationAsState()

    AnimatedContent(
        targetState = isNoData to isPortrait,
        transitionSpec = {
            fadeIn() + slideInVertically { it / 2 } togetherWith fadeOut() + slideOutVertically { it / 2 }
        }
    ) { (isEmptyState, inside) ->
        if (isEmptyState) {
            val button = @Composable {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.navigationBars.union(
                                WindowInsets.displayCutout.only(
                                    WindowInsetsSides.Horizontal
                                )
                            )
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EnhancedFloatingActionButton(
                        onClick = onSecondaryButtonClick,
                        onLongClick = onSecondaryButtonLongClick,
                        content = {
                            Spacer(Modifier.width(16.dp))
                            Icon(
                                imageVector = secondaryButtonIcon,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(secondaryButtonText)
                            Spacer(Modifier.width(16.dp))
                        }
                    )
                    if (showColumnarFabInRow && columnarFab != null) {
                        Column(
                            content = columnarFab
                        )
                    }
                }
            }
            if (showNullDataButtonAsContainer) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawHorizontalStroke(true)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainer
                        ),
                    horizontalArrangement = Arrangement.Center
                ) {
                    button()
                }
            } else button()
        } else if (inside) {
            BottomAppBar(
                modifier = Modifier.drawHorizontalStroke(true),
                actions = actions,
                floatingActionButton = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedVisibility(visible = isSecondaryButtonVisible) {
                            EnhancedFloatingActionButton(
                                onClick = onSecondaryButtonClick,
                                onLongClick = onSecondaryButtonLongClick,
                                containerColor = takeColorFromScheme {
                                    if (isPrimaryButtonVisible) tertiaryContainer
                                    else primaryContainer
                                }
                            ) {
                                Icon(
                                    imageVector = secondaryButtonIcon,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility(visible = showColumnarFabInRow) {
                            columnarFab?.let {
                                Column { it() }
                            }
                        }
                        AnimatedVisibility(visible = isPrimaryButtonVisible) {
                            EnhancedFloatingActionButton(
                                onClick = onPrimaryButtonClick.takeIf { isPrimaryButtonEnabled },
                                onLongClick = onPrimaryButtonLongClick.takeIf { isPrimaryButtonEnabled },
                                interactionSource = remember { MutableInteractionSource() }.takeIf { isPrimaryButtonEnabled },
                                containerColor = takeColorFromScheme {
                                    if (isPrimaryButtonEnabled) primaryContainer
                                    else surfaceContainerHighest
                                },
                                contentColor = takeColorFromScheme {
                                    if (isPrimaryButtonEnabled) onPrimaryContainer
                                    else outline
                                }
                            ) {
                                AnimatedContent(
                                    targetState = primaryButtonIcon to primaryButtonText,
                                    transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
                                ) { (icon, text) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (text.isNotEmpty()) {
                                            Spacer(Modifier.width(16.dp))
                                        }
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null
                                        )
                                        if (text.isNotEmpty()) {
                                            Spacer(Modifier.width(16.dp))
                                            Text(text)
                                            Spacer(Modifier.width(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
        } else {
            val direction = LocalLayoutDirection.current
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .container(
                        shape = RectangleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .navigationBarsPadding()
                    .padding(
                        end = WindowInsets.displayCutout
                            .asPaddingValues()
                            .calculateEndPadding(direction)
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row { actions() }
                Spacer(Modifier.height(8.dp))
                AnimatedVisibility(visible = isSecondaryButtonVisible) {
                    EnhancedFloatingActionButton(
                        onClick = onSecondaryButtonClick,
                        onLongClick = onSecondaryButtonLongClick,
                        containerColor = takeColorFromScheme {
                            if (isPrimaryButtonVisible) tertiaryContainer
                            else primaryContainer
                        }
                    ) {
                        Icon(
                            imageVector = secondaryButtonIcon,
                            contentDescription = null
                        )
                    }
                }
                columnarFab?.let {
                    Spacer(Modifier.height(8.dp))
                    it()
                }
                AnimatedVisibility(visible = isPrimaryButtonVisible) {
                    EnhancedFloatingActionButton(
                        onClick = onPrimaryButtonClick.takeIf { isPrimaryButtonEnabled },
                        onLongClick = onPrimaryButtonLongClick.takeIf { isPrimaryButtonEnabled },
                        interactionSource = remember { MutableInteractionSource() }.takeIf { isPrimaryButtonEnabled },
                        containerColor = takeColorFromScheme {
                            if (isPrimaryButtonEnabled) primaryContainer
                            else surfaceContainerHighest
                        },
                        contentColor = takeColorFromScheme {
                            if (isPrimaryButtonEnabled) onPrimaryContainer
                            else outline
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        AnimatedContent(
                            targetState = primaryButtonIcon to primaryButtonText,
                            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
                        ) { (icon, text) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (text.isNotEmpty()) {
                                    Spacer(Modifier.width(16.dp))
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null
                                )
                                if (text.isNotEmpty()) {
                                    Spacer(Modifier.width(16.dp))
                                    Text(text)
                                    Spacer(Modifier.width(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}