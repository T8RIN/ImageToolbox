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

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.WifiTetheringError
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import ru.tech.imageresizershrinker.core.data.utils.safeAspectRatio
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageEdit
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isLandscapeOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.ImagesPreviewWithSelection
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.core.ui.widget.utils.AutoContentBasedColors
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent

@Composable
fun LoadNetImageContent(
    component: LoadNetImageComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    AutoContentBasedColors(component.bitmap)

    val isLandscape by isLandscapeOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmaps(
            oneTimeSaveLocationUri = it,
            onResult = essentials::parseSaveResults
        )
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            Text(
                text = stringResource(R.string.load_image_from_net),
                textAlign = TextAlign.Center,
                modifier = Modifier.marquee()
            )
        },
        onGoBack = component.onGoBack,
        actions = {
            ShareButton(
                enabled = component.parsedImages.isNotEmpty(),
                onShare = {
                    component.performSharing(showConfetti)
                },
                onCopy = { manager ->
                    component.cacheCurrentImage { uri ->
                        manager.setClip(uri.asClip(context))
                        showConfetti()
                    }
                }
            )
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.bitmap != null,
            )
        },
        topAppBarPersistentActions = {
            if (component.bitmap == null) {
                TopAppBarEmoji()
            } else {
                AnimatedVisibility(component.parsedImages.size > 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val pagesSize by remember(component.imageFrames, component.parsedImages) {
                            derivedStateOf {
                                component.imageFrames.getFramePositions(component.parsedImages.size).size
                            }
                        }
                        AnimatedVisibility(
                            visible = pagesSize != component.parsedImages.size,
                            enter = fadeIn() + scaleIn() + expandHorizontally(),
                            exit = fadeOut() + scaleOut() + shrinkHorizontally()
                        ) {
                            EnhancedIconButton(
                                onClick = component::selectAllImages
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.SelectAll,
                                    contentDescription = "Select All"
                                )
                            }
                        }
                        AnimatedVisibility(
                            modifier = Modifier
                                .padding(8.dp)
                                .container(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    resultPadding = 0.dp
                                ),
                            visible = pagesSize != 0
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                pagesSize.takeIf { it != 0 }?.let {
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = it.toString(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                EnhancedIconButton(
                                    onClick = component::clearImagesSelection
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = stringResource(R.string.close)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        imagePreview = {
            Picture(
                allowHardware = false,
                model = component.targetUrl,
                modifier = Modifier
                    .then(
                        if (component.bitmap == null) {
                            Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        } else {
                            Modifier.aspectRatio(component.bitmap?.safeAspectRatio ?: 2f)
                        }
                    )
                    .container(
                        resultPadding = 8.dp
                    ),
                isLoadingFromDifferentPlace = component.isImageLoading,
                contentScale = ContentScale.FillBounds,
                shape = MaterialTheme.shapes.small,
                error = {
                    if (component.bitmap != null) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = component.bitmap,
                            contentDescription = contentDescription,
                            contentScale = ContentScale.FillBounds
                        )
                    } else {
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
                    }
                }
            )
        },
        controls = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoundedTextField(
                    modifier = Modifier
                        .container(shape = RoundedCornerShape(24.dp))
                        .padding(8.dp),
                    value = component.targetUrl,
                    onValueChange = {
                        component.updateTargetUrl(
                            newUrl = it,
                            onFailure = {
                                essentials.showToast(
                                    message = it,
                                    icon = Icons.Rounded.WifiTetheringError
                                )
                            }
                        )
                    },
                    singleLine = false,
                    label = {
                        Text(stringResource(id = R.string.image_link))
                    },
                    endIcon = {
                        AnimatedVisibility(component.targetUrl.isNotBlank()) {
                            EnhancedIconButton(
                                onClick = {
                                    component.updateTargetUrl("")
                                },
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
                AnimatedVisibility(component.parsedImages.size > 1) {
                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val screenWidth = this.maxWidth

                        ImagesPreviewWithSelection(
                            imageUris = component.parsedImages,
                            imageFrames = component.imageFrames,
                            onFrameSelectionChange = component::updateImageFrames,
                            isPortrait = true,
                            isLoadingImages = component.isImageLoading,
                            contentScale = ContentScale.Inside,
                            contentPadding = PaddingValues(20.dp),
                            modifier = Modifier
                                .height(
                                    (130.dp * component.parsedImages.size).coerceAtMost(420.dp)
                                )
                                .layout { measurable, constraints ->
                                    val result =
                                        measurable.measure(
                                            constraints.copy(
                                                maxWidth = (screenWidth + 40.dp).roundToPx()
                                            )
                                        )
                                    layout(result.measuredWidth, result.measuredHeight) {
                                        result.place(0, 0)
                                    }
                                }
                        )
                    }
                }
            }
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            BottomButtonsBlock(
                targetState = false to !isLandscape,
                onSecondaryButtonClick = {
                    component.cacheImages {
                        editSheetData = it
                    }
                },
                isPrimaryButtonVisible = component.parsedImages.isNotEmpty(),
                isSecondaryButtonVisible = component.parsedImages.isNotEmpty(),
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
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmap,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )

            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = {
                    editSheetData = emptyList()
                },
                onNavigate = component.onNavigate
            )
        },
        canShowScreenData = true,
        isPortrait = !isLandscape
    )

    ZoomModalSheet(
        data = component.bitmap,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        }
    )

    if (component.left != -1) {
        LoadingDialog(
            visible = component.isSaving,
            done = component.done,
            left = component.left,
            onCancelLoading = component::cancelSaving
        )
    } else {
        LoadingDialog(
            visible = component.isSaving,
            onCancelLoading = component::cancelSaving
        )
    }
}