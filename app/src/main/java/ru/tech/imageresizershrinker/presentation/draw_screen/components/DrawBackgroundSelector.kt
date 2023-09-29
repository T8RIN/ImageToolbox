package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.inverse
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.transparencyChecker
import ru.tech.imageresizershrinker.presentation.root.widget.color_picker.AlphaColorSelection
import ru.tech.imageresizershrinker.presentation.root.widget.color_picker.ColorSelection
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun DrawBackgroundSelector(
    backgroundColor: Color,
    onColorChange: (Color) -> Unit,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.padding(16.dp)
) {
    val settingsState = LocalSettingsState.current

    var customColor by remember { mutableStateOf<Color?>(null) }
    val showColorPicker = remember { mutableStateOf(false) }

    if (backgroundColor !in defaultColorList) {
        customColor = backgroundColor
    }

    Column(
        modifier.container(shape = RoundedCornerShape(24.dp))
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.background_color),
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp
            )
        }
        Box {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.2.dp * 40 + 32.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Box(
                        Modifier
                            .size(
                                animateDpAsState(
                                    40.dp.times(
                                        if (customColor != null) 1.3f else 1f
                                    )
                                ).value
                            )
                            .border(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(
                                    onTopOf = customColor ?: MaterialTheme.colorScheme.primary
                                ),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .transparencyChecker()
                            .background(customColor ?: MaterialTheme.colorScheme.primary)
                            .autoElevatedBorder(
                                height = 0.dp,
                                shape = CircleShape,
                                autoElevation = 1.dp
                            )
                            .clickable {
                                showColorPicker.value = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Palette,
                            contentDescription = null,
                            tint = (customColor ?: MaterialTheme.colorScheme.primary).inverse(
                                fraction = {
                                    if (it) 0.8f
                                    else 0.5f
                                },
                                darkMode = (customColor
                                    ?: MaterialTheme.colorScheme.primary).luminance() < 0.3f
                            ),
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = (customColor ?: MaterialTheme.colorScheme.primary).copy(
                                        alpha = 1f
                                    ),
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                        )
                    }
                }
                items(defaultColorList) { color ->
                    Box(
                        Modifier
                            .size(
                                animateDpAsState(
                                    40.dp.times(
                                        if (backgroundColor == color && customColor == null) {
                                            1.3f
                                        } else 1f
                                    )
                                ).value
                            )
                            .border(
                                width = settingsState.borderWidth,
                                color = MaterialTheme.colorScheme.outlineVariant(
                                    onTopOf = color
                                ),
                                shape = CircleShape
                            )
                            .clip(CircleShape)
                            .background(color)
                            .clickable {
                                onColorChange(color)
                                customColor = null
                            }
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(6.dp)
                    .height(1.3.dp * 40 + 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to MaterialTheme.colorScheme.surfaceColorAtElevation(
                                1.dp
                            ),
                            1f to Color.Transparent
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(6.dp)
                    .height(1.3.dp * 40 + 16.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to Color.Transparent,
                            1f to MaterialTheme.colorScheme.surfaceColorAtElevation(
                                1.dp
                            )
                        )
                    )
            )
        }
    }

    SimpleSheet(
        sheetContent = {
            Box {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(36.dp)
                ) {
                    AlphaColorSelection(
                        color = customColor?.toArgb() ?: 0,
                        onColorChange = {
                            customColor = Color(it)
                            onColorChange(Color(it))
                        }
                    )
                }
            }
        },
        visible = showColorPicker,
        title = {
            TitleItem(
                text = stringResource(R.string.color),
                icon = Icons.Rounded.Draw
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showColorPicker.value = false
                }
            ) {
                AutoSizeText(stringResource(R.string.ok))
            }
        }
    )
}

private val defaultColorList = listOf(
    Color(0xFFFFFFFF),
    Color(0xFF768484),
    Color(0xFF333333),
    Color(0xFF000000),
) + listOf(
    Color(0xFFf8130d),
    Color(0xFFb8070d),
    Color(0xFF7a000b),
    Color(0xFF8a3a00),
    Color(0xFFff7900),
    Color(0xFFfcf721),
    Color(0xFFf8df09),
    Color(0xFFc0dc18),
    Color(0xFF88dd20),
    Color(0xFF07ddc3),
    Color(0xFF01a0a3),
    Color(0xFF59cbf0),
    Color(0xFF005FFF),
    Color(0xFFfa64e1),
    Color(0xFFfc50a6),
    Color(0xFFd7036a),
    Color(0xFFdb94fe),
    Color(0xFFb035f8),
    Color(0xFF7b2bec),
    Color(0xFF022b6d),
).reversed()