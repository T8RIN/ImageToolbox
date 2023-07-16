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
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.drawbox.domain.DrawController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.domain.image.ImageManager
import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.model.ImageFormat
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.domain.saving.FileController
import ru.tech.imageresizershrinker.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.presentation.draw_screen.components.DrawBehavior
import ru.tech.imageresizershrinker.presentation.root.transformation.UpscaleTransformation
import javax.inject.Inject

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val fileController: FileController,
    private val imageManager: ImageManager<Bitmap, ExifInterface>
) : ViewModel() {

    var drawController: DrawController? by mutableStateOf(null)
        private set

    val navController = navController<DrawBehavior>(DrawBehavior.None)

    val drawBehavior get() = navController.backstack.entries.last().destination

    private val _uri = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    val isBitmapChanged: Boolean get() = !drawController?.paths.isNullOrEmpty()

    private val _imageFormat = mutableStateOf(ImageFormat.Default())
    val imageFormat by _imageFormat

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    fun updateMimeType(imageFormat: ImageFormat) {
        _imageFormat.value = imageFormat
    }

    fun saveBitmap(
        onComplete: (savePath: String) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            if (drawBehavior is DrawBehavior.Image) {
                getBitmapFromUriWithTransformations(
                    uri = uri,
                    originalSize = false,
                    transformations = listOf(UpscaleTransformation())
                )?.let { bitmap ->
                    if (!fileController.isExternalStorageWritable()) {
                        onComplete("")
                        fileController.requestReadWritePermissions()
                    } else {
                        drawController?.getBitmap()?.let {
                            imageManager.overlayImage(
                                image = bitmap,
                                overlay = imageManager.resize(
                                    image = it,
                                    width = bitmap.width,
                                    height = bitmap.height,
                                    resizeType = ResizeType.Explicit
                                )!!
                            )
                        }?.let { localBitmap ->
                            fileController.save(
                                ImageSaveTarget(
                                    imageInfo = ImageInfo(
                                        imageFormat = imageFormat,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    ),
                                    originalUri = _uri.value.toString(),
                                    sequenceNumber = null,
                                    data = imageManager.compress(
                                        image = localBitmap,
                                        imageInfo = ImageInfo(
                                            imageFormat = imageFormat,
                                            width = localBitmap.width,
                                            height = localBitmap.height
                                        )
                                    )
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
                    drawController?.getBitmap()?.let {
                        imageManager.resize(
                            image = it,
                            width = (drawBehavior as DrawBehavior.Background).width,
                            height = (drawBehavior as DrawBehavior.Background).height,
                            resizeType = ResizeType.Explicit
                        )
                    }?.let { localBitmap ->
                        fileController.save(
                            saveTarget = ImageSaveTarget(
                                imageInfo = ImageInfo(
                                    imageFormat = imageFormat,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                ),
                                originalUri = "drawing",
                                sequenceNumber = null,
                                data = imageManager.compress(
                                    image = localBitmap,
                                    imageInfo = ImageInfo(
                                        imageFormat = imageFormat,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    )
                                )
                            ), keepMetadata = true
                        )
                    }
                    onComplete(fileController.savingPath)
                }
            }
        }
    }

    private suspend fun calculateScreenOrientationBasedOnUri(uri: Uri): Int {
        val bmp = imageManager.getImage(uri = uri.toString(), originalSize = false)
        val imageRatio = (bmp?.width ?: 0) / (bmp?.height?.toFloat() ?: 1f)
        return if (imageRatio <= 1f) {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
    }

    fun setUri(uri: Uri) {
        viewModelScope.launch {
            drawController?.clearPaths()
            _uri.value = uri
            navController.navigate(
                DrawBehavior.Image(calculateScreenOrientationBasedOnUri(uri))
            )
        }
    }

    fun updateDrawController(drawController: DrawController) {
        this.drawController = drawController
    }

    private suspend fun getBitmapForSharing(): Bitmap? = withContext(Dispatchers.IO) {
        if (drawBehavior is DrawBehavior.Image) {
            imageManager.getImageWithTransformations(
                uri = uri.toString(),
                originalSize = false,
                transformations = listOf(UpscaleTransformation())
            )?.let { bitmap ->
                return@withContext drawController?.getBitmap()?.let {
                    imageManager.overlayImage(
                        image = bitmap,
                        overlay = imageManager.resize(
                            image = it,
                            width = bitmap.width,
                            height = bitmap.height,
                            resizeType = ResizeType.Explicit
                        )!!
                    )
                }
            }
        } else if (drawBehavior is DrawBehavior.Background) {
            return@withContext drawController?.getBitmap()?.let {
                imageManager.resize(
                    image = it,
                    width = (drawBehavior as DrawBehavior.Background).width,
                    height = (drawBehavior as DrawBehavior.Background).height,
                    resizeType = ResizeType.Explicit
                )
            }
        }
        return@withContext null
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

    fun shareBitmap(onComplete: () -> Unit) {
        viewModelScope.launch {
            getBitmapForSharing()?.let {
                imageManager.shareImage(
                    image = it,
                    imageInfo = ImageInfo(
                        imageFormat = imageFormat,
                        width = it.width,
                        height = it.height
                    ),
                    onComplete = onComplete
                )
            } ?: onComplete()
        }
    }

    suspend fun getBitmapFromUriWithTransformations(
        uri: Uri,
        transformations: List<Transformation<Bitmap>>,
        originalSize: Boolean = false
    ): Bitmap? =
        imageManager.getImageWithTransformations(uri.toString(), transformations, originalSize)

    fun overlayImage(
        image: Bitmap,
        overlay: Bitmap
    ): Bitmap = imageManager.overlayImage(image, overlay)
}
