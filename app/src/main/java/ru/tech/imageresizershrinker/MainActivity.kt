package ru.tech.imageresizershrinker

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder.createSource
import android.graphics.ImageDecoder.decodeBitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.Images.Media.getBitmap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.text.isDigitsOnly
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.ui.theme.ImageResizerShrinkerTheme
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@Suppress("DEPRECATION")
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var width by rememberSaveable { mutableStateOf("") }
            var height by rememberSaveable { mutableStateOf("") }
            var quality by rememberSaveable { mutableStateOf(0f) }
            var mime by rememberSaveable { mutableStateOf(0) }
            var resize by rememberSaveable { mutableStateOf(0) }


            ImageResizerShrinkerTheme {
                val ctx = LocalContext.current

                val bitmap = remember { mutableStateOf<Bitmap?>(null) }
                when (val s = viewModel.globalBitmap.value) {
                    else -> bitmap.value = s
                }

                val scope = rememberCoroutineScope()

                val launcher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                        uri?.let {
                            val temp =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) decodeBitmap(
                                    createSource(ctx.contentResolver, it)
                                )
                                else getBitmap(ctx.contentResolver, it)

                            viewModel.globalBitmap.value =
                                if (temp.allocationByteCount < 100000000) {
                                    width = temp.width.toString()
                                    height = temp.height.toString()
                                    quality = 100f
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
                        contentPadding = PaddingValues(horizontal = 40.dp)
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
                                        ImageVector.vectorResource(id = R.drawable.ic_twotone_image_24),
                                        null,
                                        modifier = Modifier.size(100.dp)
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text("Pick image to start")
                                    Spacer(Modifier.size(20.dp))
                                }
                                Spacer(Modifier.size(40.dp))
                                Row {
                                    TextField(
                                        value = width,
                                        onValueChange = {
                                            width = it.restrict()
                                            checkBitmapAndUpdate(
                                                scope,
                                                bitmap,
                                                quality,
                                                width,
                                                height,
                                                mime,
                                                resize
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Width") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.size(20.dp))
                                    TextField(
                                        value = height,
                                        onValueChange = {
                                            height = it.restrict()
                                            checkBitmapAndUpdate(
                                                scope,
                                                bitmap,
                                                quality,
                                                width,
                                                height,
                                                mime,
                                                resize
                                            )
                                        },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Height") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                Spacer(Modifier.size(40.dp))

                                Text("Quality")
                                Slider(
                                    value = quality,
                                    onValueChange = {
                                        quality = it
                                        checkBitmapAndUpdate(
                                            scope,
                                            bitmap,
                                            quality,
                                            width,
                                            height,
                                            mime,
                                            resize
                                        )
                                    },
                                    valueRange = 0f..100f,
                                    steps = 100
                                )

                                Spacer(Modifier.size(20.dp))
                                Row {
                                    RadioGroup(
                                        title = "Extension",
                                        options = listOf("PNG", "JPEG"),
                                        selectedOption = mime,
                                        onOptionSelected = {
                                            mime = it
                                            checkBitmapAndUpdate(
                                                scope,
                                                bitmap,
                                                quality,
                                                width,
                                                height,
                                                mime,
                                                resize
                                            )
                                        }
                                    )

                                    Spacer(Modifier.weight(1f))

                                    RadioGroup(
                                        title = "Resize type",
                                        options = listOf("Explicit", "Flexible"),
                                        selectedOption = resize,
                                        onOptionSelected = {
                                            resize = it
                                            checkBitmapAndUpdate(
                                                scope,
                                                bitmap,
                                                quality,
                                                width,
                                                height,
                                                mime,
                                                resize
                                            )
                                        }
                                    )
                                }
                                Spacer(Modifier.size(20.dp))
                                Row {
                                    FilledTonalButton(
                                        onClick = {
                                            viewModel.globalBitmap.value?.let {
                                                saveBitmap(
                                                    it,
                                                    quality,
                                                    width.toIntOrNull(),
                                                    height.toIntOrNull(),
                                                    mime,
                                                    resize
                                                )?.let { bmp ->
                                                    viewModel.globalBitmap.value = bmp
                                                    Toast.makeText(
                                                        ctx,
                                                        "SAVED",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
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
                }
            }
        }
    }

    private fun saveBitmap(
        bitmap: Bitmap,
        quality: Float,
        widthValue: Int?,
        heightValue: Int?,
        mime: Int,
        resize: Int
    ): Bitmap? {
        return if (isExternalStorageWritable()) {
            val ext = if (mime == 1) "jpg" else "png"
            val explicit = resize == 0

            val tWidth = widthValue ?: bitmap.width
            val tHeight = heightValue ?: bitmap.height

            val timeStamp: String =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val name = "ResizedImage$timeStamp.$ext"
            val localBitmap = if (explicit) {
                Bitmap.createScaledBitmap(
                    bitmap,
                    tWidth,
                    tHeight,
                    false
                )
            } else {
                bitmap.resizeBitmap(max(tWidth, tHeight))
            }
            val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver: ContentResolver = contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/$ext")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/ResizedImages")
                }
                val imageUri =
                    resolver.insert(EXTERNAL_CONTENT_URI, contentValues)
                resolver.openOutputStream(imageUri!!)
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                ).toString() + File.separator + "ResizedImages"
                val file = File(imagesDir)
                if (!file.exists()) {
                    file.mkdir()
                }
                val image = File(imagesDir, "$name.$ext")
                FileOutputStream(image)
            }
            localBitmap.compress(
                if (mime == 1) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
                quality.toInt(),
                fos
            )
            val out = ByteArrayOutputStream()
            localBitmap.compress(
                if (mime == 1) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
                quality.toInt(), out
            )
            val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
            out.flush()
            out.close()
            fos!!.flush()
            fos.close()

            decoded
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
            null
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkBitmapAndUpdate(
        scope: CoroutineScope,
        bitmap: MutableState<Bitmap?>,
        quality: Float,
        width: String,
        height: String,
        mime: Int,
        resize: Int
    ) {
        scope.launch {
            viewModel.globalBitmap.value?.let { bmp ->
                bitmap.value = updatePreview(bmp, quality, width, height, mime, resize)
            }
        }
    }

    private suspend fun updatePreview(
        globalBitmap: Bitmap,
        quality: Float,
        width: String,
        height: String,
        mime: Int,
        resize: Int
    ): Bitmap = withContext(Dispatchers.IO) {
        globalBitmap.let {
            it.previewBitmap(
                quality,
                width.toIntOrNull(),
                height.toIntOrNull(),
                mime,
                resize
            ).let { bmp ->
                return@withContext bmp
            }
        }
    }

    private fun Bitmap.previewBitmap(
        quality: Float,
        widthValue: Int?,
        heightValue: Int?,
        mime: Int,
        resize: Int
    ): Bitmap {
        val out = ByteArrayOutputStream()
        val explicit = resize == 0
        val tWidth = widthValue ?: width
        val tHeight = heightValue ?: height

        if (explicit) {
            Bitmap.createScaledBitmap(
                this,
                tWidth,
                tHeight,
                false
            )
        } else {
            this.resizeBitmap(max(tWidth, tHeight))
        }.compress(
            if (mime == 1) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
            quality.toInt(), out
        )
        return BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
    }

    private fun String.restrict(): String {
        if (isEmpty()) return this

        return if ((this.toIntOrNull() ?: 0) > 4200) "4200"
        else if (this.isDigitsOnly() && (this.toIntOrNull() ?: 0) == 0) ""
        else filter {
            !listOf('-', '.', ',', ' ').contains(it)
        }
    }

    private fun Bitmap.resizeBitmap(maxLength: Int): Bitmap {
        return try {
            if (height >= width) {
                val aspectRatio = width.toDouble() / height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(this, targetWidth, maxLength, false)
            } else {
                val aspectRatio = height.toDouble() / width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(this, maxLength, targetHeight, false)
            }
        } catch (_: Exception) {
            this
        }
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

}

