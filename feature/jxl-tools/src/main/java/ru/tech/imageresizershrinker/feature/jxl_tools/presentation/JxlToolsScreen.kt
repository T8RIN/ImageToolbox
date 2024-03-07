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

package ru.tech.imageresizershrinker.feature.jxl_tools.presentation

import android.content.Context
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.Jxl
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.failedToSaveImages
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.viewModel.JxlToolsViewModel

@Composable
fun JxlToolsScreen(
    typeState: Screen.JxlTools.Type?,
    onGoBack: () -> Unit,
    viewModel: JxlToolsViewModel = hiltViewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    LaunchedEffect(typeState) {
        typeState?.let { viewModel.setType(it) }
    }

    val settingsState = LocalSettingsState.current

    val pickJpegsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.setType(
                Screen.JxlTools.Type.JpegToJxl(uris)
            )
        }
    }

    val pickJxlsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.filter {
            it.isJxl(context)
        }?.let { uris ->
            if (uris.isEmpty()) {
                scope.launch {
                    toastHostState.showToast(
                        message = context.getString(R.string.select_jxl_image_to_start),
                        icon = Icons.Filled.Jxl
                    )
                }
            } else {
                viewModel.setType(
                    Screen.JxlTools.Type.JxlToJpeg(uris)
                )
            }
        }
    }

    val addJpegsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.setType(
                (viewModel.type as? Screen.JxlTools.Type.JpegToJxl)?.let {
                    it.copy(it.jpegImageUris?.plus(uris)?.distinct())
                }
            )
        }
    }

    val addJxlsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.filter {
            it.isJxl(context)
        }?.let { uris ->
            if (uris.isEmpty()) {
                scope.launch {
                    toastHostState.showToast(
                        message = context.getString(R.string.select_jxl_image_to_start),
                        icon = Icons.Filled.Jxl
                    )
                }
            } else {
                viewModel.setType(
                    (viewModel.type as? Screen.JxlTools.Type.JxlToJpeg)?.let {
                        it.copy(it.jxlImageUris?.plus(uris)?.distinct())
                    }
                )
            }
        }
    }

    fun pickImage(type: Screen.JxlTools.Type? = null) {
        runCatching {
            if ((type ?: viewModel.type) is Screen.JxlTools.Type.JpegToJxl) {
                pickJpegsLauncher.launch(arrayOf("image/jpeg", "image/jpg"))
            } else pickJxlsLauncher.launch(arrayOf("*/*"))
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

    val addImages: () -> Unit = {
        runCatching {
            if ((viewModel.type) is Screen.JxlTools.Type.JpegToJxl) {
                addJpegsLauncher.launch(arrayOf("image/jpeg", "image/jpg"))
            } else addJxlsLauncher.launch(arrayOf("*/*"))
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

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.type != null) showExitDialog = true
        else onGoBack()
    }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    val uris = when (val type = viewModel.type) {
        is Screen.JxlTools.Type.JpegToJxl -> type.jpegImageUris
        is Screen.JxlTools.Type.JxlToJpeg -> type.jxlImageUris
        null -> null
    } ?: emptyList()

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = when (viewModel.type) {
                    is Screen.JxlTools.Type.JpegToJxl -> {
                        stringResource(R.string.jpeg_type_to_jxl)
                    }

                    is Screen.JxlTools.Type.JxlToJpeg -> {
                        stringResource(R.string.jxl_type_to_jpeg)
                    }

                    null -> stringResource(R.string.jxl_tools)
                },
                input = viewModel.type,
                isLoading = viewModel.isLoading,
                size = null
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (viewModel.type == null) TopAppBarEmoji()
            else {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = {
                        viewModel.performSharing(showConfetti)
                    },
                    enabled = !viewModel.isLoading && viewModel.type != null
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = null
                    )
                }
            }
        },
        actions = {},
        imagePreview = {},
        placeImagePreview = false,
        showImagePreviewAsStickyHeader = false,
        autoClearFocus = false,
        controls = {
            Spacer(modifier = Modifier.height(24.dp))
            BoxWithConstraints {
                val size = uris.size + 1f

                val count = if (isPortrait) {
                    size.coerceAtLeast(2f).coerceAtMost(3f)
                } else {
                    size.coerceAtLeast(2f).coerceAtMost(8f)
                }

                val width = maxWidth / count - 4.dp * (count - 1)

                ContextualFlowRow(
                    itemCount = uris.size + 1,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) { index ->
                    val uri = uris.getOrNull(index)
                    if (uri != null) {
                        Box(
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(4.dp),
                                resultPadding = 0.dp,
                                color = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        ) {
                            Picture(
                                model = uri,
                                error = {
                                    Box {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(width / 3f)
                                                .align(Alignment.Center),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .width(width)
                                    .aspectRatio(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(0.5f)),
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopStart)
                                )
                                Icon(
                                    imageVector = Icons.Rounded.RemoveCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(0.2f))
                                        .clickable {
                                            viewModel.removeUri(uri)
                                        }
                                        .padding(4.dp)
                                        .align(Alignment.TopEnd),
                                    tint = Color.White.copy(0.7f),
                                )
                                val filename by remember(uri) {
                                    derivedStateOf {
                                        context.getFilename(uri)
                                    }
                                }
                                filename?.let {
                                    AutoSizeText(
                                        text = it,
                                        style = LocalTextStyle.current.copy(
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            lineHeight = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End
                                        ),
                                        maxLines = 3,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .container(
                                    shape = RoundedCornerShape(4.dp),
                                    resultPadding = 0.dp,
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                                .width(width)
                                .aspectRatio(1f)
                                .clickable(onClick = addImages),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddPhotoAlternate,
                                contentDescription = null,
                                modifier = Modifier.size(width / 3f)
                            )
                        }
                    }
                }
            }
        },
        contentPadding = animateDpAsState(
            if (viewModel.type == null) 12.dp
            else 20.dp
        ).value,
        buttons = {
            BottomButtonsBlock(
                targetState = (viewModel.type == null) to isPortrait,
                onSecondaryButtonClick = { pickImage() },
                isPrimaryButtonVisible = viewModel.type != null,
                onPrimaryButtonClick = {
                    viewModel.save { results, path ->
                        context.failedToSaveImages(
                            scope = scope,
                            results = results,
                            toastHostState = toastHostState,
                            savingPathString = path,
                            isOverwritten = settingsState.overwriteFiles,
                            showConfetti = showConfetti
                        )
                    }
                },
                actions = {
                    EnhancedChip(
                        selected = true,
                        onClick = null,
                        selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(uris.size.toString())
                    }
                },
                showNullDataButtonAsContainer = true
            )
        },
        noDataControls = {
            val types = remember {
                Screen.JxlTools.Type.entries
            }
            val preference1 = @Composable {
                PreferenceItem(
                    title = stringResource(types[0].title),
                    subtitle = stringResource(types[0].subtitle),
                    startIcon = types[0].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pickImage(types[0])
                    }
                )
            }
            val preference2 = @Composable {
                PreferenceItem(
                    title = stringResource(types[1].title),
                    subtitle = stringResource(types[1].subtitle),
                    startIcon = types[1].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pickImage(types[1])
                    }
                )
            }
            if (isPortrait) {
                Column {
                    preference1()
                    Spacer(modifier = Modifier.height(8.dp))
                    preference2()
                }
            } else {
                val direction = LocalLayoutDirection.current
                Row(
                    modifier = Modifier.padding(
                        WindowInsets.displayCutout.asPaddingValues().let {
                            PaddingValues(
                                start = it.calculateStartPadding(direction),
                                end = it.calculateEndPadding(direction)
                            )
                        }
                    )
                ) {
                    preference1.withModifier(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    preference2.withModifier(modifier = Modifier.weight(1f))
                }
            }
        },
        isPortrait = isPortrait,
        canShowScreenData = viewModel.type != null
    )

    if (viewModel.isSaving) {
        if (viewModel.left != -1) {
            LoadingDialog(
                done = viewModel.done,
                left = viewModel.left,
                onCancelLoading = viewModel::cancelSaving
            )
        } else {
            LoadingDialog(
                onCancelLoading = viewModel::cancelSaving
            )
        }
    }

    ExitWithoutSavingDialog(
        onExit = viewModel::clearAll,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}

private fun Uri.isJxl(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".jxl")
        .or(context.contentResolver.getType(this)?.contains("jxl") == true)
}
