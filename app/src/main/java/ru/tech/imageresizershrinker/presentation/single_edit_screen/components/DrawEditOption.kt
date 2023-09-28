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
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BitmapDrawer
import ru.tech.imageresizershrinker.presentation.draw_screen.components.BrushSoftnessSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawAlphaSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawArrowsSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawColorSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawMode
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawModeSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.LineWidthSelector
import ru.tech.imageresizershrinker.presentation.draw_screen.components.OpenColorPickerCard
import ru.tech.imageresizershrinker.presentation.draw_screen.components.PickColorFromImageSheet
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import ru.tech.imageresizershrinker.presentation.root.icons.material.Eraser
import ru.tech.imageresizershrinker.presentation.root.theme.mixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.saver.ColorSaver
import ru.tech.imageresizershrinker.presentation.root.utils.saver.DrawModeSaver
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.presentation.root.widget.other.LockScreenOrientation
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

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
        var brushSoftness by rememberSaveable(drawMode) {
            mutableFloatStateOf(if (drawMode is DrawMode.Neon) 35f else 0f)
        }
        var drawArrowsEnabled by remember {
            mutableStateOf(false)
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
                AnimatedVisibility(visible = drawMode !is DrawMode.Highlighter && drawMode !is DrawMode.PrivacyBlur) {
                    BrushSoftnessSelector(
                        modifier = Modifier
                            .padding(top = 16.dp, end = 16.dp, start = 16.dp),
                        value = brushSoftness,
                        onValueChange = { brushSoftness = it }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(visible = drawMode !is DrawMode.PrivacyBlur) {
                    DrawColorSelector(
                        drawColor = drawColor,
                        onColorChange = { drawColor = it }
                    )
                }
                AnimatedVisibility(visible = drawMode !is DrawMode.Neon && drawMode !is DrawMode.PrivacyBlur) {
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
                AnimatedVisibility(!isEraserOn) {
                    DrawArrowsSelector(
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        checked = drawArrowsEnabled,
                        onCheckedChange = { drawArrowsEnabled = it }
                    )
                }
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
                        AnimatedVisibility(visible = stateBitmap != bitmap) {
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
                            .aspectRatio(aspectRatio, useScaffold)
                            .fillMaxSize(),
                        zoomEnabled = zoomEnabled,
                        onDraw = {
                            stateBitmap = it
                        },
                        drawArrowsEnabled = drawArrowsEnabled,
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