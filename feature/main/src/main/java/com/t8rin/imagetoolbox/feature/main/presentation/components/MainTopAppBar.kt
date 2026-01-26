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

@file:Suppress("KotlinConstantConditions")

package com.t8rin.imagetoolbox.feature.main.presentation.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.twotone.BugReport
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.MobileArrowUpRight
import com.t8rin.imagetoolbox.core.resources.icons.PhotoPrints
import com.t8rin.imagetoolbox.core.settings.presentation.model.isFirstLaunch
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppVersionPreReleaseFlavored
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.canPinShortcuts
import com.t8rin.imagetoolbox.core.ui.utils.helper.ProvidesValue
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelection
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.pulsate
import com.t8rin.imagetoolbox.core.ui.widget.modifier.rotateAnimation
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
internal fun MainTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onShowFeaturesFall: () -> Unit,
    onNavigate: (Screen) -> Unit,
    sideSheetState: DrawerState,
    isSheetSlideable: Boolean,
    type: EnhancedTopAppBarType = EnhancedTopAppBarType.Large,
    modifier: Modifier = Modifier
) {
    EnhancedTopAppBar(
        type = type,
        title = {
            MainTitle(onShowSnowfall = onShowFeaturesFall)
        },
        actions = {
            PinShortcutButton()

            EmbeddedPickerButton(
                onNavigate = onNavigate
            )

            SettingsButton(
                onNavigate = onNavigate,
                sideSheetState = sideSheetState,
                isSheetSlideable = isSheetSlideable
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
private fun PinShortcutButton() {
    val context = LocalContext.current

    if (context.canPinShortcuts()) {
        val settingsState = LocalSettingsState.current

        val essentials = rememberLocalEssentials()

        var showShortcutAddingSheet by rememberSaveable {
            mutableStateOf(false)
        }
        EnhancedIconButton(
            onClick = {
                showShortcutAddingSheet = true
            },
            forceMinimumInteractiveComponentSize = false
        ) {
            Icon(
                imageVector = Icons.Outlined.MobileArrowUpRight,
                contentDescription = null
            )
        }
        EnhancedModalBottomSheet(
            visible = showShortcutAddingSheet,
            onDismiss = { showShortcutAddingSheet = it },
            confirmButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    onClick = {
                        showShortcutAddingSheet = false
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            title = {
                TitleItem(
                    text = stringResource(R.string.create_shortcut),
                    icon = Icons.Rounded.MobileArrowUpRight,
                )
            }
        ) {
            val screenList by remember(settingsState.screenList) {
                derivedStateOf {
                    settingsState.screenList.mapNotNull {
                        Screen.entries.find { s -> s.id == it }
                    }.takeIf { it.isNotEmpty() } ?: Screen.entries
                }
            }

            var showShortcutPreviewDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var selectedScreenId by rememberSaveable {
                mutableIntStateOf(-1)
            }
            val colorScheme = MaterialTheme.colorScheme
            var shortcutColor by rememberSaveable(
                selectedScreenId,
                stateSaver = ColorSaver
            ) {
                mutableStateOf(colorScheme.primaryContainer)
            }
            val selectedScreen by remember(screenList, selectedScreenId) {
                derivedStateOf {
                    screenList.find { it.id == selectedScreenId }
                }
            }

            EnhancedAlertDialog(
                visible = showShortcutPreviewDialog,
                onDismissRequest = { showShortcutPreviewDialog = false },
                confirmButton = {
                    EnhancedButton(
                        onClick = {
                            selectedScreen?.let { screen ->
                                essentials.createScreenShortcut(
                                    screen = screen,
                                    tint = shortcutColor
                                )
                            }
                        }
                    ) {
                        Text(stringResource(R.string.add))
                    }
                },
                dismissButton = {
                    EnhancedButton(
                        onClick = {
                            showShortcutPreviewDialog = false
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(stringResource(R.string.close))
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Rounded.MobileArrowUpRight,
                        contentDescription = null
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.create_shortcut)
                    )
                },
                text = {
                    val state = rememberScrollState()
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fadingEdges(
                                scrollableState = state,
                                isVertical = true
                            )
                            .verticalScroll(state)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White, ShapeDefaults.circle)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = selectedScreen?.icon
                                    ?: Icons.Rounded.MobileArrowUpRight,
                                contentDescription = null,
                                tint = shortcutColor,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        ColorSelection(
                            value = shortcutColor,
                            onValueChange = { shortcutColor = it }
                        )
                    }
                }
            )

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(250.dp),
                contentPadding = PaddingValues(16.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(
                    span = StaggeredGridItemSpan.FullLine
                ) {
                    PreferenceItem(
                        title = stringResource(R.string.create_shortcut_title),
                        subtitle = stringResource(R.string.create_shortcut_subtitle),
                        startIcon = Icons.Rounded.PushPin,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(bottom = 8.dp),
                        shape = ShapeDefaults.extremeLarge
                    )
                }

                items(
                    items = screenList,
                    key = { it.id }
                ) { screen ->
                    PreferenceItem(
                        onClick = {
                            showShortcutPreviewDialog = true
                            selectedScreenId = screen.id
                        },
                        startIcon = screen.icon,
                        title = stringResource(screen.title),
                        subtitle = stringResource(screen.subtitle),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun EmbeddedPickerButton(
    onNavigate: (Screen) -> Unit
) {
    var editSheetData by remember {
        mutableStateOf(listOf<Uri>())
    }

    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        editSheetData = uris
    }

    EnhancedIconButton(
        onClick = imagePicker::pickImage,
        onLongClick = { showOneTimeImagePickingDialog = true },
        forceMinimumInteractiveComponentSize = false
    ) {
        Icon(
            imageVector = Icons.Outlined.PhotoPrints,
            contentDescription = "Embedded Picker Button"
        )
    }

    OneTimeImagePickingDialog(
        onDismiss = { showOneTimeImagePickingDialog = false },
        picker = Picker.Multiple,
        imagePicker = imagePicker,
        visible = showOneTimeImagePickingDialog
    )

    ProcessImagesPreferenceSheet(
        uris = editSheetData,
        visible = editSheetData.isNotEmpty(),
        onDismiss = {
            editSheetData = emptyList()
        },
        onNavigate = onNavigate
    )
}

@Composable
private fun SettingsButton(
    onNavigate: (Screen) -> Unit,
    sideSheetState: DrawerState,
    isSheetSlideable: Boolean
) {
    val scope = rememberCoroutineScope()
    val settingsState = LocalSettingsState.current

    if (isSheetSlideable || settingsState.useFullscreenSettings) {
        EnhancedIconButton(
            onClick = {
                if (settingsState.useFullscreenSettings) {
                    onNavigate(Screen.Settings())
                } else {
                    scope.launch {
                        sideSheetState.open()
                    }
                }
            },
            modifier = Modifier
                .pulsate(
                    range = 0.95f..1.2f,
                    enabled = settingsState.isFirstLaunch()
                )
                .rotateAnimation(enabled = settingsState.isFirstLaunch())
        ) {
            Icon(
                imageVector = Icons.Rounded.Settings,
                contentDescription = stringResource(R.string.settings)
            )
        }
    }
}

@Composable
private fun MainTitle(
    onShowSnowfall: () -> Unit
) {
    val settingsState = LocalSettingsState.current

    LocalLayoutDirection.ProvidesValue(LayoutDirection.Ltr) {
        val badgeText = remember {
            "${Screen.FEATURES_COUNT} $AppVersionPreReleaseFlavored".trim()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.marquee()
        ) {
            AnimatedContent(settingsState.mainScreenTitle) { title ->
                Text(title)
            }
            if (BuildConfig.DEBUG) {
                Icon(
                    imageVector = Icons.TwoTone.BugReport,
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = 2.dp)
                        .size(
                            with(LocalDensity.current) {
                                LocalTextStyle.current.fontSize.toDp() * 1.05f
                            }
                        )
                )
            }

            EnhancedBadge(
                content = {
                    Text(badgeText)
                },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier
                    .padding(horizontal = 2.dp)
                    .padding(bottom = 12.dp)
                    .scaleOnTap {
                        onShowSnowfall()
                    }
            )
            Spacer(Modifier.width(12.dp))
            TopAppBarEmoji()
        }
    }
}