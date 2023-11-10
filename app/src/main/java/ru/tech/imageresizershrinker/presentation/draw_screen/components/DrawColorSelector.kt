package ru.tech.imageresizershrinker.presentation.draw_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.color_picker.ColorSelectionRow
import ru.tech.imageresizershrinker.presentation.root.widget.color_picker.ColorSelectionRowDefaults
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container

@Composable
fun DrawColorSelector(
    modifier: Modifier = Modifier
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
    drawColor: Color,
    onColorChange: (Color) -> Unit,
    titleText: String = stringResource(R.string.paint_color),
    defaultColors: List<Color> = ColorSelectionRowDefaults.colorList,
) {
    Column(
        modifier = modifier
            .container(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = titleText,
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp
            )
        }
        ColorSelectionRow(
            defaultColors = defaultColors,
            value = drawColor,
            contentPadding = PaddingValues(horizontal = 16.dp),
            onValueChange = onColorChange
        )
    }
}