package ru.tech.imageresizershrinker.presentation.root.widget.color_picker

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.calculateSecondaryColor
import com.t8rin.dynamic.theme.calculateSurfaceColor
import com.t8rin.dynamic.theme.calculateTertiaryColor
import com.t8rin.dynamic.theme.getAppColorTuple
import com.t8rin.dynamic.theme.rememberColorScheme
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.icons.PaletteSwatch
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@ExperimentalMaterial3Api
@Composable
fun ColorTuplePicker(
    visible: MutableState<Boolean>,
    colorTuple: ColorTuple,
    borderWidth: Dp = LocalSettingsState.current.borderWidth,
    title: String = stringResource(R.string.color_scheme),
    onColorChange: (ColorTuple) -> Unit
) {
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

    val appColorTuple = getAppColorTuple(
        defaultColorTuple = LocalSettingsState.current.appColorTuple,
        dynamicColor = true,
        darkTheme = true
    )

    val scheme = rememberColorScheme(
        amoledMode = false,
        isDarkTheme = true,
        colorTuple = appColorTuple
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
        title = { TitleItem(text = title, icon = Icons.Outlined.Palette, modifier = Modifier) },
        endConfirmButtonPadding = 0.dp,
        sheetContent = {
            HorizontalDivider()
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
                                .block(RoundedCornerShape(24.dp))
                                .padding(16.dp)
                        ) {
                            Icon(Icons.Rounded.PaletteSwatch, null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.monet_colors))
                            Spacer(Modifier.width(8.dp))
                            Spacer(Modifier.weight(1f))
                            OutlinedButton(
                                onClick = {
                                    scheme.apply {
                                        primary = this.primary.toArgb()
                                        secondary = this.secondary.toArgb()
                                        tertiary = this.tertiary.toArgb()
                                        surface = this.surface.toArgb()
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                border = BorderStroke(
                                    borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                )
                            ) {
                                Icon(Icons.Rounded.ContentPaste, null)
                            }
                        }
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .block(RoundedCornerShape(24.dp))
                                .padding(horizontal = 20.dp)
                        ) {
                            TitleItem(text = stringResource(R.string.primary))
                            ColorSelection(
                                color = primary,
                                onColorChange = {
                                    if (primary != it) {
                                        secondary = Color(it).calculateSecondaryColor()
                                        tertiary = Color(it).calculateTertiaryColor()
                                        surface = Color(it).calculateSurfaceColor()
                                    }
                                    primary = it
                                }
                            )
                        }
                    }
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .block(RoundedCornerShape(24.dp))
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
                                .block(RoundedCornerShape(24.dp))
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
                                .block(RoundedCornerShape(24.dp))
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

                HorizontalDivider(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(100f)
                )
            }
        },
        confirmButton = {
            OutlinedButton(
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                border = BorderStroke(
                    borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.primary)
                ),
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