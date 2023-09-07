package ru.tech.imageresizershrinker.presentation.root.widget.other

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = { /*TODO: ADD SAVE STOPPING */ }) {
        Box(Modifier.fillMaxSize()) { Loading() }
    }
    KeepScreenOn()
}

@Composable
fun LoadingDialog(done: Int, left: Int) {
    Dialog(onDismissRequest = { /*TODO: ADD SAVE STOPPING */ }) {
        Box(Modifier.fillMaxSize()) {
            Loading(done, left)
        }
    }
    KeepScreenOn()
}