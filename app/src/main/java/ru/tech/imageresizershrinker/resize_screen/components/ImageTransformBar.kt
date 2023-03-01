package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.rounded.Dataset
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.block

@Composable
fun ImageTransformBar(
    onEditExif: () -> Unit,
    onCrop: () -> Unit,
    onRotateLeft: () -> Unit,
    onFlip: () -> Unit,
    onRotateRight: () -> Unit,
) {
    Row(Modifier.block()) {
        Spacer(Modifier.width(4.dp))
        SmallFloatingActionButton(
            onClick = onEditExif,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Rounded.Dataset,
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.edit_exif))
                Spacer(Modifier.width(8.dp))
            }
        }

        Spacer(Modifier.width(4.dp))

        SmallFloatingActionButton(
            onClick = onCrop,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.Crop, null)
        }

        Spacer(
            Modifier
                .weight(1f)
                .padding(4.dp)
        )
        SmallFloatingActionButton(
            onClick = onRotateLeft,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.RotateLeft, null)
        }

        SmallFloatingActionButton(
            onClick = onFlip,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.Flip, null)
        }

        SmallFloatingActionButton(
            onClick = onRotateRight,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.RotateRight, null)
        }
    }
}

@Composable
fun ImageTransformBar(
    onRotateLeft: () -> Unit,
    onFlip: () -> Unit,
    onRotateRight: () -> Unit,
) {
    Row(Modifier.block()) {
        SmallFloatingActionButton(
            onClick = onRotateLeft,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.RotateLeft, null)
        }

        SmallFloatingActionButton(
            onClick = onFlip,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.Flip, null)
        }

        SmallFloatingActionButton(
            onClick = onRotateRight,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Icon(Icons.Default.RotateRight, null)
        }
    }
}