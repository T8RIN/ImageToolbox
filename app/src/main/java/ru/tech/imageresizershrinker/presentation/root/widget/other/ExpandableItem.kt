package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container

@Composable
fun ExpandableItem(
    modifier: Modifier = Modifier,
    visibleContent: @Composable RowScope.(Boolean) -> Unit,
    expandableContent: @Composable ColumnScope.(Boolean) -> Unit,
    initialState: Boolean = false,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
) {
    Column(
        Modifier
            .animateContentSize()
            .then(modifier)
            .container(
                color = color,
                resultPadding = 0.dp,
                shape = shape
            )
    ) {
        var expanded by rememberSaveable { mutableStateOf(initialState) }
        val rotation by animateFloatAsState(if (expanded) 180f else 0f)
        Row(
            modifier = Modifier
                .clip(shape)
                .clickable { expanded = !expanded }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(Modifier.weight(1f)) {
                visibleContent(expanded)
            }
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
        AnimatedVisibility(expanded) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                expandableContent(expanded)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}