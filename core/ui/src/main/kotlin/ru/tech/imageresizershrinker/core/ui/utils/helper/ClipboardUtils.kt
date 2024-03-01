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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService

@Composable
fun rememberClipboardData(): State<List<Uri>> {
    val context = LocalContext.current
    val clipboardManager = context.getSystemService<ClipboardManager>()

    val clip = remember {
        mutableStateOf(clipboardManager?.primaryClip?.clipList() ?: emptyList())
    }.apply {
        value = clipboardManager?.primaryClip?.clipList() ?: emptyList()
    }

    val callback = remember {
        ClipboardManager.OnPrimaryClipChangedListener {
            clip.value = clipboardManager?.primaryClip?.clipList() ?: emptyList()
        }
    }
    DisposableEffect(clipboardManager) {
        clipboardManager?.addPrimaryClipChangedListener(callback)
        onDispose {
            clipboardManager?.removePrimaryClipChangedListener(callback)
        }
    }
    return clip
}

fun ClipData.clipList() = List(
    size = itemCount,
    init = {
        getItemAt(it).uri
    }
).filterNotNull()

fun Uri.asClip(
    context: Context
): ClipEntry = ClipEntry(
    ClipData.newUri(
        context.contentResolver,
        "IMAGE",
        this
    )
)