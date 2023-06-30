package ru.tech.imageresizershrinker.presentation.load_net_image_screen.viewModel


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.model.BitmapInfo
import ru.tech.imageresizershrinker.domain.saving.model.BitmapSaveTarget
import ru.tech.imageresizershrinker.domain.saving.FileController
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class LoadNetImageViewModel @Inject constructor(
    private val fileController: FileController
) : ViewModel() {

    private val _bitmap = mutableStateOf<Bitmap?>(null)
    val bitmap by _bitmap

    private val _tempUri: MutableState<Uri?> = mutableStateOf(null)
    val tempUri by _tempUri

    fun updateBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
    }

    fun setTempUri(uri: Uri?) {
        _tempUri.value = uri
    }

    fun saveBitmap(
        getBitmap: suspend () -> Bitmap?,
        onComplete: (savingPath: String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {

            if (!fileController.isExternalStorageWritable()) {
                onComplete("")
                fileController.requestReadWritePermissions()
            } else {
                val localBitmap = getBitmap()

                if (localBitmap == null) {
                    onComplete("")
                    return@withContext
                }

                val out = ByteArrayOutputStream()
                localBitmap.compress(
                    Bitmap.CompressFormat.PNG,
                    100,
                    out
                )
                fileController.save(
                    saveTarget = BitmapSaveTarget(
                        bitmapInfo = BitmapInfo(
                            width = localBitmap.width,
                            height = localBitmap.height
                        ),
                        originalUri = "_",
                        sequenceNumber = null,
                        data = out.toByteArray()
                    ),
                    keepMetadata = false
                )
                out.flush()
                out.close()

                onComplete(fileController.savingPath)
            }
        }
    }

}