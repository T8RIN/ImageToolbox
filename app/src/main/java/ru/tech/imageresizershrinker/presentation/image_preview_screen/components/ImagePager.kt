package ru.tech.imageresizershrinker.presentation.image_preview_screen.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.image.zoom.ZoomLevel
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.icons.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.widget.image.Picture
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.BytesResizePreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.CipherPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.CropPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.DeleteExifPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.DrawPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.FilterPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.GeneratePalettePreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.LimitsPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.PickColorPreference
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.screens.SingleResizePreference
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImagePager(
    visible: Boolean,
    selectedUri: Uri?,
    uris: List<Uri>?,
    onUriSelected: (Uri?) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsState = LocalSettingsState.current
    val navController = LocalNavController.current

    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize(),
        enter = slideInHorizontally(
            animationSpec()
        ) { -it / 3 } + fadeIn(
            animationSpec(500)
        ),
        exit = slideOutHorizontally(
            animationSpec()
        ) { -it / 3 } + fadeOut(
            animationSpec(500)
        )
    ) {
        val wantToEdit = rememberSaveable { mutableStateOf(false) }
        val state = rememberPagerState(
            initialPage = selectedUri?.let {
                uris?.indexOf(it)
            } ?: 0,
            pageCount = {
                uris?.size ?: 0
            }
        )
        LaunchedEffect(state.currentPage) {
            onUriSelected(
                uris?.getOrNull(state.currentPage)
            )
        }
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectTapGestures { }
                }
        ) {
            val zoomState = rememberAnimatedZoomState(
                minZoom = 0.5f,
                maxZoom = 8f,
                moveToBounds = true,
                zoomable = true,
                pannable = true,
                rotatable = true
            )
            HorizontalPager(
                state = state,
                modifier = Modifier.fillMaxSize(),
                beyondBoundsPageCount = 5,
                userScrollEnabled = zoomState.zoom == 1f
            ) { page ->
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Picture(
                        model = uris?.getOrNull(page),
                        modifier = Modifier
                            .fillMaxSize()
                            .animatedZoom(
                                animatedZoomState = zoomState,
                                zoomOnDoubleTap = {
                                    when (it) {
                                        ZoomLevel.Min -> 1f
                                        ZoomLevel.Mid -> 4f
                                        ZoomLevel.Max -> 8f
                                    }
                                },
                                enabled = { zoom, _, _ ->
                                    zoom != 1f
                                }
                            )
                            .systemBarsPadding()
                            .displayCutoutPadding(),
                        contentScale = ContentScale.Fit,
                        shape = RectangleShape
                    )
                }
            }
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    uris?.size?.takeIf { it > 1 }?.let {
                        Text(
                            text = "${state.currentPage + 1}/$it",
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .padding(vertical = 4.dp, horizontal = 12.dp),
                            color = Color.White
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(!uris.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .clip(CircleShape)
                                .clickable {
                                    wantToEdit.value = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CreateAlt,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                },
                navigationIcon = {
                    AnimatedVisibility(!uris.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .size(40.dp)
                                .background(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .clip(CircleShape)
                                .clickable {
                                    onDismiss()
                                    onUriSelected(null)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                },
            )
        }

        SimpleSheet(
            title = {
                TitleItem(text = stringResource(R.string.image), icon = Icons.Rounded.Image)
            },
            visible = wantToEdit,
            confirmButton = {
                OutlinedButton(
                    onClick = {
                        wantToEdit.value = false
                    },
                    border = BorderStroke(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant()
                    )
                ) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            sheetContent = {
                val navigate: (Screen) -> Unit = { screen ->
                    scope.launch {
                        wantToEdit.value = false
                        delay(200)
                        navController.navigate(screen)
                    }
                }
                val color = MaterialTheme.colorScheme.secondaryContainer
                Box {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(250.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        item {
                            SingleResizePreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.SingleResize(selectedUri!!)) },
                                color = color
                            )
                        }
                        item {
                            BytesResizePreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.ResizeByBytes(listOf(selectedUri!!))) },
                                color = color
                            )
                        }
                        item {
                            FilterPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.Filter(listOf(selectedUri!!))) },
                                color = color
                            )
                        }
                        item {
                            CropPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.Crop(selectedUri)) },
                                color = color
                            )
                        }
                        item {
                            DrawPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.Draw(selectedUri)) },
                                color = color
                            )
                        }
                        item {
                            CipherPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.Cipher(selectedUri!!)) },
                                color = color
                            )
                        }
                        item {
                            PickColorPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.PickColorFromImage(selectedUri)) },
                                color = color
                            )
                        }
                        item {
                            GeneratePalettePreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.GeneratePalette(selectedUri)) },
                                color = color
                            )
                        }
                        item {
                            DeleteExifPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.DeleteExif(listOf(selectedUri!!))) },
                                color = color
                            )
                        }
                        item {
                            LimitsPreference(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navigate(Screen.LimitResize(uris)) },
                                color = color
                            )
                        }
                    }
                    Divider(Modifier.align(Alignment.TopCenter))
                    Divider(Modifier.align(Alignment.BottomCenter))
                }
            }
        )

        if (visible) {
            BackHandler {
                onDismiss()
                onUriSelected(null)
            }
        }
    }
}

private fun <T> animationSpec(
    duration: Int = 500,
    delay: Int = 0
) = tween<T>(
    durationMillis = duration,
    delayMillis = delay,
    easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
)