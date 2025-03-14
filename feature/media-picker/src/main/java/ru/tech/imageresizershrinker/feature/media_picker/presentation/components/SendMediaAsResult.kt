/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.media_picker.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import ru.tech.imageresizershrinker.core.ui.utils.helper.toClipData

internal fun ComponentActivity.sendMediaAsResult(selectedMedia: List<Uri>) {
    val newIntent = Intent(
        if (selectedMedia.size == 1) Intent.ACTION_SEND
        else Intent.ACTION_SEND_MULTIPLE
    ).apply {
        if (selectedMedia.size == 1) {
            data = selectedMedia.first()
            clipData = selectedMedia.toClipData()
            putExtra(
                Intent.EXTRA_STREAM,
                selectedMedia.first()
            )
        } else {
            clipData = selectedMedia.toClipData()
            putParcelableArrayListExtra(
                Intent.EXTRA_STREAM,
                ArrayList(selectedMedia)
            )
        }
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    setResult(RESULT_OK, newIntent)

    finish()
}