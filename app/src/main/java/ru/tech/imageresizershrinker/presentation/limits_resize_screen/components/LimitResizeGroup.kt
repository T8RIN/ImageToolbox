package ru.tech.imageresizershrinker.presentation.limits_resize_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.widget.buttons.ToggleGroupButton

@Composable
fun LimitsResizeGroup(
    enabled: Boolean,
    resizeType: ResizeType,
    onResizeChange: (ResizeType) -> Unit
) {
    ToggleGroupButton(
        modifier = Modifier
            .block(shape = RoundedCornerShape(24.dp))
            .padding(start = 3.dp, end = 2.dp),
        enabled = enabled,
        title = {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    stringResource(R.string.fallback_option),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        },
        items = listOf(
            stringResource(R.string.skip),
            stringResource(R.string.copy),
            stringResource(R.string.force)
        ),
        selectedIndex = when (resizeType) {
            ResizeType.Limits.Skip -> 0
            ResizeType.Limits.Copy -> 1
            ResizeType.Limits.Force -> 2
            else -> -1
        },
        indexChanged = {
            onResizeChange(
                when (it) {
                    0 -> ResizeType.Limits.Skip
                    1 -> ResizeType.Limits.Copy
                    else -> ResizeType.Limits.Force
                }
            )
        }
    )
}