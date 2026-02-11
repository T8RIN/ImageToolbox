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

package com.t8rin.imagetoolbox.feature.image_preview.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.FolderOpened
import com.t8rin.imagetoolbox.core.resources.icons.ImageEdit
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFolderPicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.sortedByType
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.controls.SortButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitBackHandler
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagePreviewGrid
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.image_preview.presentation.screenLogic.ImagePreviewComponent

@Composable
fun ImagePreviewContent(
    component: ImagePreviewComponent
) {
    var showExitDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val onBack = {
        if (component.uris.isNullOrEmpty()) component.onGoBack()
        else showExitDialog = true
    }

    val initialShowImagePreviewDialog = !component.initialUris.isNullOrEmpty()

    val settingsState = LocalSettingsState.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val imagePicker = rememberImagePicker(onSuccess = component::updateUris)

    val isLoadingImages = component.isImageLoading

    var previousFolder by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val openDirectoryLauncher = rememberFolderPicker(
        onSuccess = { uri ->
            previousFolder = uri
            component.updateUrisFromTree(uri)
        }
    )

    val pickImage = imagePicker::pickImage

    val selectedUris by remember(component.uris, component.imageFrames) {
        derivedStateOf {
            component.getSelectedUris() ?: emptyList()
        }
    }
    var wantToEdit by rememberSaveable(selectedUris.isNotEmpty()) {
        mutableStateOf(false)
    }

    var gridInvalidations by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(component.uris) {
        gridInvalidations++
    }

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    EnhancedTopAppBar(
                        type = EnhancedTopAppBarType.Large,
                        scrollBehavior = scrollBehavior,
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.marquee()
                            ) {
                                Text(
                                    text = stringResource(R.string.image_preview)
                                )
                                AnimatedVisibility(!component.uris.isNullOrEmpty()) {
                                    EnhancedBadge(
                                        content = {
                                            val prefix = if (isLoadingImages) "~" else ""
                                            Text(
                                                text = "$prefix${component.uris.orEmpty().size}"
                                            )
                                        },
                                        containerColor = MaterialTheme.colorScheme.tertiary,
                                        contentColor = MaterialTheme.colorScheme.onTertiary,
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .padding(bottom = 12.dp)
                                            .scaleOnTap {
                                                showConfetti()
                                            }
                                    )
                                }
                            }
                        },
                        navigationIcon = {
                            EnhancedIconButton(
                                onClick = component.onGoBack
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.exit)
                                )
                            }
                        },
                        actions = {
                            val isCanClear = selectedUris.isNotEmpty()
                            val isCanSelectAll =
                                component.uris?.size != selectedUris.size && component.uris != null

                            AnimatedContent(
                                targetState = (!isCanSelectAll && !isCanClear) to selectedUris.isEmpty(),
                                modifier = Modifier.size(40.dp)
                            ) { (notSelection, haveUris) ->
                                if (notSelection && haveUris) {
                                    TopAppBarEmoji()
                                } else if (haveUris) {
                                    SortButton(
                                        modifier = Modifier.size(40.dp),
                                        iconSize = 24.dp,
                                        containerColor = Color.Transparent,
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                        onSortTypeSelected = { sortType ->
                                            component.asyncUpdateUris(
                                                onFinish = { gridInvalidations++ },
                                                action = {
                                                    it.orEmpty().sortedByType(
                                                        sortType = sortType
                                                    )
                                                }
                                            )
                                        }
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = isCanSelectAll && isCanClear,
                                enter = fadeIn() + scaleIn() + expandHorizontally(),
                                exit = fadeOut() + scaleOut() + shrinkHorizontally()
                            ) {
                                EnhancedIconButton(
                                    onClick = {
                                        component.updateImageFrames(ImageFrames.All)
                                    }
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
                                        shape = ShapeDefaults.circle,
                                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                        resultPadding = 0.dp
                                    ),
                                visible = isCanClear
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = selectedUris.size.toString(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    EnhancedIconButton(
                                        onClick = {
                                            component.updateImageFrames(
                                                ImageFrames.ManualSelection(emptyList())
                                            )
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = stringResource(R.string.close)
                                        )
                                    }
                                }
                            }
                        }
                    )
                },
                contentWindowInsets = WindowInsets()
            ) { contentPadding ->
                AnimatedContent(
                    targetState = !component.uris.isNullOrEmpty() || isLoadingImages,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                ) { canShowGrid ->
                    if (canShowGrid) {
                        Crossfade(gridInvalidations) { key ->
                            key(key) {
                                ImagePreviewGrid(
                                    data = component.uris,
                                    onAddImages = component::updateUris,
                                    onShareImage = {
                                        component.shareImages(
                                            uriList = listOf(element = it),
                                            onComplete = showConfetti
                                        )
                                    },
                                    onRemove = component::removeUri,
                                    initialShowImagePreviewDialog = initialShowImagePreviewDialog,
                                    onNavigate = component.onNavigate,
                                    imageFrames = component.imageFrames,
                                    onFrameSelectionChange = component::updateImageFrames
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .enhancedVerticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ImageNotPickedWidget(
                                onPickImage = pickImage,
                                modifier = Modifier.padding(
                                    PaddingValues(
                                        bottom = 88.dp + WindowInsets
                                            .navigationBars
                                            .asPaddingValues()
                                            .calculateBottomPadding(),
                                        top = 12.dp,
                                        end = 12.dp,
                                        start = 12.dp
                                    )
                                )
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .align(settingsState.fabAlignment)
            ) {
                AnimatedContent(targetState = selectedUris.isNotEmpty()) { isFramesSelected ->
                    if (isFramesSelected) {
                        EnhancedFloatingActionButton(
                            onClick = {
                                wantToEdit = true
                            },
                            content = {
                                Spacer(Modifier.width(16.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ImageEdit,
                                    contentDescription = stringResource(R.string.edit)
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(stringResource(R.string.edit))
                                Spacer(Modifier.width(16.dp))
                            }
                        )
                    } else {
                        EnhancedFloatingActionButton(
                            onClick = pickImage,
                            onLongClick = {
                                showOneTimeImagePickingDialog = true
                            },
                            content = {
                                Spacer(Modifier.width(16.dp))
                                Icon(
                                    imageVector = Icons.Rounded.AddPhotoAlt,
                                    contentDescription = stringResource(R.string.pick_image_alt)
                                )
                                Spacer(Modifier.width(16.dp))
                                Text(stringResource(R.string.pick_image_alt))
                                Spacer(Modifier.width(16.dp))
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                AnimatedContent(targetState = selectedUris.isNotEmpty()) { isFramesSelected ->
                    if (isFramesSelected) {
                        EnhancedFloatingActionButton(
                            onClick = {
                                component.shareImages(
                                    uriList = null,
                                    onComplete = showConfetti
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            type = EnhancedFloatingActionButtonType.SecondaryHorizontal,
                            content = {
                                Icon(
                                    imageVector = Icons.Rounded.Share,
                                    contentDescription = stringResource(R.string.share)
                                )
                            }
                        )
                    } else {
                        EnhancedFloatingActionButton(
                            onClick = {
                                openDirectoryLauncher.pickFolder(previousFolder)
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            type = EnhancedFloatingActionButtonType.SecondaryHorizontal,
                            content = {
                                Icon(
                                    imageVector = Icons.Rounded.FolderOpened,
                                    contentDescription = stringResource(R.string.folder)
                                )
                            }
                        )
                    }
                }
            }

            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )

            ProcessImagesPreferenceSheet(
                uris = selectedUris,
                visible = wantToEdit,
                onDismiss = {
                    wantToEdit = false
                },
                onNavigate = component.onNavigate
            )

            ExitBackHandler(
                enabled = !component.uris.isNullOrEmpty(),
                onBack = onBack
            )
        }
    }

    LoadingDialog(
        visible = isLoadingImages,
        onCancelLoading = component::cancelImageLoading,
        isForSaving = false
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog,
        title = stringResource(id = R.string.image_preview),
        text = stringResource(id = R.string.preview_closing),
        icon = Icons.Outlined.ImageSearch
    )
}