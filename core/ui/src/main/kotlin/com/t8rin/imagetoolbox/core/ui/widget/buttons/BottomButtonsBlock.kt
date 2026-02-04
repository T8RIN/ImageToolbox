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

package com.t8rin.imagetoolbox.core.ui.widget.buttons

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
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.ProvideFABType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke

@Composable
fun BottomButtonsBlock(
    isNoData: Boolean,
    onSecondaryButtonClick: () -> Unit,
    onSecondaryButtonLongClick: (() -> Unit)? = null,
    secondaryButtonIcon: ImageVector = Icons.Rounded.AddPhotoAlt,
    secondaryButtonText: String = stringResource(R.string.pick_image_alt),
    onPrimaryButtonClick: () -> Unit,
    onPrimaryButtonLongClick: (() -> Unit)? = null,
    primaryButtonIcon: ImageVector = Icons.Rounded.Save,
    primaryButtonText: String = "",
    isPrimaryButtonVisible: Boolean = true,
    isSecondaryButtonVisible: Boolean = true,
    showNullDataButtonAsContainer: Boolean = false,
    middleFab: (@Composable ColumnScope.() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit,
    isPrimaryButtonEnabled: Boolean = true,
    showMiddleFabInRow: Boolean = false,
    isScreenHaveNoDataContent: Boolean = false,
    primaryButtonContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    primaryButtonContentColor: Color = contentColorFor(primaryButtonContainerColor),
) {
    val isPortrait by isPortraitOrientationAsState()
    val spacing = 8.dp

    AnimatedContent(
        targetState = Triple(isNoData, isPortrait, isScreenHaveNoDataContent),
        transitionSpec = {
            fadeIn() + slideInVertically { it / 2 } togetherWith fadeOut() + slideOutVertically { it / 2 }
        }
    ) { (isEmptyState, portrait, isHaveNoDataContent) ->
        if (isEmptyState) {
            val cutout = WindowInsets.displayCutout.only(
                WindowInsetsSides.Horizontal
            )

            val button = @Composable {
                Row(
                    modifier = Modifier
                        .windowInsetsPadding(
                            WindowInsets.navigationBars.union(cutout)
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(spacing),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val middle = @Composable {
                        if (showMiddleFabInRow && middleFab != null) {
                            ProvideFABType(EnhancedFloatingActionButtonType.SecondaryHorizontal) {
                                Column(
                                    content = middleFab
                                )
                            }
                        }
                    }

                    if (!isPrimaryButtonVisible) middle()

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

                    if (isPrimaryButtonVisible) middle()
                }
            }
            if (showNullDataButtonAsContainer) {
                if (!isPortrait && isHaveNoDataContent) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerLow
                            )
                            .consumeWindowInsets(cutout),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        button()
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawHorizontalStroke(true)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer
                            ),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        button()
                    }
                }
            } else {
                button()
            }
        } else if (portrait) {
            BottomAppBar(
                modifier = Modifier.drawHorizontalStroke(true),
                actions = actions,
                floatingActionButton = {
                    Row {
                        val middle = @Composable {
                            AnimatedVisibility(visible = showMiddleFabInRow) {
                                middleFab?.let {
                                    ProvideFABType(EnhancedFloatingActionButtonType.SecondaryHorizontal) {
                                        Column(
                                            modifier = Modifier.padding(end = spacing),
                                            content = { it() }
                                        )
                                    }
                                }
                            }
                        }
                        if (!isPrimaryButtonVisible) middle()

                        AnimatedVisibility(visible = isSecondaryButtonVisible) {
                            EnhancedFloatingActionButton(
                                onClick = onSecondaryButtonClick,
                                onLongClick = onSecondaryButtonLongClick,
                                containerColor = takeColorFromScheme {
                                    if (isPrimaryButtonVisible) tertiaryContainer
                                    else primaryContainer
                                },
                                type = if (isPrimaryButtonVisible) {
                                    EnhancedFloatingActionButtonType.SecondaryHorizontal
                                } else {
                                    EnhancedFloatingActionButtonType.Primary
                                },
                                modifier = Modifier.padding(end = spacing)
                            ) {
                                Icon(
                                    imageVector = secondaryButtonIcon,
                                    contentDescription = null
                                )
                            }
                        }

                        if (isPrimaryButtonVisible) middle()

                        AnimatedVisibility(visible = isPrimaryButtonVisible) {
                            EnhancedFloatingActionButton(
                                onClick = onPrimaryButtonClick.takeIf { isPrimaryButtonEnabled },
                                onLongClick = onPrimaryButtonLongClick.takeIf { isPrimaryButtonEnabled },
                                interactionSource = remember { MutableInteractionSource() }.takeIf { isPrimaryButtonEnabled },
                                containerColor = takeColorFromScheme {
                                    if (isPrimaryButtonEnabled) primaryButtonContainerColor
                                    else surfaceContainerHighest
                                },
                                contentColor = takeColorFromScheme {
                                    if (isPrimaryButtonEnabled) primaryButtonContentColor
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
                    .enhancedVerticalScroll(rememberScrollState())
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
                val middle = @Composable {
                    middleFab?.let {
                        Spacer(Modifier.height(spacing))
                        ProvideFABType(EnhancedFloatingActionButtonType.SecondaryVertical) {
                            it()
                        }
                    }
                }

                Row { actions() }

                if (!isPrimaryButtonVisible) middle()

                Spacer(Modifier.height(spacing))

                AnimatedVisibility(visible = isSecondaryButtonVisible) {
                    EnhancedFloatingActionButton(
                        onClick = onSecondaryButtonClick,
                        onLongClick = onSecondaryButtonLongClick,
                        containerColor = takeColorFromScheme {
                            if (isPrimaryButtonVisible) tertiaryContainer
                            else primaryContainer
                        },
                        type = if (isPrimaryButtonVisible) {
                            EnhancedFloatingActionButtonType.SecondaryVertical
                        } else {
                            EnhancedFloatingActionButtonType.Primary
                        }
                    ) {
                        Icon(
                            imageVector = secondaryButtonIcon,
                            contentDescription = null
                        )
                    }
                }

                if (isPrimaryButtonVisible) middle()

                AnimatedVisibility(visible = isPrimaryButtonVisible) {
                    EnhancedFloatingActionButton(
                        onClick = onPrimaryButtonClick.takeIf { isPrimaryButtonEnabled },
                        onLongClick = onPrimaryButtonLongClick.takeIf { isPrimaryButtonEnabled },
                        interactionSource = remember { MutableInteractionSource() }.takeIf { isPrimaryButtonEnabled },
                        containerColor = takeColorFromScheme {
                            if (isPrimaryButtonEnabled) primaryButtonContainerColor
                            else surfaceContainerHighest
                        },
                        contentColor = takeColorFromScheme {
                            if (isPrimaryButtonEnabled) primaryButtonContentColor
                            else outline
                        },
                        modifier = Modifier.padding(top = spacing)
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