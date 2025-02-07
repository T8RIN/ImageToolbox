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

package ru.tech.imageresizershrinker.feature.settings.data

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.PreferencesSerializer
import kotlinx.coroutines.coroutineScope
import okio.buffer
import okio.source
import ru.tech.imageresizershrinker.core.domain.utils.runSuspendCatching
import ru.tech.imageresizershrinker.core.resources.R
import java.io.ByteArrayInputStream
import java.io.File

internal suspend fun Context.restoreDatastore(
    fileName: String,
    backupUri: Uri,
    onFailure: (Throwable) -> Unit,
    onSuccess: suspend () -> Unit
) = coroutineScope {
    runSuspendCatching {
        contentResolver.openInputStream(backupUri)?.use { input ->
            val bytes = input.readBytes()
            restoreDatastore(
                fileName = fileName,
                backupData = bytes,
                onFailure = onFailure,
                onSuccess = onSuccess
            )
        }
    }.onFailure(onFailure).onSuccess {
        onSuccess()
    }
}

internal suspend fun Context.restoreDatastore(
    fileName: String,
    backupData: ByteArray,
    onFailure: (Throwable) -> Unit,
    onSuccess: suspend () -> Unit
) = coroutineScope {
    runSuspendCatching {

        runSuspendCatching {
            PreferencesSerializer.readFrom(ByteArrayInputStream(backupData).source().buffer())
        }.onFailure {
            onFailure(Throwable(getString(R.string.corrupted_file_or_not_a_backup)))
            return@coroutineScope
        }

        File(
            filesDir,
            "datastore/${fileName}.preferences_pb"
        ).outputStream().use {
            ByteArrayInputStream(backupData).copyTo(it)
        }
    }.onFailure(onFailure).onSuccess {
        onSuccess()
    }
}

internal suspend fun Context.obtainDatastoreData(
    fileName: String
) = coroutineScope {
    File(filesDir, "datastore/${fileName}.preferences_pb").readBytes()
}

internal suspend fun Context.resetDatastore(
    fileName: String
) = coroutineScope {
    File(
        filesDir,
        "datastore/${fileName}.preferences_pb"
    ).apply {
        delete()
        createNewFile()
    }
}