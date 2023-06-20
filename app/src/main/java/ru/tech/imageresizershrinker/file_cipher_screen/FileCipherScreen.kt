package ru.tech.imageresizershrinker.file_cipher_screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.tech.imageresizershrinker.file_cipher_screen.viewModel.FileCipherViewModel
import ru.tech.imageresizershrinker.utils.storage.LocalFileController

@Composable
fun FileCipherScreen(
    uriState: Uri?,
    onGoBack: () -> Unit,
    viewModel: FileCipherViewModel = viewModel()
) {
    val context = LocalContext.current
    val fileController = LocalFileController.current

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*"),
        onResult = {

        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.setUri(it)
            }
        }
    )


}