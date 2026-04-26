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

package com.t8rin.imagetoolbox.core.utils

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class LogsWriter(
    private val context: Application,
    private val fileProvider: String,
    private val logsFilename: String,
    isSyncCreate: Boolean,
    startupLog: Logger.Log = STARTUP_LOG,
    internal val errorHandler: (Throwable) -> Unit = {},
    private val logMapper: LogMapper = LogMapper.Default,
    private val maxFileSize: Int? = MAX_SIZE,
) {

    internal var logsFile: File? = null

    init {
        val create = suspend {
            if (logsFile != null) throw IllegalStateException("LogWriter must be initialized only once")

            logsFile = File(context.filesDir, logsFilename).apply {
                if (maxFileSize != null && length() > maxFileSize) {
                    var lineCount = 0
                    val lines = mutableListOf<String>()
                    withContext(Dispatchers.IO) {
                        coroutineScope {
                            bufferedReader().use { reader ->
                                while (reader.readLine() != null) {
                                    lineCount++
                                }
                            }
                        }
                        coroutineScope {
                            bufferedReader().use { reader ->
                                var tempLineCount = 0
                                repeat(lineCount) {
                                    tempLineCount++
                                    val line = reader.readLine()

                                    if (tempLineCount >= lineCount - 1000 && line != null) {
                                        lines.add(line)
                                    }
                                }
                            }
                            delete()
                            createNewFile()
                            writeData(this@apply) { writer ->
                                lines.forEach {
                                    writer.write(it)
                                }
                            }
                        }
                    }
                }
                if (!exists()) createNewFile()
            }
            writeData { writer ->
                writer.write(
                    logMapper.asMessage(startupLog)
                )
                writer.newLine()
            }
        }

        if (isSyncCreate) {
            runBlocking { create() }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                create()
            }
        }
    }

    private fun File.getUri(): Uri = FileProvider.getUriForFile(context, fileProvider, this)

    private fun writeData(
        file: File? = logsFile,
        use: (BufferedWriter) -> Unit
    ) {
        runCatching {
            FileOutputStream(file, true)
                .bufferedWriter()
                .use(use)
        }
    }

    fun shareLogs() {
        val sendIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(logsFile!!.getUri()))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            type = "text/plain"
        }
        val shareIntent =
            Intent.createChooser(sendIntent, "Share Logs")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareLogsViaEmail(email: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "vnd.android.cursor.dir/email"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_STREAM, logsFile!!.getUri())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(sendIntent)
    }

    fun writeLog(log: Logger.Log) {
        writeData { writer ->
            writer.write(
                logMapper.asMessage(log)
            )
            writer.newLine()
        }
    }

    companion object {
        internal val STARTUP_LOG = Logger.Log(
            tag = "Logger_Launch",
            message = "* Application Launched *",
            level = Logger.Level.Info
        )
        internal const val MAX_SIZE = 40 * 1024 * 1024 * 8
    }
}

internal class LogsWriterNotInitialized : Throwable()

fun interface LogMapper {
    fun asMessage(log: Logger.Log): String

    companion object {
        val Default by lazy {
            LogMapper { (tag, message, level) ->
                val timestamp =
                    SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault()).format(Date())

                "$timestamp ($tag) [$level]: $message"
            }
        }
    }
}