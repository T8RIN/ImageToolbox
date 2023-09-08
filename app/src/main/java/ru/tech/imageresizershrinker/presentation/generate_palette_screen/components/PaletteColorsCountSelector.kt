package ru.tech.imageresizershrinker.presentation.generate_palette_screen.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import kotlin.math.roundToInt

@Composable
fun PaletteColorsCountSelector(
    count: Int,
    onCountChange: (Int) -> Unit
) {
    val settingsState = LocalSettingsState.current

    var _count by remember(count) { mutableIntStateOf(count) }
    Column(
        modifier = Modifier
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
                text = "$_count",
                color = LocalContentColor.current.copy(alpha = 0.7f)
            )
        }
        Spacer(Modifier.weight(1f))
        Slider(
            modifier = Modifier
                .padding(horizontal = 3.dp, vertical = 3.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    CircleShape
                )
                .border(
                    settingsState.borderWidth,
                    MaterialTheme.colorScheme.outlineVariant(
                        onTopOf = MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = 0.4f
                        )
                    ),
                    CircleShape
                )
                .padding(horizontal = 12.dp),
            colors = SliderDefaults.colors(
                inactiveTrackColor =
                MaterialTheme.colorScheme.outlineVariant(
                    onTopOf = MaterialTheme.colorScheme.secondaryContainer.copy(
                        alpha = 0.4f
                    )
                )
            ),
            value = animateFloatAsState(_count.toFloat()).value,
            onValueChange = {
                _count = it.roundToInt()
            },
            onValueChangeFinished = {
                onCountChange(_count)
            },
            valueRange = 1f..128f,
            steps = 127
        )
    }
}