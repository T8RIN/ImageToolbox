package ru.tech.imageresizershrinker.feature.image_stitch.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coredomain.model.IntegerSize
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.coreui.widget.controls.OOMWarning
import ru.tech.imageresizershrinker.coreui.widget.modifier.container

@Composable
fun ImageScaleSelector(
    modifier: Modifier,
    value: Float,
    approximateImageSize: IntegerSize,
    onValueChange: (Float) -> Unit
) {
    val scaledSize by remember(approximateImageSize, value) {
        derivedStateOf {
            val s = approximateImageSize * value
            if (s.isZero()) null
            else s
        }
    }
    val showWarning by remember(scaledSize) {
        derivedStateOf {
            scaledSize!!.width * scaledSize!!.height * 4L >= 10_000 * 10_000 * 3L
        }
    }
    EnhancedSliderItem(
        modifier = modifier,
        value = value.roundToTwoDigits(),
        title = stringResource(R.string.output_image_scale),
        valueRange = 0.1f..1f,
        internalStateTransformation = {
            it.roundToTwoDigits()
        },
        onValueChange = {
            onValueChange(it.roundToTwoDigits())
        },
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        icon = Icons.Rounded.PhotoSizeSelectSmall,
        shape = RoundedCornerShape(24.dp)
    ) {
        AnimatedContent(
            targetState = scaledSize != null,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { notNull ->
            if (notNull) {
                Column {
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .container(RoundedCornerShape(20.dp))
                            .padding(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .container(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    autoShadowElevation = 0.2.dp
                                )
                                .padding(4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.width, ""))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = scaledSize!!.width.toString(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = LocalContentColor.current.copy(0.5f),
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .container(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    autoShadowElevation = 0.2.dp
                                )
                                .padding(4.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stringResource(R.string.height, ""))
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = scaledSize!!.height.toString(),
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                color = LocalContentColor.current.copy(0.5f),
                            )
                        }
                    }
                    OOMWarning(visible = showWarning)
                }
            } else {
                Text(
                    text = stringResource(R.string.loading),
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .container(RoundedCornerShape(20.dp))
                        .padding(6.dp),
                    color = LocalContentColor.current.copy(0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}