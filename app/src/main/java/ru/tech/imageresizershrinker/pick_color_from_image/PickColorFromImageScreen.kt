package ru.tech.imageresizershrinker.pick_color_from_image

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.isUnspecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import com.smarttoolfactory.colordetector.ImageColorDetector
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.generate_palette.isScrollingUp
import ru.tech.imageresizershrinker.main_screen.components.*
import ru.tech.imageresizershrinker.pick_color_from_image.viewModel.PickColorViewModel
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.resize_screen.components.LoadingDialog
import ru.tech.imageresizershrinker.resize_screen.components.LocalToastHost
import ru.tech.imageresizershrinker.theme.PaletteSwatch
import ru.tech.imageresizershrinker.theme.outlineVariant
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.widget.Marquee

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PickColorFromImageScreen(
    uriState: Uri?,
    navController: NavController<Screen>,
    onGoBack: () -> Unit,
    pushNewUri: (Uri?) -> Unit,
    viewModel: PickColorViewModel = viewModel()
) {
    val context = LocalContext.current
    val toastHostState = LocalToastHost.current
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current
    val allowChangeColor = LocalAllowChangeColorByImage.current

    var canZoom by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.setUri(it)
            pushNewUri(null)
            context.decodeBitmapFromUri(
                uri = it,
                onGetMimeType = {},
                onGetExif = {},
                onGetBitmap = viewModel::updateBitmap,
                onError = {
                    scope.launch {
                        toastHostState.showToast(
                            context.getString(
                                R.string.smth_went_wrong,
                                it.localizedMessage ?: ""
                            ),
                            Icons.Rounded.ErrorOutline
                        )
                    }
                }
            )
        }
    }

    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            if (allowChangeColor) themeState.updateColorByImage(it)
        }
    }

    LaunchedEffect(viewModel.color) {
        if (!viewModel.color.isUnspecified) {
            if (allowChangeColor) themeState.updateColor(viewModel.color)
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                viewModel.setUri(it)
                context.decodeBitmapFromUri(
                    uri = it,
                    onGetMimeType = {},
                    onGetExif = {},
                    onGetBitmap = viewModel::updateBitmap,
                    onError = {
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(
                                    R.string.smth_went_wrong,
                                    it.localizedMessage ?: ""
                                ),
                                Icons.Rounded.ErrorOutline
                            )
                        }
                    }
                )
            }
        }

    val pickImage = {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        val color = viewModel.color
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = viewModel.bitmap == null,
                transitionSpec = { fadeIn() with fadeOut() }
            ) { noBmp ->
                if (noBmp) {
                    LargeTopAppBar(
                        scrollBehavior = scrollBehavior,
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    onGoBack()
                                }
                            ) {
                                Icon(Icons.Rounded.ArrowBack, null)
                            }
                        },
                        title = {
                            Marquee(
                                edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                            ) {
                                Text(stringResource(R.string.pick_color))
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                        ),
                        modifier = Modifier.drawHorizontalStroke()
                    )
                } else {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                        modifier = Modifier.animateContentSize(),
                    ) {
                        Column {
                            Column(Modifier.navBarsLandscapePadding()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            onGoBack()
                                        },
                                        modifier = Modifier.statusBarsPadding()
                                    ) {
                                        Icon(Icons.Rounded.ArrowBack, null)
                                    }
                                    Spacer(
                                        Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp)
                                    )
                                    if (viewModel.uri != null) {
                                        IconButton(
                                            onClick = {
                                                if (navController.backstack.entries.isNotEmpty()) navController.pop()
                                                navController.navigate(Screen.GeneratePalette)
                                                pushNewUri(viewModel.uri)
                                            },
                                            modifier = Modifier.statusBarsPadding()
                                        ) {
                                            Icon(Icons.Rounded.PaletteSwatch, null)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                ProvideTextStyle(value = LocalTextStyle.current.merge(MaterialTheme.typography.headlineSmall)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                    ) {
                                        Text(stringResource(R.string.color))

                                        Text(
                                            modifier = Modifier
                                                .padding(horizontal = 8.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    context.copyColorIntoClipboard(
                                                        context.getString(R.string.color),
                                                        color.format()
                                                    )
                                                    scope.launch {
                                                        toastHostState.showToast(
                                                            icon = Icons.Rounded.ContentPaste,
                                                            message = context.getString(R.string.color_copied)
                                                        )
                                                    }
                                                }
                                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                                .padding(horizontal = 4.dp),
                                            text = color.format(),
                                            style = LocalTextStyle.current.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        )

                                        Spacer(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(2.dp)
                                        )

                                        Box(
                                            Modifier
                                                .padding(vertical = 4.dp)
                                                .background(
                                                    color = animateColorAsState(color).value,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .height(40.dp)
                                                .width(72.dp)
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.outlineVariant,
                                                    shape = RoundedCornerShape(11.dp)
                                                )
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable {
                                                    context.copyColorIntoClipboard(
                                                        context.getString(R.string.color),
                                                        color.format()
                                                    )
                                                    scope.launch {
                                                        toastHostState.showToast(
                                                            icon = Icons.Rounded.ContentPaste,
                                                            message = context.getString(R.string.color_copied)
                                                        )
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                            Divider(color = MaterialTheme.colorScheme.outlineVariant(0.3f))
                        }
                    }
                }
            }
            AnimatedContent(targetState = viewModel.bitmap, modifier = Modifier.weight(1f)) { bitmap ->
                bitmap?.let {
                    ImageColorDetector(
                        canZoom = canZoom,
                        imageBitmap = it.asImageBitmap(),
                        color = viewModel.color,
                        modifier = Modifier
                            .padding(16.dp)
                            .navBarsPaddingOnlyIfTheyAtTheEnd()
                            .block(RoundedCornerShape(4.dp))
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        onColorChange = viewModel::updateColor
                    )
                } ?: Column(Modifier.verticalScroll(scrollState)) {
                    ImageNotPickedWidget(
                        onPickImage = pickImage,
                        modifier = Modifier
                            .padding(bottom = 88.dp, top = 20.dp, start = 20.dp, end = 20.dp)
                            .navigationBarsPadding()
                    )
                }
            }
            if (viewModel.bitmap != null) {
                BottomAppBar(
                    modifier = Modifier
                        .drawHorizontalStroke(true),
                    actions = {
                        Switch(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            colors = SwitchDefaults.colors(
                                uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedIconColor = MaterialTheme.colorScheme.primary,
                            ),
                            checked = !canZoom,
                            onCheckedChange = { canZoom = !canZoom },
                            thumbContent = {
                                AnimatedContent(canZoom) { zoom ->
                                    Icon(
                                        if (!zoom) Icons.Rounded.Colorize else Icons.Rounded.ZoomIn,
                                        null,
                                        Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            }
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = pickImage,
                            modifier = Modifier.fabBorder(),
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        ) {
                            Icon(Icons.Rounded.AddPhotoAlternate, null)
                        }
                    }
                )
            }
        }

        if (viewModel.bitmap == null) {
            FloatingActionButton(
                onClick = pickImage,
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
                    .fabBorder(),
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                val expanded = scrollState.isScrollingUp() && viewModel.bitmap == null

                val horizontalPadding by animateDpAsState(targetValue = if (expanded) 16.dp else 0.dp)
                Row(
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                    AnimatedVisibility(visible = expanded) {
                        Row {
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.pick_image_alt))
                        }
                    }
                }
            }
        }
    }

    if (viewModel.isLoading) LoadingDialog()

    BackHandler {
        onGoBack()
    }
}

fun Color.format(): String =
    String.format("#%08X", (0xFFFFFFFF and this.toArgb().toLong())).replace("#FF", "#")

fun Context.copyColorIntoClipboard(label: String, value: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, value)
    clipboard.setPrimaryClip(clip)
}