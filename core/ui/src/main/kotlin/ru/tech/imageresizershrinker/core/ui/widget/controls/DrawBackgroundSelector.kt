package ru.tech.imageresizershrinker.core.ui.widget.controls

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRow
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRowDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun DrawBackgroundSelector(
    value: Color,
    onColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier
        .padding(16.dp)
        .container(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainer
        )
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                fontWeight = FontWeight.Medium,
                text = stringResource(R.string.background_color),
                modifier = Modifier.padding(top = 16.dp),
                fontSize = 18.sp
            )
        }
        ColorSelectionRow(
            defaultColors = defaultColorList,
            allowAlpha = true,
            contentPadding = PaddingValues(16.dp),
            value = value,
            onValueChange = onColorChange
        )
    }
}

private val defaultColorList by lazy {
    listOf(
        Color(0xFFFFFFFF),
        Color(0xFF768484),
        Color(0xFF333333),
        Color(0xFF000000),
    ).plus(
        ColorSelectionRowDefaults.colorList.reversed().drop(4)
    )
}