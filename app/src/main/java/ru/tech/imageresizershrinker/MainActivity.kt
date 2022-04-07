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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import ru.tech.imageresizershrinker.ui.theme.ImageResizerShrinkerTheme
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageResizerShrinkerTheme {
                val ctx = LocalContext.current
                val bitmap = remember { mutableStateOf<Bitmap?>(null) }
                val launcher =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
                        uri?.let {
                            bitmap.value =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) decodeBitmap(
                                    createSource(ctx.contentResolver, it)
                                )
                                else getBitmap(ctx.contentResolver, it)
                        }
                    }
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(reverseLayout = true) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 40.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                bitmap.value?.let {
                                    Image(
                                        it.asImageBitmap(),
                                        null
                                    )
                                    Spacer(Modifier.size(40.dp))
                                }
                                Column {
                                    var width by rememberSaveable { mutableStateOf("") }
                                    var height by rememberSaveable { mutableStateOf("") }
                                    var quality by rememberSaveable { mutableStateOf("") }
                                    var mime by rememberSaveable { mutableStateOf(0) }

                                    TextField(
                                        value = width,
                                        onValueChange = { width = it },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Width") }
                                    )
                                    Spacer(Modifier.size(20.dp))
                                    TextField(
                                        value = height,
                                        onValueChange = { height = it },
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number
                                        ),
                                        label = { Text("Height") }
                                    )
                                    Spacer(Modifier.size(40.dp))
                                    TextField(
                                        value = quality,
                                        onValueChange = {
                                            quality =
                                                if ((it.toIntOrNull() ?: 0) > 100) "100" else it
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        label = { Text("Quality") }
                                    )
                                    Spacer(Modifier.size(20.dp))
                                    RadioGroup(
                                        options = listOf("PNG", "JPEG"),
                                        selectedOption = mime,
                                        onOptionSelected = { mime = it }
                                    )
                                    Spacer(Modifier.size(20.dp))
                                    TextButton(
                                        onClick = {
                                            bitmap.value?.let {
                                                bitmap.value = saveTempBitmap(
                                                    it,
                                                    quality.toIntOrNull(),
                                                    width.toIntOrNull(),
                                                    height.toIntOrNull(),
                                                    mime
                                                )
                                            }
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SAVED",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        },
                                        enabled = bitmap.value != null
                                    ) {
                                        Text("SAVE")
                                    }
                                }
                                Spacer(Modifier.size(20.dp))
                                FilledTonalButton(onClick = { launcher.launch("image/*") }) {
                                    Text("PICK IMAGE")
                                }
                                Spacer(Modifier.size(40.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveTempBitmap(
        bitmap: Bitmap,
        quality: Int?,
        width: Int?,
        height: Int?,
        mime: Int
    ): Bitmap? {
        return if (isExternalStorageWritable()) {
            saveImage(bitmap, quality, width, height, mime)
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

    private fun saveImage(b: Bitmap, quality: Int?, width: Int?, height: Int?, mime: Int): Bitmap {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val name = "ResizedImage$timeStamp.jpg"
        val bitmap = Bitmap.createScaledBitmap(
            b,
            width ?: b.width,
            height ?: b.height,
            false
        )
        val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
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
            val image = File(imagesDir, "$name.jpeg")
            FileOutputStream(image)
        }
        bitmap.compress(
            if (mime == 1) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
            quality ?: 100,
            fos
        )
        val out = ByteArrayOutputStream()
        bitmap.compress(
            if (mime == 1) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
            quality ?: 100, out
        )
        val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
        out.flush()
        out.close()
        fos!!.flush()
        fos.close()
        return decoded
    }
}

@ExperimentalMaterial3Api
@Composable
fun RadioGroup(
    options: List<String>?,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    options?.forEachIndexed { index, item ->
        Row(
            Modifier.padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = (index == selectedOption),
                onClick = { onOptionSelected(index) },
            )
            Text(item, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

