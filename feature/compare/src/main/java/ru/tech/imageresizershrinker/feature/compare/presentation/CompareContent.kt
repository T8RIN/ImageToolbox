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

package ru.tech.imageresizershrinker.feature.compare.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.dynamic.theme.extractPrimaryColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareScreenContent
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareScreenTopAppBar
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareShareSheet
import ru.tech.imageresizershrinker.feature.compare.presentation.components.CompareType
import ru.tech.imageresizershrinker.feature.compare.presentation.viewModel.CompareViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareContent(
    onGoBack: () -> Unit,
    viewModel: CompareViewModel
) {
    val settingsState = LocalSettingsState.current

    val context = LocalContext.current
    val toastHostState = LocalToastHostState.current
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = settingsState.allowChangeColorByImage

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    var compareProgress by rememberSaveable { mutableFloatStateOf(50f) }

    LaunchedEffect(viewModel.bitmapData) {
        viewModel.bitmapData?.let { (b, a) ->
            if (allowChangeColor && a != null && b != null) {
                delay(100L) //delay to perform screen rotation
                themeState.updateColor(
                    a.second.extractPrimaryColor()
                        .blend(b.second.extractPrimaryColor(), 0.5f)
                )
            }
        }
    }

    val imagePicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { uris ->
        uris.takeIf { it.isNotEmpty() }?.let {
            if (uris.size != 2) {
                scope.launch {
                    toastHostState.showToast(
                        message = context.getString(R.string.pick_two_images),
                        icon = Icons.Rounded.ErrorOutline
                    )
                }
            } else {
                viewModel.updateUris(
                    onSuccess = {
                        compareProgress = 50f
                    },
                    uris = it[0] to it[1],
                    onError = {
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(R.string.something_went_wrong),
                                Icons.Rounded.ErrorOutline
                            )
                        }
                    }
                )
            }
        }
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = viewModel.initialComparableUris != null
    )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val isPortrait by isPortraitOrientationAsState()

    var showShareSheet by rememberSaveable { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            var isLabelsEnabled by rememberSaveable {
                mutableStateOf(true)
            }

            CompareScreenTopAppBar(
                imageNotPicked = viewModel.bitmapData == null,
                scrollBehavior = scrollBehavior,
                onNavigationIconClick = onGoBack,
                onShareButtonClick = {
                    showShareSheet = true
                },
                onSwapImagesClick = viewModel::swap,
                onRotateImagesClick = viewModel::rotate,
                isShareButtonVisible = viewModel.compareType == CompareType.Slide,
                isImagesRotated = viewModel.rotation == 90f,
                titleWhenBitmapsPicked = stringResource(viewModel.compareType.title),
                isLabelsEnabled = isLabelsEnabled,
                onToggleLabelsEnabled = { isLabelsEnabled = it }
            )

            CompareScreenContent(
                bitmapData = viewModel.bitmapData,
                compareType = viewModel.compareType,
                onCompareTypeSelected = viewModel::setCompareType,
                isPortrait = isPortrait,
                compareProgress = compareProgress,
                onCompareProgressChange = {
                    compareProgress = it
                },
                imagePicker = imagePicker,
                isLabelsEnabled = isLabelsEnabled
            )
        }

        if (viewModel.bitmapData == null) {
            EnhancedFloatingActionButton(
                onClick = pickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .align(settingsState.fabAlignment),
                content = {
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = stringResource(R.string.pick_image_alt)
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(stringResource(R.string.pick_image_alt))
                    Spacer(Modifier.width(16.dp))
                }
            )
        }
    }

    val previewBitmap by remember(viewModel.bitmapData) {
        derivedStateOf {
            viewModel.getOverlappedImage(compareProgress)
        }
    }
    CompareShareSheet(
        visible = showShareSheet,
        onVisibleChange = {
            showShareSheet = it
        },
        onSaveBitmap = { imageFormat, oneTimeSaveLocationUri ->
            viewModel.saveBitmap(
                percent = compareProgress,
                imageFormat = imageFormat,
                oneTimeSaveLocationUri = oneTimeSaveLocationUri
            ) { saveResult ->
                context.parseSaveResult(
                    saveResult = saveResult,
                    onSuccess = showConfetti,
                    toastHostState = toastHostState,
                    scope = scope
                )
            }
            showShareSheet = false
        },
        onShare = { imageFormat ->
            viewModel.shareBitmap(compareProgress, imageFormat) {
                showConfetti()
            }
            showShareSheet = false
        },
        onCopy = { imageFormat, manager ->
            viewModel.cacheCurrentImage(
                percent = compareProgress,
                imageFormat = imageFormat
            ) { uri ->
                manager.setClip(uri.asClip(context))
                showConfetti()
            }
        },
        previewBitmap = previewBitmap
    )

    if (viewModel.isImageLoading) {
        LoadingDialog(viewModel::cancelSaving)
    }
}