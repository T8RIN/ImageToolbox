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
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.t8rin.dynamic.theme.observeAsState
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.domain.image.model.Preset
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.animation.fancySlideTransition
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageReorderCarousel
import ru.tech.imageresizershrinker.core.ui.widget.controls.ScaleSmallImagesToLargeToggle
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.PresetSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components.PdfToImagesPreference
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components.PdfViewer
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components.PdfViewerOrientation
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.components.PreviewPdfPreference
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.screenLogic.PdfToolsComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfToolsContent(
    onGoBack: () -> Unit,
    component: PdfToolsComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

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
        if (component.pdfType is Screen.PdfTools.Type.Preview) onGoBack()
        else {
            if (component.haveChanges) showExitDialog = true
            else if (component.pdfType != null) {
                component.clearType()
            } else onGoBack()
        }
    }

    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf"),
        onResult = {
            it?.let { uri ->
                component.savePdfTo(
                    uri = uri,
                    onResult = essentials::parseFileSaveResult
                )
            }
        }
    )

    val pdfToImagesPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                component.setPdfToImagesUri(it)
            }
        }
    )

    val pdfPreviewPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                component.setPdfPreview(it)
            }
        }
    )

    var tempSelectionUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showSelectionPdfPicker by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(showSelectionPdfPicker) {
        if (!showSelectionPdfPicker) tempSelectionUri = null
    }
    val selectionPdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                tempSelectionUri = it
                showSelectionPdfPicker = true
            }
        }
    )

    EnhancedModalBottomSheet(
        visible = showSelectionPdfPicker,
        onDismiss = {
            showSelectionPdfPicker = it
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showSelectionPdfPicker = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        sheetContent = {
            if (tempSelectionUri == null) showSelectionPdfPicker = false

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
                            component.setPdfPreview(tempSelectionUri)
                            showSelectionPdfPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    PdfToImagesPreference(
                        onClick = {
                            component.setPdfToImagesUri(tempSelectionUri)
                            showSelectionPdfPicker = false
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
            component.setImagesToPdf(uris)
        }
    }

    val addImagesToPdfPicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            component.addImagesToPdf(uris)
        }
    }

    val focus = LocalFocusManager.current

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState,
        canScroll = { (component.pdfType !is Screen.PdfTools.Type.Preview && portrait) || component.pdfType == null }
    )

    LaunchedEffect(component.pdfType) {
        while (component.pdfType is Screen.PdfTools.Type.Preview || (component.pdfType != null && !portrait)) {
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
                    component.preformSharing(showConfetti)
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
                }.onFailure(essentials::showErrorToast)
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
            val visible by remember(component.pdfToImageState?.pages, pdfType) {
                derivedStateOf {
                    (component.pdfToImageState?.pages?.size != 0 && pdfType is Screen.PdfTools.Type.PdfToImages) || pdfType !is Screen.PdfTools.Type.PdfToImages
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
                    component.savePdfToImages(
                        oneTimeSaveLocationUri = it,
                        onComplete = essentials::parseSaveResults
                    )
                }
                var showFolderSelectionDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                EnhancedFloatingActionButton(
                    onClick = {
                        if (pdfType is Screen.PdfTools.Type.ImagesToPdf && component.imagesToPdfState != null) {
                            val name = component.generatePdfFilename()
                            component.convertImagesToPdf {
                                runCatching {
                                    savePdfLauncher.launch("$name.pdf")
                                }.onFailure {
                                    essentials.showActivateFilesToast()
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
                OneTimeSaveLocationSelectionDialog(
                    visible = showFolderSelectionDialog,
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = savePdfToImages
                )
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
                    images = component.imagesToPdfState,
                    onReorder = component::reorderImagesToPdf,
                    onNeedToAddImage = { addImagesToPdfPicker.pickImage() },
                    onNeedToRemoveImageAt = component::removeImageToPdfAt
                )
                Spacer(Modifier.height(8.dp))
                PresetSelector(
                    value = component.presetSelected,
                    includeTelegramOption = false,
                    onValueChange = {
                        if (it is Preset.Percentage) {
                            component.selectPreset(it)
                        }
                    },
                    showWarning = component.showOOMWarning
                )
                Spacer(
                    Modifier.height(8.dp)
                )
                ScaleSmallImagesToLargeToggle(
                    checked = component.scaleSmallImagesToLarge,
                    onCheckedChange = {
                        component.toggleScaleSmallImagesToLarge()
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
                    value = component.presetSelected,
                    includeTelegramOption = false,
                    onValueChange = {
                        if (it is Preset.Percentage) {
                            component.selectPreset(it)
                        }
                    },
                    showWarning = component.showOOMWarning
                )
                if (component.imageInfo.imageFormat.canChangeCompressionValue) {
                    Spacer(
                        Modifier.height(8.dp)
                    )
                }
                QualitySelector(
                    imageFormat = component.imageInfo.imageFormat,
                    enabled = true,
                    quality = component.imageInfo.quality,
                    onQualityChange = component::setQuality
                )
                Spacer(
                    Modifier.height(8.dp)
                )
                ImageFormatSelector(
                    value = component.imageInfo.imageFormat,
                    onValueChange = component::updateImageFormat
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
                        AnimatedContent(
                            targetState = component.pdfType to component.pdfPreviewUri,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            modifier = Modifier.marquee()
                        ) { (pdfType, previewUri) ->
                            Text(
                                text = previewUri?.let {
                                    context.getFilename(it)
                                } ?: stringResource(pdfType?.title ?: R.string.pdf_tools),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    val navigationIcon = @Composable {
                        EnhancedIconButton(
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
                            actionButtons(component.pdfType)
                        }
                        if (component.pdfType == null) {
                            TopAppBarEmoji()
                        } else {
                            val pagesSize = component.pdfToImageState?.pages?.size
                            val visible by remember(
                                component.pdfToImageState?.pages,
                                component.pdfType
                            ) {
                                derivedStateOf {
                                    (pagesSize != 0 && component.pdfType is Screen.PdfTools.Type.PdfToImages)
                                }
                            }
                            AnimatedVisibility(
                                visible = component.pdfType is Screen.PdfTools.Type.PdfToImages,
                                enter = fadeIn() + scaleIn() + expandHorizontally(),
                                exit = fadeOut() + scaleOut() + shrinkHorizontally()
                            ) {
                                EnhancedIconButton(
                                    onClick = {
                                        selectAllToggle.value = true
                                    },
                                    enabled = component.pdfType != null
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
                        targetState = component.pdfType
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
                                                        }.onFailure(essentials::showErrorToast)
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
                                                    essentials.showActivateFilesToast()
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
                                                        uriState = component.pdfPreviewUri,
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
                                                            uriState = component.pdfToImageState?.uri,
                                                            orientation = PdfViewerOrientation.Grid,
                                                            enableSelection = true,
                                                            selectAllToggle = selectAllToggle,
                                                            deselectAllToggle = deselectAllToggle,
                                                            selectedPages = component.pdfToImageState?.pages
                                                                ?: emptyList(),
                                                            updateSelectedPages = component::updatePdfToImageSelection,
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

                if (component.left != 0) {
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
        }
    }

    ExitWithoutSavingDialog(
        onExit = {
            if (component.pdfType != null) {
                component.clearType()
            } else onGoBack()
        },
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    BackHandler(
        enabled = (component.pdfType !is Screen.PdfTools.Type.Preview && component.pdfType != null) || component.haveChanges,
        onBack = onBack
    )
}