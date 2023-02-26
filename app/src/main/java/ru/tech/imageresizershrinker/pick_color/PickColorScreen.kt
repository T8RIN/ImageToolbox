package ru.tech.imageresizershrinker.pick_color

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
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import com.smarttoolfactory.colordetector.ImageColorDetector
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.Screen
import ru.tech.imageresizershrinker.main_screen.block
import ru.tech.imageresizershrinker.pick_color.viewModel.PickColorViewModel
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.resize_screen.components.LoadingDialog
import ru.tech.imageresizershrinker.resize_screen.components.ToastHost
import ru.tech.imageresizershrinker.resize_screen.components.rememberToastHostState
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PickColorScreen(
    uriState: Uri?,
    navController: NavController<Screen>,
    onGoBack: () -> Unit,
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                modifier = Modifier
                    .animateContentSize()
                    .shadow(6.dp),
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
                                    .background(color, RoundedCornerShape(12.dp))
                                    .size(48.dp)
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
            AnimatedContent(targetState = viewModel.bitmap) { bitmap ->
                bitmap?.let {
                    ImageColorDetector(
                        imageBitmap = it.asImageBitmap(),
                        modifier = Modifier
                            .padding(bottom = 72.dp)
                            .padding(16.dp)
                            .block(RoundedCornerShape(4.dp))
                            .padding(4.dp),
                        onColorChange = viewModel::updateColor
                    )
                } ?: Column {
                    Spacer(Modifier.height(16.dp))
                    ImageNotPickedWidget(
                        onPickImage = pickImage
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = pickImage,
            modifier = Modifier
                .navigationBarsPadding()
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

    if(viewModel.isLoading) LoadingDialog()

    ToastHost(hostState = toastHostState)
    BackHandler {
        if (navController.backstack.entries.isNotEmpty()) navController.pop()
        onGoBack()
        themeState.reset()
    }
}

fun Color.format(): String =
    String.format("#%08X", (0xFFFFFFFF and this.toArgb().toLong())).replace("#FF", "#")

fun Context.copyColorIntoClipboard(label: String, value: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, value)
    clipboard.setPrimaryClip(clip)
}