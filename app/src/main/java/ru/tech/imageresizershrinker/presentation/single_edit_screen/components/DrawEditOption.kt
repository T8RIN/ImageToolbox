package ru.tech.imageresizershrinker.presentation.single_edit_screen.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.rounded.Draw
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.draw_screen.ColorSaver
import ru.tech.imageresizershrinker.presentation.draw_screen.DrawModeSaver
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BitmapDrawer
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BlurRadiusSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawAlphaSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawColorSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawModeSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.OpenColorPickerCard
import ru.tech.imageresizershrinker.presentation.draw_screen.components.PickColorFromImageSheet
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.DrawMode
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import ru.tech.imageresizershrinker.presentation.root.theme.icons.Eraser
import ru.tech.imageresizershrinker.presentation.root.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.widget.other.LockScreenOrientation
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    orientation: Int,
    onGetBitmap: (Bitmap) -> Unit,
    undo: () -> Unit,
    redo: () -> Unit,
    paths: List<PathPaint>,
    lastPaths: List<PathPaint>,
    undonePaths: List<PathPaint>,
    addPath: (PathPaint) -> Unit,
) {
    val settingsState = LocalSettingsState.current
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
                            if (!zoom) Icons.Rounded.Draw else Icons.Rounded.ZoomIn,
                            null,
                            Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                }
            )
        }

        val showPickColorSheet = rememberSaveable { mutableStateOf(false) }

        var isEraserOn by rememberSaveable { mutableStateOf(false) }

        var strokeWidth by rememberSaveable { mutableFloatStateOf(20f) }
        var drawColor by rememberSaveable(
            stateSaver = ColorSaver
        ) { mutableStateOf(Color.Black) }
        var drawMode by rememberSaveable(stateSaver = DrawModeSaver) { mutableStateOf(DrawMode.Pen) }
        var alpha by rememberSaveable(drawMode) {
            mutableFloatStateOf(if (drawMode is DrawMode.Highlighter) 0.4f else 1f)
        }
        var blurRadius by rememberSaveable(drawMode) {
            mutableFloatStateOf(if (drawMode is DrawMode.Neon) 35f else 0f)
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
                OutlinedIconButton(
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = animateColorAsState(
                            if (isEraserOn) MaterialTheme.colorScheme.mixedColor
                            else Color.Transparent
                        ).value,
                        contentColor = animateColorAsState(
                            if (isEraserOn) MaterialTheme.colorScheme.onMixedColor
                            else MaterialTheme.colorScheme.onSurface
                        ).value,
                        disabledContainerColor = Color.Transparent
                    ),
                    border = border,
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
            canGoBack = stateBitmap == bitmap,
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
                    strokeWidth = strokeWidth,
                    onChangeStrokeWidth = { strokeWidth = it }
                )
                AnimatedVisibility(visible = drawMode !is DrawMode.Highlighter) {
                    BlurRadiusSelector(
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp, start = 16.dp),
                        blurRadius = blurRadius,
                        onRadiusChange = { blurRadius = it }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                DrawColorSelector(
                    drawColor = drawColor,
                    onColorChange = { drawColor = it }
                )
                AnimatedVisibility(visible = drawMode !is DrawMode.Neon) {
                    DrawAlphaSelector(
                        alpha = alpha,
                        onAlphaChange = { alpha = it }
                    )
                }
                DrawModeSelector(
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    drawMode = drawMode,
                    onDrawModeChange = { drawMode = it }
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
                        AnimatedVisibility(visible = stateBitmap != bitmap) {
                            OutlinedIconButton(
                                colors = IconButtonDefaults.filledTonalIconButtonColors(),
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
                        strokeWidth = strokeWidth,
                        blurRadius = blurRadius,
                        drawColor = drawColor.copy(alpha),
                        onAddPath = addPath,
                        isEraserOn = isEraserOn,
                        drawMode = drawMode,
                        modifier = Modifier
                            .padding(16.dp)
                            .aspectRatio(aspectRatio, useScaffold)
                            .fillMaxSize(),
                        zoomEnabled = zoomEnabled,
                        onDraw = {
                            stateBitmap = it
                        },
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
            LockScreenOrientation(
                orientation = orientation
            )
        }
    }
}