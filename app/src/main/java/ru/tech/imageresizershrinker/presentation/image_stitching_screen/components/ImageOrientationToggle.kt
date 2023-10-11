package ru.tech.imageresizershrinker.presentation.image_stitching_screen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ScreenLockLandscape
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material3.Icon
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container

@Composable
fun ImageOrientationToggle(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .container(shape = RoundedCornerShape(24.dp))
    ) {
        ToggleGroupButton(
            modifier = Modifier.padding(start = 3.dp, end = 2.dp),
            enabled = true,
            title = {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(id = R.string.image_orientation))
                    Spacer(modifier = Modifier.width(8.dp))
                    AnimatedContent(selected) { isHorizontal ->
                        Icon(
                            imageVector = if (isHorizontal) {
                                Icons.Rounded.ScreenLockLandscape
                            } else Icons.Rounded.ScreenLockPortrait,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            },
            items = listOf(stringResource(R.string.horizontal), stringResource(R.string.vertical)),
            selectedIndex = if (selected) 0 else 1,
            indexChanged = { onCheckedChange(!selected) }
        )
    }
}