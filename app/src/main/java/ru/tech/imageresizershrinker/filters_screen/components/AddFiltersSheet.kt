package ru.tech.imageresizershrinker.filters_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.LensBlur
import androidx.compose.material.icons.rounded.Light
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.PreferenceItem
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.coil.filters.BilaterialBlurFilter
import ru.tech.imageresizershrinker.utils.coil.filters.BlackAndWhiteFilter
import ru.tech.imageresizershrinker.utils.coil.filters.BoxBlurFilter
import ru.tech.imageresizershrinker.utils.coil.filters.BrightnessFilter
import ru.tech.imageresizershrinker.utils.coil.filters.BulgeDistortionEffect
import ru.tech.imageresizershrinker.utils.coil.filters.ColorFilter
import ru.tech.imageresizershrinker.utils.coil.filters.ColorMatrixFilter
import ru.tech.imageresizershrinker.utils.coil.filters.ContrastFilter
import ru.tech.imageresizershrinker.utils.coil.filters.CrosshatchFilter
import ru.tech.imageresizershrinker.utils.coil.filters.DilationFilter
import ru.tech.imageresizershrinker.utils.coil.filters.EmbossFilter
import ru.tech.imageresizershrinker.utils.coil.filters.ExposureFilter
import ru.tech.imageresizershrinker.utils.coil.filters.FilterTransformation
import ru.tech.imageresizershrinker.utils.coil.filters.GCAColorSpaceFilter
import ru.tech.imageresizershrinker.utils.coil.filters.GammaFilter
import ru.tech.imageresizershrinker.utils.coil.filters.GaussianBlurFilter
import ru.tech.imageresizershrinker.utils.coil.filters.GlassSphereRefractionFilter
import ru.tech.imageresizershrinker.utils.coil.filters.HalftoneFilter
import ru.tech.imageresizershrinker.utils.coil.filters.HazeFilter
import ru.tech.imageresizershrinker.utils.coil.filters.HighlightsAndShadowsFilter
import ru.tech.imageresizershrinker.utils.coil.filters.HueFilter
import ru.tech.imageresizershrinker.utils.coil.filters.KuwaharaFilter
import ru.tech.imageresizershrinker.utils.coil.filters.LaplacianFilter
import ru.tech.imageresizershrinker.utils.coil.filters.MonochromeFilter
import ru.tech.imageresizershrinker.utils.coil.filters.NegativeFilter
import ru.tech.imageresizershrinker.utils.coil.filters.OpacityFilter
import ru.tech.imageresizershrinker.utils.coil.filters.PosterizeFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SepiaFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SharpenFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SketchFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SlowBlurFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SmoothToonFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SobelEdgeDetectionFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SolarizeFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SphereRefractionFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SwirlDistortionEffect
import ru.tech.imageresizershrinker.utils.coil.filters.ToonFilter
import ru.tech.imageresizershrinker.utils.coil.filters.VibranceFilter
import ru.tech.imageresizershrinker.utils.coil.filters.VignetteFilter
import ru.tech.imageresizershrinker.utils.coil.filters.WhiteBalanceFilter
import ru.tech.imageresizershrinker.widget.TitleItem
import ru.tech.imageresizershrinker.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddFiltersSheet(
    visible: MutableState<Boolean>,
    onFilterPicked: (FilterTransformation<*>) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 5 })

    SimpleSheet(
        sheetContent = {
            Box {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ScrollableTabRow(
                            divider = {},
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
                            selectedTabIndex = pagerState.currentPage,
                            indicator = { tabPositions ->
                                if (pagerState.currentPage < tabPositions.size) {
                                    TabRowDefaults.PrimaryIndicator(
                                        modifier = Modifier
                                            .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                        width = 60.dp,
                                        height = 4.dp,
                                        shape = RoundedCornerShape(topStart = 100f, topEnd = 100f)
                                    )
                                }
                            }
                        ) {
                            listOf(
                                Icons.Rounded.FormatColorFill to stringResource(id = R.string.color),
                                Icons.Rounded.Light to stringResource(R.string.light_aka_illumination),
                                Icons.Rounded.Grain to stringResource(R.string.effect),
                                Icons.Rounded.LensBlur to stringResource(R.string.blur),
                                Icons.Rounded.Animation to stringResource(R.string.distortion)
                            ).forEachIndexed { index, (icon, title) ->
                                Tab(
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clip(CircleShape),
                                    selected = pagerState.currentPage == index,
                                    onClick = {
                                        scope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
                                    },
                                    icon = { Icon(icon, null) },
                                    text = { Text(title) }
                                )
                            }
                        }
                    }
                    Divider()
                    HorizontalPager(state = pagerState, beyondBoundsPageCount = 4) {
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            when (it) {
                                0 -> {
                                    listOf(
                                        HueFilter(context),
                                        ColorFilter(context),
                                        SaturationFilter(context),
                                        VibranceFilter(context),
                                        GCAColorSpaceFilter(context),
                                        MonochromeFilter(context),
                                        SepiaFilter(context),
                                        NegativeFilter(context),
                                        BlackAndWhiteFilter(context),
                                        ColorMatrixFilter(context)
                                    ).forEach {
                                        PreferenceItem(
                                            title = stringResource(it.title),
                                            endIcon = Icons.Rounded.AddCircleOutline,
                                            onClick = {
                                                visible.value = false
                                                onFilterPicked(it)
                                            }
                                        )
                                    }
                                }

                                1 -> {
                                    listOf(
                                        BrightnessFilter(context),
                                        ContrastFilter(context),
                                        ExposureFilter(context),
                                        WhiteBalanceFilter(context),
                                        GammaFilter(context),
                                        HighlightsAndShadowsFilter(context),
                                        SolarizeFilter(context),
                                        HazeFilter(context),
                                    ).forEach {
                                        PreferenceItem(
                                            title = stringResource(it.title),
                                            endIcon = Icons.Rounded.AddCircleOutline,
                                            onClick = {
                                                visible.value = false
                                                onFilterPicked(it)
                                            }
                                        )
                                    }
                                }

                                2 -> {
                                    listOf(
                                        SharpenFilter(context),
                                        CrosshatchFilter(context),
                                        SobelEdgeDetectionFilter(context),
                                        HalftoneFilter(context),
                                        EmbossFilter(context),
                                        LaplacianFilter(context),
                                        VignetteFilter(context),
                                        KuwaharaFilter(context),
                                        DilationFilter(context),
                                        OpacityFilter(context),
                                        ToonFilter(context),
                                        SmoothToonFilter(context),
                                        SketchFilter(context),
                                        PosterizeFilter(context)
                                    ).forEach {
                                        PreferenceItem(
                                            title = stringResource(it.title),
                                            endIcon = Icons.Rounded.AddCircleOutline,
                                            onClick = {
                                                visible.value = false
                                                onFilterPicked(it)
                                            }
                                        )
                                    }
                                }

                                3 -> {
                                    listOf(
                                        GaussianBlurFilter(context),
                                        BoxBlurFilter(context),
                                        BilaterialBlurFilter(context),
                                        SlowBlurFilter(context)
                                    ).forEach {
                                        PreferenceItem(
                                            title = stringResource(it.title),
                                            endIcon = Icons.Rounded.AddCircleOutline,
                                            onClick = {
                                                visible.value = false
                                                onFilterPicked(it)
                                            }
                                        )
                                    }
                                }

                                4 -> {
                                    listOf(
                                        SwirlDistortionEffect(context),
                                        BulgeDistortionEffect(context),
                                        SphereRefractionFilter(context),
                                        GlassSphereRefractionFilter(context)
                                    ).forEach {
                                        PreferenceItem(
                                            title = stringResource(it.title),
                                            endIcon = Icons.Rounded.AddCircleOutline,
                                            onClick = {
                                                visible.value = false
                                                onFilterPicked(it)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Divider(Modifier.align(Alignment.BottomCenter))
            }
        },
        title = {
            TitleItem(text = stringResource(R.string.filter), icon = Icons.Rounded.PhotoFilter)
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.filledTonalButtonColors(),
                border = BorderStroke(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                ),
                onClick = { visible.value = false }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        visible = visible
    )
}

