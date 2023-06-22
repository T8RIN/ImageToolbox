package ru.tech.imageresizershrinker.load_net_image_screen.viewModel


import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.common.SAVE_FOLDER
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.storage.BitmapSaveTarget
import ru.tech.imageresizershrinker.utils.storage.FileController
import ru.tech.imageresizershrinker.utils.storage.SavingFolder
import javax.inject.Inject

@HiltViewModel
class LoadNetImageViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
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

                val writeTo: (SavingFolder) -> Unit = { savingFolder ->
                    savingFolder.outputStream?.use {
                        localBitmap.compress(
                            "png".compressFormat,
                            100,
                            it
                        )
                    }
                }
                fileController.getSavingFolder(
                    BitmapSaveTarget(
                        bitmapInfo = BitmapInfo(
                            width = localBitmap.width,
                            height = localBitmap.height
                        ),
                        uri = Uri.parse("_"),
                        sequenceNumber = null
                    )
                ).getOrNull()?.let(writeTo) ?: dataStore.edit { it[SAVE_FOLDER] = "" }


                onComplete(true)
            }
        }
    }

}