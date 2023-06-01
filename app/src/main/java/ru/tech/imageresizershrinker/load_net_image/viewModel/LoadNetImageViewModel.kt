package ru.tech.imageresizershrinker.load_net_image.viewModel


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.storage.FileController
import ru.tech.imageresizershrinker.utils.storage.SaveTarget

class LoadNetImageViewModel : ViewModel() {

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
        fileController: FileController,
        onComplete: (success: Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {

            if (!fileController.isExternalStorageWritable()) {
                onComplete(false)
            } else {
                val localBitmap = getBitmap()

                if (localBitmap == null) {
                    onComplete(false)
                    return@withContext
                }

                val savingFolder = fileController.getSavingFolder(
                    SaveTarget(
                        bitmapInfo = BitmapInfo(
                            width = localBitmap.width,
                            height = localBitmap.height
                        ),
                        uri = Uri.parse("_"),
                        sequenceNumber = null
                    )
                )

                val fos = savingFolder.outputStream
                localBitmap.compress(
                    "png".compressFormat,
                    100,
                    fos
                )

                fos!!.flush()
                fos.close()

                onComplete(true)
            }
        }
    }

}