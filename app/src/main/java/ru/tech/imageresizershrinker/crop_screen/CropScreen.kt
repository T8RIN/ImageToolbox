package ru.tech.imageresizershrinker.crop_screen


import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cookhelper.dynamic.theme.LocalDynamicThemeState
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.pop
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.crop_screen.viewModel.CropViewModel
import ru.tech.imageresizershrinker.main_screen.Screen
import ru.tech.imageresizershrinker.main_screen.isExternalStorageWritable
import ru.tech.imageresizershrinker.main_screen.requestPermission
import ru.tech.imageresizershrinker.resize_screen.components.ImageNotPickedWidget
import ru.tech.imageresizershrinker.resize_screen.components.LoadingDialog
import ru.tech.imageresizershrinker.resize_screen.components.ToastHost
import ru.tech.imageresizershrinker.resize_screen.components.rememberToastHostState
import ru.tech.imageresizershrinker.utils.BitmapUtils.decodeBitmapFromUri
import ru.tech.imageresizershrinker.utils.BitmapUtils.shareBitmap
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropScreen(
    uriState: Uri?,
    navController: NavController<Screen>,
    onGoBack: () -> Unit,
    viewModel: CropViewModel = viewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = rememberToastHostState()
    val scope = rememberCoroutineScope()
    val themeState = LocalDynamicThemeState.current

    LaunchedEffect(uriState) {
        uriState?.let {
            try {
                context.decodeBitmapFromUri(
                    uri = it,
                    onGetMimeType = viewModel::updateMimeType,
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

    var showSaveLoading by rememberSaveable { mutableStateOf(false) }
    val saveBitmap: (Bitmap) -> Unit = {
        showSaveLoading = true
        viewModel.saveBitmap(
            bitmap = it,
            isExternalStorageWritable = context.isExternalStorageWritable(),
            getFileOutputStream = { name, ext ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(
                        MediaStore.MediaColumns.MIME_TYPE,
                        "image/$ext"
                    )
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "DCIM/ResizedImages"
                    )
                }
                val imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                context.contentResolver.openOutputStream(imageUri!!)
            },
            getExternalStorageDir = {
                File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DCIM
                    ), "ResizedImages"
                )
            }
        ) { success ->
            if (!success) context.requestPermission()
            else {
                scope.launch {
                    toastHostState.showToast(
                        context.getString(R.string.saved_to),
                        Icons.Rounded.Save
                    )
                }
            }
            showSaveLoading = false
        }
    }

    var crop by remember { mutableStateOf(false) }
    var share by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TopAppBar(
                modifier = Modifier.shadow(6.dp),
                title = {
                    Text(stringResource(R.string.crop))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                        3.dp
                    )
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (navController.backstack.entries.isNotEmpty()) navController.pop()
                            onGoBack()
                            themeState.reset()
                        }
                    ) {
                        Icon(Icons.Rounded.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            share = true
                            crop = true
                        },
                        enabled = viewModel.bitmap != null
                    ) {
                        Icon(Icons.Outlined.Share, null)
                    }
                }
            )
            viewModel.bitmap?.let {
                val bmp = remember(it) { it.asImageBitmap() }
                ImageCropper(
                    modifier = Modifier.padding(bottom = 120.dp),
                    imageBitmap = bmp,
                    contentDescription = null,
                    cropProperties = CropDefaults.properties(
                        cropOutlineProperty = CropOutlineProperty(
                            OutlineType.Rect,
                            RectCropShape(0, "")
                        )
                    ),
                    onCropStart = {},
                    crop = crop,
                    onCropSuccess = { image ->
                        if (share) {
                            context.shareBitmap(
                                bitmap = image.asAndroidBitmap(),
                                compressFormat = viewModel.mimeType
                            )
                        } else {
                            saveBitmap(image.asAndroidBitmap())
                        }
                        crop = false
                        share = false
                    }
                )
            } ?: Column {
                Spacer(Modifier.height(16.dp))
                ImageNotPickedWidget(
                    onPickImage = pickImage
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .navigationBarsPadding()
                .align(Alignment.BottomEnd)
        ) {
            FloatingActionButton(
                onClick = pickImage,
                modifier = Modifier.navigationBarsPadding()
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
            if (viewModel.bitmap != null) {
                Spacer(modifier = Modifier.width(16.dp))
                FloatingActionButton(
                    onClick = {
                        crop = true
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Icon(Icons.Rounded.Save, null)
                }
            }
        }
    }

    if (showSaveLoading) {
        LoadingDialog()
    }

    ToastHost(hostState = toastHostState)
    BackHandler {
        if (navController.backstack.entries.isNotEmpty()) navController.pop()
        onGoBack()
        themeState.reset()
    }
}