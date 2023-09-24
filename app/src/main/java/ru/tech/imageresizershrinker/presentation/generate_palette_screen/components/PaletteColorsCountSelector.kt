package ru.tech.imageresizershrinker.presentation.generate_palette_screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedSlider
import kotlin.math.roundToInt

@Composable
fun PaletteColorsCountSelector(
    modifier: Modifier = Modifier,
    count: Int,
    onCountChange: (Int) -> Unit
) {
    var count1 by remember(count) { mutableIntStateOf(count) }
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .container(shape = RoundedCornerShape(24.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.max_colors_count),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$count1",
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )
        }
        Spacer(Modifier.weight(1f))
        EnhancedSlider(
            modifier = Modifier.padding(horizontal = 3.dp, vertical = 3.dp),
            value = animateFloatAsState(count1.toFloat()).value,
            onValueChange = {
                count1 = it.roundToInt()
            },
            onValueChangeFinished = {
                onCountChange(count1)
            },
            valueRange = 1f..128f,
            steps = 127
        )
    }
}