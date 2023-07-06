package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Redo
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Undo
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.t8rin.drawbox.domain.AbstractDrawController
import com.t8rin.drawbox.domain.DrawController
import com.t8rin.drawbox.presentation.compose.DrawBox
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavController
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.core.android.BitmapUtils.restrict
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.presentation.root.theme.icons.Eraser
import ru.tech.imageresizershrinker.presentation.root.theme.mixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedColor
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.model.transformation.UpscaleTransformation
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheBottom
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.controls.ExtensionGroup
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun DrawHost(
    modifier: Modifier,
    navController: NavController<DrawBehavior>,
    drawController: DrawController?,
    portrait: Boolean,
    zoomEnabled: Boolean,
    onGetDrawController: (DrawController) -> Unit,
    onSaveRequest: () -> Unit,
    mimeType: MimeType,
    onMimeTypeChange: (MimeType) -> Unit,
    uri: Uri,
    onPickImage: () -> Unit,
    switch: @Composable () -> Unit,
    startDrawOnBackground: (Int, Int, Color) -> Unit
) {
    val showBackgroundDrawingSetup = rememberSaveable { mutableStateOf(false) }
    val settingsState = LocalSettingsState.current
    AnimatedNavHost(
        modifier = modifier,
        controller = navController,
        transitionSpec = { _, _, _ ->
            fadeIn() togetherWith fadeOut()
        }
    ) { drawBehavior ->
        when (drawBehavior) {
            is DrawBehavior.Background -> {
                if (portrait) {
                    DrawBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        zoomEnabled = zoomEnabled,
                        drawController = drawController,
                        drawingModifier = Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant()
                        ),
                        onGetDrawController = onGetDrawController
                    ) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(drawBehavior.run { width / height.toFloat() })
                                .fillMaxSize()
                                .background(
                                    drawController?.backgroundColor ?: Color.Transparent
                                )
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .weight(1.2f)
                                .clipToBounds()
                        ) {
                            DrawBox(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .navBarsPaddingOnlyIfTheyAtTheBottom(),
                                drawController = drawController,
                                drawingModifier = Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant()
                                ),
                                zoomEnabled = zoomEnabled,
                                onGetDrawController = onGetDrawController
                            ) {
                                Box(
                                    modifier = Modifier
                                        .aspectRatio(drawBehavior.run { width / height.toFloat() })
                                        .fillMaxSize()
                                        .background(
                                            drawController?.backgroundColor
                                                ?: Color.Transparent
                                        )
                                )
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                        )
                        drawController?.let { drawController ->
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(
                                    bottom = WindowInsets
                                        .navigationBars
                                        .asPaddingValues()
                                        .calculateBottomPadding() + WindowInsets.ime
                                        .asPaddingValues()
                                        .calculateBottomPadding(),
                                    top = if (drawBehavior is DrawBehavior.None) 20.dp else 0.dp,
                                ),
                                modifier = Modifier
                                    .weight(0.5f)
                                    .clipToBounds()
                            ) {
                                item {
                                    val border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(
                                            luminance = 0.1f
                                        )
                                    )
                                    Row(
                                        Modifier
                                            .padding(16.dp)
                                            .block(shape = CircleShape)
                                    ) {
                                        switch()
                                        OutlinedIconButton(
                                            border = border,
                                            onClick = { drawController.undo() },
                                            enabled = drawController.lastPaths.isNotEmpty() || drawController.paths.isNotEmpty()
                                        ) {
                                            Icon(Icons.Rounded.Undo, null)
                                        }
                                        OutlinedIconButton(
                                            border = border,
                                            onClick = { drawController.redo() },
                                            enabled = drawController.undonePaths.isNotEmpty()
                                        ) {
                                            Icon(Icons.Rounded.Redo, null)
                                        }
                                        val isEraserOn = drawController.isEraserOn
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
                                            onClick = { drawController.toggleEraser() }
                                        ) {
                                            Icon(Icons.Rounded.Eraser, null)
                                        }
                                    }
                                    DrawBackgroundSelector(drawController)
                                    DrawColorSelector(drawController)
                                    DrawAlphaSelector(drawController)
                                    LineWidthSelector(drawController)
                                    ExtensionGroup(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .navigationBarsPadding(),
                                        enabled = drawBehavior !is DrawBehavior.None,
                                        mimeType = mimeType,
                                        onMimeChange = onMimeTypeChange
                                    )
                                }
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                                .padding(start = 20.dp)
                        )
                        Column(
                            Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxHeight()
                                .navigationBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            FloatingActionButton(
                                onClick = onSaveRequest,
                                modifier = Modifier.fabBorder(),
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(Icons.Rounded.Save, null)
                            }
                        }
                    }
                }
            }

            is DrawBehavior.Image -> {
                if (portrait) {
                    DrawBox(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        drawController = drawController,
                        drawingModifier = Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant()
                        ),
                        zoomEnabled = zoomEnabled,
                        onGetDrawController = onGetDrawController
                    ) {
                        Picture(
                            model = uri,
                            contentScale = ContentScale.Fit,
                            shape = RectangleShape,
                            transformations = listOf(UpscaleTransformation())
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .weight(1.2f)
                                .clipToBounds()
                        ) {
                            DrawBox(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(8.dp)
                                    .navBarsPaddingOnlyIfTheyAtTheBottom(),
                                drawController = drawController,
                                drawingModifier = Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant()
                                ),
                                zoomEnabled = zoomEnabled,
                                onGetDrawController = onGetDrawController
                            ) {
                                Picture(
                                    model = uri,
                                    contentScale = ContentScale.Fit,
                                    shape = RectangleShape,
                                    transformations = listOf(UpscaleTransformation())
                                )
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                        )
                        drawController?.let { drawController ->
                            LazyColumn(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                contentPadding = PaddingValues(
                                    bottom = WindowInsets
                                        .navigationBars
                                        .asPaddingValues()
                                        .calculateBottomPadding() + WindowInsets.ime
                                        .asPaddingValues()
                                        .calculateBottomPadding(),
                                    top = if (drawBehavior is DrawBehavior.None) 20.dp else 0.dp,
                                ),
                                modifier = Modifier
                                    .weight(0.5f)
                                    .clipToBounds()
                            ) {
                                item {
                                    val border = BorderStroke(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.outlineVariant(
                                            luminance = 0.1f
                                        )
                                    )
                                    Row(
                                        Modifier
                                            .padding(16.dp)
                                            .block(shape = CircleShape)
                                    ) {
                                        switch()
                                        OutlinedIconButton(
                                            border = border,
                                            onClick = { drawController.undo() },
                                            enabled = drawController.lastPaths.isNotEmpty() || drawController.paths.isNotEmpty()
                                        ) {
                                            Icon(Icons.Rounded.Undo, null)
                                        }
                                        OutlinedIconButton(
                                            border = border,
                                            onClick = { drawController.redo() },
                                            enabled = drawController.undonePaths.isNotEmpty()
                                        ) {
                                            Icon(Icons.Rounded.Redo, null)
                                        }
                                        val isEraserOn = drawController.isEraserOn
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
                                            onClick = { drawController.toggleEraser() }
                                        ) {
                                            Icon(Icons.Rounded.Eraser, null)
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    DrawColorSelector(drawController)
                                    DrawAlphaSelector(drawController)
                                    LineWidthSelector(drawController)
                                    ExtensionGroup(
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .navigationBarsPadding(),
                                        enabled = drawBehavior !is DrawBehavior.None,
                                        mimeType = mimeType,
                                        onMimeChange = onMimeTypeChange
                                    )
                                }
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant())
                                .padding(start = 20.dp)
                        )
                        Column(
                            Modifier
                                .padding(horizontal = 20.dp)
                                .fillMaxHeight()
                                .navigationBarsPadding(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            FloatingActionButton(
                                onClick = onPickImage,
                                modifier = Modifier.fabBorder(),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                content = {
                                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            FloatingActionButton(
                                onClick = onSaveRequest,
                                modifier = Modifier.fabBorder(),
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(Icons.Rounded.Save, null)
                            }
                        }
                    }
                }
            }

            is DrawBehavior.None -> {
                Column {
                    val cutout = WindowInsets.displayCutout.asPaddingValues()
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.weight(1f),
                        columns = StaggeredGridCells.Adaptive(400.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            12.dp,
                            Alignment.CenterHorizontally
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
                        item {
                            PreferenceItem(
                                onClick = onPickImage,
                                icon = Icons.Rounded.Image,
                                title = stringResource(R.string.draw_on_image),
                                subtitle = stringResource(R.string.draw_on_image_sub),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        item {
                            PreferenceItem(
                                onClick = { showBackgroundDrawingSetup.value = true },
                                icon = Icons.Rounded.FormatPaint,
                                title = stringResource(R.string.draw_on_background),
                                subtitle = stringResource(R.string.draw_on_background_sub),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    3.dp
                                )
                            )
                            .drawHorizontalStroke(true),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ExtendedFloatingActionButton(
                            onClick = onPickImage,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .padding(16.dp)
                                .fabBorder(),
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            text = {
                                Text(stringResource(R.string.pick_image_alt))
                            },
                            icon = {
                                Icon(Icons.Rounded.AddPhotoAlternate, null)
                            }
                        )
                    }
                }
            }
        }
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    var height by remember(showBackgroundDrawingSetup.value, configuration) {
        mutableIntStateOf(with(density) { configuration.screenHeightDp.dp.roundToPx() })
    }
    var width by remember(showBackgroundDrawingSetup.value, configuration) {
        mutableIntStateOf(with(density) { configuration.screenWidthDp.dp.roundToPx() })
    }
    var color by remember { mutableStateOf(Color.White) }
    SimpleSheet(
        title = {
            TitleItem(
                text = stringResource(R.string.draw),
                icon = Icons.Rounded.Draw
            )
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.filledTonalButtonColors(),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ),
                onClick = {
                    showBackgroundDrawingSetup.value = false
                    startDrawOnBackground(width, height, color)
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        sheetContent = {
            Box {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .block(shape = RoundedCornerShape(24.dp))
                    ) {
                        RoundedTextField(
                            value = width.takeIf { it != 0 }?.toString() ?: "",
                            onValueChange = {
                                width = it.restrict(8192).toIntOrNull() ?: 0
                            },
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            label = {
                                Text(stringResource(R.string.width, " "))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    start = 8.dp,
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    end = 4.dp
                                )
                        )
                        RoundedTextField(
                            value = height.takeIf { it != 0 }?.toString() ?: "",
                            onValueChange = {
                                height = it.restrict(8192).toIntOrNull() ?: 0
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            shape = RoundedCornerShape(12.dp),
                            label = {
                                Text(stringResource(R.string.height, " "))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    start = 4.dp,
                                    top = 8.dp,
                                    bottom = 8.dp,
                                    end = 8.dp
                                ),
                        )
                    }
                    DrawBackgroundSelector(
                        drawController = colorSelectorDrawController(
                            onColorChange = { color = it },
                            color = color
                        )
                    )
                }
                Divider()
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        visible = showBackgroundDrawingSetup
    )
}

@Composable
private fun colorSelectorDrawController(
    onColorChange: (Color) -> Unit, color: Color
) = remember(onColorChange, color) {
    object : AbstractDrawController() {
        override val backgroundColor: Color
            get() = color

        override fun setDrawBackground(color: Color) = onColorChange(color)
    }
}