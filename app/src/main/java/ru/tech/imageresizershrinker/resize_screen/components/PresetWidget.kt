package ru.tech.imageresizershrinker.resize_screen.components

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.LocalBorderWidth
import ru.tech.imageresizershrinker.main_screen.components.LocalEditPresets
import ru.tech.imageresizershrinker.main_screen.components.LocalPresetsProvider
import ru.tech.imageresizershrinker.main_screen.components.block
import ru.tech.imageresizershrinker.theme.CreateAlt
import ru.tech.imageresizershrinker.theme.mixedColor
import ru.tech.imageresizershrinker.theme.onMixedColor
import ru.tech.imageresizershrinker.utils.BitmapUtils.with
import ru.tech.imageresizershrinker.widget.AutoSizeText

@Composable
fun PresetWidget(
    selectedPreset: Int,
    bitmap: Bitmap?,
    bitmapInfo: BitmapInfo,
    onChangeBitmapInfo: (BitmapInfo) -> Unit
) {
    val editPresetsState = LocalEditPresets.current
    val data = LocalPresetsProvider.current

    var isRevealed by rememberSaveable { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        isRevealed = true
                    }
                )
            }
    ) {
        FilledTonalIconButton(
            onClick = { editPresetsState.value = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterStart)
        ) {
            Icon(Icons.Rounded.CreateAlt, null)
        }
        DraggableLayout(
            isRevealed = isRevealed,
            cardOffset = with(LocalDensity.current) { 80.dp.toPx() },
            onExpand = {
                isRevealed = true
            },
            onCollapse = {
                isRevealed = false
            }
        ) {
            Column(Modifier.block()) {
                Spacer(Modifier.size(8.dp))
                Text(
                    stringResource(R.string.presets),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))

                Box {
                    LazyRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        data.forEach {
                            item {
                                val selected = selectedPreset == it
                                OutlinedIconButton(
                                    shape = RoundedCornerShape(12.dp),
                                    onClick = {
                                        onChangeBitmapInfo(
                                            it.with(
                                                bitmap,
                                                bitmapInfo
                                            )
                                        )
                                    },
                                    border = BorderStroke(
                                        max(LocalBorderWidth.current, 1.dp), animateColorAsState(
                                            if (selected) MaterialTheme.colorScheme.outlineVariant
                                            else Color.Transparent
                                        ).value
                                    ),
                                    colors = IconButtonDefaults.outlinedIconButtonColors(
                                        containerColor = animateColorAsState(
                                            if (selected) MaterialTheme.colorScheme.mixedColor
                                            else MaterialTheme.colorScheme.secondaryContainer.copy(
                                                alpha = 0.3f
                                            )
                                        ).value,
                                        contentColor = animateColorAsState(
                                            if (selected) MaterialTheme.colorScheme.onMixedColor
                                            else MaterialTheme.colorScheme.onSurface
                                        ).value,
                                    )
                                ) {
                                    AutoSizeText(it.toString())
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(6.dp)
                            .height(52.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    0f to MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                    1f to Color.Transparent
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(6.dp)
                            .height(52.dp)
                            .matchParentSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    0f to Color.Transparent,
                                    1f to MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                                )
                            )
                    )
                }
            }
        }
    }
}