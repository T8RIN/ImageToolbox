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

package com.t8rin.imagetoolbox.feature.main.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuOpen
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.idapgroup.snowfall.snowfall
import com.idapgroup.snowfall.types.FlakeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalWindowSizeClass
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withModifier
import com.t8rin.imagetoolbox.core.ui.widget.other.LocalToastHostState
import com.t8rin.imagetoolbox.feature.main.presentation.components.MainContentImpl
import com.t8rin.imagetoolbox.feature.main.presentation.components.MainDrawerContent
import com.t8rin.imagetoolbox.feature.main.presentation.screenLogic.MainComponent
import com.t8rin.imagetoolbox.feature.settings.presentation.SettingsContent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainContent(
    component: MainComponent
) {
    val isUpdateAvailable by component.isUpdateAvailable.subscribeAsState()

    val settingsState = LocalSettingsState.current
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact

    val sideSheetState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val isSheetSlideable = (isGrid && !settingsState.showSettingsInLandscape) || !isGrid
    val layoutDirection = LocalLayoutDirection.current

    var sheetExpanded by rememberSaveable { mutableStateOf(false) }

    val drawerContent = remember(isSheetSlideable) {
        movableContentOf {
            MainDrawerContent(
                sideSheetState = sideSheetState,
                isSheetSlideable = isSheetSlideable,
                sheetExpanded = sheetExpanded,
                layoutDirection = layoutDirection,
                settingsBlockContent = {
                    SettingsContent(
                        component = component.settingsComponent,
                        appBarNavigationIcon = { showSettingsSearch, onCloseSearch ->
                            AnimatedContent(
                                targetState = !isSheetSlideable to showSettingsSearch,
                                transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() }
                            ) { (expanded, searching) ->
                                if (searching) {
                                    EnhancedIconButton(onClick = onCloseSearch) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = stringResource(R.string.exit)
                                        )
                                    }
                                } else if (expanded) {
                                    EnhancedIconButton(
                                        onClick = {
                                            sheetExpanded = !sheetExpanded
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.MenuOpen,
                                            contentDescription = "Expand",
                                            modifier = Modifier.rotate(
                                                animateFloatAsState(if (!sheetExpanded) 0f else 180f).value
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            )
        }
    }

    var showFeaturesFall by rememberSaveable { mutableStateOf(false) }

    val content = remember {
        movableContentOf {
            val context = LocalContext.current
            val toastHost = LocalToastHostState.current
            val scope = rememberCoroutineScope()
            MainContentImpl(
                layoutDirection = layoutDirection,
                isSheetSlideable = isSheetSlideable,
                sideSheetState = sideSheetState,
                sheetExpanded = sheetExpanded,
                isGrid = isGrid,
                onShowFeaturesFall = {
                    showFeaturesFall = true
                },
                onGetClipList = component::parseClipList,
                onTryGetUpdate = {
                    component.tryGetUpdate(
                        isNewRequest = true,
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
                isUpdateAvailable = isUpdateAvailable,
                onNavigate = component.onNavigate,
                onToggleFavorite = component::toggleFavoriteScreen
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
    ) {
        if (settingsState.useFullscreenSettings) {
            content()
        } else {
            if (isSheetSlideable) {
                LocalLayoutDirection.ProvidesValue(
                    if (layoutDirection == LayoutDirection.Ltr) LayoutDirection.Rtl
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
                                        DrawerDefaults.standardContainerColor
                                    )
                                )
                        )
                    }
                    drawerContent.withModifier(
                        modifier = Modifier.container(
                            shape = RectangleShape,
                            borderColor = MaterialTheme.colorScheme.outlineVariant(
                                0.3f,
                                DrawerDefaults.standardContainerColor
                            ),
                            autoShadowElevation = 2.dp,
                            resultPadding = 0.dp
                        )
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showFeaturesFall,
            modifier = Modifier
                .fillMaxSize(),
            enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -it / 4 },
            exit = fadeOut(tween(1000)) + slideOutVertically(tween(1000)) { it / 4 }
        ) {
            val snowFallList = Screen.entries.mapNotNull { screen ->
                screen.icon?.let { rememberVectorPainter(image = it) }
            }
            val color = MaterialTheme.colorScheme.onSecondaryContainer.copy(0.5f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .snowfall(
                        type = FlakeType.Custom(snowFallList),
                        color = color
                    )
            )
            LaunchedEffect(showFeaturesFall) {
                if (showFeaturesFall) {
                    delay(5000)
                    showFeaturesFall = false
                }
            }
            DisposableEffect(Unit) {
                onDispose { showFeaturesFall = false }
            }
        }
    }
}