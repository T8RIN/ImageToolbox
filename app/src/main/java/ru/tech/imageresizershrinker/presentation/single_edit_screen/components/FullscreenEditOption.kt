package ru.tech.imageresizershrinker.presentation.single_edit_screen.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenEditOption(
    visible: Boolean,
    canGoBack: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    sheetSize: Float = 0.6f,
    showControls: Boolean = true,
    controls: @Composable () -> Unit,
    fabButtons: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    topAppBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsState = LocalSettingsState.current
    AnimatedVisibility(visible) {
        Surface(Modifier.fillMaxSize()) {
            Column {
                if (useScaffold) {
                    val scaffoldState = rememberBottomSheetScaffoldState()
                    BottomSheetScaffold(
                        topBar = topAppBar,
                        scaffoldState = scaffoldState,
                        sheetPeekHeight = 80.dp + WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding(),
                        sheetDragHandle = null,
                        sheetShape = RectangleShape,
                        sheetContent = {
                            Column(if (showControls) Modifier.fillMaxHeight(sheetSize) else Modifier) {
                                BottomAppBar(
                                    modifier = Modifier.drawHorizontalStroke(true),
                                    actions = {
                                        actions()
                                        if (showControls) {
                                            IconButton(
                                                onClick = {
                                                    scope.launch {
                                                        if (!scaffoldState.bottomSheetState.hasExpandedState) scaffoldState.bottomSheetState.expand()
                                                        else scaffoldState.bottomSheetState.partialExpand()
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Rounded.Build, null)
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
                                            fabButtons()
                                        }
                                    }
                                )
                                if (showControls) {
                                    HorizontalDivider()
                                    Column(Modifier.verticalScroll(rememberScrollState())) {
                                        controls()
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
                    topAppBar()
                    Row(
                        modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.weight(0.8f)
                        ) {
                            content()
                        }

                        if (showControls) {
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                    .background(MaterialTheme.colorScheme.outlineVariant())
                            )
                            Column(
                                Modifier
                                    .weight(0.7f)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                controls()
                            }
                        }

                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                                .padding(start = 20.dp)
                        )
                        Column(
                            Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxHeight()
                                .navigationBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(
                                8.dp,
                                Alignment.CenterVertically
                            )
                        ) {
                            fabButtons()
                        }
                    }
                }
            }
        }
        if (visible) {
            BackHandler {
                if (canGoBack) onDismiss()
            }
        }
    }
}