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

package ru.tech.imageresizershrinker.feature.main.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.idapgroup.snowfall.snowfall
import com.idapgroup.snowfall.types.FlakeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isInstalledFromPlayStore
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.main.presentation.components.MainScreenContent
import ru.tech.imageresizershrinker.feature.main.presentation.components.MainScreenDrawerContent
import ru.tech.imageresizershrinker.feature.main.presentation.components.settings.SettingsBlock
import ru.tech.imageresizershrinker.feature.main.presentation.viewModel.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val settingsState = LocalSettingsState.current
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact

    val sideSheetState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isSheetSlideable = !isGrid
    val layoutDirection = LocalLayoutDirection.current

    var sheetExpanded by rememberSaveable { mutableStateOf(false) }

    val drawerContent = remember {
        movableContentOf {
            MainScreenDrawerContent(
                sideSheetState = sideSheetState,
                isSheetSlideable = isSheetSlideable,
                sheetExpanded = sheetExpanded,
                onToggleSheetExpanded = { sheetExpanded = it },
                layoutDirection = layoutDirection,
                settingsBlockContent = { settingsSearchKeyword ->
                    SettingsBlock(
                        searchKeyword = settingsSearchKeyword,
                        viewModel = viewModel
                    )
                }
            )
        }
    }

    var showSnowfall by rememberSaveable { mutableStateOf(false) }

    val content = @Composable {
        val context = LocalContext.current
        val toastHost = LocalToastHostState.current
        val scope = rememberCoroutineScope()
        MainScreenContent(
            layoutDirection = layoutDirection,
            isSheetSlideable = isSheetSlideable,
            sideSheetState = sideSheetState,
            sheetExpanded = sheetExpanded,
            isGrid = isGrid,
            onShowSnowfall = {
                showSnowfall = true
            },
            onGetClipList = viewModel::updateUris,
            onTryGetUpdate = {
                viewModel.tryGetUpdate(
                    newRequest = true,
                    installedFromMarket = context.isInstalledFromPlayStore(),
                    onNoUpdates = {
                        scope.launch {
                            toastHost.showToast(
                                icon = Icons.Rounded.FileDownloadOff,
                                message = context.getString(R.string.no_updates)
                            )
                        }
                    }
                )
            },
            updateAvailable = viewModel.updateAvailable
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isSheetSlideable) {
            CompositionLocalProvider(
                LocalLayoutDirection provides if (layoutDirection == LayoutDirection.Ltr) LayoutDirection.Rtl
                else LayoutDirection.Ltr
            ) {
                ModalNavigationDrawer(
                    drawerState = sideSheetState,
                    drawerContent = drawerContent,
                    content = content
                )
            }
        } else {
            Row {
                content.withModifier(
                    modifier = Modifier.weight(1f)
                )
                if (settingsState.borderWidth > 0.dp) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(settingsState.borderWidth)
                            .background(
                                MaterialTheme.colorScheme.outlineVariant(
                                    0.3f,
                                    DrawerDefaults.containerColor
                                )
                            )
                    )
                }
                drawerContent.withModifier(
                    modifier = Modifier.container(
                        shape = RectangleShape,
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            1.dp
                        ),
                        borderColor = MaterialTheme.colorScheme.outlineVariant(
                            0.3f,
                            DrawerDefaults.containerColor
                        ),
                        autoShadowElevation = 2.dp,
                        resultPadding = 0.dp
                    )
                )
            }
        }

        val snowFallList = Screen.entries.mapNotNull { screen ->
            screen.icon?.let { rememberVectorPainter(image = it) }
        }
        AnimatedVisibility(
            visible = showSnowfall,
            modifier = Modifier
                .fillMaxSize(),
            enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -it / 4 },
            exit = fadeOut(tween(1000)) + slideOutVertically(tween(1000)) { it / 4 }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .snowfall(
                        type = FlakeType.Custom(snowFallList),
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
            )
            LaunchedEffect(showSnowfall) {
                if (showSnowfall) {
                    delay(5000)
                    showSnowfall = false
                }
            }
            DisposableEffect(Unit) {
                onDispose { showSnowfall = false }
            }
        }
    }
}