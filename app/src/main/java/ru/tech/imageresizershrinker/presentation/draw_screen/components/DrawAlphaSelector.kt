package ru.tech.imageresizershrinker.presentation.draw_screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.drawbox.domain.DrawController
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState
import kotlin.math.roundToInt

@Composable
fun DrawAlphaSelector(drawController: DrawController) {
    val settingsState = LocalSettingsState.current
    Column(
        Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .block()
            .animateContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.paint_alpha),
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        end = 16.dp,
                        start = 16.dp
                    )
                    .weight(1f)
            )
            Text(
                text = "${(drawController.paintOptions.alpha.toFloat() / 255 * 100).roundToInt()}",
                color = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.5f
                ),
                modifier = Modifier.padding(top = 16.dp, end = 16.dp),
                lineHeight = 18.sp
            )
        }
        Slider(
            modifier = Modifier
                .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 8.dp)
                .offset(y = (-2).dp)
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = CircleShape
                )
                .height(40.dp)
                .border(
                    width = settingsState.borderWidth,
                    color = MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer),
                    shape = CircleShape
                )
                .padding(horizontal = 10.dp),
            colors = SliderDefaults.colors(
                inactiveTrackColor =
                MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
            ),
            value = drawController.paintOptions.alpha.toFloat() / 255 * 100,
            valueRange = 1f..100f,
            onValueChange = {
                drawController.setAlpha(it.roundToInt())
            }
        )
    }
}