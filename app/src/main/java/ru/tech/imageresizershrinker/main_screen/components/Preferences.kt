package ru.tech.imageresizershrinker.main_screen.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.theme.Github
import ru.tech.imageresizershrinker.theme.PaletteSwatch
import ru.tech.imageresizershrinker.utils.APP_LINK


@Composable
fun SourceCodePreference(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
    color: Color = MaterialTheme.colorScheme.tertiaryContainer
) {
    val context = LocalContext.current
    PreferenceItem(
        onClick = {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(APP_LINK)
                )
            )
        },
        icon = Icons.Rounded.Github,
        title = stringResource(R.string.check_source_code),
        subtitle = stringResource(R.string.check_source_code_sub),
        color = color,
        modifier = modifier
    )
}

@Composable
fun SingleResizePreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.PhotoSizeSelectLarge,
        title = stringResource(R.string.single_resize),
        subtitle = stringResource(R.string.resize_single_image),
        color = color,
        modifier = modifier
    )
}

@Composable
fun BytesResizePreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.InsertDriveFile,
        title = stringResource(R.string.by_bytes_resize),
        subtitle = stringResource(R.string.by_bytes_resize_sub),
        color = color,
        modifier = modifier
    )
}


@Composable
fun BatchResizePreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.PhotoLibrary,
        title = stringResource(R.string.batch_resize),
        subtitle = stringResource(R.string.resize_batch_image),
        modifier = modifier,
        color = color
    )
}

@Composable
fun GeneratePalettePreference(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.PaletteSwatch,
        title = stringResource(R.string.generate_palette),
        subtitle = stringResource(R.string.palette_sub),
        modifier = modifier,
        color = color
    )
}

@Composable
fun CropPreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.Crop,
        title = stringResource(R.string.crop),
        subtitle = stringResource(R.string.crop_sub),
        color = color,
        modifier = modifier
    )
}

@Composable
fun PickColorPreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.Colorize,
        title = stringResource(R.string.pick_color),
        subtitle = stringResource(R.string.pick_color_sub),
        color = color,
        modifier = modifier
    )
}

@Composable
fun ComparePreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.Compare,
        title = stringResource(R.string.compare),
        subtitle = stringResource(R.string.compare_sub),
        color = color,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun PreferenceItem(
    onClick: () -> Unit,
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    endIcon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
) {
    val contentColor =
        if (color == MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)) contentColorFor(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        ) else contentColorFor(backgroundColor = color)

    ProvideTextStyle(value = LocalTextStyle.current.copy(textAlign = TextAlign.Start)) {
        Card(
            modifier = modifier,
            onClick = onClick,
            colors = CardDefaults.cardColors(
                containerColor = color,
                contentColor = contentColor
            )
        ) {
            Row(Modifier.padding(16.dp)) {
                icon?.let {
                    Icon(imageVector = icon, contentDescription = null)
                    Spacer(modifier = Modifier.width(16.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    AnimatedContent(
                        targetState = subtitle,
                        transitionSpec = { fadeIn() with fadeOut() }
                    ) { sub ->
                        sub?.let {
                            Column {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sub,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 14.sp,
                                    color = LocalContentColor.current.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
                AnimatedContent(
                    targetState = endIcon,
                    transitionSpec = { fadeIn() with fadeOut() }
                ) { icon ->
                    icon?.let {
                        Row {
                            Spacer(modifier = Modifier.width(16.dp))
                            Icon(imageVector = it, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}