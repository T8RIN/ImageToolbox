package ru.tech.imageresizershrinker.compare_screen.viewModel

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.utils.BitmapUtils.canShow
import ru.tech.imageresizershrinker.utils.BitmapUtils.resizeBitmap
import ru.tech.imageresizershrinker.utils.BitmapUtils.rotate

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
                        resize = 1
                    )
                } else newBeforeBitmap

                while (bmp1?.canShow() == false) {
                    bmp1 = bmp1?.resizeBitmap(
                        height_ = (bmp1!!.height * 0.9f).toInt(),
                        width_ = (bmp1!!.width * 0.9f).toInt(),
                        resize = 1
                    )
                }
                ///
                bmp2 = if (newAfterBitmap?.canShow() == false) {
                    newAfterBitmap.resizeBitmap(
                        height_ = (newAfterBitmap.height * 0.9f).toInt(),
                        width_ = (newAfterBitmap.width * 0.9f).toInt(),
                        resize = 1
                    )
                } else newAfterBitmap

                while (bmp2?.canShow() == false) {
                    bmp2 = bmp2?.resizeBitmap(
                        height_ = (bmp2!!.height * 0.9f).toInt(),
                        width_ = (bmp2!!.width * 0.9f).toInt(),
                        resize = 1
                    )
                }
            }
            _rotation.value = 0f
            _bitmapData.value = bmp1 to bmp2
            _isLoading.value = false
        }
    }

    fun rotate() {
        val old = _rotation.value
        _rotation.value = _rotation.value.let {
            if (it == 90f) 0f
            else 90f
        }
        _bitmapData.value?.let { (f, s) ->
            if (f != null && s != null) {
                _bitmapData.value = f.rotate(180f-old).rotate(rotation) to s.rotate(180f - old).rotate(rotation)
            }
        }
    }

}