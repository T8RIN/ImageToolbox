package ru.tech.imageresizershrinker.main_screen.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popUpTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.*
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
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
    showConfetti: () -> Unit,
    viewModel: MainViewModel
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact

    val sideSheetState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()

    CompositionLocalProvider(
        LocalLayoutDirection provides if (layoutDirection == LayoutDirection.Ltr) LayoutDirection.Rtl
        else LayoutDirection.Ltr
    ) {
        ModalNavigationDrawer(
            drawerState = sideSheetState,
            drawerContent = {
                if (sideSheetState.isOpen) {
                    BackHandler {
                        scope.launch {
                            sideSheetState.close()
                        }
                    }
                }
                ModalDrawerSheet(
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                            title = { Text(stringResource(R.string.settings)) },
                            actions = {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            sideSheetState.close()
                                        }
                                    }
                                ) {
                                    Icon(Icons.Rounded.Close, null)
                                }
                            }
                        )
                        Divider()
                        LazyColumn(contentPadding = WindowInsets.navigationBars.asPaddingValues()) {
                            item {
                                Column {
                                    val launcher = rememberLauncherForActivityResult(
                                        contract = object :
                                            ActivityResultContracts.OpenDocumentTree() {
                                            override fun createIntent(
                                                context: Context,
                                                input: Uri?
                                            ): Intent {
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
                                    Row(
                                        Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.Folder, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.folder))
                                    }
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
                                    Spacer(Modifier.height(16.dp))
                                }
                                Divider()
                                Column {
                                    Row(
                                        Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.DeveloperMode, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.night_mode))
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf(
                                            stringResource(R.string.dark) to Icons.Rounded.ModeNight,
                                            stringResource(R.string.light) to Icons.Rounded.WbSunny,
                                            stringResource(R.string.system) to Icons.Rounded.SettingsSystemDaydream
                                        ).forEachIndexed { index, (title, icon) ->
                                            PreferenceItem(
                                                onClick = { viewModel.setNightMode(index) },
                                                title = title,
                                                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                    alpha = animateFloatAsState(
                                                        if (index == viewModel.nightMode) 1f
                                                        else 0.5f
                                                    ).value
                                                ),
                                                icon = icon,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp)
                                                    .border(
                                                        width = 1.dp,
                                                        color = animateColorAsState(
                                                            if (index == viewModel.nightMode) MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                                                alpha = 0.5f
                                                            )
                                                            else Color.Transparent
                                                        ).value,
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                                Divider()
                                Column {
                                    Row(
                                        Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.Palette, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(stringResource(R.string.customization))
                                    }
                                    ChangeLanguagePreference()
                                    Spacer(Modifier.height(8.dp))
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable { viewModel.updateDynamicColors() }
                                                .block(
                                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                        alpha = 0.5f
                                                    )
                                                )
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(R.string.dynamic_colors),
                                                modifier = Modifier.weight(1f)
                                            )
                                            Switch(
                                                checked = viewModel.dynamicColors,
                                                onCheckedChange = {
                                                    viewModel.updateDynamicColors()
                                                }
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                    }
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable { showPickColorDialog = true }
                                            .block(
                                                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stringResource(R.string.color_scheme),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(
                                            Modifier
                                                .background(viewModel.appPrimaryColor)
                                                .clip(RoundedCornerShape(30))
                                                .size(64.dp)
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable { viewModel.updateAllowImageMonet() }
                                            .block(
                                                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = stringResource(R.string.allow_image_monet))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = stringResource(R.string.allow_image_monet_sub),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 14.sp,
                                                color = LocalContentColor.current.copy(alpha = 0.5f)
                                            )
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Switch(
                                            checked = viewModel.allowImageMonet,
                                            onCheckedChange = {
                                                viewModel.updateAllowImageMonet()
                                            }
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable { viewModel.updateAmoledMode() }
                                            .block(
                                                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                    alpha = 0.5f
                                                )
                                            )
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = stringResource(R.string.amoled_mode))
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = stringResource(R.string.amoled_mode_sub),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 14.sp,
                                                color = LocalContentColor.current.copy(alpha = 0.5f)
                                            )
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Switch(
                                            checked = viewModel.amoledMode,
                                            onCheckedChange = {
                                                viewModel.updateAmoledMode()
                                            }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
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
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        sideSheetState.open()
                                    }
                                }
                            ) {
                                Icon(Icons.Rounded.Settings, null)
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
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                        alpha = 0.5f
                                    )
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
            }
        }
    }
}