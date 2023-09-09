package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.presentation.root.icons.material.Transparency
import ru.tech.imageresizershrinker.presentation.root.theme.mixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.onMixedContainer
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun ImageTransformBar(
    onEditExif: () -> Unit = {},
    imageFormat: ImageFormat? = null,
    onRotateLeft: () -> Unit,
    onFlip: () -> Unit,
    onRotateRight: () -> Unit,
) {
    val settingsState = LocalSettingsState.current
    val colors = IconButtonDefaults.filledTonalIconButtonColors()
    val border = BorderStroke(
        settingsState.borderWidth,
        MaterialTheme.colorScheme.outlineVariant()
    )

    Column(
        modifier = Modifier
            .container(shape = RoundedCornerShape(animateDpAsState(targetValue = if (imageFormat?.canWriteExif == true) 48.dp else 24.dp).value))
            .animateContentSize()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(
                visible = imageFormat != null,
                modifier = Modifier.weight(1f, false)
            ) {
                Row {
                    Spacer(Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                            .border(border, CircleShape)
                            .clip(CircleShape)
                            .clickable(onClick = onEditExif),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Rounded.Fingerprint,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.edit_exif),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                    Spacer(
                        Modifier
                            .weight(1f)
                            .padding(4.dp)
                    )
                }
            }

            OutlinedIconButton(
                onClick = onRotateLeft,
                colors = colors,
                border = border
            ) {
                Icon(Icons.Default.RotateLeft, null)
            }

            OutlinedIconButton(
                onClick = onFlip,
                colors = colors,
                border = border
            ) {
                Icon(Icons.Default.Flip, null)
            }

            OutlinedIconButton(
                onClick = onRotateRight,
                colors = colors,
                border = border
            ) {
                Icon(Icons.Default.RotateRight, null)
            }
        }
        FormatExifWarning(imageFormat)
    }
}

@Composable
fun ImageExtraTransformBar(
    onCrop: () -> Unit,
    onFilter: () -> Unit,
    onDraw: () -> Unit,
    onEraseBackground: () -> Unit
) {
    val settingsState = LocalSettingsState.current
    val colors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.mixedContainer,
        contentColor = MaterialTheme.colorScheme.onMixedContainer
    )
    val border = BorderStroke(
        settingsState.borderWidth,
        MaterialTheme.colorScheme.outlineVariant()
    )

    Row(Modifier.container(shape = CircleShape)) {
        OutlinedIconButton(
            onClick = onCrop,
            colors = colors,
            border = border
        ) {
            Icon(Icons.Rounded.Crop, null)
        }

        OutlinedIconButton(
            onClick = onFilter,
            colors = colors,
            border = border
        ) {
            Icon(Icons.Rounded.PhotoFilter, null)
        }

        OutlinedIconButton(
            onClick = onDraw,
            colors = colors,
            border = border
        ) {
            Icon(Icons.Rounded.Draw, null)
        }

        OutlinedIconButton(
            onClick = onEraseBackground,
            colors = colors,
            border = border
        ) {
            Icon(Icons.Filled.Transparency, null)
        }
    }
}