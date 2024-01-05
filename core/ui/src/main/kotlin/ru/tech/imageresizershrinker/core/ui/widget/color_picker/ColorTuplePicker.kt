package ru.tech.imageresizershrinker.core.ui.widget.color_picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.calculateSecondaryColor
import com.t8rin.dynamic.theme.calculateSurfaceColor
import com.t8rin.dynamic.theme.calculateTertiaryColor
import com.t8rin.dynamic.theme.rememberAppColorTuple
import com.t8rin.dynamic.theme.rememberColorScheme
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@ExperimentalMaterial3Api
@Composable
fun ColorTuplePicker(
    visible: MutableState<Boolean>,
    colorTuple: ColorTuple,
    title: String = stringResource(R.string.color_scheme),
    onColorChange: (ColorTuple) -> Unit
) {
    val settingsState = LocalSettingsState.current

    var primary by rememberSaveable(colorTuple) { mutableIntStateOf(colorTuple.primary.toArgb()) }
    var secondary by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.secondary?.toArgb() ?: colorTuple.primary.calculateSecondaryColor()
        )
    }
    var tertiary by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.tertiary?.toArgb() ?: colorTuple.primary.calculateTertiaryColor()
        )
    }

    var surface by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.surface?.toArgb() ?: colorTuple.primary.calculateSurfaceColor()
        )
    }

    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = true,
        darkTheme = true
    )

    val scheme = rememberColorScheme(
        amoledMode = false,
        isDarkTheme = true,
        colorTuple = appColorTuple,
        contrastLevel = settingsState.themeContrastLevel,
        style = settingsState.themeStyle,
        dynamicColor = settingsState.isDynamicColors,
        isInvertColors = settingsState.isInvertThemeColors
    )

    LaunchedEffect(visible.value) {
        if (!visible.value) {
            delay(1000)
            primary = colorTuple.primary.toArgb()
            secondary = colorTuple.secondary?.toArgb()
                ?: colorTuple.primary.calculateSecondaryColor()
            tertiary =
                colorTuple.tertiary?.toArgb()
                    ?: colorTuple.primary.calculateTertiaryColor()
            surface =
                colorTuple.surface?.toArgb()
                    ?: colorTuple.primary.calculateSurfaceColor()
        }
    }

    SimpleSheet(
        visible = visible,
        title = {
            TitleItem(
                text = title,
                icon = Icons.Outlined.Palette
            )
        },
        sheetContent = {
            Box {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item(
                        span = {
                            GridItemSpan(maxCurrentLineSpan)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .container(RoundedCornerShape(24.dp))
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Rounded.FormatColorFill, null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.monet_colors))
                            Spacer(Modifier.width(8.dp))
                            Spacer(Modifier.weight(1f))
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                onClick = {
                                    scheme.apply {
                                        primary = this.primary.toArgb()
                                        if (settingsState.themeStyle == PaletteStyle.TonalSpot) {
                                            secondary = this.secondary.toArgb()
                                            tertiary = this.tertiary.toArgb()
                                            surface = this.surface.toArgb()
                                        }
                                    }
                                },
                            ) {
                                Icon(Icons.Rounded.ContentPaste, null)
                            }
                        }
                    }
                    item(
                        span = {
                            if (settingsState.themeStyle == PaletteStyle.TonalSpot) GridItemSpan(
                                maxCurrentLineSpan
                            )
                            else GridItemSpan(1)
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .container(RoundedCornerShape(24.dp))
                                .padding(horizontal = 20.dp)
                        ) {
                            TitleItem(text = stringResource(R.string.primary))
                            ColorSelection(
                                color = primary,
                                onColorChange = {
                                    if (primary != it && settingsState.themeStyle == PaletteStyle.TonalSpot) {
                                        secondary = Color(it).calculateSecondaryColor()
                                        tertiary = Color(it).calculateTertiaryColor()
                                        surface = Color(it).calculateSurfaceColor()
                                    }
                                    primary = it
                                }
                            )
                        }
                    }
                    if (settingsState.themeStyle == PaletteStyle.TonalSpot) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .container(RoundedCornerShape(24.dp))
                                    .padding(horizontal = 20.dp)
                            ) {
                                TitleItem(text = stringResource(R.string.secondary))
                                ColorSelection(
                                    color = secondary,
                                    onColorChange = {
                                        secondary = it
                                    }
                                )
                            }
                        }
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .container(RoundedCornerShape(24.dp))
                                    .padding(horizontal = 20.dp)
                            ) {
                                TitleItem(text = stringResource(R.string.tertiary))
                                ColorSelection(
                                    color = tertiary,
                                    onColorChange = {
                                        tertiary = it
                                    }
                                )
                            }
                        }
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .container(RoundedCornerShape(24.dp))
                                    .padding(horizontal = 20.dp)
                            ) {
                                TitleItem(text = stringResource(R.string.surface))
                                ColorSelection(
                                    color = surface,
                                    onColorChange = {
                                        surface = it
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onColorChange(
                        ColorTuple(
                            Color(primary),
                            Color(secondary),
                            Color(tertiary),
                            Color(surface)
                        )
                    )
                    visible.value = false
                }
            ) {
                AutoSizeText(stringResource(R.string.save))
            }
        },
    )
}