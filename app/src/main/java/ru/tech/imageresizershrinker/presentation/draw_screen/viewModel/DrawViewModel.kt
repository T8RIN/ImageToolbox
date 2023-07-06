package ru.tech.imageresizershrinker.presentation.draw_screen.viewModel

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.drawbox.domain.DrawController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.domain.model.MimeType
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawBehavior
import ru.tech.imageresizershrinker.core.android.ImageUtils.compress
import ru.tech.imageresizershrinker.core.android.ImageUtils.overlayWith
import ru.tech.imageresizershrinker.core.android.ImageUtils.resizeBitmap
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val fileController: FileController
) : ViewModel() {

    var drawController: DrawController? by mutableStateOf(null)
        private set

    val navController = navController<DrawBehavior>(DrawBehavior.None)

    val drawBehavior get() = navController.backstack.entries.last().destination

    private val _uri = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    val isBitmapChanged: Boolean get() = !drawController?.paths.isNullOrEmpty()

    private val _mimeType = mutableStateOf(MimeType.Default())
    val mimeType by _mimeType

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    fun updateMimeType(mimeType: MimeType) {
        _mimeType.value = mimeType
    }

    fun saveBitmap(
        getBitmap: suspend (Uri) -> Bitmap?,
        onComplete: (savePath: String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            if (drawBehavior is DrawBehavior.Image) {
                getBitmap(_uri.value)?.let { bitmap ->
                    if (!fileController.isExternalStorageWritable()) {
                        onComplete("")
                        fileController.requestReadWritePermissions()
                    } else {
                        drawController?.getBitmap()?.let {
                            bitmap.overlayWith(
                                it.resizeBitmap(
                                    width_ = bitmap.width,
                                    height_ = bitmap.height,
                                    resizeType = ResizeType.Explicit
                                )
                            )
                        }?.let { localBitmap ->
                            val out = ByteArrayOutputStream()
                            localBitmap.compress(mimeType = mimeType, quality = 100, out = out)

                            fileController.save(
                                ImageSaveTarget(
                                    imageInfo = ImageInfo(
                                        mimeType = mimeType,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    ),
                                    originalUri = _uri.value.toString(),
                                    sequenceNumber = null,
                                    data = out.toByteArray()
                                ), keepMetadata = true
                            )
                        }
                        onComplete(fileController.savingPath)
                    }
                }
            } else if (drawBehavior is DrawBehavior.Background) {
                if (!fileController.isExternalStorageWritable()) {
                    onComplete("")
                    fileController.requestReadWritePermissions()
                } else {
                    drawController?.getBitmap()?.resizeBitmap(
                        width_ = (drawBehavior as DrawBehavior.Background).width,
                        height_ = (drawBehavior as DrawBehavior.Background).height,
                        resizeType = ResizeType.Explicit
                    )?.let { localBitmap ->
                        val out = ByteArrayOutputStream()
                        localBitmap.compress(mimeType = mimeType, quality = 100, out = out)

                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = ImageInfo(
                                    mimeType = mimeType,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                ),
                                originalUri = "drawing",
                                sequenceNumber = null,
                                data = out.toByteArray(),
                            ), keepMetadata = true
                        )
                    }
                    onComplete(fileController.savingPath)
                }
            }
        }
    }

    fun setUri(uri: Uri, getDrawOrientation: suspend (Uri) -> Int) {
        viewModelScope.launch {
            drawController?.clearPaths()
            _uri.value = uri
            navController.navigate(
                DrawBehavior.Image(getDrawOrientation(uri))
            )
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
            if (drawBehavior is DrawBehavior.Image) {
                getBitmap(_uri.value)?.let { bitmap ->
                    onComplete(
                        drawController?.getBitmap()?.let {
                            bitmap.overlayWith(
                                it.resizeBitmap(
                                    width_ = bitmap.width,
                                    height_ = bitmap.height,
                                    resizeType = ResizeType.Explicit
                                )
                            )
                        }
                    )
                }
            } else if (drawBehavior is DrawBehavior.Background) {
                onComplete(
                    drawController?.getBitmap()?.resizeBitmap(
                        width_ = (drawBehavior as DrawBehavior.Background).width,
                        height_ = (drawBehavior as DrawBehavior.Background).height,
                        resizeType = ResizeType.Explicit
                    )
                )
            }
        }
    }

    fun resetDrawBehavior() {
        drawController?.apply {
            setDrawBackground(Color.Transparent)
            setColor(Color.Black.toArgb())
            setAlpha(100)
            setStrokeWidth(8f)
            clearPaths()
        }
        navController.navigate(DrawBehavior.None)
        _uri.value = Uri.EMPTY
    }

    fun startDrawOnBackground(reqWidth: Int, reqHeight: Int, color: Color) {
        val width = reqWidth.takeIf { it > 0 } ?: 1
        val height = reqHeight.takeIf { it > 0 } ?: 1
        val imageRatio = width / height.toFloat()
        navController.navigate(
            DrawBehavior.Background(
                orientation = if (imageRatio <= 1f) {
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                },
                width = width,
                height = height
            )
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                while (drawController?.backgroundColor != color) {
                    drawController?.setDrawBackground(color)
                }
            }
        }
    }

}
