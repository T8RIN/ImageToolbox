package ru.tech.imageresizershrinker.main_screen.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhotoSizeSelectLarge
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.Screen
import ru.tech.imageresizershrinker.theme.Github
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController<Screen>) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact
    Column(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        LargeTopAppBar(
            title = {
                var scaleState by remember { mutableStateOf(1f) }
                val scale by animateFloatAsState(scaleState)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.app_name))
                    Spacer(Modifier.width(12.dp))
                    Box(
                        Modifier
                            .scale(scale)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onPress = {
                                        scaleState = 1.3f
                                        delay(200)
                                        tryAwaitRelease()
                                        scaleState = 0.8f
                                        delay(200)
                                        scaleState = 1f
                                    }
                                )
                            }
                    ) {
                        Text(
                            text = "✨",
                            style = LocalTextStyle.current.copy(
                                shadow = Shadow(
                                    color = MaterialTheme.colorScheme.scrim,
                                    offset = Offset(0f, 0f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                    3.dp
                )
            ),
            modifier = Modifier.shadow(6.dp),
            scrollBehavior = scrollBehavior,
        )

        val footer: @Composable ColumnScope.() -> Unit = {
            Spacer(modifier = Modifier.weight(1f))
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.version) + " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isGrid) {
                Spacer(modifier = Modifier.height(16.dp))
                SingleResizePreference(onClick = { navController.navigate(Screen.SingleResize) })
                Spacer(modifier = Modifier.height(16.dp))
                CropPreference(onClick = { navController.navigate(Screen.Crop) })
                Spacer(modifier = Modifier.height(16.dp))
                PickColorPreference(onClick = { navController.navigate(Screen.PickColor) })
                Spacer(modifier = Modifier.height(32.dp))
                SourceCodePreference()
                Spacer(modifier = Modifier.height(16.dp))
                footer()
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    SingleResizePreference(
                        onClick = { navController.navigate(Screen.SingleResize) },
                        modifier = Modifier
                            .widthIn(max = 350.dp)
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    CropPreference(
                        onClick = { navController.navigate(Screen.Crop) },
                        modifier = Modifier
                            .widthIn(max = 350.dp)
                            .weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PickColorPreference(
                        onClick = { navController.navigate(Screen.PickColor) },
                        modifier = Modifier
                            .widthIn(max = 350.dp)
                            .weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    SourceCodePreference(
                        modifier = Modifier
                            .widthIn(max = 350.dp)
                            .weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                footer()
            }
        }
    }
}

@Composable
fun SourceCodePreference(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
) {
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
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = modifier
    )
}

@Composable
fun SingleResizePreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
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
fun CropPreference(
    onClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
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
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
) {
    PreferenceItem(
        onClick = onClick,
        icon = Icons.Rounded.Palette,
        title = stringResource(R.string.pick_color),
        subtitle = stringResource(R.string.pick_color_sub),
        color = color,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceItem(
    onClick: () -> Unit,
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
) {
    Card(
        modifier = modifier,
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