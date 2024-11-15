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

package ru.tech.imageresizershrinker.core.ui.widget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun AdaptiveBottomScaffoldLayoutScreen(
    title: @Composable () -> Unit,
    onGoBack: () -> Unit,
    shouldDisableBackHandler: Boolean,
    isPortrait: Boolean,
    actions: @Composable RowScope.() -> Unit,
    topAppBarPersistentActions: @Composable RowScope.(BottomSheetScaffoldState) -> Unit = {},
    mainContent: @Composable () -> Unit,
    mainContentWeight: Float = 0.5f,
    controls: @Composable ColumnScope.() -> Unit,
    buttons: @Composable (actions: @Composable RowScope.() -> Unit) -> Unit,
    noDataControls: @Composable () -> Unit = {},
    canShowScreenData: Boolean,
    showActionsInTopAppBar: Boolean = true,
    collapseTopAppBarWhenHaveData: Boolean = true,
    autoClearFocus: Boolean = true,
) {
    val settingsState = LocalSettingsState.current

    val scrollBehavior = if (collapseTopAppBarWhenHaveData && canShowScreenData) null
    else TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val scrollState = rememberScrollState()


    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            confirmValueChange = {
                when (it) {
                    SheetValue.Hidden -> false
                    else -> true
                }
            }
        )
    )

    val focus = LocalFocusManager.current

    LaunchedEffect(scaffoldState.bottomSheetState.currentValue) {
        if (scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded) {
            focus.clearFocus()
        }
    }

    val content: @Composable (PaddingValues) -> Unit = { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .then(
                    if (scrollBehavior != null) {
                        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
                    } else Modifier
                )
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                EnhancedTopAppBar(
                    type = if (collapseTopAppBarWhenHaveData && canShowScreenData) EnhancedTopAppBarType.Normal
                    else EnhancedTopAppBarType.Large,
                    scrollBehavior = scrollBehavior,
                    title = title,
                    navigationIcon = {
                        EnhancedIconButton(
                            onClick = onGoBack
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.exit)
                            )
                        }
                    },
                    actions = {
                        if (!isPortrait && canShowScreenData && showActionsInTopAppBar) actions()
                        topAppBarPersistentActions(scaffoldState)
                    },
                )

                canShowScreenData.takeIf { it }?.let {
                    if (isPortrait) {
                        mainContent()
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .zIndex(-100f)
                                    .container(shape = RectangleShape, resultPadding = 0.dp)
                                    .weight(0.8f)
                            ) {
                                mainContent()
                            }
                            Column(
                                Modifier
                                    .weight(mainContentWeight)
                                    .verticalScroll(scrollState)
                            ) {
                                controls()
                            }
                            buttons(actions)
                        }
                    }
                } ?: Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                        .navigationBarsPadding()
                ) {
                    noDataControls()
                }
            }

            if (!canShowScreenData) {
                Box(
                    modifier = Modifier.align(settingsState.fabAlignment)
                ) {
                    buttons(actions)
                }
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = if (autoClearFocus) {
            Modifier.pointerInput(Unit) {
                detectTapGestures {
                    focus.clearFocus()
                }
            }
        } else Modifier
    ) {
        if (isPortrait && canShowScreenData) {
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            BottomSheetScaffold(
                scaffoldState = scaffoldState,
                sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                    .calculateBottomPadding(),
                sheetDragHandle = null,
                sheetShape = RectangleShape,
                sheetContent = {
                    Column(
                        Modifier
                            .heightIn(max = screenHeight * 0.7f)
                            .pointerInput(Unit) {
                                detectTapGestures { focus.clearFocus() }
                            }
                    ) {
                        buttons(actions)
                        ProvideContainerDefaults(
                            color = EnhancedBottomSheetDefaults.contentContainerColor
                        ) {
                            Column(Modifier.verticalScroll(scrollState)) {
                                controls()
                            }
                        }
                    }
                },
                content = content
            )
        } else {
            content(PaddingValues())
        }
    }

    BackHandler(
        enabled = !shouldDisableBackHandler,
        onBack = onGoBack
    )
}