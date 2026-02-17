/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.ui.utils.content_pickers

import android.Manifest
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import com.t8rin.dynamic.theme.LocalDynamicThemeState
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.model.PicturePickerMode
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.IntentUtils.parcelable
import com.t8rin.imagetoolbox.core.ui.utils.helper.IntentUtils.parcelableArrayList
import com.t8rin.imagetoolbox.core.ui.utils.helper.clipList
import com.t8rin.imagetoolbox.core.ui.utils.helper.createMediaPickerIntent
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalComponentActivity
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.logger.makeLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.random.Random


private class ImagePickerImpl(
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
) : ImagePicker {
    override fun pickImage() {
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
            }.onFailure {
                it.makeLog("Image Picker")
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
            val intent = Intent(Intent.ACTION_PICK).apply {
                setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/$imageExtension"
                )
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
            getContent.launch(
                createMediaPickerIntent(
                    context = context,
                    allowMultiple = mode == ImagePickerMode.EmbeddedMultiple,
                    currentAccent = currentAccent,
                    imageExtension = imageExtension
                )
            )
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

        mode.makeLog("Image Picker Start")

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
        }.onFailure {
            it.makeLog("Image Picker Failure")
            if (it is SecurityException && mode == ImagePickerMode.CameraCapture) {
                onFailure(CameraException())
            } else onFailure(it)
        }.onSuccess {
            mode.makeLog("Image Picker Success")
        }
    }

    override fun pickImageWithMode(
        picker: Picker,
        picturePickerMode: PicturePickerMode
    ) {
        val multiple = picker == Picker.Multiple
        val mode = when (picturePickerMode) {
            PicturePickerMode.Embedded -> if (multiple) ImagePickerMode.EmbeddedMultiple else ImagePickerMode.Embedded
            PicturePickerMode.PhotoPicker -> if (multiple) ImagePickerMode.PhotoPickerMultiple else ImagePickerMode.PhotoPickerSingle
            PicturePickerMode.Gallery -> if (multiple) ImagePickerMode.GalleryMultiple else ImagePickerMode.GallerySingle
            PicturePickerMode.GetContent -> if (multiple) ImagePickerMode.GetContentMultiple else ImagePickerMode.GetContentSingle
            PicturePickerMode.CameraCapture -> ImagePickerMode.CameraCapture
        }

        val basePicker = ImagePickerImpl(
            context = context,
            mode = mode,
            currentAccent = currentAccent,
            photoPickerSingle = photoPickerSingle,
            photoPickerMultiple = photoPickerMultiple,
            getContent = getContent,
            takePhoto = takePhoto,
            onCreateTakePhotoUri = onCreateTakePhotoUri,
            imageExtension = imageExtension,
            onFailure = onFailure
        )

        basePicker.pickImage()
    }
}

@Stable
@Immutable
interface ImagePicker {

    fun pickImage()

    fun pickImageWithMode(
        picker: Picker,
        picturePickerMode: PicturePickerMode
    )

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
    return remember(mode, picker) {
        derivedStateOf {
            val multiple = picker == Picker.Multiple

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
    picker: Picker = Picker.Single,
    imageExtension: String = DefaultExtension,
    onFailure: () -> Unit = {},
    onSuccess: (List<Uri>) -> Unit,
): ImagePicker = rememberImagePicker(
    mode = localImagePickerMode(picker = picker),
    imageExtension = imageExtension,
    onFailure = onFailure,
    onSuccess = onSuccess
)

@JvmName("rememberSingleImagePicker")
@Composable
fun rememberImagePicker(
    imageExtension: String = DefaultExtension,
    onFailure: () -> Unit = {},
    onSuccess: (Uri) -> Unit,
): ImagePicker = rememberImagePicker(
    mode = localImagePickerMode(picker = Picker.Single),
    imageExtension = imageExtension,
    onFailure = onFailure,
    onSuccess = {
        it.firstOrNull()?.let(onSuccess)
    }
)

@JvmName("rememberMultipleImagePicker")
@Composable
fun rememberImagePicker(
    imageExtension: String = DefaultExtension,
    onFailure: () -> Unit = {},
    onSuccess: (List<Uri>) -> Unit,
): ImagePicker = rememberImagePicker(
    mode = localImagePickerMode(picker = Picker.Multiple),
    imageExtension = imageExtension,
    onFailure = onFailure,
    onSuccess = onSuccess
)

@Composable
fun rememberImagePicker(
    mode: ImagePickerMode,
    imageExtension: String = DefaultExtension,
    onFailure: () -> Unit = {},
    onSuccess: (List<Uri>) -> Unit,
): ImagePicker {
    val essentials = rememberLocalEssentials()
    val context = LocalComponentActivity.current

    val photoPickerSingle = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            essentials.launch {
                delay(300)
                uri?.takeIf {
                    it != Uri.EMPTY
                }?.let {
                    onSuccess(listOf(it))
                } ?: onFailure()
            }
        }
    )
    val photoPickerMultiple = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            essentials.launch {
                delay(300)
                uris.takeIf { it.isNotEmpty() }?.let(onSuccess) ?: onFailure()
            }
        }
    )

    val getContent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val intent = result.data
            val data = intent?.data
            val clipData = intent?.clipData

            val resultList: List<Uri> = clipData?.clipList()
                ?: if (data != null) {
                    listOf(data)
                } else if (intent?.action == Intent.ACTION_SEND_MULTIPLE) {
                    intent.parcelableArrayList<Uri>(Intent.EXTRA_STREAM) ?: emptyList()
                } else if (intent?.action == Intent.ACTION_SEND) {
                    listOfNotNull(intent.parcelable<Uri>(Intent.EXTRA_STREAM))
                } else {
                    emptyList()
                }

            essentials.launch {
                delay(300)
                resultList.takeIf { it.isNotEmpty() }?.let(onSuccess) ?: onFailure()
            }
        }
    )

    var takePhotoUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }
    val takePhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {
            essentials.launch {
                val uri = takePhotoUri
                delay(300)
                if (it && uri != null && uri != Uri.EMPTY) {
                    onSuccess(listOf(uri))
                } else onFailure()
                takePhotoUri = null
            }
        }
    )

    val currentAccent = LocalDynamicThemeState.current.colorTuple.value.primary

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            essentials.showToast(
                message = context.getString(R.string.grant_camera_permission_to_capture_image),
                icon = Icons.Outlined.CameraAlt
            )
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
            ImagePickerImpl(
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

                    when (it) {
                        is CameraException -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        else -> essentials.handleFileSystemFailure(it)
                    }
                }
            )
        }
    }.value
}

private class CameraException : Throwable("No Camera permission")

private const val DefaultExtension: String = "*"