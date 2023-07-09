package ru.tech.imageresizershrinker.presentation.pick_color_from_image_screen.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.AndroidEntryPoint
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.image.ImageManager
import javax.inject.Inject

@AndroidEntryPoint
class ScreenshotLauncher : AppCompatActivity() {

    @Inject
    lateinit var imageManager: ImageManager<Bitmap, ExifInterface>

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = projectionManager.createScreenCaptureIntent()

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val resultCode = it.resultCode
            val data = it.data
            if (resultCode == RESULT_OK) {
                val serviceIntent = Intent(this, ScreenshotService::class.java).apply {
                    putExtra("data", data)
                    putExtra("resultCode", resultCode)
                    putExtra("screen", intent.getStringExtra("screen"))
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }

            } else Toast.makeText(
                applicationContext,
                R.string.something_went_wrong,
                Toast.LENGTH_LONG
            ).show()
            finish()
        }.launch(captureIntent)
    }
}