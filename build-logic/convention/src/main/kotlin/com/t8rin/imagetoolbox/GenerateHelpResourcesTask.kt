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

package com.t8rin.imagetoolbox

import com.android.aaptcompiler.XmlStringBuilder
import groovy.json.JsonGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.w3c.dom.Element
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

@CacheableTask
abstract class GenerateHelpResourcesTask : DefaultTask() {

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    @TaskAction
    fun generate() {
        fileSystemOperations.sync {
            from(sourceDirectory)
            into(outputDirectory)
        }

        val directory = outputDirectory.get().asFile
        val parser = DocumentBuilderFactory.newInstance().apply {
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }.newDocumentBuilder()
        val translations = directory.listFiles().orEmpty()
            .filter { it.isDirectory && (it.name == "values" || it.name.startsWith("values-")) }
            .mapNotNull { folder ->
                val file = folder.resolve("strings.xml").takeIf { it.isFile }
                    ?: return@mapNotNull null
                val document = parser.parse(file)
                val strings = document.getElementsByTagName("string")
                val help = (0 until strings.length)
                    .map { strings.item(it) as Element }
                    .filter {
                        val name = it.getAttribute("name")
                        name.startsWith("help_tip_") || name.startsWith("filter_help_")
                    }
                val values = help.associate { element ->
                    val builder = XmlStringBuilder().append(element.textContent)
                    val value = builder.getFlattenedXml()
                    check(value.success) { "${file.path}: ${builder.error}" }
                    element.getAttribute("name") to value.styleString.str
                }

                help.forEach { element ->
                    if (folder.name == "values") {
                        // Keep existing R.string IDs without putting the translated text in the table.
                        element.textContent = element.getAttribute("name")
                        element.setAttribute("translatable", "false")
                        element.removeAttribute("formatted")
                    } else {
                        element.parentNode.removeChild(element)
                    }
                }
                TransformerFactory.newInstance().newTransformer().transform(
                    DOMSource(document),
                    StreamResult(file)
                )
                folder.name to values
            }.toMap()

        val fallback = checkNotNull(translations["values"])
        val json = JsonGenerator.Options().disableUnicodeEscaping().build()
        translations.forEach { (qualifiers, values) ->
            val raw = directory.resolve(qualifiers.replaceFirst("values", "raw"))
            raw.mkdirs()
            raw.resolve("help_texts.json").writeText(json.toJson(fallback + values))
        }
    }
}