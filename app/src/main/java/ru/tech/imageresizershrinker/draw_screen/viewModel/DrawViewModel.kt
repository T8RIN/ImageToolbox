package ru.tech.imageresizershrinker.draw_screen.viewModel

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.t8rin.drawbox.domain.DrawController
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.common.SAVE_FOLDER
import ru.tech.imageresizershrinker.draw_screen.components.DrawBehavior
import ru.tech.imageresizershrinker.utils.helper.BitmapInfo
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.overlayWith
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.helper.compressFormat
import ru.tech.imageresizershrinker.utils.helper.extension
import ru.tech.imageresizershrinker.utils.helper.mimeTypeInt
import ru.tech.imageresizershrinker.utils.storage.BitmapSaveTarget
import ru.tech.imageresizershrinker.utils.storage.FileController
import ru.tech.imageresizershrinker.utils.storage.SavingFolder
import javax.inject.Inject

@HiltViewModel
class DrawViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    var drawController: DrawController? by mutableStateOf(null)
        private set

    val navController = navController<DrawBehavior>(DrawBehavior.None)

    val drawBehavior get() = navController.backstack.entries.last().destination

    private val _uri = mutableStateOf(Uri.EMPTY)
    val uri: Uri by _uri

    val isBitmapChanged: Boolean get() = !drawController?.paths.isNullOrEmpty()

    private val _mimeType = mutableStateOf(0)
    val mimeType by _mimeType

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    fun updateMimeType(mime: Int) {
        _mimeType.value = mime
    }

    fun saveBitmap(
        getBitmap: suspend (Uri) -> Bitmap?,
        fileController: FileController,
        onComplete: (success: Boolean) -> Unit
    ) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            if (drawBehavior is DrawBehavior.Image) {
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
                            val writeTo: (SavingFolder) -> Unit = { savingFolder ->
                                savingFolder.outputStream?.use {
                                    localBitmap.compress(mimeType.extension.compressFormat, 100, it)
                                }
                            }
                            fileController.getSavingFolder(
                                BitmapSaveTarget(
                                    bitmapInfo = BitmapInfo(
                                        mimeTypeInt = mimeType.extension.mimeTypeInt,
                                        width = localBitmap.width,
                                        height = localBitmap.height
                                    ),
                                    uri = _uri.value,
                                    sequenceNumber = null
                                )
                            ).getOrNull()?.let(writeTo) ?: dataStore.edit { it[SAVE_FOLDER] = "" }
                        }
                        onComplete(true)
                    }
                }
            } else if (drawBehavior is DrawBehavior.Background) {
                if (!fileController.isExternalStorageWritable()) {
                    onComplete(false)
                } else {
                    drawController?.getBitmap()?.resizeBitmap(
                        width_ = (drawBehavior as DrawBehavior.Background).width,
                        height_ = (drawBehavior as DrawBehavior.Background).height,
                        resize = 0
                    )?.let { localBitmap ->
                        val writeTo: (SavingFolder) -> Unit = { savingFolder ->
                            savingFolder.outputStream?.use {
                                localBitmap.compress(mimeType.extension.compressFormat, 100, it)
                            }
                        }
                        fileController.getSavingFolder(
                            BitmapSaveTarget(
                                bitmapInfo = BitmapInfo(
                                    mimeTypeInt = mimeType.extension.mimeTypeInt,
                                    width = localBitmap.width,
                                    height = localBitmap.height
                                ),
                                uri = Uri.parse("drawing"),
                                sequenceNumber = null
                            )
                        ).getOrNull()?.let(writeTo) ?: dataStore.edit { it[SAVE_FOLDER] = "" }
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
                                    resize = 0
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
                        resize = 0
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
