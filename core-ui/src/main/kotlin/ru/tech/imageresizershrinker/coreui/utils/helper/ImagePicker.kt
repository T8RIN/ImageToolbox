package ru.tech.imageresizershrinker.coreui.utils.helper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState


class ImagePicker(
    private val context: Context,
    private val mode: ImagePickerMode,
    private val photoPickerSingle: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    private val photoPickerMultiple: ManagedActivityResultLauncher<PickVisualMediaRequest, List<Uri>>,
    private val getContent: ManagedActivityResultLauncher<Intent, ActivityResult>,
) {
    fun pickImage() {
        runCatching {
            when (mode) {
                ImagePickerMode.PhotoPickerSingle -> photoPickerSingle.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )

                ImagePickerMode.PhotoPickerMultiple -> photoPickerMultiple.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )

                ImagePickerMode.GallerySingle, ImagePickerMode.GalleryMultiple -> {
                    val intent =
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        ).apply {
                            type = "image/*"
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

                ImagePickerMode.GetContentSingle, ImagePickerMode.GetContentMultiple -> {
                    val intent = Intent().apply {
                        type = "image/*"
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
            }
        }
    }
}

enum class ImagePickerMode {
    PhotoPickerSingle,
    PhotoPickerMultiple,
    GallerySingle,
    GalleryMultiple,
    GetContentSingle,
    GetContentMultiple
}

enum class Picker {
    Single, Multiple
}

@Composable
fun localImagePickerMode(picker: Picker = Picker.Single): ImagePickerMode {
    val modeInt = LocalSettingsState.current.imagePickerModeInt
    val multiple = picker == Picker.Multiple
    return remember(modeInt) {
        derivedStateOf {
            when (modeInt) {
                0 -> if (multiple) ImagePickerMode.PhotoPickerMultiple else ImagePickerMode.PhotoPickerSingle
                1 -> if (multiple) ImagePickerMode.GalleryMultiple else ImagePickerMode.GallerySingle
                else -> if (multiple) ImagePickerMode.GetContentMultiple else ImagePickerMode.GetContentSingle
            }
        }
    }.value
}

@Composable
fun rememberImagePicker(
    mode: ImagePickerMode,
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

    val getContent =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                val data = result.data?.data
                val clipData = result.data?.clipData
                if (clipData != null) {
                    onSuccess(
                        List(
                            size = clipData.itemCount,
                            init = {
                                clipData.getItemAt(it).uri
                            }
                        )
                    )
                } else if (data != null) {
                    onSuccess(listOf(data))
                } else onFailure()
            }
        )

    return remember {
        ImagePicker(
            context = context,
            mode = mode,
            photoPickerSingle = photoPickerSingle,
            photoPickerMultiple = photoPickerMultiple,
            getContent = getContent
        )
    }
}
