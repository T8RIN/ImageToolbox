package ru.tech.imageresizershrinker.coreui.widget.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.shapes.CloverShape
import ru.tech.imageresizershrinker.coreui.widget.modifier.container

@Composable
fun ImageNotPickedWidget(
    modifier: Modifier = Modifier,
    onPickImage: () -> Unit,
    text: String = stringResource(R.string.pick_image),
) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = modifier.container(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Icon(
            Icons.TwoTone.Image,
            null,
            modifier = Modifier
                .size(100.dp)
                .container(
                    shape = CloverShape,
                    resultPadding = 0.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
                .clickable {
                    onPickImage()
                    haptics.performHapticFeedback(
                        HapticFeedbackType.LongPress
                    )
                }
                .padding(12.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text,
            Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}