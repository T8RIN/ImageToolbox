package ru.tech.imageresizershrinker.presentation.draw_screen.components

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
import ru.tech.imageresizershrinker.presentation.root.icons.material.Highlighter
import ru.tech.imageresizershrinker.presentation.root.icons.material.Laser
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
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
            .container(shape = RoundedCornerShape(24.dp))
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
                    SegmentedButton(
                        onClick = { onDrawModeChange(item) },
                        border = SegmentedButtonBorder(max(settingsState.borderWidth, 1.dp)),
                        selected = drawMode == item,
                        icon = {},
                        colors = SegmentedButtonDefaults.colors(
                            activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                            inactiveContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                6.dp
                            ),
                            activeContainerColor = MaterialTheme.colorScheme.secondary,
                            activeContentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = DrawMode.entries.size
                        )
                    ) {
                        Icon(
                            imageVector = when (item) {
                                DrawMode.Highlighter -> Icons.Rounded.Highlighter
                                DrawMode.Neon -> Icons.Rounded.Laser
                                DrawMode.Pen -> Icons.Rounded.Brush
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
                            0f to MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
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
                            1f to MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                        )
                    )
            )
        }
    }
}