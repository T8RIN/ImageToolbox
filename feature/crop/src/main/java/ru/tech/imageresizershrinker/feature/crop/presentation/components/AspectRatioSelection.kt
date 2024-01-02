package ru.tech.imageresizershrinker.feature.crop.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropAspectRatio
import com.smarttoolfactory.cropper.util.createRectShape
import com.smarttoolfactory.cropper.widget.AspectRatioSelectionCard
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.utils.trimTrailingZero
import ru.tech.imageresizershrinker.coredomain.model.DomainAspectRatio
import ru.tech.imageresizershrinker.coreui.theme.outlineVariant
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.modifier.fadingEdges

@Composable
fun AspectRatioSelection(
    modifier: Modifier = Modifier,
    selectedAspectRatio: DomainAspectRatio = DomainAspectRatio.Free,
    unselectedCardColor: Color = MaterialTheme.colorScheme.surfaceContainerLowest,
    contentPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        top = 4.dp,
        bottom = 4.dp,
        end = 16.dp + WindowInsets
            .navigationBars
            .asPaddingValues()
            .calculateEndPadding(LocalLayoutDirection.current)
    ),
    enableFadingEdges: Boolean = false,
    onAspectRatioChange: (DomainAspectRatio, AspectRatio) -> Unit
) {
    val aspectRatios = aspectRatios()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.aspect_ratio),
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            fontWeight = FontWeight.Medium
        )
        val listState = rememberLazyListState()
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            contentPadding = contentPadding,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fadingEdges(
                scrollableState = listState,
                enabled = enableFadingEdges
            )
        ) {
            itemsIndexed(aspectRatios) { index, item ->
                val selected = item == selectedAspectRatio
                val cropAspectRatio = item.toCropAspectRatio(
                    original = stringResource(R.string.original),
                    free = stringResource(R.string.free)
                )
                if (item != DomainAspectRatio.Original && item != DomainAspectRatio.Free) {
                    AspectRatioSelectionCard(
                        modifier = Modifier
                            .width(90.dp)
                            .container(
                                resultPadding = 0.dp,
                                color = animateColorAsState(
                                    targetValue = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else unselectedCardColor,
                                ).value,
                                borderColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    0.7f
                                )
                                else MaterialTheme.colorScheme.outlineVariant()
                            )
                            .clickable {
                                onAspectRatioChange(
                                    aspectRatios[index],
                                    cropAspectRatio.aspectRatio
                                )
                            }
                            .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 2.dp),
                        contentColor = Color.Transparent,
                        color = MaterialTheme.colorScheme.onSurface,
                        cropAspectRatio = cropAspectRatio
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .height(106.dp)
                            .container(
                                resultPadding = 0.dp,
                                color = animateColorAsState(
                                    targetValue = if (selected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else unselectedCardColor,
                                ).value,
                                borderColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    0.7f
                                )
                                else MaterialTheme.colorScheme.outlineVariant()
                            )
                            .clickable {
                                onAspectRatioChange(
                                    aspectRatios[index],
                                    cropAspectRatio.aspectRatio
                                )
                            }
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (item is DomainAspectRatio.Original) {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.CropFree,
                                    contentDescription = null
                                )
                            }
                            Text(
                                text = cropAspectRatio.title,
                                fontSize = 14.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

fun DomainAspectRatio.toCropAspectRatio(
    original: String,
    free: String
): CropAspectRatio = when (this) {
    is DomainAspectRatio.Original -> {
        CropAspectRatio(
            title = original,
            shape = createRectShape(AspectRatio.Original),
            aspectRatio = AspectRatio.Original
        )
    }

    is DomainAspectRatio.Free -> {
        CropAspectRatio(
            title = free,
            shape = createRectShape(AspectRatio.Original),
            aspectRatio = AspectRatio.Original
        )
    }

    else -> {
        val width = widthProportion.toString().trimTrailingZero()
        val height = heightProportion.toString().trimTrailingZero()
        CropAspectRatio(
            title = "$width:$height",
            shape = createRectShape(AspectRatio(value)),
            aspectRatio = AspectRatio(value)
        )
    }
}