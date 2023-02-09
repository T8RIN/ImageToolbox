package ru.tech.imageresizershrinker

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.BitmapUtils.decodeBitmapFromUri
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

        installSplashScreen()
        super.onCreate(savedInstanceState)
        actionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ImageResizerShrinkerTheme {
                val focus = LocalFocusManager.current
                val toastHostState = rememberToastHostState()
                val scope = rememberCoroutineScope()
                var showDialog by remember { mutableStateOf(false) }
                var showResetDialog by remember { mutableStateOf(false) }
                var showSaveLoading by remember { mutableStateOf(false) }
                var showOriginal by remember { mutableStateOf(false) }
                val state = rememberLazyListState()

                val bitmapInfo = viewModel.bitmapInfo

                val pickImageLauncher =
                    rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri ->
                        uri?.let {
                            try {
                                decodeBitmapFromUri(it)?.let { bmp ->
                                    viewModel.updateBitmap(bmp)
                                }
                            } catch (e: Exception) {
                                scope.launch {
                                    toastHostState.showToast(
                                        "Something went wrong: ${e.localizedMessage}",
                                        Icons.Rounded.ErrorOutline
                                    )
                                }
                            }
                        }
                    }

                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    focus.clearFocus()
                                }
                            )
                        }
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Column(Modifier.fillMaxSize()) {
                            CenterAlignedTopAppBar(
                                title = {
                                    if (viewModel.bitmap == null) {
                                        Text("Image Resizer")
                                    } else if (!viewModel.isLoading) {
                                        Text("Size ${byteCount(bitmapInfo.size)}")
                                    } else {
                                        Text("Loading...")
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                        3.dp
                                    )
                                ),
                                actions = {
                                    val interactionSource = remember { MutableInteractionSource() }
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .indication(interactionSource, LocalIndication.current)
                                            .pointerInput(Unit) {
                                                detectTapGestures(
                                                    onPress = {
                                                        val press = PressInteraction.Press(it)
                                                        interactionSource.emit(press)
                                                        val pos =
                                                            state.firstVisibleItemIndex to state.firstVisibleItemScrollOffset
                                                        showOriginal = true
                                                        delay(100)
                                                        state.animateScrollToItem(
                                                            0,
                                                            10000
                                                        )
                                                        tryAwaitRelease()
                                                        showOriginal = false
                                                        interactionSource.emit(
                                                            PressInteraction.Release(
                                                                press
                                                            )
                                                        )
                                                        state.animateScrollToItem(
                                                            pos.first,
                                                            pos.second
                                                        )
                                                    },
                                                    onDoubleTap = {
                                                        showResetDialog = true
                                                    }
                                                )
                                            }

                                    ) {
                                        Icon(
                                            Icons.Default.History,
                                            null,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .padding(8.dp)
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
                                state = state,
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
                                                AnimatedContent(
                                                    targetState = showOriginal,
                                                    transitionSpec = {
                                                        fadeIn(tween(0)) with fadeOut(tween(0))
                                                    }
                                                ) { showOrig ->
                                                    if (showOrig) {
                                                        viewModel.bitmap?.asImageBitmap()?.let {
                                                            Image(
                                                                bitmap = it,
                                                                contentDescription = null,
                                                                modifier = Modifier.clip(
                                                                    RoundedCornerShape(4.dp)
                                                                )
                                                            )
                                                        }
                                                    } else {
                                                        bmp?.asImageBitmap()?.let {
                                                            Image(
                                                                bitmap = it,
                                                                contentDescription = null,
                                                                modifier = Modifier.clip(
                                                                    RoundedCornerShape(4.dp)
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                                if (loading) {
                                                    Box(
                                                        Modifier
                                                            .size(84.dp)
                                                            .clip(RoundedCornerShape(24.dp))
                                                            .shadow(8.dp, RoundedCornerShape(24.dp))
                                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                                            .align(Alignment.Center)
                                                    ) {
                                                        CircularProgressIndicator(
                                                            Modifier.align(
                                                                Alignment.Center
                                                            ),
                                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                        if (viewModel.previewBitmap != null) {
                                            Spacer(Modifier.size(20.dp))
                                            Row {
                                                SmallFloatingActionButton(
                                                    onClick = { viewModel.rotateLeft() },
                                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                                ) {
                                                    Icon(Icons.Default.RotateLeft, null)
                                                }

                                                SmallFloatingActionButton(
                                                    onClick = { viewModel.flip() },
                                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                                ) {
                                                    Icon(Icons.Default.Flip, null)
                                                }

                                                SmallFloatingActionButton(
                                                    onClick = { viewModel.rotateRight() },
                                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                                ) {
                                                    Icon(Icons.Default.RotateRight, null)
                                                }
                                            }
                                        } else if (!viewModel.isLoading) {
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
                                            RoundedTextField(
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
                                            RoundedTextField(
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
                                .navigationBarsPadding()
                        ) {
                            if (viewModel.bitmap != null) {
                                FloatingActionButton(
                                    onClick = {
                                        showSaveLoading = true
                                        viewModel.saveBitmap(
                                            isExternalStorageWritable = isExternalStorageWritable(),
                                            getFileOutputStream = { name, ext ->
                                                val contentValues = ContentValues().apply {
                                                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                                                    put(
                                                        MediaStore.MediaColumns.MIME_TYPE,
                                                        "image/$ext"
                                                    )
                                                    put(
                                                        MediaStore.MediaColumns.RELATIVE_PATH,
                                                        "DCIM/ResizedImages"
                                                    )
                                                }
                                                val imageUri = contentResolver.insert(
                                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    contentValues
                                                )
                                                contentResolver.openOutputStream(imageUri!!)
                                            }
                                        ) { success ->
                                            if (!success) requestPermission()
                                            else {
                                                scope.launch {
                                                    toastHostState.showToast(
                                                        "Saved to DCIM/ResizedImages folder",
                                                        Icons.Rounded.Save
                                                    )
                                                }
                                            }
                                            showSaveLoading = false
                                        }
                                    },
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
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

                        if (showSaveLoading) {
                            Dialog(onDismissRequest = { }) {
                                Box(Modifier.fillMaxSize()) {
                                    Box(
                                        Modifier
                                            .size(84.dp)
                                            .clip(RoundedCornerShape(24.dp))
                                            .shadow(8.dp, RoundedCornerShape(24.dp))
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .align(Alignment.Center)
                                    ) {
                                        CircularProgressIndicator(
                                            Modifier.align(
                                                Alignment.Center
                                            ),
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }
                        } else if (showResetDialog) {
                            AlertDialog(
                                icon = { Icon(Icons.Rounded.LockReset, null) },
                                title = { Text("Reset image") },
                                text = { Text("Image changes will be rollback to start values") },
                                onDismissRequest = { showResetDialog = false },
                                confirmButton = {
                                    TextButton(onClick = { showResetDialog = false }) {
                                        Text("Cancel")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        viewModel.resetValues()
                                        showResetDialog = false
                                        scope.launch {
                                            toastHostState.showToast(
                                                "Values properly reset",
                                                Icons.Rounded.DoneOutline
                                            )
                                        }
                                    }) {
                                        Text("Reset")
                                    }
                                }
                            )
                        }

                        ToastHost(hostState = toastHostState)

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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) true
        else ContextCompat.checkSelfPermission(
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
    var tempBytes = bytes
    if (-1000 < tempBytes && tempBytes < 1000) {
        return "$tempBytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (tempBytes <= -999950 || tempBytes >= 999950) {
        tempBytes /= 1000
        ci.next()
    }
    return java.lang.String.format(
        Locale.getDefault(),
        "%.1f %cB",
        tempBytes / 1000.0,
        ci.current()
    )
}
