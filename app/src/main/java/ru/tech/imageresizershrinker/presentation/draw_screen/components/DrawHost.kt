package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavController
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.erase_background_screen.components.PathPaint
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.fabBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.navBarsPaddingOnlyIfTheyAtTheEnd
import ru.tech.imageresizershrinker.presentation.root.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.Marquee
import ru.tech.imageresizershrinker.presentation.root.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawHost(
    modifier: Modifier,
    navController: NavController<DrawBehavior>,
    portrait: Boolean,
    zoomEnabled: Boolean,
    onSaveRequest: () -> Unit,
    controls: @Composable () -> Unit,
    secondaryControls: @Composable () -> Unit,
    paths: List<PathPaint>,
    isEraserOn: Boolean,
    drawMode: DrawMode,
    clearDrawing: () -> Unit,
    onPickImage: () -> Unit,
    startDrawOnBackground: (Int, Int, Color) -> Unit,
    scaffoldState: BottomSheetScaffoldState,
    isBitmapChanged: Boolean,
    onBack: () -> Unit,
    onShare: () -> Unit,
    backgroundColor: Color,
    drawColor: Color,
    drawAlpha: Float,
    strokeWidth: Float,
    bitmap: Bitmap,
    blurRadius: Float,
    addPath: (PathPaint) -> Unit,
    onDraw: (Bitmap) -> Unit
) {
    val showBackgroundDrawingSetup = rememberSaveable { mutableStateOf(false) }
    val settingsState = LocalSettingsState.current

    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val drawBox = @Composable {
        AnimatedContent(
            targetState = remember(bitmap) {
                derivedStateOf {
                    bitmap.copy(Bitmap.Config.ARGB_8888, true).asImageBitmap()
                }
            }.value,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { imageBitmap ->
            val aspectRatio = imageBitmap.width / imageBitmap.height.toFloat()
            BitmapDrawer(
                imageBitmap = imageBitmap,
                paths = paths,
                strokeWidth = strokeWidth,
                blurRadius = blurRadius,
                drawColor = drawColor.copy(drawAlpha),
                onAddPath = addPath,
                isEraserOn = isEraserOn,
                drawMode = drawMode,
                modifier = Modifier
                    .padding(16.dp)
                    .aspectRatio(aspectRatio, portrait)
                    .fillMaxSize(),
                zoomEnabled = zoomEnabled,
                onDraw = onDraw,
                backgroundColor = backgroundColor
            )
        }
    }

    AnimatedNavHost(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        controller = navController,
        transitionSpec = { _, _, _ ->
            fadeIn() togetherWith fadeOut()
        }
    ) { drawBehavior ->
        val topAppBar: @Composable () -> Unit = {
            TopAppBar(
                modifier = Modifier.drawHorizontalStroke(),
                title = {
                    Marquee(
                        edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    ) {
                        Text(stringResource(R.string.draw))
                    }
                },
                actions = {
                    if (portrait) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                                        scaffoldState.bottomSheetState.partialExpand()
                                    } else {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            },
                        ) {
                            Icon(Icons.Rounded.Tune, null)
                        }
                    }
                    IconButton(
                        onClick = onShare,
                        enabled = drawBehavior !is DrawBehavior.None
                    ) {
                        Icon(Icons.Outlined.Share, null)
                    }
                    IconButton(
                        onClick = {
                            clearDrawing()
                        },
                        enabled = drawBehavior !is DrawBehavior.None && isBitmapChanged
                    ) {
                        Icon(Icons.Outlined.Delete, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        3.dp
                    )
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                },
            )
        }

        Column {
            when (drawBehavior) {
                is DrawBehavior.Background -> {
                    topAppBar()
                    if (portrait) {
                        drawBox()
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
                                drawBox()
                            }
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                    .background(MaterialTheme.colorScheme.outlineVariant())
                            )
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
                                    .weight(0.7f)
                                    .clipToBounds()
                            ) {
                                item {
                                    secondaryControls()
                                    controls()
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
                    topAppBar()
                    if (portrait) {
                        drawBox()
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
                                drawBox()
                            }
                            Box(
                                Modifier
                                    .fillMaxHeight()
                                    .width(settingsState.borderWidth.coerceAtLeast(0.25.dp))
                                    .background(MaterialTheme.colorScheme.outlineVariant())
                            )
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
                                    .weight(0.7f)
                                    .clipToBounds()
                            ) {
                                item {
                                    secondaryControls()
                                    controls()
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

                    LargeTopAppBar(
                        scrollBehavior = scrollBehavior,
                        modifier = Modifier.drawHorizontalStroke(),
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.draw))
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                3.dp
                            )
                        ),
                        navigationIcon = {
                            IconButton(
                                onClick = onBack
                            ) {
                                Icon(Icons.Rounded.ArrowBack, null)
                            }
                        },
                        actions = {
                            TopAppBarEmoji()
                        }
                    )
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
                            .drawHorizontalStroke(true)
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    3.dp
                                )
                            ),
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
    var sheetBackgroundColor by remember { mutableStateOf(Color.White) }
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
                    startDrawOnBackground(width, height, sheetBackgroundColor)
                }
            ) {
                AutoSizeText(stringResource(R.string.ok))
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
                        backgroundColor = sheetBackgroundColor,
                        onColorChange = { sheetBackgroundColor = it }
                    )
                }
                HorizontalDivider()
                HorizontalDivider(Modifier.align(Alignment.BottomCenter))
            }
        },
        visible = showBackgroundDrawingSetup
    )
}