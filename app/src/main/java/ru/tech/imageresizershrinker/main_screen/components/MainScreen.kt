package ru.tech.imageresizershrinker.main_screen.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoSizeSelectLarge
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.Screen
import ru.tech.imageresizershrinker.theme.Github

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController<Screen>) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LargeTopAppBar(
            title = { Text(stringResource(R.string.app_name) + " ✨") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    3.dp
                )
            ),
            modifier = Modifier.shadow(6.dp),
            scrollBehavior = scrollBehavior,
        )
        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            SingleResizePreference(onClick = { navController.navigate(Screen.SingleResize) })
            Spacer(modifier = Modifier.height(16.dp))
            CropPreference(onClick = { navController.navigate(Screen.Crop) })
            Spacer(modifier = Modifier.height(16.dp))
            PickColorPreference(onClick = { navController.navigate(Screen.PickColor) })
            Spacer(modifier = Modifier.height(32.dp))
            SourceCodePreference()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SourceCodePreference() {
    val context = LocalContext.current
    PreferenceItem(
        onClick = {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/T8RIN/ImageResizer")
                )
            )
        },
        icon = Icons.Rounded.Github,
        title = stringResource(R.string.check_source_code),
        subtitle = stringResource(R.string.check_source_code_sub),
        color = MaterialTheme.colorScheme.tertiaryContainer
    )
}

@Composable
fun SingleResizePreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.PhotoSizeSelectLarge,
        title = stringResource(R.string.single_resize),
        subtitle = stringResource(R.string.resize_single_image),
        color = color
    )
}

@Composable
fun CropPreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.Crop,
        title = stringResource(R.string.crop),
        subtitle = stringResource(R.string.crop_sub),
        color = color
    )
}

@Composable
fun PickColorPreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.Palette,
        title = stringResource(R.string.pick_color),
        subtitle = stringResource(R.string.pick_color_sub),
        color = color
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceItem(
    onClick: () -> Unit,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Row(Modifier.padding(16.dp)) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 14.sp,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }
        }
    }
}