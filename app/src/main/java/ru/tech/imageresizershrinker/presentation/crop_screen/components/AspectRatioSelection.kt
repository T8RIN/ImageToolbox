package ru.tech.imageresizershrinker.presentation.crop_screen.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
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
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.AspectRatio.Numeric
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.fadingEdges
import kotlin.math.roundToInt
import ru.tech.imageresizershrinker.domain.model.AspectRatio as DomainAspectRatio

@Composable
fun AspectRatioSelection(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 2,
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
    onAspectRatioChange: (CropAspectRatio) -> Unit
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
                if (item.aspectRatio != AspectRatio.Original) {
                    val selected = selectedIndex == index
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
                            .clickable { onAspectRatioChange(aspectRatios[index]) }
                            .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 2.dp),
                        contentColor = Color.Transparent,
                        color = MaterialTheme.colorScheme.onSurface,
                        cropAspectRatio = item
                    )
                } else {
                    val selected = selectedIndex == index
                    Box(
                        modifier = Modifier
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
                            .clickable { onAspectRatioChange(aspectRatios[index]) }
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
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
    }
}

fun CropAspectRatio.toDomainAspect(): DomainAspectRatio =
    if (this.aspectRatio == AspectRatio.Original) {
        DomainAspectRatio.Original
    } else title.split(":").let {
        Numeric(
            widthProportion = it[0].toFloat().run { (this * 100.0f).roundToInt() / 100.0f },
            heightProportion = it[1].toFloat().run { (this * 100.0f).roundToInt() / 100.0f }
        )
    }

fun DomainAspectRatio.toCropAspectRatio(
    original: String
): CropAspectRatio = if (this is DomainAspectRatio.Original) {
    CropAspectRatio(
        title = original,
        shape = createRectShape(AspectRatio.Original),
        aspectRatio = AspectRatio.Original
    )
} else {
    val width = widthProportion.toString().trimTrailingZero()
    val height = heightProportion.toString().trimTrailingZero()
    CropAspectRatio(
        title = "$width:$height",
        shape = createRectShape(AspectRatio(value)),
        aspectRatio = AspectRatio(value)
    )
}

private fun String.trimTrailingZero(): String {
    val value = this
    return if (value.isNotEmpty()) {
        if (value.indexOf(".") < 0) {
            value
        } else {
            value.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
        }
    } else {
        value
    }
}