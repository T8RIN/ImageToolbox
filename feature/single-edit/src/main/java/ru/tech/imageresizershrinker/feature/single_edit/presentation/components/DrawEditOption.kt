package ru.tech.imageresizershrinker.feature.single_edit.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.ZoomIn
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coredomain.image.ImageManager
import ru.tech.imageresizershrinker.coredomain.image.draw.DrawMode
import ru.tech.imageresizershrinker.coredomain.image.draw.DrawPathMode
import ru.tech.imageresizershrinker.coredomain.image.draw.pt
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.icons.material.Eraser
import ru.tech.imageresizershrinker.coreui.model.PtSaver
import ru.tech.imageresizershrinker.coreui.model.UiPathPaint
import ru.tech.imageresizershrinker.coreui.theme.mixedContainer
import ru.tech.imageresizershrinker.coreui.theme.onMixedContainer
import ru.tech.imageresizershrinker.coreui.theme.outlineVariant
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSwitch
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSwitchDefaults
import ru.tech.imageresizershrinker.coreui.widget.controls.draw.BrushSoftnessSelector
import ru.tech.imageresizershrinker.coreui.widget.controls.draw.DrawAlphaSelector
import ru.tech.imageresizershrinker.coreui.widget.controls.draw.DrawColorSelector
import ru.tech.imageresizershrinker.coreui.widget.controls.draw.DrawModeSelector
import ru.tech.imageresizershrinker.coreui.widget.controls.draw.DrawPathModeSelector
import ru.tech.imageresizershrinker.coreui.widget.controls.draw.LineWidthSelector
import ru.tech.imageresizershrinker.coreui.widget.controls.draw.OpenColorPickerCard
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.coreui.widget.other.DrawLockScreenOrientation
import ru.tech.imageresizershrinker.coreui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.coreui.widget.saver.DrawModeSaver
import ru.tech.imageresizershrinker.coreui.widget.saver.DrawPathModeSaver
import ru.tech.imageresizershrinker.coreui.widget.text.Marquee
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState
import ru.tech.imageresizershrinker.feature.draw.presentation.components.BitmapDrawer
import ru.tech.imageresizershrinker.feature.pick_color.presentation.components.PickColorFromImageSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawEditOption(
    visible: Boolean,
    imageManager: ImageManager<Bitmap, *>,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    orientation: Int,
    onGetBitmap: (Bitmap) -> Unit,
    undo: () -> Unit,
    redo: () -> Unit,
    paths: List<UiPathPaint>,
    lastPaths: List<UiPathPaint>,
    undonePaths: List<UiPathPaint>,
    addPath: (UiPathPaint) -> Unit,
) {
    val settingsState = LocalSettingsState.current
    bitmap?.let {
        var zoomEnabled by rememberSaveable { mutableStateOf(false) }

        val switch = @Composable {
            EnhancedSwitch(
                modifier = Modifier.then(
                    if (!useScaffold) Modifier.padding(start = 8.dp)
                    else Modifier
                ),
                colors = EnhancedSwitchDefaults.uncheckableColors(),
                checked = !zoomEnabled,
                onCheckedChange = { zoomEnabled = !zoomEnabled },
                thumbIcon = if (!zoomEnabled) {
                    Icons.Rounded.Draw
                } else Icons.Rounded.ZoomIn,
            )
        }

        val showPickColorSheet = rememberSaveable { mutableStateOf(false) }

        var isEraserOn by rememberSaveable { mutableStateOf(false) }

        var strokeWidth by rememberSaveable(stateSaver = PtSaver) { mutableStateOf(20.pt) }
        var drawColor by rememberSaveable(
            stateSaver = ColorSaver
        ) { mutableStateOf(Color.Black) }
        var drawMode by rememberSaveable(stateSaver = DrawModeSaver) { mutableStateOf(DrawMode.Pen) }
        var alpha by rememberSaveable(drawMode) {
            mutableFloatStateOf(if (drawMode is DrawMode.Highlighter) 0.4f else 1f)
        }
        var brushSoftness by rememberSaveable(drawMode, stateSaver = PtSaver) {
            mutableStateOf(if (drawMode is DrawMode.Neon) 35.pt else 0.pt)
        }
        var drawPathMode by rememberSaveable(stateSaver = DrawPathModeSaver) {
            mutableStateOf(DrawPathMode.Free)
        }

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
                EnhancedIconButton(
                    containerColor = animateColorAsState(
                        if (isEraserOn) MaterialTheme.colorScheme.mixedContainer
                        else Color.Transparent
                    ).value,
                    contentColor = animateColorAsState(
                        if (isEraserOn) MaterialTheme.colorScheme.onMixedContainer
                        else MaterialTheme.colorScheme.onSurface
                    ).value,
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        luminance = 0.1f
                    ),
                    onClick = {
                        isEraserOn = !isEraserOn
                    }
                ) {
                    Icon(Icons.Rounded.Eraser, null)
                }
            }
        }

        var stateBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }
        FullscreenEditOption(
            canGoBack = paths.isEmpty(),
            visible = visible,
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = {
                if (!useScaffold) secondaryControls()
                OpenColorPickerCard(
                    onOpen = {
                        showPickColorSheet.value = true
                    }
                )
                LineWidthSelector(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp
                    ),
                    value = strokeWidth.value,
                    onValueChange = { strokeWidth = it.pt }
                )
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.Highlighter && drawMode !is DrawMode.PathEffect,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    BrushSoftnessSelector(
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp, start = 16.dp),
                        value = brushSoftness.value,
                        onValueChange = { brushSoftness = it.pt }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.PathEffect,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    DrawColorSelector(
                        drawColor = drawColor,
                        onColorChange = { drawColor = it }
                    )
                }
                AnimatedVisibility(
                    visible = drawMode !is DrawMode.Neon && drawMode !is DrawMode.PathEffect,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    DrawAlphaSelector(
                        value = alpha,
                        onValueChange = { alpha = it }
                    )
                }
                DrawModeSelector(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    value = drawMode,
                    onValueChange = { drawMode = it }
                )
                DrawPathModeSelector(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    value = drawPathMode,
                    onValueChange = { drawPathMode = it }
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
                            visible = paths.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                onClick = {
                                    onGetBitmap(stateBitmap)
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
                                text = stringResource(R.string.draw),
                            )
                        }
                    }
                )
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                remember(bitmap) {
                    derivedStateOf {
                        bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                    }
                }.value.let { imageBitmap ->
                    val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
                    BitmapDrawer(
                        imageBitmap = imageBitmap,
                        paths = paths,
                        imageManager = imageManager,
                        strokeWidth = strokeWidth,
                        brushSoftness = brushSoftness,
                        drawColor = drawColor.copy(alpha),
                        onAddPath = addPath,
                        isEraserOn = isEraserOn,
                        drawMode = drawMode,
                        modifier = Modifier
                            .padding(16.dp)
                            .aspectRatio(aspectRatio, !useScaffold)
                            .fillMaxSize(),
                        zoomEnabled = zoomEnabled,
                        onDraw = {
                            stateBitmap = it
                        },
                        drawPathMode = drawPathMode,
                        backgroundColor = Color.Transparent
                    )
                }
            }
        }
        var color by rememberSaveable(stateSaver = ColorSaver) {
            mutableStateOf(Color.Black)
        }
        PickColorFromImageSheet(
            visible = showPickColorSheet,
            bitmap = stateBitmap,
            onColorChange = { color = it },
            color = color
        )

        if (visible) {
            DrawLockScreenOrientation(
                orientation = orientation
            )
        }
    }
}