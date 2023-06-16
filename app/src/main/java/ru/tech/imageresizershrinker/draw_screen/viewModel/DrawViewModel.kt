package ru.tech.imageresizershrinker.draw_screen.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.drawbox.domain.DrawController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.overlayWith
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.scaleUntilCanShow
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.utils.helper.mimeTypeInt
import ru.tech.imageresizershrinker.utils.storage.FileController
import ru.tech.imageresizershrinker.utils.storage.SaveTarget
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class DrawViewModel : ViewModel() {

    var drawController: DrawController? = null
        private set

    private val _uri = mutableStateOf(Uri.EMPTY)

    private var internalBitmap = mutableStateOf<Bitmap?>(null)

    private val _bitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val bitmap: Bitmap? by _bitmap

    val isBitmapChanged: Boolean get() = !drawController?.paths.isNullOrEmpty()

    private val _mimeType = mutableStateOf(0)
    val mimeType by _mimeType

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    fun updateBitmap(bitmap: Bitmap?, newBitmap: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            var bmp: Bitmap?
            withContext(Dispatchers.IO) {
                drawController?.clearPaths()
                bmp = bitmap?.scaleUntilCanShow()
            }
            if (newBitmap) {
                internalBitmap.value = bmp
            }
            _bitmap.value = bmp
            _isLoading.value = false
        }
    }

    fun updateMimeType(mime: Int) {
        _mimeType.value = mime
    }

    fun saveBitmap(
        bitmap: Bitmap? = _bitmap.value,
        fileController: FileController,
        onComplete: (success: Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            bitmap?.let { bitmap ->
                if (!fileController.isExternalStorageWritable()) {
                    onComplete(false)
                } else {
                    drawController?.getBitmap()?.let {
                        bitmap.overlayWith(
                            it.resizeBitmap(
                                width_ = bitmap.width,
                                height_ = bitmap.height,
                                resize = 0
                            )
                        )
                    }?.let { localBitmap ->
                        val savingFolder = fileController.getSavingFolder(
                            SaveTarget(
                                bitmapInfo = BitmapInfo(
                                    mimeTypeInt = mimeType.extension.mimeTypeInt,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                ),
                                uri = _uri.value,
                                sequenceNumber = null
                            )
                        )

                        val fos = savingFolder.outputStream

                        localBitmap.compress(mimeType.extension.compressFormat, 100, fos)
                        val out = ByteArrayOutputStream()
                        localBitmap.compress(mimeType.extension.compressFormat, 100, out)
                        val decoded =
                            BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))

                        out.flush()
                        out.close()
                        fos!!.flush()
                        fos.close()
                    }
                    onComplete(true)
                }
            }
        }
    }

    fun resetBitmap() {
        _bitmap.value = internalBitmap.value
    }

    fun setUri(uri: Uri) {
        _uri.value = uri
    }

    fun updateDrawController(drawController: DrawController) {
        this.drawController = drawController
    }

}
