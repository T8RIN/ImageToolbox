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

package ru.tech.imageresizershrinker.feature.image_preview.presentation

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.FolderOpen
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.listFilesInDirectory
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.LazyImagesGrid
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.feature.image_preview.presentation.viewModel.ImagePreviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagePreviewScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: ImagePreviewViewModel = hiltViewModel()
) {
    var showExitDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val onBack = {
        if (viewModel.uris.isNullOrEmpty()) onGoBack()
        else showExitDialog = true
    }

    val settingsState = LocalSettingsState.current
    LaunchedEffect(uriState) {
        uriState?.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.updateUris(uris)
        }
    }

    val confettiController = LocalConfettiController.current
    val scope = rememberCoroutineScope()
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    val pickImageLauncher = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple),
        onSuccess = { list ->
            list.takeIf { it.isNotEmpty() }?.let {
                viewModel.updateUris(list)
            }
        }
    )

    var isLoadingImages by rememberSaveable {
        mutableStateOf(false)
    }

    var previousFolder by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val context = LocalContext.current as Activity
    val openDirectoryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree(),
        onResult = { treeUri ->
            treeUri?.let { uri ->
                scope.launch {
                    isLoadingImages = true
                    previousFolder = uri
                    val uris = context.listFilesInDirectory(uri)
                    viewModel.updateUris(uris)
                    isLoadingImages = false
                }
            }
        }
    )

    val pickImage = {
        pickImageLauncher.pickImage()
    }

    val toastHostState = LocalToastHostState.current
    val pickDirectory: () -> Unit = {
        runCatching {
            openDirectoryLauncher.launch(previousFolder)
        }.onFailure {
            scope.launch {
                toastHostState.showToast(
                    message = context.getString(R.string.activate_files),
                    icon = Icons.Outlined.FolderOff,
                    duration = ToastDuration.Long
                )
            }
        }
    }

    val gridState = rememberLazyStaggeredGridState()

    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Column(Modifier.fillMaxSize()) {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier.drawHorizontalStroke(),
                    title = {
                        Marquee(
                            edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ) {
                            Text(
                                stringResource(R.string.image_preview),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    navigationIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onGoBack
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                        }
                    },
                    actions = {
                        TopAppBarEmoji()
                    }
                )
                AnimatedVisibility(
                    visible = !isLoadingImages,
                    modifier = Modifier.weight(1f)
                ) {
                    if (viewModel.uris.isNullOrEmpty()) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
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
                    } else {
                        LazyImagesGrid(
                            data = viewModel.uris,
                            onAddImages = viewModel::updateUris,
                            modifier = Modifier.fillMaxSize(),
                            onShareImage = {
                                viewModel.shareImage(it, showConfetti)
                            },
                            state = gridState,
                            onRemove = viewModel::removeUri
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .align(settingsState.fabAlignment)
            ) {
                EnhancedFloatingActionButton(
                    onClick = pickImage,
                    content = {
                        Spacer(Modifier.width(16.dp))
                        Icon(Icons.Rounded.AddPhotoAlternate, null)
                        Spacer(Modifier.width(16.dp))
                        Text(stringResource(R.string.pick_image_alt))
                        Spacer(Modifier.width(16.dp))
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                EnhancedFloatingActionButton(
                    onClick = pickDirectory,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    content = {
                        Icon(
                            imageVector = Icons.Rounded.FolderOpen,
                            contentDescription = null
                        )
                    }
                )
            }

            BackHandler(onBack = onBack)
        }
    }

    if (isLoadingImages) {
        LoadingDialog(canCancel = false)
    }

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog,
        title = stringResource(id = R.string.image_preview),
        text = stringResource(id = R.string.preview_closing),
        icon = Icons.Outlined.ImageSearch
    )
}