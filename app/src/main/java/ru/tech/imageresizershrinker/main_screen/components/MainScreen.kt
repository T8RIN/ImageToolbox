package ru.tech.imageresizershrinker.main_screen.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.AlternateEmail
import androidx.compose.material.icons.rounded.BugReport
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Coffee
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.FileDownloadOff
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.ModeNight
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SettingsSystemDaydream
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.getAppColorTuple
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.popUpTo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.BuildConfig
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.viewModel.MainViewModel
import ru.tech.imageresizershrinker.theme.CreateAlt
import ru.tech.imageresizershrinker.theme.Github
import ru.tech.imageresizershrinker.theme.GooglePlay
import ru.tech.imageresizershrinker.theme.Lamp
import ru.tech.imageresizershrinker.theme.Sparkles
import ru.tech.imageresizershrinker.theme.Telegram
import ru.tech.imageresizershrinker.theme.inverse
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.APP_LINK
import ru.tech.imageresizershrinker.utils.AUTHOR_AVATAR
import ru.tech.imageresizershrinker.utils.AUTHOR_LINK
import ru.tech.imageresizershrinker.utils.ContextUtils.verifyInstallerId
import ru.tech.imageresizershrinker.utils.DONATE
import ru.tech.imageresizershrinker.utils.ISSUE_TRACKER
import ru.tech.imageresizershrinker.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.utils.WEBLATE_LINK
import ru.tech.imageresizershrinker.utils.toUiPath
import ru.tech.imageresizershrinker.widget.LocalToastHost
import ru.tech.imageresizershrinker.widget.Marquee
import ru.tech.imageresizershrinker.widget.Picture
import java.lang.Integer.max

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class
)
@Composable
fun MainScreen(
    navController: NavController<Screen>,
    currentFolderUri: Uri?,
    onGetNewFolder: (Uri?) -> Unit,
    showConfetti: () -> Unit,
    viewModel: MainViewModel
) {
    val editPresetsState = LocalEditPresets.current
    val context = LocalContext.current
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isGrid = LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact

    var showPickColorDialog by rememberSaveable { mutableStateOf(false) }
    var showAuthorDialog by rememberSaveable { mutableStateOf(false) }

    val sideSheetState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()
    val toastHost = LocalToastHost.current

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
                    modifier = Modifier
                        .offset(-((LocalBorderWidth.current + 1.dp)))
                        .width(
                            min(
                                LocalConfiguration.current.screenWidthDp.dp * 0.85f,
                                DrawerDefaults.MaximumDrawerWidth
                            )
                        )
                        .border(
                            LocalBorderWidth.current,
                            MaterialTheme.colorScheme.outlineVariant(
                                0.3f,
                                DrawerDefaults.containerColor
                            ),
                            DrawerDefaults.shape
                        ),
                    windowInsets = WindowInsets(0)
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(Color.Transparent),
                            title = {
                                Marquee(
                                    edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        1.dp
                                    )
                                ) {
                                    Text(stringResource(R.string.settings))
                                }
                            },
                            modifier = Modifier
                                .zIndex(6f)
                                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
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
                                    TitleItem(
                                        icon = Icons.Rounded.Folder,
                                        text = stringResource(R.string.folder),
                                    )
                                    PreferenceItem(
                                        onClick = { onGetNewFolder(null) },
                                        title = stringResource(R.string.def),
                                        subtitle = stringResource(R.string.default_folder),
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                            alpha = animateFloatAsState(
                                                if (currentFolderUri == null) 0.7f
                                                else 0.2f
                                            ).value
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .border(
                                                width = LocalBorderWidth.current,
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
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                            alpha = animateFloatAsState(
                                                if (currentFolderUri != null) 0.7f
                                                else 0.2f
                                            ).value
                                        ),
                                        endIcon = if (currentFolderUri != null) Icons.Rounded.CreateAlt else Icons.Rounded.AddCircleOutline,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                            .border(
                                                width = LocalBorderWidth.current,
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
                                    TitleItem(
                                        icon = Icons.Rounded.PhotoSizeSelectSmall,
                                        text = stringResource(R.string.presets),
                                    )
                                    PreferenceItem(
                                        onClick = { editPresetsState.value = true },
                                        title = stringResource(R.string.values),
                                        subtitle = LocalPresetsProvider.current.joinToString(", "),
                                        color = MaterialTheme
                                            .colorScheme
                                            .secondaryContainer
                                            .copy(alpha = 0.2f),
                                        endIcon = Icons.Rounded.CreateAlt,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    )
                                    Spacer(Modifier.height(16.dp))
                                }
                                Divider()
                                Column {
                                    TitleItem(
                                        icon = Icons.Rounded.Lamp,
                                        text = stringResource(R.string.night_mode),
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf(
                                            stringResource(R.string.dark) to Icons.Rounded.ModeNight,
                                            stringResource(R.string.light) to Icons.Rounded.WbSunny,
                                            stringResource(R.string.system) to Icons.Rounded.SettingsSystemDaydream
                                        ).forEachIndexed { index, (title, icon) ->
                                            val selected = index == viewModel.nightMode
                                            PreferenceItem(
                                                onClick = { viewModel.setNightMode(index) },
                                                title = title,
                                                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                    alpha = animateFloatAsState(
                                                        if (selected) 0.7f
                                                        else 0.2f
                                                    ).value
                                                ),
                                                icon = icon,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 16.dp)
                                                    .border(
                                                        width = LocalBorderWidth.current,
                                                        color = animateColorAsState(
                                                            if (selected) MaterialTheme
                                                                .colorScheme
                                                                .onSecondaryContainer
                                                                .copy(alpha = 0.5f)
                                                            else Color.Transparent
                                                        ).value,
                                                        shape = RoundedCornerShape(12.dp)
                                                    ),
                                                endIcon = if (selected) Icons.Rounded.Check else null
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                                Divider()
                                Column {
                                    TitleItem(
                                        icon = Icons.Rounded.Palette,
                                        text = stringResource(R.string.customization),
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        ChangeLanguagePreference()
                                        PreferenceRowSwitch(
                                            title = stringResource(R.string.dynamic_colors),
                                            subtitle = stringResource(R.string.dynamic_colors_sub),
                                            checked = viewModel.dynamicColors,
                                            onClick = { viewModel.updateDynamicColors() }
                                        )
                                        val enabled = !viewModel.dynamicColors
                                        PreferenceRow(
                                            modifier = Modifier.alpha(
                                                animateFloatAsState(
                                                    if (enabled) 1f
                                                    else 0.5f
                                                ).value
                                            ),
                                            title = stringResource(R.string.color_scheme),
                                            subtitle = stringResource(R.string.pick_accent_color),
                                            onClick = {
                                                if (enabled) showPickColorDialog = true
                                                else scope.launch {
                                                    toastHost.showToast(
                                                        icon = Icons.Rounded.Palette,
                                                        message = context.getString(R.string.cannot_change_palette_while_dynamic_colors_applied)
                                                    )
                                                }
                                            },
                                            endContent = {
                                                ColorTupleItem(
                                                    modifier = Modifier
                                                        .size(64.dp)
                                                        .offset(7.dp)
                                                        .border(
                                                            LocalBorderWidth.current,
                                                            MaterialTheme.colorScheme.outlineVariant(
                                                                0.2f
                                                            ),
                                                            MaterialTheme.shapes.medium
                                                        ),
                                                    colorTuple = viewModel.appColorTuple,
                                                    backgroundColor = MaterialTheme
                                                        .colorScheme
                                                        .surfaceVariant
                                                        .copy(alpha = 0.5f)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .size(28.dp)
                                                            .background(
                                                                animateColorAsState(
                                                                    viewModel.appColorTuple.primary.inverse(
                                                                        fraction = {
                                                                            if (it) 0.8f
                                                                            else 0.5f
                                                                        },
                                                                        darkMode = viewModel.appColorTuple.primary.luminance() < 0.3f
                                                                    )
                                                                ).value,
                                                                CircleShape
                                                            )
                                                    )
                                                    Icon(
                                                        imageVector = Icons.Rounded.CreateAlt,
                                                        contentDescription = null,
                                                        tint = viewModel.appColorTuple.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        )
                                        PreferenceRowSwitch(
                                            title = stringResource(R.string.allow_image_monet),
                                            subtitle = stringResource(R.string.allow_image_monet_sub),
                                            checked = viewModel.allowImageMonet,
                                            onClick = { viewModel.updateAllowImageMonet() }
                                        )
                                        PreferenceRowSwitch(
                                            title = stringResource(R.string.amoled_mode),
                                            subtitle = stringResource(R.string.amoled_mode_sub),
                                            checked = viewModel.amoledMode,
                                            onClick = { viewModel.updateAmoledMode() }
                                        )
                                        Column(
                                            Modifier
                                                .padding(horizontal = 16.dp)
                                                .block(
                                                    color = MaterialTheme
                                                        .colorScheme
                                                        .secondaryContainer
                                                        .copy(alpha = 0.2f)
                                                )
                                                .animateContentSize()
                                        ) {
                                            var sliderValue by remember {
                                                mutableStateOf(
                                                    kotlin.math.max(
                                                        0f,
                                                        viewModel.borderWidth
                                                    )
                                                )
                                            }
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.border_thickness),
                                                    modifier = Modifier
                                                        .padding(
                                                            top = 16.dp,
                                                            end = 16.dp,
                                                            start = 16.dp
                                                        )
                                                        .weight(1f)
                                                )
                                                AnimatedContent(
                                                    targetState = sliderValue,
                                                    transitionSpec = {
                                                        fadeIn() with fadeOut()
                                                    }
                                                ) { value ->
                                                    Text(
                                                        text = "$value",
                                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                                            alpha = 0.5f
                                                        ),
                                                        modifier = Modifier.padding(top = 16.dp)
                                                    )
                                                }
                                                Text(
                                                    maxLines = 1,
                                                    text = "Dp",
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.5f
                                                    ),
                                                    modifier = Modifier.padding(
                                                        start = 4.dp,
                                                        top = 16.dp,
                                                        end = 16.dp
                                                    )
                                                )
                                            }
                                            Slider(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                value = animateFloatAsState(sliderValue).value,
                                                onValueChange = {
                                                    sliderValue = it
                                                },
                                                onValueChangeFinished = {
                                                    viewModel.setBorderWidth(if (sliderValue > 0) sliderValue else -1f)
                                                },
                                                valueRange = 0f..4f,
                                                steps = 15
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                                Divider()
                                Column {
                                    TitleItem(
                                        icon = Icons.Rounded.Info,
                                        text = stringResource(R.string.about_app)
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        PreferenceRow(
                                            title = stringResource(R.string.version),
                                            subtitle = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                                            endContent = {
                                                Icon(Icons.Rounded.Download, null)
                                            },
                                            onClick = {
                                                viewModel.tryGetUpdate(
                                                    newRequest = true,
                                                    onNoUpdates = {
                                                        scope.launch {
                                                            toastHost.showToast(
                                                                icon = Icons.Rounded.FileDownloadOff,
                                                                message = context.getString(R.string.no_updates)
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        )
                                        PreferenceRow(
                                            title = stringResource(R.string.help_translate),
                                            subtitle = stringResource(R.string.help_translate_sub),
                                            endContent = {
                                                Icon(Icons.Rounded.Translate, null)
                                            },
                                            onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(WEBLATE_LINK)
                                                    )
                                                )
                                            }
                                        )
                                        PreferenceRow(
                                            title = stringResource(R.string.issue_tracker),
                                            subtitle = stringResource(R.string.issue_tracker_sub),
                                            endContent = {
                                                Icon(Icons.Rounded.BugReport, null)
                                            },
                                            onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(ISSUE_TRACKER)
                                                    )
                                                )
                                            }
                                        )
                                        PreferenceRow(
                                            title = stringResource(R.string.buy_me_a_coffee),
                                            subtitle = stringResource(R.string.buy_me_a_coffee_sub),
                                            endContent = {
                                                Icon(Icons.Rounded.Coffee, null)
                                            },
                                            onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse(DONATE)
                                                    )
                                                )
                                            }
                                        )
                                        PreferenceRow(
                                            title = stringResource(R.string.app_developer),
                                            subtitle = stringResource(R.string.app_developer_nick),
                                            maxLines = 1,
                                            startContent = {
                                                Picture(
                                                    model = AUTHOR_AVATAR,
                                                    modifier = Modifier
                                                        .padding(horizontal = 8.dp)
                                                        .size(48.dp),
                                                )
                                            },
                                            onClick = {
                                                showAuthorDialog = true
                                            }
                                        )
                                        SourceCodePreference(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
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
                            CompositionLocalProvider(
                                LocalLayoutDirection provides LayoutDirection.Ltr
                            ) {
                                Marquee(
                                    edgeColor = MaterialTheme
                                        .colorScheme
                                        .surfaceColorAtElevation(3.dp)
                                ) {
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
                        modifier = Modifier.drawHorizontalStroke(),
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
                            Spacer(modifier = Modifier.height(12.dp))
                            SingleResizePreference(
                                onClick = {
                                    navController.popUpTo { it == Screen.Main }
                                    navController.navigate(Screen.SingleResize)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            BatchResizePreference(
                                onClick = {
                                    navController.popUpTo { it == Screen.Main }
                                    navController.navigate(Screen.BatchResize)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            BytesResizePreference(
                                onClick = {
                                    navController.popUpTo { it == Screen.Main }
                                    navController.navigate(Screen.ResizeByBytes)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            CropPreference(
                                onClick = {
                                    navController.popUpTo { it == Screen.Main }
                                    navController.navigate(Screen.Crop)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PickColorPreference(
                                onClick = {
                                    navController.popUpTo { it == Screen.Main }
                                    navController.navigate(Screen.PickColorFromImage)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            GeneratePalettePreference(
                                onClick = {
                                    navController.popUpTo { it == Screen.Main }
                                    navController.navigate(Screen.GeneratePalette)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            ComparePreference(
                                onClick = {
                                    navController.popUpTo { it == Screen.Main }
                                    navController.navigate(Screen.Compare)
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
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
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
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
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp),
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
                            Spacer(modifier = Modifier.height(12.dp))
                            ComparePreference(
                                onClick = {
                                    navController.popUpTo { it == Screen.Main }
                                    navController.navigate(Screen.Compare)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .widthIn(max = 350.dp)
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    val updateButtonColors = if (viewModel.updateAvailable) {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.9f
                            ),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.5f
                            )
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(
                                alpha = 0.9f
                            ),
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                                alpha = 0.5f
                            )
                        )
                    }

                    BottomAppBar(
                        modifier = Modifier.drawHorizontalStroke(top = true),
                        actions = {
                            OutlinedButton(
                                colors = updateButtonColors,
                                border = BorderStroke(
                                    LocalBorderWidth.current,
                                    MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                                onClick = {
                                    viewModel.tryGetUpdate(
                                        newRequest = true,
                                        onNoUpdates = {
                                            scope.launch {
                                                toastHost.showToast(
                                                    icon = Icons.Rounded.FileDownloadOff,
                                                    message = context.getString(R.string.no_updates)
                                                )
                                            }
                                        }
                                    )
                                }
                            ) {
                                Text(
                                    stringResource(R.string.version) + " ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                                )
                            }
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = {
                                    if (context.verifyInstallerId()) {
                                        try {
                                            context.startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("market://details?id=${context.packageName}")
                                                )
                                            )
                                        } catch (e: ActivityNotFoundException) {
                                            context.startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
                                                )
                                            )
                                        }
                                    } else {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(APP_LINK)
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fabBorder()
                                    .requiredSize(size = 56.dp),
                                containerColor = if (viewModel.updateAvailable) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.primaryContainer
                                },
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                content = {
                                    if (context.verifyInstallerId()) {
                                        Icon(
                                            Icons.Rounded.GooglePlay,
                                            null,
                                            modifier = Modifier.offset(1.5.dp)
                                        )
                                    } else {
                                        Icon(Icons.Rounded.Github, null)
                                    }
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    if (showPickColorDialog) {
        val showColorPicker = rememberSaveable { mutableStateOf(false) }
        AvailableColorTuplesDialog(
            modifier = Modifier
                .width(352.dp)
                .systemBarsPadding()
                .padding(16.dp)
                .alertDialog(),
            showColorPicker = showColorPicker,
            colorTupleList = viewModel.colorTupleList,
            currentColorTuple = getAppColorTuple(
                defaultColorTuple = viewModel.appColorTuple,
                dynamicColor = viewModel.dynamicColors,
                darkTheme = viewModel.nightMode.isNightMode()
            ),
            colorPicker = { onUpdateColorTuples ->
                ColorPickerDialog(
                    modifier = Modifier.alertDialog(),
                    colorTuple = viewModel.appColorTuple,
                    onDismissRequest = { showColorPicker.value = false },
                    onColorChange = {
                        viewModel.updateColorTuple(it)
                        onUpdateColorTuples(viewModel.colorTupleList + it)
                    }
                )
            },
            onUpdateColorTuples = {
                viewModel.updateColorTuples(it)
            },
            onPickTheme = { viewModel.updateColorTuple(it) },
            onDismissRequest = { showPickColorDialog = false }
        )
    } else if (showAuthorDialog) {
        AlertDialog(
            modifier = Modifier.alertDialog(),
            onDismissRequest = { showAuthorDialog = false },
            title = { Text(stringResource(R.string.app_developer_nick)) },
            icon = {
                Icon(imageVector = Icons.Rounded.Person, contentDescription = null)
            },
            text = {
                Box {
                    Divider(Modifier.align(Alignment.TopCenter))
                    Column {
                        Spacer(Modifier.height(8.dp))
                        PreferenceItem(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("http://t.me/${context.getString(R.string.app_developer_nick)}")
                                    )
                                )
                            },
                            title = stringResource(R.string.telegram),
                            icon = Icons.Rounded.Telegram,
                            subtitle = stringResource(R.string.app_developer_nick)
                        )
                        Spacer(Modifier.height(8.dp))
                        PreferenceItem(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                Intent(Intent.ACTION_SENDTO).apply {
                                    data =
                                        Uri.parse("mailto:${context.getString(R.string.developer_email)}")
                                    context.startActivity(this)
                                }
                            },
                            title = stringResource(R.string.email),
                            icon = Icons.Rounded.AlternateEmail,
                            subtitle = stringResource(R.string.developer_email)
                        )
                        Spacer(Modifier.height(8.dp))
                        PreferenceItem(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = {
                                context.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(AUTHOR_LINK)
                                    )
                                )
                            },
                            title = stringResource(R.string.github),
                            icon = Icons.Rounded.Github,
                            subtitle = stringResource(R.string.app_developer_nick)
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Divider(Modifier.align(Alignment.BottomCenter))
                }
            },
            confirmButton = {
                OutlinedButton(
                    onClick = { showAuthorDialog = false },
                    border = BorderStroke(
                        LocalBorderWidth.current, MaterialTheme.colorScheme.outlineVariant()
                    )
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        )
    }
}
