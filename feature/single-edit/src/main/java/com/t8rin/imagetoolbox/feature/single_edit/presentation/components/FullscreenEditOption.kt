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

package com.t8rin.imagetoolbox.feature.single_edit.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.PredictiveBackObserver
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitBackHandler
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.modifier.onSwipeDown
import com.t8rin.imagetoolbox.core.ui.widget.modifier.toShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withLayoutCorners
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FullscreenEditOption(
    visible: Boolean,
    canGoBack: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    modifier: Modifier = Modifier,
    showControls: Boolean = true,
    controls: @Composable (BottomSheetScaffoldState?) -> Unit,
    fabButtons: (@Composable () -> Unit)?,
    actions: @Composable RowScope.() -> Unit,
    topAppBar: @Composable (closeButton: @Composable () -> Unit) -> Unit,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    content: @Composable () -> Unit
) {
    var predictiveBackProgress by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(predictiveBackProgress, visible) {
        if (!visible && predictiveBackProgress != 0f) {
            delay(600)
            predictiveBackProgress = 0f
        }
    }

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(tween(600)),
        exit = fadeOut(tween(600))
    ) {
        var showExitDialog by remember(visible) { mutableStateOf(false) }
        val internalOnDismiss = {
            if (!canGoBack) showExitDialog = true
            else onDismiss()
        }
        val direction = LocalLayoutDirection.current

        val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)
        val scale = (1f - animatedPredictiveBackProgress).coerceAtLeast(0.75f)


        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(0.5f * scale))
        )

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .withLayoutCorners { corners ->
                    graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        shape = corners.toShape(animatedPredictiveBackProgress)
                        clip = true
                    }
                }
        ) {
            Column {
                if (useScaffold) {
                    val screenHeight = LocalScreenSize.current.height
                    val sheetSwipeEnabled =
                        scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded
                                && !scaffoldState.bottomSheetState.isAnimationRunning

                    BottomSheetScaffold(
                        topBar = {
                            topAppBar {
                                EnhancedIconButton(
                                    onClick = internalOnDismiss
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.close)
                                    )
                                }
                            }
                        },
                        scaffoldState = scaffoldState,
                        sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding(),
                        sheetDragHandle = null,
                        sheetShape = RectangleShape,
                        sheetSwipeEnabled = sheetSwipeEnabled,
                        sheetContent = {
                            Scaffold(
                                modifier = modifier
                                    .heightIn(max = screenHeight * 0.7f)
                                    .clearFocusOnTap(),
                                topBar = {
                                    val scope = rememberCoroutineScope()
                                    Box(
                                        modifier = Modifier.onSwipeDown(!sheetSwipeEnabled) {
                                            scope.launch {
                                                scaffoldState.bottomSheetState.partialExpand()
                                            }
                                        }
                                    ) {
                                        BottomAppBar(
                                            modifier = Modifier.drawHorizontalStroke(true),
                                            actions = {
                                                actions()
                                                if (showControls) {
                                                    EnhancedIconButton(
                                                        onClick = {
                                                            scope.launch {
                                                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                                                    scaffoldState.bottomSheetState.partialExpand()
                                                                } else {
                                                                    scaffoldState.bottomSheetState.expand()
                                                                }
                                                            }
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Rounded.Tune,
                                                            contentDescription = stringResource(R.string.properties)
                                                        )
                                                    }
                                                }
                                            },
                                            floatingActionButton = {
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(
                                                        8.dp,
                                                        Alignment.CenterHorizontally
                                                    ),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    if (fabButtons != null) {
                                                        fabButtons()
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            ) { contentPadding ->
                                if (showControls) {
                                    Column(
                                        modifier = Modifier
                                            .enhancedVerticalScroll(rememberScrollState())
                                            .padding(contentPadding),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ProvideContainerDefaults(
                                            color = EnhancedBottomSheetDefaults.contentContainerColor
                                        ) {
                                            controls(scaffoldState)
                                        }
                                    }
                                }
                            }
                        },
                        content = {
                            Box(Modifier.padding(it)) {
                                content()
                            }
                        }
                    )
                } else {
                    Scaffold(
                        topBar = {
                            topAppBar {
                                EnhancedIconButton(
                                    onClick = internalOnDismiss
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.close)
                                    )
                                }
                            }
                        },
                        contentWindowInsets = WindowInsets()
                    ) { contentPadding ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(contentPadding)
                        ) {
                            Box(
                                Modifier
                                    .container(
                                        shape = RectangleShape,
                                        resultPadding = 0.dp
                                    )
                                    .weight(0.8f)
                                    .fillMaxHeight()
                                    .clipToBounds()
                            ) {
                                content()
                            }

                            if (showControls) {
                                Column(
                                    modifier = Modifier
                                        .weight(0.7f)
                                        .clearFocusOnTap()
                                        .enhancedVerticalScroll(rememberScrollState())
                                        .then(
                                            if (fabButtons == null) {
                                                Modifier.padding(
                                                    end = WindowInsets.displayCutout
                                                        .asPaddingValues()
                                                        .calculateEndPadding(direction)
                                                )
                                            } else Modifier
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ProvideContainerDefaults(
                                        color = MaterialTheme.colorScheme.surfaceContainerLowest
                                    ) {
                                        controls(null)
                                    }
                                }
                            }
                            fabButtons?.let {
                                Column(
                                    Modifier
                                        .container(
                                            shape = RectangleShape,
                                            resultPadding = 0.dp
                                        )
                                        .padding(horizontal = 20.dp)
                                        .padding(
                                            end = WindowInsets.displayCutout
                                                .asPaddingValues()
                                                .calculateEndPadding(direction)
                                        )
                                        .fillMaxHeight()
                                        .navigationBarsPadding(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(
                                        8.dp,
                                        Alignment.CenterVertically
                                    )
                                ) {
                                    it()
                                }
                            }
                        }
                    }
                }
            }
        }
        if (visible) {
            if (canGoBack) {
                PredictiveBackObserver(
                    onProgress = {
                        predictiveBackProgress = it / 6f
                    },
                    onClean = { isCompleted ->
                        if (isCompleted) {
                            internalOnDismiss()
                            delay(400)
                        }
                        predictiveBackProgress = 0f
                    }
                )
            } else {
                ExitBackHandler(onBack = internalOnDismiss)
            }

            ExitWithoutSavingDialog(
                onExit = onDismiss,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )
        }
    }
}