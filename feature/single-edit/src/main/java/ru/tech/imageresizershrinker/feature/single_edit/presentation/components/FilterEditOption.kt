package ru.tech.imageresizershrinker.feature.single_edit.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.exifinterface.media.ExifInterface
import coil.size.Size
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.coredomain.image.ImageManager
import ru.tech.imageresizershrinker.coredomain.image.Transformation
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.theme.mixedContainer
import ru.tech.imageresizershrinker.coreui.transformation.filter.UiFilter
import ru.tech.imageresizershrinker.coreui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.coreui.widget.image.Picture
import ru.tech.imageresizershrinker.coreui.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.coreui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.coreui.widget.other.FilterItem
import ru.tech.imageresizershrinker.coreui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.coreui.widget.other.showError
import ru.tech.imageresizershrinker.coreui.widget.sheets.AddFiltersSheet
import ru.tech.imageresizershrinker.coreui.widget.sheets.FilterReorderSheet
import ru.tech.imageresizershrinker.coreui.widget.sheets.PickColorFromImageSheet
import ru.tech.imageresizershrinker.coreui.widget.text.Marquee
import ru.tech.imageresizershrinker.coreui.widget.text.TitleItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap) -> Unit,
    imageManager: ImageManager<Bitmap, ExifInterface>,
    filterList: List<UiFilter<*>>,
    updateFilter: (Any, Int, (Throwable) -> Unit) -> Unit,
    removeAt: (Int) -> Unit,
    addFilter: (UiFilter<*>) -> Unit,
    updateOrder: (List<UiFilter<*>>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHost.current
    val context = LocalContext.current
    bitmap?.let {
        val scaffoldState = rememberBottomSheetScaffoldState()

        val showFilterSheet = rememberSaveable { mutableStateOf(false) }
        val showReorderSheet = rememberSaveable { mutableStateOf(false) }

        var stateBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }

        val showColorPicker = remember { mutableStateOf(false) }
        var tempColor by remember { mutableStateOf(Color.Black) }

        FullscreenEditOption(
            sheetSize = -1f,
            showControls = filterList.isNotEmpty(),
            canGoBack = stateBitmap == bitmap,
            visible = visible,
            modifier = Modifier.heightIn(max = LocalConfiguration.current.screenHeightDp.dp / 1.5f),
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Column(Modifier.container(MaterialTheme.shapes.extraLarge)) {
                        TitleItem(text = stringResource(R.string.filters))
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            filterList.forEachIndexed { index, filter ->
                                FilterItem(
                                    filter = filter,
                                    onFilterChange = {
                                        updateFilter(
                                            it,
                                            index
                                        ) {
                                            scope.launch {
                                                toastHostState.showError(
                                                    context,
                                                    it
                                                )
                                            }
                                        }
                                    },
                                    onLongPress = {
                                        showReorderSheet.value = true
                                    },
                                    showDragHandle = false,
                                    onRemove = {
                                        removeAt(index)
                                    }
                                )
                            }
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.mixedContainer,
                                onClick = { showFilterSheet.value = true },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            ) {
                                Icon(Icons.Rounded.PhotoFilter, null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(id = R.string.add_filter))
                            }
                        }
                    }
                }
            },
            fabButtons = {
                FloatingActionButton(
                    onClick = {
                        showFilterSheet.value = true
                    },
                    modifier = Modifier.autoElevatedBorder(autoElevation = 1.5.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    Icon(Icons.Rounded.PhotoFilter, null)
                }
            },
            scaffoldState = scaffoldState,
            actions = {
                if (filterList.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.add_filter),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                } else {
                    EnhancedIconButton(
                        containerColor = Color.Transparent,
                        contentColor = LocalContentColor.current,
                        enableAutoShadowAndBorder = false,
                        onClick = {
                            showColorPicker.value = true
                        },
                    ) {
                        Icon(Icons.Outlined.Colorize, null)
                    }
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
                            visible = stateBitmap != bitmap,
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
                                text = stringResource(R.string.filter),
                            )
                        }
                    }
                )
            }
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Picture(
                    model = bitmap,
                    shape = RectangleShape,
                    transformations = remember(filterList) {
                        derivedStateOf {
                            filterList.map {
                                imageManager.getFilterProvider().filterToTransformation(it).toCoil()
                            }
                        }
                    }.value,
                    onSuccess = {
                        stateBitmap = it.result.drawable.toBitmap()
                    },
                    showTransparencyChecker = false,
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                        .transparencyChecker()
                        .animatedZoom(animatedZoomState = rememberAnimatedZoomState()),
                    contentScale = ContentScale.Fit,
                )
            }
        }

        AddFiltersSheet(
            visible = showFilterSheet,
            previewBitmap = stateBitmap,
            onFilterPicked = {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
                addFilter(it.newInstance())
            },
            onFilterPickedWithParams = {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
                addFilter(it)
            },
            imageManager = imageManager
        )

        FilterReorderSheet(
            filterList = filterList,
            visible = showReorderSheet,
            updateOrder = updateOrder
        )

        PickColorFromImageSheet(
            visible = showColorPicker,
            bitmap = stateBitmap,
            onColorChange = { tempColor = it },
            color = tempColor
        )
    }
}

private fun Transformation<Bitmap>.toCoil(): coil.transform.Transformation {
    return object : coil.transform.Transformation {
        override val cacheKey: String
            get() = this@toCoil.cacheKey

        override suspend fun transform(
            input: Bitmap,
            size: Size
        ): Bitmap = this@toCoil.transform(input, size)
    }
}