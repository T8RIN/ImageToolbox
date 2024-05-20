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

package ru.tech.imageresizershrinker.feature.pdf_tools.presentation

import android.app.Activity
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.t8rin.dynamic.theme.observeAsState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.animation.fancySlideTransition
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.openWriteableStream
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.showReview
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageReorderCarousel
import ru.tech.imageresizershrinker.core.ui.widget.controls.PresetSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.ScaleSmallImagesToLargeToggle
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components.PdfToImagesPreference
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components.PdfViewer
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components.PdfViewerOrientation
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components.PreviewPdfPreference
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.viewModel.PdfToolsViewModel

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun PdfToolsScreen(
    type: Screen.PdfTools.Type?,
    onGoBack: () -> Unit,
    viewModel: PdfToolsViewModel = hiltViewModel()
) {
    LaunchedEffect(type) {
        type?.let { viewModel.setType(it) }
    }

    val context = LocalContext.current as Activity
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val sizeClass = LocalWindowSizeClass.current.widthSizeClass
    val portrait by remember(
        LocalLifecycleOwner.current.lifecycle.observeAsState().value,
        sizeClass,
        configuration
    ) {
        derivedStateOf {
            configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || sizeClass == WindowWidthSizeClass.Compact
        }
    }

    val onBack = {
        if (type is Screen.PdfTools.Type.Preview) onGoBack()
        else {
            if (viewModel.haveChanges) showExitDialog = true
            else if (viewModel.pdfType != null) {
                viewModel.clearType()
            } else onGoBack()
        }
    }

    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    val writeDenied: (Throwable) -> Unit = {
        scope.launch {
            toastHostState.showError(context, it)
        }
    }
    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = {
            it?.let { uri ->
                viewModel.savePdfTo(
                    outputStream = context.openWriteableStream(uri, writeDenied)
                ) { t ->
                    if (t != null) {
                        scope.launch {
                            toastHostState.showError(context, t)
                        }
                    } else {
                        scope.launch {
                            confettiHostState.showConfetti()
                        }
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(
                                    R.string.saved_to_without_filename,
                                    ""
                                ),
                                Icons.Rounded.Save
                            )
                            showReview(context)
                        }
                    }
                }
            }
        }
    )

    val pdfToImagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.setPdfToImagesUri(it)
            }
        }
    )

    val pdfPreviewPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                viewModel.setPdfPreview(it)
            }
        }
    )

    var tempSelectionUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    val showSelectionPdfPicker = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(showSelectionPdfPicker.value) {
        if (!showSelectionPdfPicker.value) tempSelectionUri = null
    }
    val selectionPdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                tempSelectionUri = it
                showSelectionPdfPicker.value = true
            }
        }
    )

    SimpleSheet(
        visible = showSelectionPdfPicker,
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showSelectionPdfPicker.value = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        sheetContent = {
            if (tempSelectionUri == null) showSelectionPdfPicker.value = false

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(250.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(12.dp),
            ) {
                item {
                    PreviewPdfPreference(
                        onClick = {
                            viewModel.setPdfPreview(tempSelectionUri)
                            showSelectionPdfPicker.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    PdfToImagesPreference(
                        onClick = {
                            viewModel.setPdfToImagesUri(tempSelectionUri)
                            showSelectionPdfPicker.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        title = {
            TitleItem(text = stringResource(id = R.string.pick_file), icon = Icons.Rounded.FileOpen)
        }
    )

    val imagesToPdfPicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.setImagesToPdf(uris)
        }
    }

    val addImagesToPdfPicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            viewModel.addImagesToPdf(uris)
        }
    }

    val focus = LocalFocusManager.current

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState,
        canScroll = { (viewModel.pdfType !is Screen.PdfTools.Type.Preview && portrait) || viewModel.pdfType == null }
    )

    LaunchedEffect(viewModel.pdfType) {
        while (viewModel.pdfType is Screen.PdfTools.Type.Preview || (viewModel.pdfType != null && !portrait)) {
            topAppBarState.apply {
                heightOffset = (heightOffset - 10).coerceAtLeast(heightOffsetLimit)
            }
            delay(10)
        }
    }

    val selectAllToggle = remember { mutableStateOf(false) }
    val deselectAllToggle = remember { mutableStateOf(false) }

    val actionButtons: @Composable RowScope.(pdfType: Screen.PdfTools.Type?) -> Unit = {
        val pdfType = it
        AnimatedVisibility(
            visible = pdfType != null,
            enter = fadeIn() + scaleIn() + expandHorizontally(),
            exit = fadeOut() + scaleOut() + shrinkHorizontally()
        ) {
            ShareButton(
                onShare = {
                    viewModel.preformSharing(showConfetti)
                }
            )
        }
    }

    val buttons: @Composable (pdfType: Screen.PdfTools.Type?) -> Unit = { pdfType ->
        EnhancedFloatingActionButton(
            onClick = {
                runCatching {
                    when (pdfType) {
                        is Screen.PdfTools.Type.ImagesToPdf -> {
                            imagesToPdfPicker.pickImage()
                        }

                        is Screen.PdfTools.Type.Preview -> {
                            pdfPreviewPicker.launch(arrayOf("application/pdf"))
                        }

                        else -> {
                            pdfToImagesPicker.launch(arrayOf("application/pdf"))
                        }
                    }
                }.onFailure {
                    scope.launch {
                        toastHostState.showError(context, it)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ) {
            Icon(
                imageVector = when (pdfType) {
                    is Screen.PdfTools.Type.ImagesToPdf -> Icons.Rounded.AddPhotoAlternate
                    else -> Icons.Rounded.FileOpen
                },
                contentDescription = stringResource(R.string.pick)
            )
        }
        if (pdfType !is Screen.PdfTools.Type.Preview) {
            val visible by remember(viewModel.pdfToImageState?.pages, pdfType) {
                derivedStateOf {
                    (viewModel.pdfToImageState?.pages?.size != 0 && pdfType is Screen.PdfTools.Type.PdfToImages) || pdfType !is Screen.PdfTools.Type.PdfToImages
                }
            }
            if (visible) {
                if (portrait) {
                    Spacer(modifier = Modifier.width(8.dp))
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + scaleIn() + expandIn(),
                exit = fadeOut() + scaleOut() + shrinkOut()
            ) {
                val savePdfToImages: (oneTimeSaveLocationUri: String?) -> Unit = {
                    viewModel.savePdfToImages(it) { results ->
                        context.parseSaveResults(
                            scope = scope,
                            results = results,
                            toastHostState = toastHostState,
                            isOverwritten = false,
                            showConfetti = showConfetti
                        )
                    }
                }
                var showFolderSelectionDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                EnhancedFloatingActionButton(
                    onClick = {
                        if (pdfType is Screen.PdfTools.Type.ImagesToPdf && viewModel.imagesToPdfState != null) {
                            val name = viewModel.generatePdfFilename()
                            viewModel.convertImagesToPdf {
                                runCatching {
                                    savePdfLauncher.launch("$name.pdf")
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
                        } else if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                            savePdfToImages(null)
                        }
                    },
                    onLongClick = if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                        { showFolderSelectionDialog = true }
                    } else null
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = stringResource(R.string.save)
                    )
                }
                if (showFolderSelectionDialog) {
                    OneTimeSaveLocationSelectionDialog(
                        onDismiss = { showFolderSelectionDialog = false },
                        onSaveRequest = savePdfToImages
                    )
                }
            }
        }
    }

    val controls: @Composable (pdfType: Screen.PdfTools.Type?) -> Unit = { pdfType ->
        if (pdfType is Screen.PdfTools.Type.ImagesToPdf) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageReorderCarousel(
                    images = viewModel.imagesToPdfState,
                    onReorder = viewModel::reorderImagesToPdf,
                    onNeedToAddImage = { addImagesToPdfPicker.pickImage() },
                    onNeedToRemoveImageAt = viewModel::removeImageToPdfAt
                )
                Spacer(Modifier.height(8.dp))
                PresetSelector(
                    value = viewModel.presetSelected,
                    includeTelegramOption = false,
                    onValueChange = {
                        if (it is Preset.Percentage) {
                            viewModel.selectPreset(it)
                        }
                    },
                    showWarning = viewModel.showOOMWarning
                )
                Spacer(
                    Modifier.height(8.dp)
                )
                ScaleSmallImagesToLargeToggle(
                    checked = viewModel.scaleSmallImagesToLarge,
                    onCheckedChange = {
                        viewModel.toggleScaleSmallImagesToLarge()
                    }
                )
            }
        } else if (pdfType is Screen.PdfTools.Type.PdfToImages) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PresetSelector(
                    value = viewModel.presetSelected,
                    includeTelegramOption = false,
                    onValueChange = {
                        if (it is Preset.Percentage) {
                            viewModel.selectPreset(it)
                        }
                    },
                    showWarning = viewModel.showOOMWarning
                )
                if (viewModel.imageInfo.imageFormat.canChangeCompressionValue) {
                    Spacer(
                        Modifier.height(8.dp)
                    )
                }
                QualitySelector(
                    imageFormat = viewModel.imageInfo.imageFormat,
                    enabled = true,
                    quality = viewModel.imageInfo.quality,
                    onQualityChange = viewModel::setQuality
                )
                Spacer(
                    Modifier.height(8.dp)
                )
                ImageFormatSelector(
                    value = viewModel.imageInfo.imageFormat,
                    onValueChange = viewModel::updateImageFormat
                )
            }
        }
    }

    Box {
        Surface(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focus.clearFocus()
                    }
                )
            },
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                Column(Modifier.fillMaxSize()) {
                    val title = @Composable {
                        Marquee {
                            AnimatedContent(
                                targetState = viewModel.pdfType to viewModel.pdfPreviewUri,
                                transitionSpec = { fadeIn() togetherWith fadeOut() }
                            ) { (pdfType, previewUri) ->
                                Text(
                                    text = previewUri?.let {
                                        context.getFilename(it)
                                    } ?: stringResource(pdfType?.title ?: R.string.pdf_tools),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    val navigationIcon = @Composable {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = onBack
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.exit)
                            )
                        }
                    }
                    val actions: @Composable RowScope.() -> Unit = {
                        if (!portrait) {
                            actionButtons(viewModel.pdfType)
                        }
                        if (viewModel.pdfType == null) {
                            TopAppBarEmoji()
                        } else {
                            val pagesSize = viewModel.pdfToImageState?.pages?.size
                            val visible by remember(
                                viewModel.pdfToImageState?.pages,
                                viewModel.pdfType
                            ) {
                                derivedStateOf {
                                    (pagesSize != 0 && viewModel.pdfType is Screen.PdfTools.Type.PdfToImages)
                                }
                            }
                            AnimatedVisibility(
                                visible = viewModel.pdfType is Screen.PdfTools.Type.PdfToImages,
                                enter = fadeIn() + scaleIn() + expandHorizontally(),
                                exit = fadeOut() + scaleOut() + shrinkHorizontally()
                            ) {
                                EnhancedIconButton(
                                    containerColor = Color.Transparent,
                                    contentColor = LocalContentColor.current,
                                    enableAutoShadowAndBorder = false,
                                    onClick = {
                                        selectAllToggle.value = true
                                    },
                                    enabled = viewModel.pdfType != null
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
                                visible = visible
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    pagesSize?.takeIf { it != 0 }?.let {
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = pagesSize.toString(),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                    EnhancedIconButton(
                                        containerColor = Color.Transparent,
                                        contentColor = LocalContentColor.current,
                                        enableAutoShadowAndBorder = false,
                                        onClick = {
                                            deselectAllToggle.value = true
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
                    }

                    EnhancedTopAppBar(
                        type = EnhancedTopAppBarType.Large,
                        scrollBehavior = scrollBehavior,
                        title = title,
                        navigationIcon = navigationIcon,
                        actions = actions
                    )

                    val screenWidth = LocalConfiguration.current.screenWidthDp

                    AnimatedContent(
                        transitionSpec = {
                            fancySlideTransition(
                                isForward = targetState != null,
                                screenWidthDp = screenWidth
                            )
                        },
                        targetState = viewModel.pdfType
                    ) { pdfType ->
                        when (pdfType) {
                            null -> {
                                Column {
                                    val cutout = WindowInsets.displayCutout.asPaddingValues()
                                    LazyVerticalStaggeredGrid(
                                        modifier = Modifier.weight(1f),
                                        columns = StaggeredGridCells.Adaptive(300.dp),
                                        horizontalArrangement = Arrangement.spacedBy(
                                            space = 12.dp,
                                            alignment = Alignment.CenterHorizontally
                                        ),
                                        verticalItemSpacing = 12.dp,
                                        contentPadding = PaddingValues(
                                            bottom = 12.dp + WindowInsets
                                                .navigationBars
                                                .asPaddingValues()
                                                .calculateBottomPadding(),
                                            top = 12.dp,
                                            end = 12.dp + cutout.calculateEndPadding(
                                                LocalLayoutDirection.current
                                            ),
                                            start = 12.dp + cutout.calculateStartPadding(
                                                LocalLayoutDirection.current
                                            )
                                        ),
                                    ) {
                                        Screen.PdfTools.Type.entries.forEach {
                                            item {
                                                PreferenceItem(
                                                    onClick = {
                                                        runCatching {
                                                            when (it) {
                                                                is Screen.PdfTools.Type.ImagesToPdf -> {
                                                                    imagesToPdfPicker.pickImage()
                                                                }

                                                                is Screen.PdfTools.Type.PdfToImages -> {
                                                                    pdfToImagesPicker.launch(
                                                                        arrayOf(
                                                                            "application/pdf"
                                                                        )
                                                                    )
                                                                }

                                                                is Screen.PdfTools.Type.Preview -> {
                                                                    pdfPreviewPicker.launch(
                                                                        arrayOf(
                                                                            "application/pdf"
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        }.onFailure {
                                                            scope.launch {
                                                                toastHostState.showError(
                                                                    context,
                                                                    it
                                                                )
                                                            }
                                                        }
                                                    },
                                                    startIcon = it.icon,
                                                    title = stringResource(it.title),
                                                    subtitle = stringResource(it.subtitle),
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .drawHorizontalStroke(true)
                                            .background(
                                                MaterialTheme.colorScheme.surfaceContainer
                                            ),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        EnhancedFloatingActionButton(
                                            onClick = {
                                                runCatching {
                                                    selectionPdfPicker.launch(arrayOf("application/pdf"))
                                                }.onFailure {
                                                    scope.launch {
                                                        toastHostState.showToast(
                                                            message = context.getString(R.string.activate_files),
                                                            icon = Icons.Outlined.FolderOff,
                                                            duration = ToastDuration.Long
                                                        )
                                                    }
                                                }
                                            },
                                            modifier = Modifier
                                                .navigationBarsPadding()
                                                .padding(16.dp),
                                            content = {
                                                Spacer(Modifier.width(16.dp))
                                                Icon(
                                                    imageVector = Icons.Rounded.FileOpen,
                                                    contentDescription = stringResource(R.string.pick_file)
                                                )
                                                Spacer(Modifier.width(16.dp))
                                                Text(stringResource(R.string.pick_file))
                                                Spacer(Modifier.width(16.dp))
                                            }
                                        )
                                    }
                                }
                            }

                            else -> {
                                Column {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (pdfType is Screen.PdfTools.Type.Preview || pdfType is Screen.PdfTools.Type.PdfToImages) {
                                            val direction = LocalLayoutDirection.current
                                            Box(
                                                modifier = Modifier
                                                    .container(
                                                        shape = RectangleShape,
                                                        resultPadding = 0.dp,
                                                        color = if (pdfType is Screen.PdfTools.Type.Preview || !portrait) {
                                                            MaterialTheme.colorScheme.surfaceContainerLow
                                                        } else MaterialTheme.colorScheme.surface
                                                    )
                                                    .weight(1.2f)
                                                    .clipToBounds(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (pdfType is Screen.PdfTools.Type.Preview) {
                                                    PdfViewer(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        uriState = viewModel.pdfPreviewUri,
                                                        contentPadding = PaddingValues(
                                                            start = 20.dp + WindowInsets.displayCutout
                                                                .asPaddingValues()
                                                                .calculateStartPadding(direction),
                                                            end = 20.dp
                                                        )
                                                    )
                                                } else {
                                                    Column(
                                                        modifier = if (portrait) {
                                                            Modifier
                                                                .fillMaxSize()
                                                                .verticalScroll(rememberScrollState())
                                                        } else Modifier,
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        var pagesCount by remember {
                                                            mutableIntStateOf(
                                                                1
                                                            )
                                                        }
                                                        PdfViewer(
                                                            modifier = if (portrait) {
                                                                Modifier
                                                                    .height(
                                                                        (130.dp * pagesCount).coerceAtMost(
                                                                            420.dp
                                                                        )
                                                                    )
                                                                    .fillMaxWidth()
                                                            } else {
                                                                Modifier.fillMaxWidth()
                                                            }.padding(
                                                                start = WindowInsets
                                                                    .displayCutout
                                                                    .asPaddingValues()
                                                                    .calculateStartPadding(direction)
                                                            ),
                                                            onGetPagesCount = { pagesCount = it },
                                                            uriState = viewModel.pdfToImageState?.uri,
                                                            orientation = PdfViewerOrientation.Grid,
                                                            enableSelection = true,
                                                            selectAllToggle = selectAllToggle,
                                                            deselectAllToggle = deselectAllToggle,
                                                            selectedPages = viewModel.pdfToImageState?.pages
                                                                ?: emptyList(),
                                                            updateSelectedPages = viewModel::updatePdfToImageSelection,
                                                            spacing = 4.dp
                                                        )
                                                        if (portrait) {
                                                            controls(pdfType)
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (pdfType !is Screen.PdfTools.Type.Preview && !portrait || pdfType is Screen.PdfTools.Type.ImagesToPdf) {
                                            val direction = LocalLayoutDirection.current
                                            Box(
                                                modifier = Modifier
                                                    .weight(0.7f)
                                                    .fillMaxHeight()
                                                    .clipToBounds()
                                            ) {
                                                Column(
                                                    modifier = Modifier
                                                        .verticalScroll(rememberScrollState())
                                                        .then(
                                                            if (pdfType is Screen.PdfTools.Type.ImagesToPdf) {
                                                                Modifier.padding(
                                                                    start = WindowInsets
                                                                        .displayCutout
                                                                        .asPaddingValues()
                                                                        .calculateStartPadding(
                                                                            direction
                                                                        )
                                                                )
                                                            } else Modifier
                                                        )
                                                ) {
                                                    controls(pdfType)
                                                }
                                            }
                                        }
                                        if (!portrait) {
                                            val direction = LocalLayoutDirection.current
                                            Column(
                                                Modifier
                                                    .container(RectangleShape)
                                                    .fillMaxHeight()
                                                    .padding(horizontal = 16.dp)
                                                    .navigationBarsPadding()
                                                    .padding(
                                                        end = WindowInsets.displayCutout
                                                            .asPaddingValues()
                                                            .calculateEndPadding(direction)
                                                    ),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                buttons(pdfType)
                                            }
                                        }
                                    }
                                    if (portrait) {
                                        BottomAppBar(
                                            actions = {
                                                actionButtons(pdfType)
                                            },
                                            floatingActionButton = {
                                                Row {
                                                    buttons(pdfType)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (viewModel.isSaving) {
                    if (viewModel.left != 0) {
                        LoadingDialog(
                            done = viewModel.done,
                            left = viewModel.left,
                            onCancelLoading = viewModel::cancelSaving
                        )
                    } else {
                        LoadingDialog(viewModel::cancelSaving)
                    }
                }
            }
        }
    }

    ExitWithoutSavingDialog(
        onExit = {
            if (viewModel.pdfType != null) {
                viewModel.clearType()
            } else onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(onBack = onBack)
}