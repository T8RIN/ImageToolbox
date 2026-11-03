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

package com.t8rin.imagetoolbox.feature.filters.data.model

import androidx.annotation.StringRes
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

class FilterDslCompilationTest {

    @get:Rule
    val temporary = TemporaryFolder()

    @Test
    fun typedCustomDefaultsCodecsAndReusableDescriptorsCompile() {
        val result = compile(
            """
            val descriptor = editorParam(default = { 0.5f })
            Value::amount.parameter(descriptor)
            Value::optional.editor { null }
        """
        )
        assertEquals(result.second, ExitCode.OK, result.first)

        val withCodec = compile(
            """
            Value::amount.editor(
                encode = Float::toString,
                decode = String::toFloat
            ) { 0.5f }
            Value::optional.editor { floatArrayOf(1f) }
        """
        )
        assertEquals(withCodec.second, ExitCode.OK, withCodec.first)
    }

    @Test
    fun incompatibleCustomDefaultsAndCodecsCannotWidenThePropertyType() {
        listOf(
            "Value::amount.editor { \"wrong type\" }",
            "Value::amount.parameter(editorParam(default = { \"wrong type\" }))",
            "Value::amount.editor(decode = { \"wrong type\" }) { 0.5f }"
        ).forEach { declaration ->
            val result = compile(declaration)
            assertEquals(result.second, ExitCode.COMPILATION_ERROR, result.first)
            assertTrue(result.second, "Float" in result.second && "String" in result.second)
        }
    }

    private fun compile(declarations: String): Pair<ExitCode, String> {
        val directory = temporary.newFolder()
        val source = directory.resolve("Schema.kt").apply {
            writeText(
                """
                package com.t8rin.imagetoolbox.feature.filters.data.model

                import com.t8rin.imagetoolbox.core.filters.domain.model.Filter

                private interface Value : Filter.ParameterValue {
                    val amount: Float
                    val optional: FloatArray?
                }

                private fun schema() = structuredParameters<Value> {
                    ${declarations.trimIndent()}
                }
            """.trimIndent()
            )
        }
        val libraries = listOf(
            StructuredParametersBuilder::class.java,
            Filter::class.java,
            ColorModel::class.java,
            FontType::class.java,
            StringRes::class.java,
            Unit::class.java
        ).map { it.classpathLocation() }.distinct()
        val mainClasses = StructuredParametersBuilder::class.java.classpathLocation()
        val output = ByteArrayOutputStream()
        val result = K2JVMCompiler().exec(
            PrintStream(output),
            "-no-stdlib", "-no-reflect", "-jvm-target", "17",
            "-classpath", libraries.joinToString(File.pathSeparator),
            "-Xfriend-paths=${mainClasses.path}",
            "-d", directory.resolve("classes").path,
            source.path
        )
        return result to output.toString()
    }

    private fun Class<*>.classpathLocation(): File = File(
        requireNotNull(protectionDomain?.codeSource?.location) {
            "Missing compiled class location for $name"
        }.toURI()
    )
}
