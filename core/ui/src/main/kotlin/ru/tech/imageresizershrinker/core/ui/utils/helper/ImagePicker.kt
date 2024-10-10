/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.model.PicturePickerMode
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelable
import ru.tech.imageresizershrinker.core.ui.utils.helper.IntentUtils.parcelableArrayList
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import java.io.File
import kotlin.random.Random


class ImagePicker(
    private val context: Context,
    private val mode: ImagePickerMode,
    private val currentAccent: Color,
    private val photoPickerSingle: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    private val photoPickerMultiple: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>,
    private val getContent: ManagedActivityResultLauncher<Intent, ActivityResult>,
    private val takePhoto: ManagedActivityResultLauncher<Uri, Boolean>,
    private val onCreateTakePhotoUri: (Uri) -> Unit,
    private val imageExtension: String,
    private val onFailure: (Throwable) -> Unit,
) {
    fun pickImage() {
        val cameraAction = {
            val imagesFolder = File(context.cacheDir, "images")
            runCatching {
                imagesFolder.mkdirs()
                val file = File(imagesFolder, "${Random.nextLong()}.jpg")
                FileProvider.getUriForFile(
                    context,
                    context.getString(R.string.file_provider),
                    file
                )
            }.onSuccess {
                onCreateTakePhotoUri(it)
                takePhoto.launch(it)
            }
        }
        val singlePhotoPickerAction = {
            photoPickerSingle.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        val multiplePhotoPickerAction = {
            photoPickerMultiple.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        val galleryAction = {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply {
                type = "image/$imageExtension"
                if (mode == ImagePickerMode.GalleryMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
            }
            getContent.launch(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.pick_image)
                )
            )
        }
        val embeddedAction = {
            val intent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                context,
                MediaPickerActivityClass
            ).apply {
                type = "image/$imageExtension"
                if (mode == ImagePickerMode.EmbeddedMultiple) {
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                putExtra(ColorSchemeName, currentAccent.toArgb())
            }
            getContent.launch(intent)
        }
        val getContentAction = {
            val intent = Intent().apply {
                type = "image/$imageExtension"
                action = Intent.ACTION_OPEN_DOCUMENT
                putExtra(
                    Intent.EXTRA_ALLOW_MULTIPLE,
                    mode == ImagePickerMode.GetContentMultiple
                )
            }
            getContent.launch(
                Intent.createChooser(
                    intent,
                    context.getString(R.string.pick_image)
                )
            )
        }

        runCatching {
            when (mode) {
                ImagePickerMode.PhotoPickerSingle -> singlePhotoPickerAction()
                ImagePickerMode.PhotoPickerMultiple -> multiplePhotoPickerAction()
                ImagePickerMode.CameraCapture -> cameraAction()

                ImagePickerMode.GallerySingle,
                ImagePickerMode.GalleryMultiple -> galleryAction()

                ImagePickerMode.GetContentSingle,
                ImagePickerMode.GetContentMultiple -> getContentAction()

                ImagePickerMode.Embedded,
                ImagePickerMode.EmbeddedMultiple -> embeddedAction()
            }
        }.onFailure(onFailure)
    }
}

enum class ImagePickerMode {
    Embedded,
    EmbeddedMultiple,
    PhotoPickerSingle,
    PhotoPickerMultiple,
    GallerySingle,
    GalleryMultiple,
    GetContentSingle,
    GetContentMultiple,
    CameraCapture
}

enum class Picker {
    Single, Multiple
}

@Composable
fun localImagePickerMode(
    picker: Picker = Picker.Single,
    mode: PicturePickerMode = LocalSettingsState.current.picturePickerMode,
): ImagePickerMode {
    val multiple = picker == Picker.Multiple
    return remember(mode, multiple) {
        derivedStateOf {
            when (mode) {
                PicturePickerMode.Embedded -> if (multiple) ImagePickerMode.EmbeddedMultiple else ImagePickerMode.Embedded
                PicturePickerMode.PhotoPicker -> if (multiple) ImagePickerMode.PhotoPickerMultiple else ImagePickerMode.PhotoPickerSingle
                PicturePickerMode.Gallery -> if (multiple) ImagePickerMode.GalleryMultiple else ImagePickerMode.GallerySingle
                PicturePickerMode.GetContent -> if (multiple) ImagePickerMode.GetContentMultiple else ImagePickerMode.GetContentSingle
                PicturePickerMode.CameraCapture -> ImagePickerMode.CameraCapture
            }
        }
    }.value
}

@Composable
fun rememberImagePicker(
    mode: ImagePickerMode,
    imageExtension: String = DefaultExtension,
    onFailure: () -> Unit = {},
    onSuccess: (List<Uri>) -> Unit,
): ImagePicker {
    val context = LocalContext.current

    val photoPickerSingle = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            it?.let { onSuccess(listOf(it)) } ?: onFailure()
        }
    )
    val photoPickerMultiple = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            uris.takeIf { it.isNotEmpty() }?.let { onSuccess(it) } ?: onFailure()
        }
    )

    val getContent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val intent = result.data
            val data = intent?.data
            val clipData = intent?.clipData
            if (clipData != null) {
                onSuccess(clipData.clipList())
            } else if (data != null) {
                onSuccess(listOf(data))
            } else if (intent?.action == Intent.ACTION_SEND_MULTIPLE) {
                onSuccess(
                    intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM) ?: emptyList()
                )
            } else if (intent?.action == Intent.ACTION_SEND) {
                onSuccess(
                    listOfNotNull(intent.parcelable<Uri>(Intent.EXTRA_STREAM))
                )
            } else onFailure()
        }
    )

    var takePhotoUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val takePhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            val uri = takePhotoUri
            if (it && uri != null) {
                onSuccess(listOf(uri))
            } else onFailure()
            takePhotoUri = null
        }
    )

    val scope = rememberCoroutineScope()
    val toastHostState = LocalToastHostState.current
    val currentAccent = LocalDynamicThemeState.current.colorTuple.value.primary

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            scope.launch {
                toastHostState.showToast(
                    message = context.getString(R.string.grant_camera_permission_to_capture_image),
                    icon = Icons.Outlined.CameraAlt
                )
            }
        }
    }

    return remember(
        imageExtension,
        currentAccent,
        photoPickerSingle,
        photoPickerMultiple,
        getContent,
        takePhoto,
        mode
    ) {
        derivedStateOf {
            ImagePicker(
                context = context,
                mode = mode,
                currentAccent = currentAccent,
                photoPickerSingle = photoPickerSingle,
                photoPickerMultiple = photoPickerMultiple,
                getContent = getContent,
                takePhoto = takePhoto,
                onCreateTakePhotoUri = {
                    takePhotoUri = it
                },
                imageExtension = imageExtension,
                onFailure = {
                    onFailure()

                    scope.launch {
                        if (it is ActivityNotFoundException) {
                            toastHostState.showToast(
                                message = context.getString(R.string.activate_files),
                                icon = Icons.Outlined.FolderOff,
                                duration = ToastDuration.Long
                            )
                        } else if (mode == ImagePickerMode.CameraCapture && it is SecurityException) {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        } else {
                            toastHostState.showError(
                                context = context,
                                error = it
                            )
                        }
                    }
                }
            )
        }
    }.value
}

private const val DefaultExtension: String = "*"