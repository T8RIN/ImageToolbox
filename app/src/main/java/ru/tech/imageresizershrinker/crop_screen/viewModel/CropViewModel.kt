package ru.tech.imageresizershrinker.crop_screen.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class CropViewModel : ViewModel() {

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    var mimeType = Bitmap.CompressFormat.PNG
        private set

    fun updateBitmap(bitmap: Bitmap?) {
        _bitmap.value = bitmap
    }

    fun updateMimeType(mime: Int) {
        when (mime) {
            0 -> mimeType = Bitmap.CompressFormat.JPEG
            1 -> mimeType = Bitmap.CompressFormat.WEBP
            2 -> mimeType = Bitmap.CompressFormat.PNG
        }
    }

    fun saveBitmap(
        bitmap: Bitmap? = _bitmap.value,
        isExternalStorageWritable: Boolean,
        getFileOutputStream: (name: String, ext: String) -> OutputStream?,
        getExternalStorageDir: () -> File?,
        onSuccess: (Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            bitmap?.let { bitmap ->
                if (!isExternalStorageWritable) {
                    onSuccess(false)
                } else {
                    val ext =
                        if (mimeType == Bitmap.CompressFormat.WEBP) "webp" else if (mimeType == Bitmap.CompressFormat.PNG) "png" else "jpg"

                    val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val name = "ResizedImage$timeStamp.$ext"
                    val localBitmap = bitmap
                    val fos: OutputStream? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            getFileOutputStream(name, ext)
                        } else {
                            val imagesDir = getExternalStorageDir()
                            if (imagesDir?.exists() == false) imagesDir.mkdir()
                            val image = File(imagesDir, name)
                            FileOutputStream(image)
                        }
                    localBitmap.compress(mimeType, 100, fos)
                    val out = ByteArrayOutputStream()
                    localBitmap.compress(mimeType, 100, out)
                    val decoded =
                        BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))

                    out.flush()
                    out.close()
                    fos!!.flush()
                    fos.close()

                    _bitmap.value = decoded
                    onSuccess(true)
                }
            }
        }
    }

}
