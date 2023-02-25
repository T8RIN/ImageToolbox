package ru.tech.imageresizershrinker.pcik_color

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import com.github.skydoves.colorpicker.compose.ImageColorPicker
import com.github.skydoves.colorpicker.compose.PaletteContentScale
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.Screen
import ru.tech.imageresizershrinker.pcik_color.viewModel.PickColorViewModel
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.resize_screen.components.ToastHost
import ru.tech.imageresizershrinker.resize_screen.components.rememberToastHostState
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PickColorScreen(
    uriState: Uri?,
    navController: NavController<Screen>,
    viewModel: PickColorViewModel = viewModel()
) {
    val context = LocalContext.current
    val toastHostState = rememberToastHostState()
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    LaunchedEffect(uriState) {
        uriState?.let {
            try {
                context.decodeBitmapFromUri(
                    uri = it,
                    onGetMimeType = {},
                    onGetExif = {},
                    onGetBitmap = viewModel::updateBitmap,
                )
            } catch (e: Exception) {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(
                            R.string.smth_went_wrong,
                            e.localizedMessage ?: ""
                        ),
                        Icons.Rounded.ErrorOutline
                    )
                }
            }
        }
    }
    LaunchedEffect(viewModel.bitmap) {
        viewModel.bitmap?.let {
            themeState.updateColorByImage(it)
        }
    }

    val pickImageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                try {
                    context.decodeBitmapFromUri(
                        uri = it,
                        onGetMimeType = {},
                        onGetExif = {},
                        onGetBitmap = viewModel::updateBitmap,
                    )
                } catch (e: Exception) {
                    scope.launch {
                        toastHostState.showToast(
                            context.getString(
                                R.string.smth_went_wrong,
                                e.localizedMessage ?: ""
                            ),
                            Icons.Rounded.ErrorOutline
                        )
                    }
                }
            }
        }

    val pickImage = {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
    Box(Modifier.fillMaxSize()) {
        val color = viewModel.color
        Column {
            Surface(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(30.dp))
                    ProvideTextStyle(value = LocalTextStyle.current.merge(MaterialTheme.typography.headlineSmall)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(16.dp)
                        ) {
                            if (color != Color.Unspecified) Text(stringResource(R.string.color))
                            else Text(stringResource(R.string.pick_color))

                            if (color != Color.Unspecified) {
                                Text(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(vertical = 2.dp, horizontal = 4.dp)
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
                                        },
                                    text = color.format(),
                                    style = LocalTextStyle.current.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                            }

                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                            )
                            if (color != Color.Unspecified) {
                                Box(
                                    Modifier
                                        .background(color, RoundedCornerShape(12.dp))
                                        .size(40.dp)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                            RoundedCornerShape(12.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }
            AnimatedContent(targetState = viewModel.bitmap) { bitmap ->
                bitmap?.let {
                    ImageColorPicker(
                        paletteContentScale = PaletteContentScale.CROP,
                        paletteImageBitmap = it.asImageBitmap(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 88.dp),
                        onColorChanged = {
                            viewModel.updateColor(it.color)
                        },
                        controller = rememberColorPickerController()
                    )
                } ?: ImageNotPickedWidget(
                    onPickImage = pickImage
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
        }

        FloatingActionButton(
            onClick = pickImage,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.AddPhotoAlternate, null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.pick_image_alt))
            }
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            dismissButton = {
                FilledTonalButton(
                    onClick = {
                        showExitDialog = false
                        if (navController.backstack.entries.isNotEmpty()) navController.pop()
                        themeState.reset()
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
            },
            confirmButton = {
                Button(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.stay))
                }
            },
            title = { Text(stringResource(R.string.image_not_saved)) },
            text = {
                Text(
                    stringResource(R.string.image_not_saved_sub),
                    textAlign = TextAlign.Center
                )
            },
            icon = { Icon(Icons.Outlined.Save, null) }
        )
    }

    ToastHost(hostState = toastHostState)
    BackHandler {
        if (viewModel.bitmap != null) showExitDialog = true
        else if (navController.backstack.entries.isNotEmpty()) navController.pop()
    }
}

fun Color.format(): String =
    String.format("#%08X", (0xFFFFFFFF and this.toArgb().toLong())).replace("#FF", "#")

fun Context.copyColorIntoClipboard(label: String, value: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, value)
    clipboard.setPrimaryClip(clip)
}