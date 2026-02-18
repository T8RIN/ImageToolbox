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

package com.t8rin.imagetoolbox.feature.pdf_tools.data

import android.graphics.Path
import android.graphics.PointF
import com.tom_roush.pdfbox.contentstream.PDFGraphicsStreamEngine
import com.tom_roush.pdfbox.contentstream.operator.Operator
import com.tom_roush.pdfbox.cos.COSBase
import com.tom_roush.pdfbox.cos.COSName
import com.tom_roush.pdfbox.pdfwriter.ContentStreamWriter
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.PDPage
import com.tom_roush.pdfbox.pdmodel.common.PDStream
import com.tom_roush.pdfbox.pdmodel.font.PDFont
import com.tom_roush.pdfbox.pdmodel.graphics.form.PDFormXObject
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImage
import com.tom_roush.pdfbox.util.Matrix
import com.tom_roush.pdfbox.util.Vector
import java.io.IOException
import java.io.OutputStream


internal open class PdfContentStreamEditor(val document: PDDocument, page: PDPage?) :
    PDFGraphicsStreamEngine(page) {
    /**
     * 
     * 
     * This method retrieves the next operation before its registered
     * listener is called. The default does nothing.
     * 
     * 
     * 
     * Override this method to retrieve state information from before the
     * operation execution.
     * 
     */
    protected fun nextOperation(operator: Operator?, operands: List<COSBase>) {
    }

    /**
     * 
     * 
     * This method writes content stream operations to the target canvas. The default
     * implementation writes them as they come, so it essentially generates identical
     * copies of the original instructions [.processOperator]
     * forwards to it.
     * 
     * 
     * 
     * Override this method to achieve some fancy editing effect.
     * 
     */
    @Throws(IOException::class)
    protected open fun write(
        contentStreamWriter: ContentStreamWriter,
        operator: Operator,
        operands: List<COSBase>
    ) {
        contentStreamWriter.writeTokens(operands)
        contentStreamWriter.writeToken(operator)
    }

    // stub implementation of PDFGraphicsStreamEngine abstract methods

    override fun appendRectangle(
        p0: PointF?,
        p1: PointF?,
        p2: PointF?,
        p3: PointF?
    ) {
    }

    @Throws(IOException::class)
    override fun drawImage(pdImage: PDImage?) {
    }

    override fun clip(windingRule: Path.FillType?) {
    }

    @Throws(IOException::class)
    override fun moveTo(x: Float, y: Float) {
    }

    @Throws(IOException::class)
    override fun lineTo(x: Float, y: Float) {
    }

    @Throws(IOException::class)
    override fun curveTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
    }

    @Throws(IOException::class)
    override fun getCurrentPoint(): PointF? {
        return PointF()
    }

    @Throws(IOException::class)
    override fun closePath() {
    }

    @Throws(IOException::class)
    override fun endPath() {
    }

    @Throws(IOException::class)
    override fun strokePath() {
    }

    override fun fillPath(windingRule: Path.FillType?) {

    }

    override fun fillAndStrokePath(windingRule: Path.FillType?) {

    }

    @Throws(IOException::class)
    override fun shadingFill(shadingName: COSName?) {
    }

    // Actual editing methods
    @Throws(IOException::class)
    override fun processPage(page: PDPage) {
        val stream = PDStream(document)
        replacement = ContentStreamWriter(
            stream.createOutputStream(COSName.FLATE_DECODE).also { replacementStream = it })
        super.processPage(page)
        replacementStream!!.close()
        page.setContents(stream)
        replacement = null
        replacementStream = null
    }

    @Throws(IOException::class)
    fun processFormXObject(formXObject: PDFormXObject, page: PDPage?) {
        val stream = PDStream(document)
        replacement = ContentStreamWriter(
            stream.createOutputStream(COSName.FLATE_DECODE).also { replacementStream = it })
        super.processChildStream(formXObject, page)
        replacementStream!!.close()
        try {
            formXObject.cosObject.createOutputStream().use { outputStream ->
                stream.createInputStream().copyTo(outputStream)
            }
        } finally {
            replacement = null
            replacementStream = null
        }
    }

    // PDFStreamEngine overrides to allow editing
    @Throws(IOException::class)
    override fun showForm(form: PDFormXObject?) {
        // DON'T descend into XObjects
        // super.showForm(form);
    }

    @Throws(IOException::class)
    protected override fun processOperator(operator: Operator, operands: List<COSBase>) {
        if (inOperator) {
            super.processOperator(operator, operands)
        } else {
            inOperator = true
            nextOperation(operator, operands)
            super.processOperator(operator, operands)
            write(replacement!!, operator, operands)
            inOperator = false
        }
    }

    var replacementStream: OutputStream? = null
    var replacement: ContentStreamWriter? = null
    var inOperator: Boolean = false
}

internal fun PDDocument.removeText(linesToRemove: Set<String>) {
    for (page in pages) {
        val editor = object : PdfContentStreamEditor(this, page) {
            val recentChars = StringBuilder()
            val TEXT_SHOWING_OPERATORS = listOf("Tj", "'", "\"", "TJ")

            @Deprecated("Deprecated in Java")
            override fun showGlyph(
                textRenderingMatrix: Matrix,
                font: PDFont,
                code: Int,
                unicode: String,
                displacement: Vector
            ) {
                recentChars.append(unicode)
                super.showGlyph(textRenderingMatrix, font, code, unicode, displacement)
            }

            override fun write(
                contentStreamWriter: ContentStreamWriter,
                operator: Operator,
                operands: List<COSBase>
            ) {
                val recentText = recentChars.toString()
                recentChars.setLength(0)
                if (TEXT_SHOWING_OPERATORS.contains(operator.name) &&
                    linesToRemove.any { recentText.contains(it, ignoreCase = true) }
                ) {
                    return
                }
                super.write(contentStreamWriter, operator, operands)
            }
        }
        editor.processPage(page)
    }
}