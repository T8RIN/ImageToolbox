package ru.tech.imageresizershrinker.filters_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.Light
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
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
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.coil.filters.BrightnessFilter
import ru.tech.imageresizershrinker.utils.coil.filters.ColorFilter
import ru.tech.imageresizershrinker.utils.coil.filters.FilterTransformation
import ru.tech.imageresizershrinker.utils.coil.filters.HueFilter
import ru.tech.imageresizershrinker.utils.coil.filters.SaturationFilter
import ru.tech.imageresizershrinker.widget.TitleItem
import ru.tech.imageresizershrinker.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.widget.utils.LocalSettingsState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddFiltersSheet(
    visible: MutableState<Boolean>,
    onFilterPicked: (FilterTransformation) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { 3 })

    SimpleSheet(
        sheetContent = {
            Box {
                Column {
                    TabRow(
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
                            Icons.Rounded.Light to stringResource(R.string.light_aka_illumination)
                        ).forEachIndexed { index, (icon, title) ->
                            Tab(
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
                    HorizontalPager(state = pagerState) {
                        if(it == 0) {
                            listOf(
                                HueFilter(context),
                                ColorFilter(context),
                                SaturationFilter(context),
                            ).forEach(onFilterPicked)
                        }
                    }
                }
                Divider(Modifier.align(Alignment.TopCenter))
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

