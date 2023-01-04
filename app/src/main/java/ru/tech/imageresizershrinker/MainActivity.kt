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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.twotone.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import ru.tech.imageresizershrinker.MainViewModel.Companion.restrict
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.*

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            ImageResizerShrinkerTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(Modifier.fillMaxSize()) {
                        val ctx = LocalContext.current
                        var showDialog by remember { mutableStateOf(false) }

                        val bitmapInfo = viewModel.bitmapInfo

                        val pickImageLauncher =
                            rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.PickVisualMedia()
                            ) { uri ->
                                uri?.let {
                                    val bmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        decodeBitmap(createSource(contentResolver, uri))
                                    } else {
                                        @Suppress("DEPRECATION") getBitmap(contentResolver, uri)
                                    }
                                    if (bmp.allocationByteCount < 100000000) {
                                        viewModel.updateBitmap(bmp)
                                    } else {
                                        Toast.makeText(ctx, "TOO LARGE", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        Column(Modifier.fillMaxSize()) {
                            CenterAlignedTopAppBar(
                                title = {
                                    if (viewModel.bitmap == null) {
                                        Text("Image Resizer")
                                    } else {
                                        Text("Size ${byteCount(bitmapInfo.size)}")
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        3.dp
                                    )
                                ),
                                actions = {
                                    IconButton(onClick = {
                                        viewModel.resetValues()
                                        Toast
                                            .makeText(
                                                ctx,
                                                "Values properly reset",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }) {
                                        Icon(
                                            Icons.Outlined.SettingsBackupRestore,
                                            null
                                        )
                                    }
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { viewModel.setTelegramSpecs() }
                                    ) {
                                        Icon(
                                            Icons.Rounded.Send,
                                            null,
                                            modifier = Modifier
                                                .graphicsLayer(
                                                    translationX = 4f,
                                                    translationY = -2f
                                                )
                                                .rotate(-45f)
                                        )
                                    }
                                }
                            )
                            LazyColumn(
                                reverseLayout = true,
                                contentPadding = PaddingValues(
                                    top = WindowInsets.statusBars.asPaddingValues()
                                        .calculateBottomPadding(),
                                    bottom = WindowInsets.navigationBars.asPaddingValues()
                                        .calculateBottomPadding() + 108.dp,
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
                                        AnimatedContent(
                                            targetState = viewModel.previewBitmap to viewModel.isLoading,
                                            transitionSpec = { fadeIn() with fadeOut() }
                                        ) { pair ->
                                            val bmp = pair.first
                                            val loading = pair.second
                                            Box {
                                                bmp?.asImageBitmap()?.let { Image(it, null) }
                                                if (loading) {
                                                    Box(
                                                        Modifier
                                                            .size(84.dp)
                                                            .clip(RoundedCornerShape(24.dp))
                                                            .shadow(8.dp, RoundedCornerShape(24.dp))
                                                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                                                            .align(Alignment.Center)
                                                    ) {
                                                        CircularProgressIndicator(
                                                            Modifier.align(
                                                                Alignment.Center
                                                            ),
                                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        if (viewModel.previewBitmap != null) {
                                            Spacer(Modifier.size(20.dp))
                                            Row {
                                                SmallFloatingActionButton(
                                                    onClick = {
                                                        viewModel.rotateLeft()
                                                    },
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                                ) {
                                                    Icon(Icons.Default.RotateLeft, null)
                                                }

                                                SmallFloatingActionButton(
                                                    onClick = {
                                                        viewModel.flip()
                                                    },
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                                ) {
                                                    Icon(Icons.Default.Flip, null)
                                                }

                                                SmallFloatingActionButton(
                                                    onClick = {
                                                        viewModel.rotateRight()
                                                    },
                                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                                ) {
                                                    Icon(Icons.Default.RotateRight, null)
                                                }
                                            }
                                        } else {
                                            Spacer(Modifier.height(60.dp))
                                            Icon(
                                                Icons.TwoTone.Image,
                                                null,
                                                modifier = Modifier.size(100.dp)
                                            )
                                            Spacer(Modifier.height(10.dp))
                                            Text("Pick image to start")
                                        }
                                        Spacer(Modifier.size(30.dp))
                                        Row {
                                            OutlinedTextField(
                                                value = bitmapInfo.width,
                                                onValueChange = {
                                                    viewModel.updateWidth(it.restrict())
                                                },
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number
                                                ),
                                                label = { Text("Width ${viewModel.bitmap?.width ?: ""}") },
                                                modifier = Modifier.weight(1f)
                                            )
                                            Spacer(Modifier.size(20.dp))
                                            OutlinedTextField(
                                                value = bitmapInfo.height,
                                                onValueChange = {
                                                    viewModel.updateHeight(it.restrict())
                                                },
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number
                                                ),
                                                label = { Text("Height ${viewModel.bitmap?.height ?: ""}") },
                                                modifier = Modifier.weight(1f),
                                            )
                                        }
                                        Spacer(Modifier.size(40.dp))

                                        Text("Quality")
                                        Slider(
                                            value = bitmapInfo.quality,
                                            onValueChange = {
                                                viewModel.setQuality(it)
                                            },
                                            valueRange = 0f..100f,
                                            steps = 100
                                        )
                                        Spacer(Modifier.size(20.dp))

                                        Row {
                                            RadioGroup(
                                                title = "Extension",
                                                options = listOf("PNG", "WEBP", "JPEG"),
                                                selectedOption = bitmapInfo.mime,
                                                onOptionSelected = {
                                                    viewModel.setMime(it)
                                                }
                                            )

                                            Spacer(Modifier.weight(1f))

                                            RadioGroup(
                                                title = "Resize type",
                                                options = listOf("Explicit", "Flexible"),
                                                selectedOption = bitmapInfo.resizeType,
                                                onOptionSelected = {
                                                    viewModel.setResizeType(it)
                                                }
                                            )
                                        }

                                    }
                                }
                            }
                        }

                        Row(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        ) {
                            if (viewModel.bitmap != null) {
                                FloatingActionButton(
                                    onClick = {
                                        viewModel.bitmap?.let {
                                            viewModel.saveBitmap(
                                                it,
                                                isExternalStorageWritable(),
                                                contentResolver
                                            ) { success ->
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
                                    }
                                ) {
                                    Icon(Icons.Rounded.Save, null)
                                }
                                Spacer(Modifier.width(16.dp))
                            }
                            ExtendedFloatingActionButton(
                                onClick = {
                                    pickImageLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                text = {
                                    Text("Pick Image")
                                },
                                icon = {
                                    Icon(Icons.Rounded.AddPhotoAlternate, null)
                                }
                            )
                        }

                        if (showDialog) {
                            AlertDialog(
                                onDismissRequest = { showDialog = false },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            finishAffinity()
                                        }
                                    ) {
                                        Text("Close")
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showDialog = false }) {
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

                        BackHandler { showDialog = true }
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
    onOptionSelected: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.End) {
        Column {
            title?.let { Text(title) }
            options?.forEachIndexed { index, item ->
                Row(
                    Modifier.padding(top = 8.dp),
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
    }

}

@Stable
data class BitmapInfo(
    val width: String = "",
    val height: String = "",
    val quality: Float = 100f,
    val mime: Int = 0,
    val resizeType: Int = 0,
    val rotation: Float = 0f,
    val isFlipped: Boolean = false,
    val size: Int = 0
)

fun byteCount(bytes: Int): String {
    var bytes = bytes
    if (-1000 < bytes && bytes < 1000) {
        return "$bytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (bytes <= -999950 || bytes >= 999950) {
        bytes /= 1000
        ci.next()
    }
    return java.lang.String.format(Locale.getDefault(), "%.1f %cB", bytes / 1000.0, ci.current())
}
