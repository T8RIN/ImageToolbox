package ru.tech.imageresizershrinker

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val globalBitmap = mutableStateOf<Bitmap?>(null)

}