package ru.tech.imageresizershrinker.presentation.draw_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonBorder
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.draw.DrawMode
import ru.tech.imageresizershrinker.presentation.root.icons.material.Cube
import ru.tech.imageresizershrinker.presentation.root.icons.material.Highlighter
import ru.tech.imageresizershrinker.presentation.root.icons.material.Laser
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.controls.resize_group.components.BlurRadiusSelector
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawModeSelector(
    modifier: Modifier,
    drawMode: DrawMode,
    onDrawModeChange: (DrawMode) -> Unit
) {
    val settingsState = LocalSettingsState.current
    Column(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            )
            .padding(start = 3.dp, end = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.draw_mode),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
        Box {
            SingleChoiceSegmentedButtonRow(
                space = max(settingsState.borderWidth, 1.dp),
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 6.dp, end = 6.dp, bottom = 8.dp, top = 8.dp)
            ) {
                DrawMode.entries.forEachIndexed { index, item ->
                    val selected by remember(drawMode, item) {
                        derivedStateOf {
                            drawMode::class.isInstance(item)
                        }
                    }
                    val shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = DrawMode.entries.size
                    )
                    SegmentedButton(
                        onClick = { onDrawModeChange(item) },
                        selected = selected,
                        icon = {},
                        border = SegmentedButtonBorder(settingsState.borderWidth),
                        colors = SegmentedButtonDefaults.colors(
                            activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                            inactiveContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                6.dp
                            )
                        ),
                        modifier = Modifier.materialShadow(
                            shape = shape,
                            elevation = animateDpAsState(
                                if (settingsState.borderWidth >= 0.dp || !settingsState.allowShowingShadowsInsteadOfBorders) 0.dp
                                else if (selected) 2.dp
                                else 1.dp
                            ).value
                        ),
                        shape = shape
                    ) {
                        Icon(
                            imageVector = when (item) {
                                is DrawMode.Highlighter -> Icons.Rounded.Highlighter
                                is DrawMode.Neon -> Icons.Rounded.Laser
                                is DrawMode.Pen -> Icons.Rounded.Brush
                                is DrawMode.PathEffect.PrivacyBlur -> Icons.Rounded.BlurCircular
                                is DrawMode.PathEffect.Pixelation -> Icons.Rounded.Cube
                            },
                            contentDescription = null
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(8.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to MaterialTheme.colorScheme.surfaceContainer,
                            1f to Color.Transparent
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(8.dp)
                    .height(50.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            0f to Color.Transparent,
                            1f to MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
            )
        }
        AnimatedVisibility(
            visible = drawMode is DrawMode.PathEffect.PrivacyBlur,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BlurRadiusSelector(
                modifier = Modifier.padding(8.dp),
                value = (drawMode as? DrawMode.PathEffect.PrivacyBlur)?.blurRadius ?: 0,
                valueRange = 5f..50f,
                onValueChange = {
                    onDrawModeChange(DrawMode.PathEffect.PrivacyBlur(it))
                }
            )
        }
        AnimatedVisibility(
            visible = drawMode is DrawMode.PathEffect.Pixelation,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            PixelSizeSelector(
                modifier = Modifier.padding(8.dp),
                value = (drawMode as? DrawMode.PathEffect.Pixelation)?.pixelSize ?: 0f,
                onValueChange = {
                    onDrawModeChange(DrawMode.PathEffect.Pixelation(it))
                }
            )
        }
    }
}