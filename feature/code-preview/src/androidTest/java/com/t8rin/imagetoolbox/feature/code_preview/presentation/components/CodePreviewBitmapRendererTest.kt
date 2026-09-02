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

package com.t8rin.imagetoolbox.feature.code_preview.presentation.components

import android.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodePreviewParams
import com.t8rin.imagetoolbox.feature.code_preview.presentation.model.CodePreviewTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import androidx.compose.ui.graphics.Color as ComposeColor

@RunWith(AndroidJUnit4::class)
class CodePreviewBitmapRendererTest {

    @Test
    fun previewAndExportKeepTheSameLayout() {
        val code = CodePreviewParams.DefaultCode
        val highlightedCode = AnnotatedString.Builder(code).apply {
            addStyle(
                style = SpanStyle(color = ComposeColor.Magenta),
                start = 0,
                end = 3
            )
        }.toAnnotatedString()
        val params = CodePreviewParams.Default.copy(rotation = 12f)

        val preview = renderCodePreviewBitmap(
            params = params,
            highlightedCode = highlightedCode,
            maxBitmapPixels = 2_000_000f
        )
        val export = renderCodePreviewBitmap(params, highlightedCode)

        val previewRatio = preview.width.toFloat() / preview.height
        val exportRatio = export.width.toFloat() / export.height
        assertTrue(
            "Preview ratio $previewRatio differs from export ratio $exportRatio",
            abs(previewRatio - exportRatio) < 0.01f
        )
        preview.recycle()
        export.recycle()
    }

    @Test
    fun longCodeStaysInsideSafeBitmapLimits() {
        val code = (1..1200).joinToString(separator = "\n") { line ->
            "val renderedLine$line = renderCodePreview(line = $line)"
        }
        val bitmap = renderCodePreviewBitmap(
            params = CodePreviewParams.Default.copy(
                code = code,
                rotation = 15f,
                showCanvasBackground = false
            ),
            highlightedCode = AnnotatedString(code)
        )

        val dimensions = "${bitmap.width}x${bitmap.height}"
        assertTrue(dimensions, bitmap.height > 4096)
        assertTrue(
            dimensions,
            bitmap.width.toLong() * bitmap.height <= 65_545_216L
        )
        assertEquals(0, Color.alpha(bitmap.getPixel(0, 0)))
        bitmap.recycle()
    }

    @Test
    fun emptyTitleIsNotReplacedWithAPlaceholder() {
        val code = CodePreviewParams.DefaultCode
        val highlightedCode = AnnotatedString(code)
        val params = CodePreviewParams.Default.copy(title = "")

        val emptyTitle = renderCodePreviewBitmap(params, highlightedCode)
        val placeholderTitle = renderCodePreviewBitmap(
            params = params.copy(title = "Untitled.kt"),
            highlightedCode = highlightedCode
        )

        assertFalse(emptyTitle.sameAs(placeholderTitle))
        emptyTitle.recycle()
        placeholderTitle.recycle()
    }

    @Test
    fun allCodeThemesCanBeResolved() {
        assertEquals(256, CodePreviewTheme.entries.size)
        CodePreviewTheme.entries.forEach { theme ->
            assertFalse(theme.highlightTheme().backgroundColor == ComposeColor.Unspecified)
            assertFalse(theme.highlightTheme().defaultTextColor == ComposeColor.Unspecified)
        }
    }

    @Test
    fun highlightJsThemeFamiliesAreAvailable() {
        assertEquals("Dracula", CodePreviewTheme.Dracula.title)
        assertEquals("Tomorrow Night", CodePreviewTheme.TomorrowNight.title)

        val expectedThemes = setOf(
            "atom-one-dark",
            "github-dark",
            "night-owl",
            "base16/dracula",
            "base16/material-palenight",
            "base16/solarized-light",
            "base16/windows-95"
        )
        val actualThemes = CodePreviewTheme.entries.mapTo(mutableSetOf()) { it.assetName }

        assertTrue(
            "Missing highlight.js themes: ${expectedThemes - actualThemes}",
            actualThemes.containsAll(expectedThemes)
        )
    }

    @Test
    fun canvasPaddingCanBeCompletelyRemoved() {
        val code = CodePreviewParams.DefaultCode
        val highlightedCode = AnnotatedString(code)
        val withoutPadding = renderCodePreviewBitmap(
            params = CodePreviewParams.Default.copy(
                outerPadding = 0,
                showCardShadow = false
            ),
            highlightedCode = highlightedCode
        )
        val withPadding = renderCodePreviewBitmap(
            params = CodePreviewParams.Default.copy(
                outerPadding = 8,
                showCardShadow = false
            ),
            highlightedCode = highlightedCode
        )

        assertEquals(48, withPadding.width - withoutPadding.width)
        assertEquals(48, withPadding.height - withoutPadding.height)
        withoutPadding.recycle()
        withPadding.recycle()
    }

    @Test
    fun customShadowGetsEnoughRenderingSpace() {
        val code = CodePreviewParams.DefaultCode
        val highlightedCode = AnnotatedString(code)
        val withoutShadow = renderCodePreviewBitmap(
            params = CodePreviewParams.Default.copy(
                outerPadding = 0,
                showCardShadow = false
            ),
            highlightedCode = highlightedCode
        )
        val withShadow = renderCodePreviewBitmap(
            params = CodePreviewParams.Default.copy(
                outerPadding = 0,
                showCardShadow = true,
                cardShadowBlurRadius = 20,
                cardShadowOffsetX = 30,
                cardShadowOffsetY = -30
            ),
            highlightedCode = highlightedCode
        )

        assertEquals(300, withShadow.width - withoutShadow.width)
        assertEquals(300, withShadow.height - withoutShadow.height)
        withoutShadow.recycle()
        withShadow.recycle()
    }
}
