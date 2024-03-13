/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.quick_tiles

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ru.tech.imageresizershrinker.core.resources.R

class ScreenshotLauncher : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val projectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = projectionManager.createScreenCaptureIntent()

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            runCatching {
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
                    finish()
                } else throw SecurityException()
            }.onFailure {
                Toast.makeText(
                    applicationContext,
                    R.string.something_went_wrong,
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }.launch(captureIntent)
    }
}