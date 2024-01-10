package ru.tech.imageresizershrinker.feature.single_edit.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.ImageManager
import ru.tech.imageresizershrinker.core.domain.image.draw.pt
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.model.PtSaver
import ru.tech.imageresizershrinker.core.ui.model.UiPathPaint
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.PanModeButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.draw.AutoEraseBackgroundCard
import ru.tech.imageresizershrinker.core.ui.widget.controls.draw.BrushSoftnessSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.draw.EraseModeCard
import ru.tech.imageresizershrinker.core.ui.widget.controls.draw.LineWidthSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.draw.RecoverModeButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.draw.TrimImageToggle
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.other.DrawLockScreenOrientation
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.feature.erase_background.presentation.components.BitmapEraser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EraseBackgroundEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    orientation: Int,
    onGetBitmap: (Bitmap) -> Unit,
    clearErasing: (Boolean) -> Unit,
    undo: () -> Unit,
    redo: () -> Unit,
    paths: List<UiPathPaint>,
    lastPaths: List<UiPathPaint>,
    undonePaths: List<UiPathPaint>,
    addPath: (UiPathPaint) -> Unit,
    imageManager: ImageManager<Bitmap, *>
) {
    val settingsState = LocalSettingsState.current

    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiController.showEmpty()
        }
    }

    val toastHostState = LocalToastHost.current
    val context = LocalContext.current

    bitmap?.let {
        var zoomEnabled by rememberSaveable { mutableStateOf(false) }

        val switch = @Composable {
            PanModeButton(
                selected = zoomEnabled,
                onClick = {
                    zoomEnabled = !zoomEnabled
                }
            )
        }

        var isRecoveryOn by rememberSaveable { mutableStateOf(false) }

        var strokeWidth by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(20.pt) }
        var brushSoftness by rememberSaveable(stateSaver = PtSaver) {
            mutableStateOf(0.pt)
        }

        var trimImage by rememberSaveable { mutableStateOf(true) }

        val secondaryControls = @Composable {
            val border = BorderStroke(
                settingsState.borderWidth,
                MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.1f
                )
            )
            Row(
                Modifier
                    .padding(16.dp)
                    .then(if (!useScaffold) Modifier.container(shape = CircleShape) else Modifier)
            ) {
                switch()
                Spacer(Modifier.width(8.dp))
                OutlinedIconButton(
                    border = border,
                    onClick = undo,
                    enabled = lastPaths.isNotEmpty() || paths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Undo, null)
                }
                OutlinedIconButton(
                    border = border,
                    onClick = redo,
                    enabled = undonePaths.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Rounded.Redo, null)
                }
                RecoverModeButton(
                    selected = isRecoveryOn,
                    onClick = { isRecoveryOn = !isRecoveryOn }
                )
            }
        }

        var bitmapState by remember(bitmap, visible) { mutableStateOf(bitmap) }
        var erasedBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }

        var loading by remember { mutableStateOf(false) }

        var autoErased by remember { mutableStateOf(false) }

        FullscreenEditOption(
            canGoBack = paths.isEmpty() && !autoErased,
            visible = visible,
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = { scaffoldState ->
                if (!useScaffold) secondaryControls()
                Spacer(modifier = Modifier.height(8.dp))
                EraseModeCard(
                    isRecoveryOn = isRecoveryOn,
                    onClick = { isRecoveryOn = !isRecoveryOn }
                )
                AutoEraseBackgroundCard(
                    onClick = {
                        scope.launch {
                            scaffoldState?.bottomSheetState?.partialExpand()
                        }
                        loading = true
                        imageManager.removeBackgroundFromImage(
                            image = erasedBitmap,
                            onSuccess = {
                                loading = false
                                bitmapState = it
                                clearErasing(false)
                                autoErased = true
                                showConfetti()
                            },
                            onFailure = {
                                loading = false
                                scope.launch {
                                    toastHostState.showError(context, it)
                                }
                            }
                        )
                    },
                    onReset = {
                        bitmapState = bitmap
                        autoErased = true
                    }
                )
                LineWidthSelector(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    value = strokeWidth.value,
                    onValueChange = { strokeWidth = it.pt }
                )
                BrushSoftnessSelector(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 16.dp, start = 16.dp),
                    value = brushSoftness.value,
                    onValueChange = { brushSoftness = it.pt }
                )
                TrimImageToggle(
                    checked = trimImage,
                    onCheckedChange = { trimImage = it },
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    )
                )
            },
            fabButtons = null,
            actions = {
                if (useScaffold) {
                    secondaryControls()
                    Spacer(Modifier.weight(1f))
                }
            },
            topAppBar = { closeButton ->
                CenterAlignedTopAppBar(
                    navigationIcon = closeButton,
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    modifier = Modifier.drawHorizontalStroke(),
                    actions = {
                        AnimatedVisibility(
                            visible = paths.isNotEmpty() || autoErased,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                onClick = {
                                    scope.launch {
                                        onGetBitmap(
                                            if (trimImage) imageManager.trimEmptyParts(
                                                erasedBitmap
                                            ) else erasedBitmap
                                        )
                                        clearErasing(false)
                                    }
                                    onDismiss()
                                }
                            ) {
                                Icon(Icons.Rounded.Done, null)
                            }
                        }
                    },
                    title = {
                        Marquee(edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)) {
                            Text(
                                text = stringResource(R.string.erase_background),
                            )
                        }
                    }
                )
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = remember(bitmapState) {
                        derivedStateOf {
                            bitmapState.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                        }
                    }.value,
                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                ) { imageBitmap ->
                    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                    BitmapEraser(
                        imageBitmapForShader = bitmap.asImageBitmap(),
                        imageBitmap = imageBitmap,
                        paths = paths,
                        strokeWidth = strokeWidth,
                        brushSoftness = brushSoftness,
                        onAddPath = addPath,
                        isRecoveryOn = isRecoveryOn,
                        modifier = Modifier
                            .padding(16.dp)
                            .aspectRatio(aspectRatio, !useScaffold)
                            .fillMaxSize(),
                        zoomEnabled = zoomEnabled,
                        onErased = { erasedBitmap = it }
                    )
                }

                AnimatedVisibility(
                    visible = loading,
                    modifier = Modifier.fillMaxSize(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scrim.copy(0.5f))
                    ) {
                        Loading()
                    }
                }
            }
        }

        if (visible) {
            DrawLockScreenOrientation(
                orientation = orientation
            )
        }
    }
}