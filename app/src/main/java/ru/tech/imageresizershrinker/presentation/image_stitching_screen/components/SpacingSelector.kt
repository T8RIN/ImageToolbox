package ru.tech.imageresizershrinker.presentation.image_stitching_screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatLineSpacing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import kotlin.math.roundToInt

@Composable
fun SpacingSelector(
    modifier: Modifier = Modifier,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    val density = LocalDensity.current
    Column(
        modifier = modifier
            .container(shape = RoundedCornerShape(24.dp))
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.FormatLineSpacing,
                contentDescription = null,
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        start = 16.dp
                    )
            )
            Text(
                text = stringResource(R.string.spacing),
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        end = 16.dp,
                        start = 16.dp
                    )
                    .weight(1f)
            )
            Text(
                text = "$value",
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                ),
                modifier = Modifier.padding(top = 16.dp),
                lineHeight = 18.sp
            )
            Text(
                maxLines = 1,
                text = "Px",
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                ),
                modifier = Modifier.padding(
                    start = 4.dp,
                    top = 16.dp,
                    end = 16.dp
                )
            )
        }
        EnhancedSlider(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 8.dp
                )
                .offset(y = (-2).dp),
            value = animateFloatAsState(targetValue = value.toFloat()).value,
            valueRange = 0f..128f,
            onValueChange = {
                onValueChange(it.roundToInt())
            }
        )
    }
}