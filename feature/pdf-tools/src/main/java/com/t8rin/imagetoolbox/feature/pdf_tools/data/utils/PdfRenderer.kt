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

package com.t8rin.imagetoolbox.feature.pdf_tools.data.utils

import android.graphics.Bitmap
import android.graphics.Color.WHITE
import android.graphics.pdf.LoadParams
import android.graphics.pdf.PdfRendererPreV
import android.graphics.pdf.RenderParams
import android.os.Build
import android.os.ext.SdkExtensions
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresExtension
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.getString
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.encryption.InvalidPasswordException
import com.tom_roush.pdfbox.rendering.PDFRenderer
import java.lang.AutoCloseable
import kotlin.math.roundToInt
import android.graphics.Matrix as AndroidMatrix
import android.graphics.pdf.PdfRenderer as AndroidPdfRenderer

class PdfRenderer private constructor(
    private val backend: Backend
) : AutoCloseable {

    val pageCount: Int get() = backend.pageCount

    fun openPage(index: Int): Page = backend.openPage(index)

    fun renderImage(
        pageIndex: Int,
        scale: Float
    ): Bitmap = backend.renderImage(pageIndex, scale)

    override fun close() = backend.close()

    class Page(
        val width: Int,
        val height: Int
    ) {
        val size = IntegerSize(width, height)
    }

    private sealed interface Backend : AutoCloseable {
        val pageCount: Int

        fun openPage(index: Int): Page

        fun renderImage(
            pageIndex: Int,
            scale: Float
        ): Bitmap
    }

    private class AndroidBackend(
        private val renderer: AndroidPdfRenderer
    ) : Backend {
        override val pageCount: Int get() = renderer.pageCount

        override fun openPage(index: Int): Page = renderer.openPage(index).use { page ->
            Page(
                width = page.width,
                height = page.height
            )
        }

        override fun renderImage(
            pageIndex: Int,
            scale: Float
        ): Bitmap = renderer.openPage(pageIndex).use { page ->
            createBitmap(
                (page.width * scale).toInt().coerceAtLeast(1),
                (page.height * scale).toInt().coerceAtLeast(1)
            ).apply {
                eraseColor(WHITE)
                page.render(
                    this,
                    null,
                    AndroidMatrix().apply { setScale(scale, scale) },
                    AndroidPdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
            }
        }

        override fun close() = renderer.close()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    private class AndroidPreVBackend(
        private val renderer: PdfRendererPreV
    ) : Backend {
        private val renderParams = RenderParams.Builder(RenderParams.RENDER_MODE_FOR_DISPLAY)
            .build()

        override val pageCount: Int get() = renderer.pageCount

        override fun openPage(index: Int): Page = renderer.openPage(index).use { page ->
            Page(
                width = page.width,
                height = page.height
            )
        }

        override fun renderImage(
            pageIndex: Int,
            scale: Float
        ): Bitmap = renderer.openPage(pageIndex).use { page ->
            createBitmap(
                (page.width * scale).toInt().coerceAtLeast(1),
                (page.height * scale).toInt().coerceAtLeast(1)
            ).apply {
                eraseColor(WHITE)
                page.render(
                    this,
                    null,
                    AndroidMatrix().apply { setScale(scale, scale) },
                    renderParams
                )
            }
        }

        override fun close() = renderer.close()
    }

    private class ApacheBackend(
        val document: PDDocument
    ) : Backend {
        private val renderer = PDFRenderer(document)

        override val pageCount: Int get() = document.numberOfPages

        override fun openPage(index: Int): Page = document.getPage(index).let { page ->
            page.cropBox.run {
                Page(
                    width = width.roundToInt(),
                    height = height.roundToInt()
                )
            }
        }

        override fun renderImage(
            pageIndex: Int,
            scale: Float
        ): Bitmap = renderer.renderImage(pageIndex, scale)

        override fun close() = document.close()
    }

    companion object {
        fun create(
            uri: String,
            password: String?
        ): PdfRenderer = runCatching {
            createAndroidRenderer(uri, password)
        }.getOrElse {
            PdfRenderer(
                ApacheBackend(
                    safeOpenPdf(
                        uri = uri,
                        password = password
                    )
                )
            )
        }

        private fun createAndroidRenderer(
            uri: String,
            password: String?
        ): PdfRenderer {
            val passwordOrNull = password?.takeIf { it.isNotEmpty() }

            return if (passwordOrNull == null) {
                createAndroidRenderer(uri)
            } else if (canUseNewPdfFully()) {
                createAndroidPreVRenderer(uri, passwordOrNull)
            } else {
                error(getString(R.string.something_went_wrong))
            }
        }

        private fun createAndroidRenderer(uri: String): PdfRenderer {
            if (!canUseNewPdf()) error(getString(R.string.something_went_wrong))

            val descriptor = appContext.contentResolver.openFileDescriptor(
                uri.toUri(),
                "r"
            ) ?: error(getString(R.string.something_went_wrong))

            return try {
                PdfRenderer(AndroidBackend(AndroidPdfRenderer(descriptor)))
            } catch (throwable: Throwable) {
                descriptor.close()
                throw throwable
            }
        }

        @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
        private fun createAndroidPreVRenderer(
            uri: String,
            password: String
        ): PdfRenderer {
            val descriptor = appContext.contentResolver.openFileDescriptor(
                uri.toUri(),
                "r"
            ) ?: error(getString(R.string.something_went_wrong))

            return try {
                PdfRenderer(
                    AndroidPreVBackend(
                        PdfRendererPreV(
                            descriptor,
                            LoadParams.Builder()
                                .setPassword(password)
                                .build()
                        )
                    )
                )
            } catch (throwable: Throwable) {
                descriptor.close()
                throw throwable
            }
        }
    }
}

fun PdfRenderer(
    uri: String,
    password: String? = null,
    onFailure: (Throwable) -> Unit = {},
    onPasswordRequest: (() -> Unit)? = null
): PdfRenderer? = runCatching {
    PdfRenderer.create(
        uri = uri,
        password = password
    )
}.onFailure { throwable ->
    when (throwable) {
        is InvalidPasswordException -> onPasswordRequest?.invoke() ?: onFailure(throwable)
        else -> onFailure(throwable)
    }
}.getOrNull()


@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.P)
fun canUseNewPdf(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

@ChecksSdkIntAtLeast(api = 13, extension = Build.VERSION_CODES.S)
fun canUseNewPdfFully(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM
        || Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.S) >= 13