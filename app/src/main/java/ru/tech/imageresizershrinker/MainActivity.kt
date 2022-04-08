package ru.tech.imageresizershrinker

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageDecoder.createSource
import android.graphics.ImageDecoder.decodeBitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images.Media.getBitmap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material.icons.outlined.SettingsBackupRestore
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.MainViewModel.Companion.restrict

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContent {

            BackHandler { viewModel.showDialog = true }

            ImageResizerShrinkerTheme {
                val ctx = LocalContext.current

                val bitmap = remember { mutableStateOf(viewModel.globalBitmap.value) }
                when (val s = viewModel.globalBitmap.value) {
                    else -> bitmap.value = s
                }

                val launcher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                        uri?.let {
                            val temp =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) decodeBitmap(
                                    createSource(contentResolver, it)
                                )
                                else @Suppress("DEPRECATION") getBitmap(contentResolver, it)

                            viewModel.globalBitmap.value =
                                if (temp.allocationByteCount < 100000000) {
                                    viewModel.width = temp.width.toString()
                                    viewModel.height = temp.height.toString()
                                    viewModel.quality = 100f
                                    temp
                                } else {
                                    Toast.makeText(ctx, "TOO LARGE", Toast.LENGTH_SHORT).show()
                                    null
                                }
                        }
                    }

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        reverseLayout = true,
                        contentPadding = PaddingValues(
                            top = WindowInsets.statusBars.asPaddingValues()
                                .calculateBottomPadding(),
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding(),
                            start = 40.dp, end = 40.dp
                        )
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 40.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (bitmap.value != null) {
                                    bitmap.value?.asImageBitmap()?.let { Image(it, null) }
                                } else {
                                    Icon(
                                        Icons.TwoTone.Image,
                                        null,
                                        modifier = Modifier.size(100.dp)
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text("Pick image to start")
                                    Spacer(Modifier.size(20.dp))
                                }
                                Spacer(Modifier.size(40.dp))
                                if (viewModel.globalBitmap.value != null) {
                                    Row {
                                        SmallFloatingActionButton(onClick = {
                                            viewModel.rotation -= 90f
                                            viewModel.checkBitmapAndUpdate(bitmap)
                                        }) {
                                            Icon(Icons.Default.RotateLeft, null)
                                        }

                                        SmallFloatingActionButton(onClick = {
                                            viewModel.isFlipped = !viewModel.isFlipped
                                            viewModel.checkBitmapAndUpdate(bitmap)
                                        }) {
                                            Icon(Icons.Default.Flip, null)
                                        }

                                        SmallFloatingActionButton(onClick = {
                                            viewModel.rotation += 90f
                                            viewModel.checkBitmapAndUpdate(bitmap)
                                        }) {
                                            Icon(Icons.Default.RotateRight, null)
                                        }
                                    }
                                }
                                Spacer(Modifier.size(10.dp))
                                Row {
                                    TextField(
                                        value = viewModel.width,
                                        onValueChange = {
                                            viewModel.width = it.restrict()
                                            viewModel.checkBitmapAndUpdate(bitmap)
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Width ${viewModel.globalBitmap.value?.width ?: ""}") },
                                        modifier = Modifier.weight(1f),

                                        )
                                    Spacer(Modifier.size(20.dp))
                                    TextField(
                                        value = viewModel.height,
                                        onValueChange = {
                                            viewModel.height = it.restrict()
                                            viewModel.checkBitmapAndUpdate(bitmap)
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Height ${viewModel.globalBitmap.value?.height ?: ""}") },
                                        modifier = Modifier.weight(1f),
                                    )
                                }
                                Spacer(Modifier.size(40.dp))

                                Text("Quality")
                                Slider(
                                    value = viewModel.quality,
                                    onValueChange = {
                                        viewModel.quality = it
                                        viewModel.checkBitmapAndUpdate(bitmap)
                                    },
                                    valueRange = 0f..100f,
                                    steps = 100
                                )

                                Spacer(Modifier.size(20.dp))
                                Row {
                                    RadioGroup(
                                        title = "Extension",
                                        options = listOf("PNG", "WEBP", "JPEG"),
                                        selectedOption = viewModel.mime,
                                        onOptionSelected = {
                                            viewModel.mime = it
                                            viewModel.checkBitmapAndUpdate(bitmap)
                                        }
                                    )

                                    Spacer(Modifier.weight(1f))

                                    RadioGroup(
                                        title = "Resize type",
                                        options = listOf("Explicit", "Flexible"),
                                        selectedOption = viewModel.resize,
                                        onOptionSelected = {
                                            viewModel.resize = it
                                            viewModel.checkBitmapAndUpdate(bitmap)
                                        },
                                        outer = {
                                            if (viewModel.globalBitmap.value != null) {
                                                Spacer(Modifier.size(20.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .shadow(
                                                            6.dp,
                                                            shape = RoundedCornerShape(12.dp)
                                                        )
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                                        .combinedClickable(
                                                            onDoubleClick = {
                                                                viewModel.resetValues()
                                                                viewModel.checkBitmapAndUpdate(
                                                                    bitmap
                                                                )
                                                                Toast
                                                                    .makeText(
                                                                        ctx,
                                                                        "Values properly reset",
                                                                        Toast.LENGTH_SHORT
                                                                    )
                                                                    .show()
                                                            },
                                                            onClick = {
                                                                Toast
                                                                    .makeText(
                                                                        ctx,
                                                                        "Click twice to reset image state",
                                                                        Toast.LENGTH_SHORT
                                                                    )
                                                                    .show()
                                                            }
                                                        )
                                                ) {
                                                    Icon(
                                                        Icons.Outlined.SettingsBackupRestore,
                                                        null,
                                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                        modifier = Modifier
                                                            .align(Alignment.Center)
                                                            .padding(8.dp)
                                                    )
                                                }
                                            }
                                        }
                                    )
                                }
                                Spacer(Modifier.size(50.dp))
                                Row {
                                    FilledTonalButton(
                                        onClick = {
                                            lifecycleScope.launch {
                                                viewModel.globalBitmap.value?.let {
                                                    val success = viewModel.saveBitmap(
                                                        it,
                                                        isExternalStorageWritable(),
                                                        contentResolver
                                                    )
                                                    if (!success) requestPermission()
                                                    else {
                                                        Toast.makeText(
                                                            ctx,
                                                            "SAVED",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                        },
                                        enabled = viewModel.globalBitmap.value != null
                                    ) {
                                        Text("SAVE")
                                    }
                                    Spacer(Modifier.weight(1f))
                                    ElevatedButton(onClick = { launcher.launch("image/*") }) {
                                        Text("PICK IMAGE")
                                    }
                                }
                                Spacer(Modifier.size(40.dp))
                            }
                        }
                    }

                    if (viewModel.showDialog) {
                        AlertDialog(
                            onDismissRequest = { viewModel.showDialog = false },
                            dismissButton = {
                                TextButton(onClick = {
                                    viewModel.showDialog = false
                                    finishAffinity()
                                }) {
                                    Text("Close")
                                }
                            },
                            confirmButton = {
                                TextButton(onClick = { viewModel.showDialog = false }) {
                                    Text("Stay")
                                }
                            },
                            title = { Text("App closing") },
                            text = {
                                Text(
                                    "Are you really want to close the app, unsaved changes in the current image will be ignored",
                                    textAlign = TextAlign.Center
                                )
                            },
                            icon = { Icon(Icons.Outlined.DoorBack, null) }
                        )
                    }
                }
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun isExternalStorageWritable(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@ExperimentalMaterial3Api
@Composable
fun RadioGroup(
    title: String?,
    options: List<String>?,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit,
    outer: @Composable (() -> Unit)? = null
) {
    Column(horizontalAlignment = Alignment.End) {
        Column {
            title?.let { Text(title) }
            options?.forEachIndexed { index, item ->
                Row(
                    Modifier.padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (index == selectedOption),
                        onClick = { onOptionSelected(index) },
                    )
                    Text(item)
                }
            }
        }
        outer?.invoke()
    }

}

