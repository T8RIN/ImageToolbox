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

package com.t8rin.imagetoolbox.feature.load_net_image.data

import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.reflect.Proxy
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class HtmlImageParserTest {
    @Test
    fun directImageIsFetchedOnceAndOriginalBytesArePreserved() = runBlocking {
        val svg =
            "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"4\" height=\"4\"><rect width=\"4\" height=\"4\" fill=\"red\"/></svg>".toByteArray()
        Server("image/svg+xml", svg).use { server ->
            val bytes = ByteArrayOutputStream()
            val share = proxy<ImageShareProvider<Bitmap>> { name, args ->
                check(name == "cacheDataOrThrow")
                @Suppress("UNCHECKED_CAST")
                val writer = args[1] as suspend (Writeable) -> Unit
                val output = object : Writeable {
                    override fun writeBytes(byteArray: ByteArray) {
                        bytes.write(byteArray)
                    }

                    override fun close() = Unit
                }
                runBlocking { writer(output) }
                "content://cached/image.svg"
            }
            val getter =
                proxy<ImageGetter<Bitmap>> { _, _ -> error("Image was decoded during discovery") }
            val result =
                parser(share, getter).parseImagesSrc(server.url + "/image?token=abc") { error(it) }
            assertEquals(listOf("content://cached/image.svg"), result)
            assertArrayEquals(svg, bytes.toByteArray())
            assertEquals(1, server.requests.get())
        }
    }

    @Test
    fun htmlIsFetchedOnceAndRelativeSignedUrlsArePreserved() = runBlocking {
        val html =
            "<html><head><meta property=\"og:image\" content=\"/cover.png?token=123\"></head><body><img src=\"photo.jpg?width=640&amp;token=abc\"></body></html>"
        Server("text/html", html.toByteArray()).use { server ->
            val getter = proxy<ImageGetter<Bitmap>> { _, _ -> null }
            val result = parser(proxy { _, _ -> error("Unexpected image cache") }, getter)
                .parseImagesSrc(server.url + "/album/index.html") { error(it) }
            assertEquals(
                listOf(
                    server.url + "/cover.png?token=123",
                    server.url + "/album/photo.jpg?width=640&token=abc"
                ), result
            )
            assertEquals(1, server.requests.get())
        }
    }

    @Test
    fun htmlKeepsTheExistingResponseLimit() = runBlocking {
        val html = "<html><body><img src='/first.png'>" + " ".repeat(2 * 1024 * 1024) +
                "<img src='/after-limit.png'></body></html>"
        Server("text/html", html.toByteArray()).use { server ->
            val getter = proxy<ImageGetter<Bitmap>> { _, _ -> null }
            val result = parser(proxy { _, _ -> error("Unexpected image cache") }, getter)
                .parseImagesSrc(server.url + "/page") { error(it) }
            assertEquals(listOf(server.url + "/first.png"), result)
            assertEquals(1, server.requests.get())
        }
    }

    @Test
    fun unknownMimeTypeStillDecodesTheDownloadedBytes() = runBlocking {
        val image = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
        val bytes = ByteArrayOutputStream().also {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }.toByteArray()
        try {
            Server("application/octet-stream", bytes).use { server ->
                var decodes = 0
                val getter = proxy<ImageGetter<Bitmap>> { name, args ->
                    check(name == "getImage")
                    assertArrayEquals(bytes, args[0] as ByteArray)
                    decodes++
                    image
                }
                val share = proxy<ImageShareProvider<Bitmap>> { name, args ->
                    check(name == "cacheImage")
                    assertSame(image, args[0])
                    "content://cached/unknown.png"
                }
                val result =
                    parser(share, getter).parseImagesSrc(server.url + "/download") { error(it) }
                assertEquals(listOf("content://cached/unknown.png"), result)
                assertEquals(1, decodes)
                assertEquals(1, server.requests.get())
            }
        } finally {
            image.recycle()
        }
    }

    @Test
    fun httpFailureKeepsTheImageLoaderFallback() = runBlocking {
        val image = Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888)
        try {
            Server("text/html", ByteArray(0), status = "403 Forbidden").use { server ->
                val url = server.url + "/cached-image?token=abc"
                val getter = proxy<ImageGetter<Bitmap>> { name, args ->
                    check(name == "getImage")
                    assertEquals(url, args[0])
                    image
                }
                val share = proxy<ImageShareProvider<Bitmap>> { name, _ ->
                    check(name == "cacheImage")
                    "content://cached/fallback.png"
                }
                assertEquals(
                    listOf("content://cached/fallback.png"),
                    parser(share, getter).parseImagesSrc(url) { error(it) })
                assertEquals(1, server.requests.get())
            }
        } finally {
            image.recycle()
        }
    }

    private fun parser(share: ImageShareProvider<Bitmap>, getter: ImageGetter<Bitmap>) =
        AndroidHtmlImageParser(
            InstrumentationRegistry.getInstrumentation().targetContext,
            share,
            getter,
            Dispatchers
        )

    private class Server(
        contentType: String,
        body: ByteArray,
        status: String = "200 OK"
    ) : AutoCloseable {
        private val socket = ServerSocket(0, 8, InetAddress.getByName("127.0.0.1"))
        val url = "http://127.0.0.1:${socket.localPort}"
        val requests = AtomicInteger()
        private val worker = thread(isDaemon = true) {
            while (!socket.isClosed) {
                val client = runCatching { socket.accept() }.getOrNull() ?: break
                try {
                    client.use {
                        val reader = it.getInputStream().bufferedReader()
                        while (!reader.readLine().isNullOrEmpty()) Unit
                        requests.incrementAndGet()
                        val output = it.getOutputStream()
                        output.write("HTTP/1.1 $status\r\nContent-Type: $contentType\r\nContent-Length: ${body.size}\r\nConnection: close\r\n\r\n".toByteArray())
                        output.write(body)
                        output.flush()
                    }
                } catch (_: IOException) {
                    // The client may stop after reaching its response size limit.
                }
            }
        }

        override fun close() {
            socket.close(); worker.join(1000)
        }
    }

    private inline fun <reified T> proxy(crossinline call: (String, Array<out Any?>) -> Any?): T =
        Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java)
        ) { _, method, args ->
            call(method.name, args.orEmpty())
        } as T

    private object Dispatchers : DispatchersHolder {
        override val uiDispatcher = kotlinx.coroutines.Dispatchers.Main
        override val ioDispatcher = kotlinx.coroutines.Dispatchers.IO
        override val encodingDispatcher = kotlinx.coroutines.Dispatchers.Default
        override val decodingDispatcher = kotlinx.coroutines.Dispatchers.Default
        override val defaultDispatcher = kotlinx.coroutines.Dispatchers.Default
    }
}