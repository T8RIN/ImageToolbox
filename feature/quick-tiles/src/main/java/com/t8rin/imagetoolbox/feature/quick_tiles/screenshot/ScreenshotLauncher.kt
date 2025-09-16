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

package com.t8rin.imagetoolbox.feature.quick_tiles.screenshot

import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.buildIntent
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getScreenExtra
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.postToast
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.putScreenExtra

class ScreenshotLauncher : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val projectionManager = getSystemService<MediaProjectionManager>()
        val captureIntent = projectionManager?.createScreenCaptureIntent()

        if (captureIntent == null) {
            onFailure(NullPointerException("No projection manager"))
        }

        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            runCatching {
                val resultCode = result.resultCode
                val data = result.data
                if (resultCode == RESULT_OK) {
                    val serviceIntent = buildIntent(ScreenshotService::class.java) {
                        putExtra(DATA_EXTRA, data)
                        putExtra(RESULT_CODE_EXTRA, resultCode)
                        action = intent.action
                        putScreenExtra(intent.getScreenExtra())
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent)
                    } else {
                        startService(serviceIntent)
                    }
                    finish()
                } else throw SecurityException()
            }.onFailure(::onFailure)
        }.launch(captureIntent!!)
    }

    private fun onFailure(throwable: Throwable) {
        postToast(
            textRes = R.string.smth_went_wrong,
            isLong = true,
            throwable.localizedMessage ?: ""
        )
        finish()
    }

}