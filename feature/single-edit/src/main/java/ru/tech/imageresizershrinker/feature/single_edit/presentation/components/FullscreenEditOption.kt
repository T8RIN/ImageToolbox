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

package ru.tech.imageresizershrinker.feature.single_edit.presentation.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheetDefaults

@OptIn(ExperimentalMaterial3Api::class)
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
    val scope = rememberCoroutineScope()

    var showExitDialog by remember(visible) { mutableStateOf(false) }
    val internalOnDismiss = {
        if (!canGoBack) showExitDialog = true
        else onDismiss()
    }
    val direction = LocalLayoutDirection.current
    val focus = LocalFocusManager.current
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(Modifier.fillMaxSize()) {
            Column {
                if (useScaffold) {
                    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
                    BottomSheetScaffold(
                        topBar = {
                            topAppBar {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
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
                        sheetContent = {
                            Column(
                                modifier
                                    .heightIn(max = screenHeight * 0.7f)
                                    .pointerInput(Unit) {
                                        detectTapGestures { focus.clearFocus() }
                                    }
                            ) {
                                BottomAppBar(
                                    modifier = Modifier.drawHorizontalStroke(true),
                                    actions = {
                                        actions()
                                        if (showControls) {
                                            EnhancedIconButton(
                                                containerColor = Color.Transparent,
                                                contentColor = LocalContentColor.current,
                                                enableAutoShadowAndBorder = false,
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
                                if (showControls) {
                                    Column(
                                        modifier = Modifier
                                            .verticalScroll(rememberScrollState())
                                            .navigationBarsPadding(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        ProvideContainerDefaults(
                                            color = SimpleSheetDefaults.contentContainerColor
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
                    topAppBar {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = internalOnDismiss
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.close)
                            )
                        }
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
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
                                    .pointerInput(Unit) {
                                        detectTapGestures { focus.clearFocus() }
                                    }
                                    .verticalScroll(rememberScrollState())
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
        if (visible) {
            BackHandler { internalOnDismiss() }
            ExitWithoutSavingDialog(
                onExit = onDismiss,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )
        }
    }
}