package ru.tech.imageresizershrinker.draw_screen.viewModel

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
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
import ru.tech.imageresizershrinker.draw_screen.components.DrawBehavior
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.overlayWith
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.utils.helper.mimeTypeInt
import ru.tech.imageresizershrinker.utils.storage.FileController
import ru.tech.imageresizershrinker.utils.storage.SaveTarget


class DrawViewModel : ViewModel() {

    var drawController: DrawController? = null
        private set

    private val _uri = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    val isBitmapChanged: Boolean get() = !drawController?.paths.isNullOrEmpty()

    private val _mimeType = mutableStateOf(0)
    val mimeType by _mimeType

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _drawBehavior: MutableState<DrawBehavior> = mutableStateOf(DrawBehavior.None)
    val drawBehavior by _drawBehavior

    fun updateMimeType(mime: Int) {
        _mimeType.value = mime
    }

    fun saveBitmap(
        getBitmap: suspend (Uri) -> Bitmap?,
        fileController: FileController,
        onComplete: (success: Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            getBitmap(_uri.value)?.let { bitmap ->
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

                        savingFolder.outputStream?.use {
                            localBitmap.compress(mimeType.extension.compressFormat, 100, it)
                        }

                    }
                    onComplete(true)
                }
            }
        }
    }

    fun setUri(uri: Uri, getDrawOrientation: suspend (Uri) -> Int) {
        viewModelScope.launch {
            drawController?.clearPaths()
            _uri.value = uri
            _drawBehavior.value = DrawBehavior.Image(getDrawOrientation(uri))
        }
    }

    fun updateDrawController(drawController: DrawController) {
        this.drawController = drawController
    }

    fun processBitmapForSharing(
        getBitmap: suspend (Uri) -> Bitmap?,
        onComplete: (Bitmap?) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            getBitmap(_uri.value)?.let { bitmap ->
                onComplete(
                    drawController?.getBitmap()?.let {
                        bitmap.overlayWith(
                            it.resizeBitmap(
                                width_ = bitmap.width,
                                height_ = bitmap.height,
                                resize = 0
                            )
                        )
                    }
                )
            }
        }
    }

    fun resetDrawBehavior() {
        _drawBehavior.value = DrawBehavior.None
        _uri.value = Uri.EMPTY
    }

    fun startDrawOnBackground(width: Int, height: Int) {
        val imageRatio = width / height.toFloat()
        _drawBehavior.value = DrawBehavior.Background(
            orientation = if (imageRatio <= 1f) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            },
            width = width,
            height = height
        )
    }

}
