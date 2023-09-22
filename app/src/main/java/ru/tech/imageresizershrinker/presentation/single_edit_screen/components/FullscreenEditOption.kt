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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenEditOption(
    visible: Boolean,
    canGoBack: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    modifier: Modifier = Modifier,
    sheetSize: Float = 0.6f,
    showControls: Boolean = true,
    controls: @Composable (BottomSheetScaffoldState?) -> Unit,
    fabButtons: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    topAppBar: @Composable (closeButton: @Composable () -> Unit) -> Unit,
    scaffoldState: BottomSheetScaffoldState = rememberBottomSheetScaffoldState(),
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsState = LocalSettingsState.current

    var showExitDialog by remember(visible) { mutableStateOf(false) }
    val internalOnDismiss = {
        if (!canGoBack) showExitDialog = true
        else onDismiss()
    }
    AnimatedVisibility(visible) {
        Surface(Modifier.fillMaxSize()) {
            Column {
                if (useScaffold) {
                    BottomSheetScaffold(
                        topBar = {
                            topAppBar {
                                IconButton(onClick = internalOnDismiss) {
                                    Icon(Icons.Rounded.Close, null)
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
                                modifier.then(
                                    if (showControls && sheetSize > 0f) Modifier.fillMaxHeight(
                                        sheetSize
                                    ) else Modifier
                                )
                            ) {
                                BottomAppBar(
                                    modifier = Modifier.drawHorizontalStroke(true),
                                    actions = {
                                        actions()
                                        if (showControls) {
                                            IconButton(
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
                                                Icon(Icons.Rounded.Tune, null)
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
                                    Column(
                                        modifier = Modifier
                                            .verticalScroll(rememberScrollState())
                                            .navigationBarsPadding(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        controls(scaffoldState)
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
                        IconButton(onClick = internalOnDismiss) {
                            Icon(Icons.Rounded.Close, null)
                        }
                    }
                    Row(
                        modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .weight(0.8f)
                                .clipToBounds()
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
                                modifier = Modifier
                                    .weight(0.7f)
                                    .verticalScroll(rememberScrollState()),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                controls(null)
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
            BackHandler { internalOnDismiss() }
            ExitWithoutSavingDialog(
                onExit = onDismiss,
                onDismiss = { showExitDialog = false },
                visible = showExitDialog
            )
        }
    }
}