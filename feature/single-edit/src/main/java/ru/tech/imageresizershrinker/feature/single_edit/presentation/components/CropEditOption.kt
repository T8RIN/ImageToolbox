package ru.tech.imageresizershrinker.feature.single_edit.presentation.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.settings.CropProperties
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.coredomain.model.DomainAspectRatio
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.coreui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.coreui.widget.other.Loading
import ru.tech.imageresizershrinker.coreui.widget.text.Marquee
import ru.tech.imageresizershrinker.feature.crop.presentation.components.AspectRatioSelection
import ru.tech.imageresizershrinker.feature.crop.presentation.components.CropMaskSelection
import ru.tech.imageresizershrinker.feature.crop.presentation.components.Cropper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropEditOption(
    visible: Boolean,
    onDismiss: () -> Unit,
    useScaffold: Boolean,
    bitmap: Bitmap?,
    onGetBitmap: (Bitmap) -> Unit,
    cropProperties: CropProperties,
    selectedAspectRatio: DomainAspectRatio,
    setCropAspectRatio: (DomainAspectRatio, AspectRatio) -> Unit,
    setCropMask: (CropOutlineProperty) -> Unit,
    loadImage: suspend (Uri) -> Bitmap?
) {
    val scope = rememberCoroutineScope()
    bitmap?.let {
        var crop by remember(visible) { mutableStateOf(false) }
        var stateBitmap by remember(bitmap, visible) { mutableStateOf(bitmap) }
        FullscreenEditOption(
            canGoBack = stateBitmap == bitmap,
            visible = visible,
            onDismiss = onDismiss,
            useScaffold = useScaffold,
            controls = {
                AspectRatioSelection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    selectedAspectRatio = selectedAspectRatio,
                    onAspectRatioChange = setCropAspectRatio
                )
                HorizontalDivider()
                CropMaskSelection(
                    onCropMaskChange = { setCropMask(it) },
                    selectedItem = cropProperties.cropOutlineProperty,
                    loadImage = {
                        loadImage(it)?.asImageBitmap()
                    }
                )
            },
            fabButtons = {
                var job by remember { mutableStateOf<Job?>(null) }
                FloatingActionButton(
                    onClick = {
                        job?.cancel()
                        job = scope.launch {
                            delay(500)
                            crop = true
                        }
                    },
                    modifier = Modifier.autoElevatedBorder(autoElevation = 1.5.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                ) {
                    Icon(Icons.Rounded.Crop, null)
                }
            },
            actions = {},
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
                                text = stringResource(R.string.crop),
                            )
                        }
                    }
                )
            }
        ) {
            var loading by remember { mutableStateOf(false) }
            Box(contentAlignment = Alignment.Center) {
                Cropper(
                    bitmap = stateBitmap,
                    crop = crop,
                    imageCropStarted = { loading = true },
                    imageCropFinished = {
                        stateBitmap = it
                        crop = false
                        scope.launch {
                            delay(500)
                            loading = false
                        }
                    },
                    cropProperties = cropProperties
                )
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
    }
}