package ru.tech.imageresizershrinker.crop_screen.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.CropAspectRatio
import com.smarttoolfactory.cropper.util.createRectShape
import com.smarttoolfactory.cropper.widget.AspectRatioSelectionCard
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.utils.modifier.block

@Composable
fun AspectRatioSelection(
    modifier: Modifier = Modifier,
    selectedIndex: Int = 2,
    horizontal: Boolean = true,
    onAspectRatioChange: (CropAspectRatio) -> Unit
) {
    val aspectRatios = aspectRatios()

    if (horizontal) {
        LazyRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(
                start = 4.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 4.dp + WindowInsets
                    .navigationBars
                    .asPaddingValues()
                    .calculateEndPadding(LocalLayoutDirection.current)
            )
        ) {
            itemsIndexed(aspectRatios) { index, item ->
                AspectRatioSelectionCard(
                    modifier = Modifier
                        .width(80.dp)
                        .border(
                            2.dp,
                            animateColorAsState(if (selectedIndex == index) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent).value,
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onAspectRatioChange(aspectRatios[index]) }
                        .block(),
                    contentColor = Color.Transparent,
                    color = MaterialTheme.colorScheme.onSurface,
                    cropAspectRatio = item
                )
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
            )
        ) {
            itemsIndexed(aspectRatios) { index, item ->
                AspectRatioSelectionCard(
                    modifier = Modifier
                        .width(80.dp)
                        .border(
                            2.dp,
                            animateColorAsState(if (selectedIndex == index) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent).value,
                            RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onAspectRatioChange(aspectRatios[index]) }
                        .block(),
                    contentColor = Color.Transparent,
                    color = MaterialTheme.colorScheme.onSurface,
                    cropAspectRatio = item
                )
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
            shape = createRectShape(AspectRatio.Unspecified),
            aspectRatio = AspectRatio.Unspecified
        ),
        CropAspectRatio(
            title = "9:16",
            shape = createRectShape(AspectRatio(9 / 16f)),
            aspectRatio = AspectRatio(9 / 16f)
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
            title = "16:9",
            shape = createRectShape(AspectRatio(16 / 9f)),
            aspectRatio = AspectRatio(16 / 9f)
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
        )
    )
}