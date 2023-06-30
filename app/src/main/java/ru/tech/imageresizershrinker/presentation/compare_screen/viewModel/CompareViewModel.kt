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
import ru.tech.imageresizershrinker.domain.model.ResizeType
import ru.tech.imageresizershrinker.core.android.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.core.android.BitmapUtils.rotate
import ru.tech.imageresizershrinker.core.android.BitmapUtils.scaleUntilCanShow

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
                bmp1 = newBeforeBitmap?.scaleUntilCanShow()
                bmp2 = newAfterBitmap?.scaleUntilCanShow()
            }
            _rotation.value = 0f
            _bitmapData.value = (bmp1 to bmp2)
                .let { (b, a) ->
                    val (bW, bH) = b?.run { width to height } ?: (0 to 0)
                    val (aW, aH) = a?.run { width to height } ?: (0 to 0)

                    if (bW * bH > aH * aW) {
                        b to a?.resizeBitmap(bW, bH, ResizeType.Flexible)
                    } else if (bW * bH < aH * aW) {
                        b?.resizeBitmap(aW, aH, ResizeType.Flexible) to a
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