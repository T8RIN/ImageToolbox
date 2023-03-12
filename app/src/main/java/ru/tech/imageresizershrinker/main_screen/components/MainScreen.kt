package ru.tech.imageresizershrinker.main_screen.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popUpTo
import kotlinx.coroutines.delay
import nl.dionsegijn.konfetti.core.*
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.resize_screen.components.blend
import ru.tech.imageresizershrinker.theme.CreateAlt
import ru.tech.imageresizershrinker.theme.Github
import ru.tech.imageresizershrinker.theme.Sparkles
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.utils.toUiPath
import java.lang.Integer.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController<Screen>,
    currentFolderUri: Uri?,
    onGetNewFolder: (Uri?) -> Unit,
    showConfetti: () -> Unit
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact
    val colorScheme = MaterialTheme.colorScheme
    val themeState = LocalDynamicThemeState.current

    var showSelectFolderDialog by rememberSaveable { mutableStateOf(false) }

    val colors = remember(colorScheme) {
        colorScheme.run {
            listOf(
                primary,
                secondary,
                tertiary,
                primaryContainer,
                secondaryContainer,
                tertiaryContainer,
                Color.Red.blend(primary, 0.4f),
                Color.Green.blend(primary, 0.4f),
                Color.Yellow.blend(primary, 0.4f),
                Color.Magenta.blend(primary, 0.4f),
                Color.Cyan.blend(primary, 0.4f),
                Color.Red.blend(Color.Yellow, 1f).blend(primary),
            )
        }
    }

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
                                        showConfetti()
                                        themeState.updateColor(colors.random())
                                        scaleState = 0.8f
                                        delay(200)
                                        scaleState = 1f
                                    }
                                )
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Sparkles,
                            contentDescription = null,
                            modifier = Modifier
                                .size(
                                    with(LocalDensity.current) {
                                        LocalTextStyle.current.fontSize.toDp()
                                    }
                                )
                                .offset(1.dp, 1.dp),
                            tint = Color(0, 0, 0, 40)
                        )
                        Icon(
                            imageVector = Icons.Rounded.Sparkles,
                            contentDescription = null,
                            modifier = Modifier.size(
                                with(LocalDensity.current) {
                                    LocalTextStyle.current.fontSize.toDp()
                                }
                            ),
                            tint = Color.Unspecified
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme
                    .colorScheme
                    .surfaceColorAtElevation(3.dp)
            ),
            actions = {
                IconButton(onClick = { showSelectFolderDialog = true }) {
                    Icon(Icons.Rounded.Folder, null)
                }
            },
            modifier = Modifier
                .shadow(6.dp)
                .zIndex(6f),
            scrollBehavior = scrollBehavior,
        )

        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .navBarsPaddingOnlyIfTheyAtTheEnd()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isGrid) {
                Spacer(modifier = Modifier.height(8.dp))
                SingleResizePreference(
                    onClick = {
                        navController.popUpTo { it == Screen.Main }
                        navController.navigate(Screen.SingleResize)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                BatchResizePreference(
                    onClick = {
                        navController.popUpTo { it == Screen.Main }
                        navController.navigate(Screen.BatchResize)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                BytesResizePreference(
                    onClick = {
                        navController.popUpTo { it == Screen.Main }
                        navController.navigate(Screen.ResizeByBytes)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                CropPreference(
                    onClick = {
                        navController.popUpTo { it == Screen.Main }
                        navController.navigate(Screen.Crop)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                PickColorPreference(
                    onClick = {
                        navController.popUpTo { it == Screen.Main }
                        navController.navigate(Screen.PickColorFromImage)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                GeneratePalettePreference(
                    onClick = {
                        navController.popUpTo { it == Screen.Main }
                        navController.navigate(Screen.GeneratePalette)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ComparePreference(
                    onClick = {
                        navController.popUpTo { it == Screen.Main }
                        navController.navigate(Screen.Compare)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    var heightOne by remember { mutableStateOf(0) }
                    var heightTwo by remember { mutableStateOf(0) }
                    SingleResizePreference(
                        onClick = {
                            navController.popUpTo { it == Screen.Main }
                            navController.navigate(Screen.SingleResize)
                        },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightOne = it.height
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BatchResizePreference(
                        onClick = {
                            navController.popUpTo { it == Screen.Main }
                            navController.navigate(Screen.BatchResize)
                        },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightTwo = it.height
                            }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    var heightOne by remember { mutableStateOf(0) }
                    var heightTwo by remember { mutableStateOf(0) }
                    BytesResizePreference(
                        onClick = {
                            navController.popUpTo { it == Screen.Main }
                            navController.navigate(Screen.ResizeByBytes)
                        },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightOne = it.height
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CropPreference(
                        onClick = {
                            navController.popUpTo { it == Screen.Main }
                            navController.navigate(Screen.Crop)
                        },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightTwo = it.height
                            }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    var heightOne by remember { mutableStateOf(0) }
                    var heightTwo by remember { mutableStateOf(0) }
                    PickColorPreference(
                        onClick = {
                            navController.popUpTo { it == Screen.Main }
                            navController.navigate(Screen.PickColorFromImage)
                        },
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightOne = it.height
                            }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GeneratePalettePreference(
                        modifier = Modifier
                            .then(
                                if (heightOne != 0 && heightTwo != 0) {
                                    Modifier.height(
                                        with(LocalDensity.current) {
                                            max(heightOne, heightTwo).toDp()
                                        }
                                    )
                                } else Modifier
                            )
                            .widthIn(max = 350.dp)
                            .fillMaxWidth()
                            .onSizeChanged {
                                heightTwo = it.height
                            },
                        onClick = {
                            navController.popUpTo { it == Screen.Main }
                            navController.navigate(Screen.GeneratePalette)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ComparePreference(
                    onClick = {
                        navController.popUpTo { it == Screen.Main }
                        navController.navigate(Screen.Compare)
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .widthIn(max = 350.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        BottomAppBar(
            modifier = Modifier
                .shadow(6.dp)
                .zIndex(6f),
            actions = {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.version) + " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    )
                }
            },
            floatingActionButton = {
                val context = LocalContext.current
                FloatingActionButton(
                    onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/T8RIN/ImageResizer")
                            )
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.1.dp,
                        pressedElevation = 0.1.dp,
                        focusedElevation = 0.1.dp,
                        hoveredElevation = 0.1.dp
                    ),
                    content = { Icon(Icons.Rounded.Github, null) }
                )
            }
        )
    }

    if (showSelectFolderDialog) {
        val launcher = rememberLauncherForActivityResult(
            contract = object : ActivityResultContracts.OpenDocumentTree() {
                override fun createIntent(context: Context, input: Uri?): Intent {
                    val intent = super.createIntent(context, input)
                    intent.addFlags(
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    return intent
                }
            },
            onResult = { uri ->
                uri?.let {
                    onGetNewFolder(uri)
                }
            }
        )
        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = Modifier
                .systemBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            onDismissRequest = { showSelectFolderDialog = false },
            title = { Text(stringResource(R.string.folder)) },
            icon = { Icon(Icons.Rounded.Folder, null) },
            confirmButton = {
                FilledTonalButton(onClick = { showSelectFolderDialog = false }) {
                    Text(stringResource(R.string.close))
                }
            },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    PreferenceItem(
                        onClick = { onGetNewFolder(null) },
                        title = stringResource(R.string.def),
                        subtitle = stringResource(R.string.default_folder),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(
                                width = 1.dp,
                                color = animateColorAsState(
                                    if (currentFolderUri == null) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.5f
                                    )
                                    else Color.Transparent
                                ).value,
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PreferenceItem(
                        onClick = { launcher.launch(currentFolderUri) },
                        title = stringResource(R.string.custom),
                        subtitle = currentFolderUri.toUiPath(
                            context = LocalContext.current,
                            default = stringResource(R.string.unspecified)
                        ),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        endIcon = Icons.Rounded.CreateAlt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .border(
                                width = 1.dp,
                                color = animateColorAsState(
                                    if (currentFolderUri != null) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.5f
                                    )
                                    else Color.Transparent
                                ).value,
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                }
            }
        )
    }
}