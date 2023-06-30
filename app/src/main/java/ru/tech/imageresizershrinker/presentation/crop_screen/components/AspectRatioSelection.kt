package ru.tech.imageresizershrinker.presentation.crop_screen.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropAspectRatio
import com.smarttoolfactory.cropper.util.createRectShape
import com.smarttoolfactory.cropper.widget.AspectRatioSelectionCard
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun AspectRatioSelection(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 2,
    horizontal: Boolean = true,
    onAspectRatioChange: (CropAspectRatio) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val aspectRatios = aspectRatios()

    if (horizontal) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 16.dp + WindowInsets
                    .navigationBars
                    .asPaddingValues()
                    .calculateEndPadding(LocalLayoutDirection.current)
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(aspectRatios) { index, item ->
                if (item.aspectRatio != AspectRatio.Original) {
                    val selected = selectedIndex == index
                    AspectRatioSelectionCard(
                        modifier = Modifier
                            .width(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAspectRatioChange(aspectRatios[index]) }
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                                    .copy(alpha = animateFloatAsState(if (selected) 1f else 0.5f).value),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                animateDpAsState(
                                    if (!selected) {
                                        settingsState.borderWidth
                                    } else {
                                        settingsState.borderWidth.coerceAtLeast(1.dp) * 2
                                    }
                                ).value,
                                MaterialTheme.colorScheme.outlineVariant(
                                    animateFloatAsState(if (selected) 0.5f else 0.2f).value
                                ),
                                RoundedCornerShape(16.dp)
                            ),
                        contentColor = Color.Transparent,
                        color = MaterialTheme.colorScheme.onSurface,
                        cropAspectRatio = item
                    )
                } else {
                    val selected = selectedIndex == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAspectRatioChange(aspectRatios[index]) }
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                                    .copy(alpha = animateFloatAsState(if (selected) 1f else 0.5f).value),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                animateDpAsState(
                                    if (!selected) {
                                        settingsState.borderWidth
                                    } else {
                                        settingsState.borderWidth.coerceAtLeast(1.dp) * 2
                                    }
                                ).value,
                                MaterialTheme.colorScheme.outlineVariant(
                                    animateFloatAsState(if (selected) 0.5f else 0.2f).value
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Image, null)
                            Text(
                                text = item.title,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            contentPadding = PaddingValues(
                top = 20.dp,
                bottom = 20.dp + WindowInsets
                    .navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(aspectRatios) { index, item ->
                if (item.aspectRatio != AspectRatio.Original) {
                    val selected = selectedIndex == index
                    AspectRatioSelectionCard(
                        modifier = Modifier
                            .width(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAspectRatioChange(aspectRatios[index]) }
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                                    .copy(alpha = animateFloatAsState(if (selected) 1f else 0.5f).value),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                animateDpAsState(
                                    if (!selected) {
                                        settingsState.borderWidth
                                    } else {
                                        settingsState.borderWidth.coerceAtLeast(1.dp) * 2
                                    }
                                ).value,
                                MaterialTheme.colorScheme.outlineVariant(
                                    animateFloatAsState(if (selected) 0.5f else 0.2f).value
                                ),
                                RoundedCornerShape(16.dp)
                            ),
                        contentColor = Color.Transparent,
                        color = MaterialTheme.colorScheme.onSurface,
                        cropAspectRatio = item
                    )
                } else {
                    val selected = selectedIndex == index
                    Box(
                        modifier = Modifier
                            .width(90.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onAspectRatioChange(aspectRatios[index]) }
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant
                                    .copy(alpha = animateFloatAsState(if (selected) 1f else 0.5f).value),
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                animateDpAsState(
                                    if (!selected) {
                                        settingsState.borderWidth
                                    } else {
                                        settingsState.borderWidth.coerceAtLeast(1.dp) * 2
                                    }
                                ).value,
                                MaterialTheme.colorScheme.outlineVariant(
                                    animateFloatAsState(if (selected) 0.5f else 0.2f).value
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Icon(Icons.Outlined.Image, null)
                            AutoSizeText(text = item.title, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun aspectRatios(
    original: String = stringResource(R.string.original)
) = remember(original) {
    listOf(
        CropAspectRatio(
            title = original,
            shape = createRectShape(AspectRatio.Original),
            aspectRatio = AspectRatio.Original
        ),
        CropAspectRatio(
            title = "10:16",
            shape = createRectShape(AspectRatio(10 / 16f)),
            aspectRatio = AspectRatio(10 / 16f)
        ),
        CropAspectRatio(
            title = "9:16",
            shape = createRectShape(AspectRatio(9 / 16f)),
            aspectRatio = AspectRatio(9 / 16f)
        ),
        CropAspectRatio(
            title = "9:18.5",
            shape = createRectShape(AspectRatio(9f / 18.5f)),
            aspectRatio = AspectRatio(9f / 18.5f)
        ),
        CropAspectRatio(
            title = "9:21",
            shape = createRectShape(AspectRatio(9 / 21f)),
            aspectRatio = AspectRatio(9 / 21f)
        ),
        CropAspectRatio(
            title = "1:1.91",
            shape = createRectShape(AspectRatio(1 / 1.91f)),
            aspectRatio = AspectRatio(1f / 1.91f)
        ),
        CropAspectRatio(
            title = "2:3",
            shape = createRectShape(AspectRatio(2 / 3f)),
            aspectRatio = AspectRatio(2 / 3f)
        ),
        CropAspectRatio(
            title = "1:1",
            shape = createRectShape(AspectRatio(1 / 1f)),
            aspectRatio = AspectRatio(1 / 1f)
        ),
        CropAspectRatio(
            title = "1:2",
            shape = createRectShape(AspectRatio(1 / 2f)),
            aspectRatio = AspectRatio(1 / 2f)
        ),
        CropAspectRatio(
            title = "5:3",
            shape = createRectShape(AspectRatio(5 / 3f)),
            aspectRatio = AspectRatio(5 / 3f)
        ),
        CropAspectRatio(
            title = "4:3",
            shape = createRectShape(AspectRatio(4 / 3f)),
            aspectRatio = AspectRatio(4 / 3f)
        ),
        CropAspectRatio(
            title = "21:9",
            shape = createRectShape(AspectRatio(21 / 9f)),
            aspectRatio = AspectRatio(21 / 9f)
        ),
        CropAspectRatio(
            title = "18.5:9",
            shape = createRectShape(AspectRatio(18.5f / 9f)),
            aspectRatio = AspectRatio(18.5f / 9f)
        ),
        CropAspectRatio(
            title = "16:9",
            shape = createRectShape(AspectRatio(16 / 9f)),
            aspectRatio = AspectRatio(16 / 9f)
        ),
        CropAspectRatio(
            title = "16:10",
            shape = createRectShape(AspectRatio(16 / 10f)),
            aspectRatio = AspectRatio(16 / 10f)
        ),
        CropAspectRatio(
            title = "1.91:1",
            shape = createRectShape(AspectRatio(1.91f / 1f)),
            aspectRatio = AspectRatio(1.91f / 1f)
        ),
        CropAspectRatio(
            title = "3:2",
            shape = createRectShape(AspectRatio(3 / 2f)),
            aspectRatio = AspectRatio(3 / 2f)
        ),
        CropAspectRatio(
            title = "3:4",
            shape = createRectShape(AspectRatio(3 / 4f)),
            aspectRatio = AspectRatio(3 / 4f)
        ),
        CropAspectRatio(
            title = "3:5",
            shape = createRectShape(AspectRatio(3 / 5f)),
            aspectRatio = AspectRatio(3 / 5f)
        ),
        CropAspectRatio(
            title = "2:1",
            shape = createRectShape(AspectRatio(2 / 1f)),
            aspectRatio = AspectRatio(2 / 1f)
        ),
    )
}