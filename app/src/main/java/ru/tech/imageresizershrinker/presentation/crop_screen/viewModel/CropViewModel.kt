package ru.tech.imageresizershrinker.presentation.crop_screen.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smarttoolfactory.cropper.model.AspectRatio
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import java.io.ByteArrayInputStream
import javax.inject.Inject

@HiltViewModel
class CropViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _cropProperties = mutableStateOf(
        CropDefaults.properties(
            cropOutlineProperty = CropOutlineProperty(
                OutlineType.Rect,
                RectCropShape(0, "")
            ),
            fling = true
        )
    )
    val cropProperties by _cropProperties

    private val _uri = mutableStateOf(Uri.EMPTY)

    private var internalBitmap = mutableStateOf<Bitmap?>(null)

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    val isBitmapChanged get() = internalBitmap.value != _bitmap.value

    private val _mimeType = mutableStateOf(MimeType.Default())
    val mimeType by _mimeType

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    fun updateBitmap(bitmap: Bitmap?, newBitmap: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            val bmp = imageManager.scaleUntilCanShow(bitmap)
            if (newBitmap) {
                internalBitmap.value = bmp
            }
            _bitmap.value = bmp
            _isLoading.value = false
        }
    }

    fun updateMimeType(mimeType: MimeType) {
        _mimeType.value = mimeType
    }

    fun saveBitmap(
        bitmap: Bitmap? = _bitmap.value,
        onComplete: (savingPath: String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            bitmap?.let { bitmap ->
                if (!fileController.isExternalStorageWritable()) {
                    onComplete("")
                    fileController.requestReadWritePermissions()
                } else {
                    val localBitmap = bitmap

                    val byteArray = imageManager.compress(
                        image = localBitmap,
                        imageInfo = ImageInfo(
                            mimeType = mimeType,
                            width = localBitmap.width,
                            height = localBitmap.height
                        )
                    )

                    val decoded = BitmapFactory.decodeStream(
                        ByteArrayInputStream(byteArray)
                    )

                    _bitmap.value = decoded

                    fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = ImageInfo(
                                mimeType = mimeType,
                                width = localBitmap.width,
                                height = localBitmap.height
                            ),
                            originalUri = _uri.value.toString(),
                            sequenceNumber = null,
                            data = byteArray
                        ),
                        keepMetadata = false
                    )

                    onComplete(fileController.savingPath)
                }
            }
        }
    }

    fun setCropAspectRatio(aspectRatio: AspectRatio) {
        _cropProperties.value = _cropProperties.value.copy(
            aspectRatio = aspectRatio,
            fixedAspectRatio = aspectRatio != AspectRatio.Original
        )
    }

    fun resetBitmap() {
        _bitmap.value = internalBitmap.value
    }

    fun imageCropStarted() {
        _isLoading.value = true
    }

    fun imageCropFinished() {
        _isLoading.value = false
    }

    fun setUri(uri: Uri) {
        _uri.value = uri
    }

    fun decodeBitmapByUri(
        uri: Uri,
        originalSize: Boolean = true,
        onGetMimeType: (MimeType) -> Unit,
        onGetExif: (ExifInterface?) -> Unit,
        onGetBitmap: (Bitmap) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        imageManager.getImageAsync(
            uri = uri.toString(),
            originalSize = originalSize,
            onGetImage = onGetBitmap,
            onGetMetadata = onGetExif,
            onGetMimeType = onGetMimeType,
            onError = onError
        )
    }

    fun shareBitmap(bitmap: Bitmap, onComplete: () -> Unit) {
        viewModelScope.launch {
            imageManager.shareImage(
                image = bitmap,
                imageInfo = ImageInfo(
                    mimeType = mimeType,
                    width = bitmap.width,
                    height = bitmap.height
                ),
                onComplete = onComplete
            )
        }
    }

}
