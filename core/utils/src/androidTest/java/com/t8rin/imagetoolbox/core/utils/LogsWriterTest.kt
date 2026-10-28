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
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class LogsWriterTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val directory = File(context.filesDir, "logs_writer_${System.nanoTime()}").apply {
        check(mkdirs())
    }
    private val file = File(directory, "logs.txt")

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun fileRemainsBoundedWhileApplicationIsRunning() {
        val writer = writer()
        repeat(100) { index ->
            writer.writeLog(log("$index: ${"message ".repeat(40)}"))
            assertTrue("Log exceeded its limit after entry $index", file.length() <= LIMIT)
        }
        assertTrue(file.readText().contains("99: message"))
    }

    @Test(timeout = 10_000)
    fun oversizedExistingFileIsTrimmedWithoutReadingItAll() {
        RandomAccessFile(file, "rw").use {
            val tail = "\nolder entry\nrecent entry\n".toByteArray()
            val length = 11L * 1024 * 1024 * 1024
            it.setLength(length)
            it.seek(length - tail.size)
            it.write(tail)
        }

        writer()

        assertTrue(file.length() <= LIMIT)
        assertTrue(file.readText().contains("recent entry"))
        assertTrue(file.readText().endsWith("startup\n"))
    }

    @Test
    fun concurrentWritersKeepValidUtf8AndRespectTheLimit() {
        val writer = writer()
        val start = CountDownLatch(1)
        val workers = List(8) { index ->
            thread {
                start.await()
                repeat(100) {
                    writer.writeLog(log("$index/$it: ${"Текст 🌻 ".repeat(20)}"))
                }
            }
        }
        start.countDown()
        repeat(100) {
            writer.readLogs()
            writer.readLogLineReferences().lines.firstOrNull()?.let(writer::readLogLine)
        }
        workers.forEach { it.join(10_000) }
        assertTrue(workers.none(Thread::isAlive))
        writer.writeLog(log("Последняя запись 🌻"))

        assertTrue(file.length() <= LIMIT)
        val text = Charsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .decode(ByteBuffer.wrap(file.readBytes()))
            .toString()
        assertTrue(text.endsWith("Последняя запись 🌻\n"))
    }

    @Test
    fun oversizedSingleEntryCannotLeaveAnOversizedFile() {
        val writer = writer()
        writer.writeLog(log("🌻".repeat(LIMIT)))
        assertTrue(file.length() <= LIMIT)

        writer.writeLog(log("new entry"))
        assertTrue(file.readText().endsWith("new entry\n"))
    }

    @Test
    fun nullLimitKeepsAllEntries() {
        val writer = writer(limit = null)
        val message = "message ".repeat(LIMIT)
        writer.writeLog(log(message))

        assertTrue(file.length() > LIMIT)
        assertTrue(file.readText().endsWith("$message\n"))
    }

    private fun writer(limit: Int? = LIMIT) = LogsWriter(
        context = context,
        fileProvider = "unused",
        logsFilename = "${directory.name}/${file.name}",
        isSyncCreate = true,
        startupLog = log("startup"),
        logMapper = { it.message },
        maxFileSize = limit
    )

    private fun log(message: String) = Logger.Log(
        tag = "test",
        message = message,
        level = Logger.Level.Debug
    )

    private companion object {
        const val LIMIT = 4096
    }
}