package ru.tech.imageresizershrinker.presentation.load_net_image_screen.viewModel


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.model.ImageData
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.SaveResult
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class LoadNetImageViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    private val _bitmap = mutableStateOf<Bitmap?>(null)
    val bitmap by _bitmap

    private val _tempUri: MutableState<Uri?> = mutableStateOf(null)
    val tempUri by _tempUri

    private val _isSaving: MutableState<Boolean> = mutableStateOf(false)
    val isSaving by _isSaving

    fun updateBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
    }

    private var savingJob: Job? = null

    fun saveBitmap(
        link: String,
        onComplete: (saveResult: SaveResult) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            imageManager.getImage(data = link)?.let { bitmap ->
                ByteArrayOutputStream().use { out ->
                    bitmap.compress(
                        Bitmap.CompressFormat.PNG,
                        100,
                        out
                    )
                    onComplete(
                        fileController.save(
                            saveTarget = ImageSaveTarget<ExifInterface>(
                                imageInfo = ImageInfo(
                                    width = bitmap.width,
                                    height = bitmap.height
                                ),
                                originalUri = "_",
                                sequenceNumber = null,
                                data = out.toByteArray()
                            ),
                            keepMetadata = false
                        )
                    )
                }
            }
            _isSaving.value = false
        }
    }.also {
        _isSaving.value = false
        savingJob?.cancel()
        savingJob = it
    }

    fun cacheImage(image: Bitmap, imageInfo: ImageInfo) {
        viewModelScope.launch {
            _isSaving.value = true
            _tempUri.value = imageManager.cacheImage(image, imageInfo)?.toUri()
            _isSaving.value = false
        }
    }

    fun shareBitmap(bitmap: Bitmap, imageInfo: ImageInfo, onComplete: () -> Unit) {
        viewModelScope.launch {
            _isSaving.value = true
            imageManager.shareImage(
                imageData = ImageData(bitmap, imageInfo),
                onComplete = {
                    _isSaving.value = false
                    onComplete()
                }
            )
        }.also {
            _isSaving.value = false
            savingJob?.cancel()
            savingJob = it
        }
    }

    fun cancelSaving() {
        savingJob?.cancel()
        savingJob = null
        _isSaving.value = false
    }

}