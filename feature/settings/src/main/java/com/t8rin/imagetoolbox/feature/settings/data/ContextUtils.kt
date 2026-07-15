/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.settings.data

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.getSystemService
import androidx.datastore.preferences.core.PreferencesSerializer
import com.t8rin.imagetoolbox.core.domain.model.PerformanceClass
import com.t8rin.imagetoolbox.core.domain.utils.FileMode
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.resources.R
import kotlinx.coroutines.coroutineScope
import okio.buffer
import okio.source
import java.io.ByteArrayInputStream
import java.io.File
import java.io.RandomAccessFile
import java.util.Locale
import kotlin.math.ceil

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

internal val Context.performanceClass: PerformanceClass
    get() {
        cachedPerformanceClass?.let { return it }

        val androidVersion = Build.VERSION.SDK_INT
        val cpuCount = Runtime.getRuntime().availableProcessors()
        val activityManager = getSystemService<ActivityManager>()!!
        val memoryClass = activityManager.memoryClass
        val socModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Build.SOC_MODEL
        } else null

        if (socModel?.uppercase()?.hashCode()?.let { it in LOW_SOC } == true) {
            return PerformanceClass.Low.also { cachedPerformanceClass = it }
        }

        var totalCpuFreq = 0
        var freqResolved = 0
        for (i in 0 until cpuCount) {
            runCatching {
                val reader = RandomAccessFile(
                    String.format(
                        Locale.ENGLISH,
                        "/sys/devices/system/cpu/cpu%d/cpufreq/cpuinfo_max_freq",
                        i
                    ), FileMode.Read.mode
                )
                val line = reader.readLine()
                if (line != null) {
                    totalCpuFreq += line.toInt() / 1000
                    freqResolved++
                }
                reader.close()
            }
        }
        val maxCpuFreq =
            if (freqResolved == 0) -1 else ceil((totalCpuFreq / freqResolved.toFloat()).toDouble())
                .toInt()
        val ram = runCatching {
            ActivityManager.MemoryInfo()
                .also(activityManager::getMemoryInfo)
                .totalMem
        }.getOrDefault(-1L)

        return if (androidVersion < 21 || cpuCount <= 2 || memoryClass <= 100 || cpuCount <= 4 && maxCpuFreq != -1 && maxCpuFreq <= 1250 || cpuCount <= 4 && maxCpuFreq <= 1600 && memoryClass <= 128 && androidVersion <= 21 || cpuCount <= 4 && maxCpuFreq <= 1300 && memoryClass <= 128 && androidVersion <= 24 || ram != -1L && ram < 2L * 1024L * 1024L * 1024L) {
            PerformanceClass.Low
        } else if (cpuCount < 8 || memoryClass <= 160 || maxCpuFreq != -1 && maxCpuFreq <= 2055 || maxCpuFreq == -1 && cpuCount == 8 && androidVersion <= 23) {
            PerformanceClass.Average
        } else {
            PerformanceClass.High
        }
    }

private val LOW_SOC = intArrayOf(
    -1775228513,
    802464304,
    802464333,
    802464302,
    2067362118,
    2067362060,
    2067362084,
    2067362241,
    2067362117,
    2067361998,
    -1853602818
)
private var cachedPerformanceClass: PerformanceClass? = null