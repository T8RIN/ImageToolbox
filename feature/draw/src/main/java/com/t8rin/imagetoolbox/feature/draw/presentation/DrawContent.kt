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

package com.t8rin.imagetoolbox.feature.draw.presentation


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.imagetoolbox.core.domain.model.coerceIn
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.rememberAppColorTuple
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.other.DrawLockScreenOrientation
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.saver.PtSaver
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.draw.domain.DrawBehavior
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.BitmapDrawer
import com.t8rin.imagetoolbox.feature.draw.presentation.components.controls.DrawContentControls
import com.t8rin.imagetoolbox.feature.draw.presentation.components.controls.DrawContentNoDataControls
import com.t8rin.imagetoolbox.feature.draw.presentation.components.controls.DrawContentSecondaryControls
import com.t8rin.imagetoolbox.feature.draw.presentation.screenLogic.DrawComponent
import kotlinx.coroutines.launch

@Composable
fun DrawContent(
    component: DrawComponent,
) {
    val settingsState = LocalSettingsState.current
    val themeState = LocalDynamicThemeState.current

    val appColorTuple = rememberAppColorTuple()

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        when (component.drawBehavior) {
            !is DrawBehavior.None if component.haveChanges -> showExitDialog = true

            !is DrawBehavior.None -> {
                component.resetDrawBehavior()
                themeState.updateColorTuple(appColorTuple)
            }

            else -> component.onGoBack()
        }
    }

    AutoContentBasedColors(component.bitmap)

    val imagePicker = rememberImagePicker { uri: Uri ->
        component.setUri(
            uri = uri,
            onFailure = essentials::showFailureToast
        )
    }

    val pickImage = imagePicker::pickImage

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    val screenSize = LocalScreenSize.current
    val isPortrait by isPortraitOrientationAsState()

    var panEnabled by rememberSaveable(component.drawBehavior) { mutableStateOf(false) }

    var strokeWidth by rememberSaveable(
        component.drawBehavior,
        stateSaver = PtSaver
    ) { mutableStateOf(settingsState.defaultDrawLineWidth.pt) }

    var drawColor by rememberSaveable(
        component.drawBehavior,
        stateSaver = ColorSaver
    ) { mutableStateOf(settingsState.defaultDrawColor) }

    var isEraserOn by rememberSaveable(component.drawBehavior) { mutableStateOf(false) }

    val drawMode = component.drawMode

    var alpha by rememberSaveable(component.drawBehavior, drawMode) {
        mutableFloatStateOf(if (drawMode is DrawMode.Highlighter) 0.4f else 1f)
    }

    var brushSoftness by rememberSaveable(component.drawBehavior, drawMode, stateSaver = PtSaver) {
        mutableStateOf(if (drawMode is DrawMode.Neon) 35.pt else 0.pt)
    }

    val drawPathMode = component.drawPathMode

    val drawLineStyle = component.drawLineStyle

    LaunchedEffect(drawMode, strokeWidth) {
        strokeWidth = if (drawMode is DrawMode.Image) {
            strokeWidth.coerceIn(10.pt, 120.pt)
        } else {
            strokeWidth.coerceIn(1.pt, 100.pt)
        }
    }

    val secondaryControls = @Composable {
        DrawContentSecondaryControls(
            component = component,
            panEnabled = panEnabled,
            onTogglePanEnabled = { panEnabled = !panEnabled },
            isEraserOn = isEraserOn,
            onToggleIsEraserOn = { isEraserOn = !isEraserOn }
        )
    }

    val bitmap = component.bitmap ?: (component.drawBehavior as? DrawBehavior.Background)?.run {
        remember { ImageBitmap(width, height).asAndroidBitmap() }
    } ?: remember {
        ImageBitmap(
            screenSize.widthPx,
            screenSize.heightPx
        ).asAndroidBitmap()
    }

    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    AdaptiveBottomScaffoldLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.draw),
                input = component.drawBehavior.takeIf { it !is DrawBehavior.None },
                isLoading = component.isImageLoading,
                size = null,
                originalSize = null
            )
        },
        onGoBack = onBack,
        shouldDisableBackHandler = component.drawBehavior is DrawBehavior.None,
        actions = {
            secondaryControls()
        },
        topAppBarPersistentActions = { scaffoldState ->
            if (component.drawBehavior == DrawBehavior.None) TopAppBarEmoji()
            else {
                if (isPortrait) {
                    EnhancedIconButton(
                        onClick = {
                            essentials.launch {
                                if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                    scaffoldState.bottomSheetState.partialExpand()
                                } else {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = stringResource(R.string.properties)
                        )
                    }
                }
                var editSheetData by remember {
                    mutableStateOf(listOf<Uri>())
                }
                ShareButton(
                    enabled = component.drawBehavior !is DrawBehavior.None,
                    onShare = {
                        component.shareBitmap(showConfetti)
                    },
                    onCopy = {
                        component.cacheCurrentImage(essentials::copyToClipboard)
                    },
                    onEdit = {
                        component.cacheCurrentImage { uri ->
                            editSheetData = listOf(uri)
                        }
                    }
                )
                ProcessImagesPreferenceSheet(
                    uris = editSheetData,
                    visible = editSheetData.isNotEmpty(),
                    onDismiss = {
                        editSheetData = emptyList()
                    },
                    onNavigate = component.onNavigate
                )
                EnhancedIconButton(
                    onClick = component::clearDrawing,
                    enabled = component.drawBehavior !is DrawBehavior.None && component.havePaths
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
            }
        },
        mainContent = {
            AnimatedContent(
                targetState = remember(bitmap) {
                    derivedStateOf {
                        bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                    }
                }.value,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { imageBitmap ->
                val direction = LocalLayoutDirection.current
                val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                BitmapDrawer(
                    imageBitmap = imageBitmap,
                    paths = component.paths,
                    strokeWidth = strokeWidth,
                    brushSoftness = brushSoftness,
                    drawColor = drawColor.copy(alpha),
                    onAddPath = component::addPath,
                    isEraserOn = isEraserOn,
                    drawMode = drawMode,
                    modifier = Modifier
                        .padding(
                            start = WindowInsets
                                .displayCutout
                                .asPaddingValues()
                                .calculateStartPadding(direction)
                        )
                        .padding(16.dp)
                        .aspectRatio(aspectRatio, isPortrait)
                        .fillMaxSize(),
                    panEnabled = panEnabled,
                    onRequestFiltering = component::filter,
                    drawPathMode = drawPathMode,
                    backgroundColor = component.backgroundColor,
                    drawLineStyle = drawLineStyle,
                    helperGridParams = component.helperGridParams
                )
            }
        },
        controls = {
            DrawContentControls(
                component = component,
                secondaryControls = secondaryControls,
                drawColor = drawColor,
                onDrawColorChange = { drawColor = it },
                strokeWidth = strokeWidth,
                onStrokeWidthChange = { strokeWidth = it },
                brushSoftness = brushSoftness,
                onBrushSoftnessChange = { brushSoftness = it },
                alpha = alpha,
                onAlphaChange = { alpha = it }
            )
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.drawBehavior is DrawBehavior.None,
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                isSecondaryButtonVisible = component.drawBehavior !is DrawBehavior.Background,
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                isPrimaryButtonVisible = component.drawBehavior !is DrawBehavior.None,
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
                },
                showNullDataButtonAsContainer = true
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmap,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        enableNoDataScroll = false,
        noDataControls = {
            DrawContentNoDataControls(
                component = component,
                onPickImage = pickImage
            )
        },
        canShowScreenData = component.drawBehavior !is DrawBehavior.None,
        showActionsInTopAppBar = false,
        mainContentWeight = 0.65f
    )

    LoadingDialog(
        visible = component.isSaving || component.isImageLoading,
        onCancelLoading = component::cancelSaving,
        canCancel = component.isSaving
    )

    ExitWithoutSavingDialog(
        onExit = {
            if (component.drawBehavior !is DrawBehavior.None) {
                component.resetDrawBehavior()
                themeState.updateColorTuple(appColorTuple)
            } else component.onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    DrawLockScreenOrientation()
}