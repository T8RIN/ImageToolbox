package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.utils.trimTrailingZero
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun FontScaleSettingItem(
    onValueChange: (Float) -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    Column(
        modifier
            .container(
                shape = shape,
                color = MaterialTheme
                    .colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f)
            )
            .animateContentSize()
    ) {
        val derivedValue by remember(settingsState) {
            derivedStateOf {
                settingsState.fontScale ?: 0f
            }
        }
        var sliderValue by remember(derivedValue) {
            mutableFloatStateOf(derivedValue)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.TextFields,
                contentDescription = null,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 12.dp
                )
            )
            Text(
                text = stringResource(R.string.font_scale),
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        end = 16.dp,
                        start = 16.dp
                    )
                    .weight(1f),
                fontWeight = FontWeight.Medium
            )
            AnimatedContent(
                targetState = sliderValue,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { value ->
                Text(
                    text = value.takeIf { it > 0 }?.toString()?.trimTrailingZero()
                        ?: stringResource(R.string.defaultt),
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    ),
                    modifier = Modifier.padding(top = 16.dp, end = 16.dp),
                    lineHeight = 18.sp
                )
            }
        }
        EnhancedSlider(
            modifier = Modifier
                .padding(
                    top = 14.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                ),
            value = sliderValue,
            onValueChange = {
                sliderValue = if (it == 0.45f) 0f
                else it.roundToTwoDigits()
            },
            onValueChangeFinished = {
                onValueChange(sliderValue)
            },
            valueRange = 0.45f..1.5f,
            steps = 20
        )
    }
}