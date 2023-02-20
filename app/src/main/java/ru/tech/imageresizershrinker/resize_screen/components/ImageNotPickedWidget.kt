package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.resize_screen.block

@Composable
fun ColumnScope.ImageNotPickedWidget(onPickImage: () -> Unit) {
    Column(
        Modifier.block(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        FilledIconButton(
            onClick = onPickImage,
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                Icons.TwoTone.Image,
                null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }
        Text(
            stringResource(R.string.pick_image),
            Modifier.padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}