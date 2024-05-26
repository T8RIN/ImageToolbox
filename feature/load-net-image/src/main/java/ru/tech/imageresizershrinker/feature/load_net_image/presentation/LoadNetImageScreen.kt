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

package ru.tech.imageresizershrinker.feature.load_net_image.presentation

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.rememberAppColorTuple
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageEdit
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.viewModel.LoadNetImageViewModel

@Composable
fun LoadNetImageScreen(
    url: String,
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    viewModel: LoadNetImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val themeState = LocalDynamicThemeState.current
    val settingsState = LocalSettingsState.current
    val allowChangeColor = settingsState.allowChangeColorByImage
    val toastHostState = LocalToastHostState.current

    val confettiHostState = LocalConfettiHostState.current

    val scope = rememberCoroutineScope()

    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = settingsState.isDynamicColors,
        darkTheme = settingsState.isNightMode
    )

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let { image ->
            if (allowChangeColor) {
                themeState.updateColorByImage(image)
            }
        } ?: themeState.updateColorTuple(appColorTuple)
    }

    var scaleType by rememberSaveable(
        saver = Saver(
            save = {
                if (it == ContentScale.FillWidth) 0 else 1
            },
            restore = {
                mutableStateOf(
                    if (it == 0) {
                        ContentScale.FillWidth
                    } else {
                        ContentScale.Fit
                    }
                )
            }
        )
    ) { mutableStateOf(ContentScale.FillWidth) }

    val isLandscape =
        LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact || LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var link by rememberSaveable(url) { mutableStateOf(url) }

    val showZoomSheet = rememberSaveable { mutableStateOf(false) }

    ZoomModalSheet(
        data = viewModel.bitmap,
        visible = showZoomSheet
    )

    var wantToEdit by rememberSaveable { mutableStateOf(false) }

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        viewModel.saveBitmap(link, it) { saveResult ->
            context.parseSaveResult(
                saveResult = saveResult,
                onSuccess = {
                    confettiHostState.showConfetti()
                },
                toastHostState = toastHostState,
                scope = scope
            )
        }
    }

    var imageState: AsyncImagePainter.State by remember {
        mutableStateOf(
            AsyncImagePainter.State.Empty
        )
    }

    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    AdaptiveLayoutScreen(
        title = {
            Text(
                text = stringResource(R.string.load_image_from_net),
                textAlign = TextAlign.Center
            )
        },
        onGoBack = onGoBack,
        actions = {
            ShareButton(
                enabled = viewModel.bitmap != null,
                onShare = {
                    viewModel.shareBitmap(showConfetti)
                },
                onCopy = { manager ->
                    viewModel.cacheCurrentImage { uri ->
                        manager.setClip(uri.asClip(context))
                        showConfetti()
                    }
                }
            )
        },
        topAppBarPersistentActions = {
            if (viewModel.bitmap == null) {
                TopAppBarEmoji()
            } else {
                ZoomButton(
                    onClick = { showZoomSheet.value = true },
                    visible = viewModel.bitmap != null,
                )
            }
        },
        imagePreview = {
            AnimatedContent(
                targetState = scaleType,
                modifier = Modifier.fillMaxSize()
            ) { scale ->
                Picture(
                    allowHardware = false,
                    model = link,
                    modifier = Modifier
                        .then(if (scale == ContentScale.FillWidth) Modifier.fillMaxWidth() else Modifier)
                        .padding(bottom = 16.dp)
                        .then(
                            if (viewModel.bitmap == null) Modifier.height(140.dp)
                            else Modifier
                        )
                        .container()
                        .padding(4.dp),
                    contentScale = scale,
                    shape = MaterialTheme.shapes.small,
                    error = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceContainer),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.BrokenImage,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                    .size(64.dp)
                            )
                            Text(stringResource(id = R.string.no_image))
                            Spacer(Modifier.height(8.dp))
                        }
                    },
                    onState = {
                        if (it is AsyncImagePainter.State.Error) {
                            viewModel.updateBitmap(null)
                        } else if (it is AsyncImagePainter.State.Success) {
                            viewModel.updateBitmap(it.result.drawable.toBitmap())
                        }
                        imageState = it
                    },
                )
            }
        },
        controls = {
            ToggleGroupButton(
                modifier = Modifier
                    .container(shape = RoundedCornerShape(24.dp)),
                title = stringResource(id = R.string.content_scale),
                enabled = viewModel.bitmap != null,
                items = listOf(
                    stringResource(R.string.fill),
                    stringResource(R.string.fit)
                ),
                selectedIndex = (scaleType == ContentScale.Fit).toInt(),
                indexChanged = {
                    scaleType = if (it == 0) {
                        ContentScale.FillWidth
                    } else {
                        ContentScale.Fit
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
            RoundedTextField(
                modifier = Modifier
                    .container(shape = RoundedCornerShape(24.dp))
                    .padding(8.dp),
                value = link,
                onValueChange = {
                    link = it
                },
                singleLine = false,
                label = {
                    Text(stringResource(id = R.string.image_link))
                },
                endIcon = {
                    AnimatedVisibility(link.isNotBlank()) {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = { link = "" },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = stringResource(R.string.cancel)
                            )
                        }
                    }
                }
            )
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (false) to !isLandscape,
                onSecondaryButtonClick = {
                    viewModel.bitmap?.let { bitmap ->
                        viewModel.cacheImage(
                            image = bitmap,
                            imageInfo = ImageInfo(
                                width = bitmap.width,
                                height = bitmap.height,
                            )
                        )
                        wantToEdit = true
                    }
                },
                isPrimaryButtonVisible = imageState is AsyncImagePainter.State.Success,
                isSecondaryButtonVisible = imageState is AsyncImagePainter.State.Success,
                secondaryButtonIcon = Icons.Outlined.ImageEdit,
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (!isLandscape) actions()
                }
            )
            if (showFolderSelectionDialog) {
                OneTimeSaveLocationSelectionDialog(
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = saveBitmap
                )
            }
        },
        canShowScreenData = true,
        isPortrait = !isLandscape
    )

    if (viewModel.isSaving) {
        LoadingDialog(
            onCancelLoading = viewModel::cancelSaving
        )
    }

    ProcessImagesPreferenceSheet(
        uris = listOfNotNull(viewModel.tempUri),
        visible = wantToEdit,
        onDismiss = {
            wantToEdit = it
        },
        onNavigate = { screen ->
            scope.launch {
                wantToEdit = false
                delay(200)
                onNavigate(screen)
            }
        }
    )
}

private fun Boolean.toInt() = if (this) 1 else 0