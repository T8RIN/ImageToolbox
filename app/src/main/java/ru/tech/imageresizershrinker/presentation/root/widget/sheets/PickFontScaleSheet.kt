package ru.tech.imageresizershrinker.presentation.root.widget.sheets

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.text.TitleItem
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun PickFontScaleSheet(
    visible: MutableState<Boolean>,
    onFontScaleChange: (Float) -> Unit
) {
    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    SimpleSheet(
        visible = visible,
        sheetContent = {
            val list = remember {
                List(19) { (0.6f + it / 20f).roundToTwoDigits() }
            }
            Box {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(64.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterVertically
                    ),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp
                    )
                ) {
                    item {
                        val selected = settingsState.fontScale == null
                        Box(
                            modifier = Modifier
                                .height(58.dp)
                                .container(
                                    shape = MaterialTheme.shapes.medium,
                                    color = if (selected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.secondaryContainer.copy(0.2f),
                                    borderColor = if (selected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                                    } else {
                                        MaterialTheme.colorScheme.onSecondaryContainer.copy(0.1f)
                                    },
                                    resultPadding = 0.dp
                                )
                                .clickable {
                                    onFontScaleChange(0f)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            AutoSizeText(
                                text = stringResource(id = R.string.system),
                                maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }
                    list.forEach { scale ->
                        item {
                            val selected = scale == settingsState.fontScale
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .container(
                                        shape = MaterialTheme.shapes.medium,
                                        color = if (selected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.secondaryContainer.copy(0.2f),
                                        borderColor = if (selected) {
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                                        } else {
                                            MaterialTheme.colorScheme.onSecondaryContainer.copy(0.1f)
                                        },
                                        resultPadding = 0.dp
                                    )
                                    .clickable {
                                        onFontScaleChange(scale)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = scale.toString(),
                                    fontSize = with(LocalDensity.current) {
                                        val textSize = 16 / fontScale * scale
                                        textSize.sp
                                    }
                                )
                            }
                        }
                    }
                    item(
                        span = {
                            GridItemSpan(maxCurrentLineSpan)
                        }
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = context.resources.configuration.fontScale > 1.2f,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                                        alpha = 0.7f
                                    ),
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier
                                    .padding(16.dp)
                                    .border(
                                        settingsState.borderWidth,
                                        MaterialTheme.colorScheme.onErrorContainer.copy(
                                            0.4f
                                        ),
                                        RoundedCornerShape(24.dp)
                                    ),
                                shape = RoundedCornerShape(24.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.using_large_fonts_warn),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 14.sp,
                                    color = LocalContentColor.current.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = { visible.value = false },
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                icon = Icons.Rounded.TextFields,
                text = stringResource(R.string.font_scale),
            )
        }
    )
}