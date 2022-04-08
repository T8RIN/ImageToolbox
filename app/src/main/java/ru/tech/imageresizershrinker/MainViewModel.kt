package ru.tech.imageresizershrinker

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import ru.tech.imageresizershrinker.BitmapUtils.flip
import ru.tech.imageresizershrinker.BitmapUtils.previewBitmap
import ru.tech.imageresizershrinker.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.BitmapUtils.rotate
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class MainViewModel : ViewModel() {

    var showDialog by mutableStateOf(false)
    val globalBitmap = mutableStateOf<Bitmap?>(null)

    var width by mutableStateOf("")
    var height by mutableStateOf("")
    var quality by mutableStateOf(0f)
    var mime by mutableStateOf(0)
    var resize by mutableStateOf(0)
    var rotation by mutableStateOf(0f)
    var isFlipped by mutableStateOf(false)

    fun checkBitmapAndUpdate(bitmap: MutableState<Bitmap?>) {
        val context = Dispatchers.IO + SupervisorJob()
        viewModelScope.launch(context = context) {
            globalBitmap.value?.let { bmp ->
                bitmap.value = updatePreview(bmp)
                context.cancelChildren()
            }
        }
    }

    suspend fun saveBitmap(
        bitmap: Bitmap,
        isExternalStorageWritable: Boolean,
        contentResolver: ContentResolver
    ): Boolean = withContext(Dispatchers.IO) {
        if (!isExternalStorageWritable) return@withContext false

        val ext = if (mime == 1) "webp" else if (mime == 0) "png" else "jpg"
        val explicit = resize == 0

        val tWidth = width.toIntOrNull() ?: bitmap.width
        val tHeight = height.toIntOrNull() ?: bitmap.height

        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val name = "ResizedImage$timeStamp.$ext"
        val localBitmap = if (explicit) {
            Bitmap.createScaledBitmap(
                bitmap,
                tWidth,
                tHeight,
                false
            )
        } else {
            bitmap.resizeBitmap(max(tWidth, tHeight))
        }.rotate(rotation).flip(isFlipped)

        val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/$ext")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/ResizedImages")
            }
            val imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(imageUri!!)
        } else {
            val imagesDir =
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}${File.separator}ResizedImages"
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdir()
            }
            val image = File(imagesDir, "$name.$ext")
            FileOutputStream(image)
        }
        localBitmap.compress(
            if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 0) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
            quality.toInt(),
            fos
        )
        val out = ByteArrayOutputStream()
        localBitmap.compress(
            if (mime == 1) Bitmap.CompressFormat.WEBP else if (mime == 0) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
            quality.toInt(), out
        )
        val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
        out.flush()
        out.close()
        fos!!.flush()
        fos.close()

        globalBitmap.value = decoded
        isFlipped = false
        rotation = 0f

        return@withContext true
    }

    private suspend fun updatePreview(
        bitmap: Bitmap
    ): Bitmap = withContext(Dispatchers.IO) {
        return@withContext bitmap.previewBitmap(
            quality,
            width.toIntOrNull(),
            height.toIntOrNull(),
            mime,
            resize,
            rotation,
            isFlipped
        )
    }

    fun resetValues() {
        width = globalBitmap.value?.width?.toString() ?: ""
        height = globalBitmap.value?.height?.toString() ?: ""
        quality = 100f
        rotation = 0f
        isFlipped = false
        mime = 0
        resize = 0
    }

    companion object {
        fun String.restrict(`by`: Int = 4200): String {
            if (isEmpty()) return this

            return if ((this.toIntOrNull() ?: 0) > `by`) `by`.toString()
            else if (this.isDigitsOnly() && (this.toIntOrNull() ?: 0) == 0) ""
            else this.trim().filter {
                !listOf('-', '.', ',', ' ', "\n").contains(it)
            }
        }
    }

}