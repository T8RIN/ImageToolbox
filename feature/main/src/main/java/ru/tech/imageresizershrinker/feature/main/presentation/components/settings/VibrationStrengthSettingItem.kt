package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.coreui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun VibrationStrengthSettingItem(
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp),
    shape: Shape = ContainerShapeDefaults.defaultShape
) {
    val settingsState = LocalSettingsState.current
    Box(
        modifier
            .container(
                shape = shape,
                color = MaterialTheme.colorScheme
                    .secondaryContainer
                    .copy(alpha = 0.2f)
            )
            .animateContentSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 4.dp,
                    top = 4.dp,
                    bottom = 4.dp,
                    end = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var sliderValue by remember(settingsState.hapticsStrength) {
                mutableFloatStateOf(settingsState.hapticsStrength.toFloat())
            }
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Power,
                        contentDescription = null,
                        modifier = Modifier.padding(
                            top = 16.dp,
                            start = 12.dp
                        )
                    )
                    Text(
                        text = stringResource(R.string.vibration_strength),
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
                            text = value.takeIf { it > 0 }?.toInt()?.toString()
                                ?: stringResource(R.string.disabled),
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
                        sliderValue = it
                        onValueChange(sliderValue.toInt())
                    },
                    valueRange = 0f..2f,
                    steps = 1
                )
            }
        }
    }
}