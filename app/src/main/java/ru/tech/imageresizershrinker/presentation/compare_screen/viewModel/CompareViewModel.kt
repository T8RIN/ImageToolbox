package ru.tech.imageresizershrinker.presentation.compare_screen.viewModel

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.rotate

class CompareViewModel : ViewModel() {

    private val _bitmapData: MutableState<Pair<Bitmap?, Bitmap?>?> = mutableStateOf(null)
    val bitmapData by _bitmapData

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: Boolean by _isLoading

    private val _rotation: MutableState<Float> = mutableStateOf(0f)
    val rotation by _rotation

    fun updateBitmapData(newBeforeBitmap: Bitmap?, newAfterBitmap: Bitmap?) {
        viewModelScope.launch {
            _isLoading.value = true
            var bmp1: Bitmap?
            var bmp2: Bitmap?
            withContext(Dispatchers.IO) {
                bmp1 = if (newBeforeBitmap?.canShow() == false) {
                    newBeforeBitmap.resizeBitmap(
                        height_ = (newBeforeBitmap.height * 0.9f).toInt(),
                        width_ = (newBeforeBitmap.width * 0.9f).toInt(),
                        resizeType = 1
                    )
                } else newBeforeBitmap

                while (bmp1?.canShow() == false) {
                    bmp1 = bmp1?.resizeBitmap(
                        height_ = (bmp1!!.height * 0.9f).toInt(),
                        width_ = (bmp1!!.width * 0.9f).toInt(),
                        resizeType = 1
                    )
                }
                ///
                bmp2 = if (newAfterBitmap?.canShow() == false) {
                    newAfterBitmap.resizeBitmap(
                        height_ = (newAfterBitmap.height * 0.9f).toInt(),
                        width_ = (newAfterBitmap.width * 0.9f).toInt(),
                        resizeType = 1
                    )
                } else newAfterBitmap

                while (bmp2?.canShow() == false) {
                    bmp2 = bmp2?.resizeBitmap(
                        height_ = (bmp2!!.height * 0.9f).toInt(),
                        width_ = (bmp2!!.width * 0.9f).toInt(),
                        resizeType = 1
                    )
                }
            }
            _rotation.value = 0f
            _bitmapData.value = (bmp1 to bmp2)
                .let { (b, a) ->
                    val (bW, bH) = b?.run { width to height } ?: (0 to 0)
                    val (aW, aH) = a?.run { width to height } ?: (0 to 0)

                    if (bW * bH > aH * aW) {
                        b to a?.resizeBitmap(bW, bH, 1)
                    } else if (bW * bH < aH * aW) {
                        b?.resizeBitmap(aW, aH, 1) to a
                    } else {
                        b to a
                    }
                }
            _isLoading.value = false
        }
    }

    fun rotate() {
        val old = _rotation.value
        _rotation.value = _rotation.value.let {
            if (it == 90f) 0f
            else 90f
        }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _bitmapData.value?.let { (f, s) ->
                    if (f != null && s != null) {
                        _isLoading.value = true
                        _bitmapData.value =
                            f.rotate(180f - old).rotate(rotation) to s.rotate(180f - old)
                                .rotate(rotation)
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun swap() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _isLoading.value = true
                _bitmapData.value = _bitmapData.value?.run { second to first }
                _isLoading.value = false
            }
        }
    }

    fun updateBitmapDataAsync(
        loader: suspend () -> Pair<Bitmap?, Bitmap?>,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val data = loader()
                if (data.first == null || data.second == null) onError()
                else {
                    _bitmapData.value = data
                    onSuccess()
                }
            }
        }
    }


}