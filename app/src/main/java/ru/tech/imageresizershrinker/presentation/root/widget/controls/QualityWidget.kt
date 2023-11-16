package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.presentation.filters_screen.components.ValueDialog
import ru.tech.imageresizershrinker.presentation.filters_screen.components.ValueText
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import kotlin.math.roundToInt

@Composable
fun QualityWidget(
    imageFormat: ImageFormat,
    enabled: Boolean,
    quality: Float,
    onQualityChange: (Float) -> Unit
) {
    val visible = imageFormat.canChangeCompressionValue

    val isQuality = imageFormat.compressionType is ImageFormat.Companion.CompressionType.Quality
    val isEffort = imageFormat.compressionType is ImageFormat.Companion.CompressionType.Effort

    val compressingLiteral = if (isQuality) "%" else ""

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ProvideTextStyle(
            value = TextStyle(
                color = if (!enabled) {
                    MaterialTheme.colorScheme.onSurface
                        .copy(alpha = 0.38f)
                        .compositeOver(MaterialTheme.colorScheme.surface)
                } else Color.Unspecified
            )
        ) {
            Column(
                modifier = Modifier
                    .container(shape = RoundedCornerShape(24.dp)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AnimatedContent(isEffort, modifier = Modifier.weight(1f)) { effort ->
                        Text(
                            text = if (!effort) stringResource(R.string.quality) else stringResource(
                                R.string.effort
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    AnimatedContent(compressingLiteral) { literal ->
                        var showValueDialog by remember { mutableStateOf(false) }
                        ValueText(
                            modifier = Modifier,
                            value = quality.roundToInt().coerceIn(imageFormat.compressionRange),
                            valueSuffix = literal,
                            onClick = {
                                showValueDialog = true
                            }
                        )
                        ValueDialog(
                            roundTo = 0,
                            valueState = quality.toString(),
                            onValueUpdate = {
                                onQualityChange(
                                    it.toInt().coerceIn(imageFormat.compressionRange).toFloat()
                                )
                            },
                            valueRange = imageFormat.compressionRange.let { it.first.toFloat()..it.last.toFloat() },
                            expanded = visible && showValueDialog,
                            onDismiss = { showValueDialog = false },
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                EnhancedSlider(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                    enabled = enabled,
                    value = animateFloatAsState(quality).value,
                    onValueChange = {
                        onQualityChange(it.toInt().coerceIn(imageFormat.compressionRange).toFloat())
                    },
                    valueRange = imageFormat.compressionRange.let { it.first.toFloat()..it.last.toFloat() },
                    steps = imageFormat.compressionRange.let { it.last - it.first }
                )
                AnimatedVisibility(isEffort) {
                    Text(
                        text = stringResource(
                            R.string.effort_sub,
                            imageFormat.compressionRange.first,
                            imageFormat.compressionRange.last
                        ),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 12.sp,
                        color = LocalContentColor.current.copy(0.5f),
                        modifier = Modifier
                            .padding(4.dp)
                            .container(RoundedCornerShape(20.dp))
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}