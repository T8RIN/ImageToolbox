package ru.tech.imageresizershrinker.presentation.single_edit_screen.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BlurRadiusSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.AutoEraseBackgroundCard
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.BitmapEraser
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.EraseModeButton
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.EraseModeCard
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.TrimImageToggle
import ru.tech.imageresizershrinker.presentation.root.theme.icons.Transparency
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.other.Loading
import ru.tech.imageresizershrinker.presentation.root.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.presentation.root.widget.other.LockScreenOrientation
import ru.tech.imageresizershrinker.presentation.root.widget.other.showError
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EraseBackgroundEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    orientation: Int,
    onGetBitmap: (Bitmap, Boolean) -> Unit,
    clearErasing: (Boolean) -> Unit,
    undo: () -> Unit,
    redo: () -> Unit,
    paths: List<PathPaint>,
    lastPaths: List<PathPaint>,
    undonePaths: List<PathPaint>,
    addPath: (PathPaint) -> Unit,
    imageManager: ImageManager<Bitmap, *>
) {
    //TODO: INSPECT THIS BY THE MORNING
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
            Switch(
                modifier = Modifier.then(
                    if (!useScaffold) Modifier.padding(start = 8.dp)
                    else Modifier
                ),
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                checked = !zoomEnabled,
                onCheckedChange = { zoomEnabled = !zoomEnabled },
                thumbContent = {
                    AnimatedContent(zoomEnabled) { zoom ->
                        Icon(
                            if (!zoom) Icons.Filled.Transparency else Icons.Rounded.ZoomIn,
                            null,
                            Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                }
            )
        }

        var isRecoveryOn by rememberSaveable { mutableStateOf(false) }

        var strokeWidth by rememberSaveable { mutableFloatStateOf(20f) }
        var blurRadius by rememberSaveable {
            mutableFloatStateOf(0f)
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
                    .then(if (!useScaffold) Modifier.block(shape = CircleShape) else Modifier)
            ) {
                switch()
                Spacer(Modifier.width(8.dp))
                OutlinedIconButton(
                    border = border,
                    onClick = undo,
                    enabled = lastPaths.isNotEmpty() || paths.isNotEmpty()
                ) {
                    Icon(Icons.Rounded.Undo, null)
                }
                OutlinedIconButton(
                    border = border,
                    onClick = redo,
                    enabled = undonePaths.isNotEmpty()
                ) {
                    Icon(Icons.Rounded.Redo, null)
                }
                EraseModeButton(
                    isRecoveryOn = isRecoveryOn,
                    onClick = { isRecoveryOn = !isRecoveryOn }
                )
            }
        }

        var bitmapState by remember(bitmap, visible) { mutableStateOf(bitmap) }
        var erasedBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }

        var loading by remember { mutableStateOf(false) }

        FullscreenEditOption(
            canGoBack = erasedBitmap == bitmap,
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
                    }
                )
                LineWidthSelector(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    strokeWidth = strokeWidth,
                    onChangeStrokeWidth = { strokeWidth = it }
                )
                BlurRadiusSelector(
                    modifier = Modifier
                        .padding(top = 8.dp, end = 16.dp, start = 16.dp),
                    blurRadius = blurRadius,
                    onRadiusChange = { blurRadius = it }
                )
                TrimImageToggle(
                    selected = trimImage,
                    onCheckedChange = { trimImage = it },
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp
                    )
                )
            },
            fabButtons = {},
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
                        AnimatedVisibility(visible = erasedBitmap != bitmap) {
                            OutlinedIconButton(
                                colors = IconButtonDefaults.filledTonalIconButtonColors(),
                                onClick = {
                                    scope.launch {
                                        onGetBitmap(
                                            if (trimImage) imageManager.trimEmptyParts(
                                                erasedBitmap
                                            ) else erasedBitmap,
                                            !trimImage
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
                        blurRadius = blurRadius,
                        onAddPath = addPath,
                        isRecoveryOn = isRecoveryOn,
                        modifier = Modifier
                            .padding(16.dp)
                            .aspectRatio(aspectRatio, useScaffold)
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
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
                    ) {
                        Loading()
                    }
                }
            }
        }

        if (visible) {
            LockScreenOrientation(
                orientation = orientation
            )
        }
    }
}