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

package com.t8rin.imagetoolbox.feature.assistant.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import org.commonmark.node.Code
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.Image
import org.commonmark.node.IndentedCodeBlock
import org.commonmark.node.Link
import org.commonmark.node.ListItem
import org.commonmark.node.OrderedList
import org.commonmark.node.Paragraph
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.CoreHtmlNodeRenderer
import org.commonmark.renderer.html.HtmlRenderer

private val markdownParser = Parser.builder().build()
private val markdownRenderer = HtmlRenderer.builder()
    .escapeHtml(true)
    .sanitizeUrls(true)
    .nodeRendererFactory { context ->
        // Android's HTML text supports tt and br, but not code, pre or ordered lists.
        object : CoreHtmlNodeRenderer(context) {
            // Navigation uses validated destinations below the answer, not generated URLs.
            override fun visit(link: Link) = visitChildren(link)

            override fun visit(image: Image) = visitChildren(image)

            override fun visit(code: Code) = writeCode(code.literal)

            override fun visit(code: FencedCodeBlock) = writeCodeBlock(code.literal)

            override fun visit(code: IndentedCodeBlock) = writeCodeBlock(code.literal)

            override fun visit(item: ListItem) {
                val list = item.parent as? OrderedList
                if (list == null) {
                    super.visit(item)
                } else {
                    val index = generateSequence(item.previous) { it.previous }.count()
                    context.writer.tag("p")
                    context.writer.text("${(list.markerStartNumber ?: 1) + index}. ")
                    visitChildren(item)
                    context.writer.tag("/p")
                }
            }

            override fun visit(paragraph: Paragraph) {
                if (paragraph.parent is ListItem && paragraph.parent.parent is OrderedList) {
                    visitChildren(paragraph)
                    if (paragraph.next != null) context.writer.tag("br")
                } else {
                    super.visit(paragraph)
                }
            }

            private fun writeCode(text: String) {
                context.writer.tag("tt")
                context.writer.text(text.replace(' ', '\u00A0'))
                context.writer.tag("/tt")
            }

            private fun writeCodeBlock(text: String) {
                context.writer.tag("p")
                text.trimEnd('\n').lineSequence().forEachIndexed { index, line ->
                    if (index > 0) context.writer.tag("br")
                    writeCode(line)
                }
                context.writer.tag("/p")
            }
        }
    }
    .build()

@Composable
internal fun AssistantAnswerText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = remember(text) { AnnotatedString.fromHtml(assistantMarkdownHtml(text)) },
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        overflow = TextOverflow.Clip
    )
}

internal fun assistantMarkdownHtml(text: String): String =
    markdownRenderer.render(markdownParser.parse(text))