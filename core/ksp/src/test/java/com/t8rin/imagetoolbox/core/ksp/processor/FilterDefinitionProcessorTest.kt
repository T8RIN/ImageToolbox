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

package com.t8rin.imagetoolbox.core.ksp.processor

import com.google.devtools.ksp.impl.KotlinSymbolProcessing
import com.google.devtools.ksp.processing.KSPJvmConfig
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.net.URLClassLoader

class FilterDefinitionProcessorTest {

    @get:Rule
    val temporary = TemporaryFolder()

    @Test
    fun `generated values compile and preserve arrays nullable fields and empty values`() {
        val fixture = Fixture()
        fixture.source(
            "Filter.kt",
            contract(
                """
                interface Sample : Filter<Sample.Value> {
                    interface Value : ParameterValue {
                        val amount: Float
                        val matrix: FloatArray
                        val caption: String?
                    }
                }
                interface Empty : Filter<Empty.Value> {
                    interface Value : EmptyValue
                }
                """
            )
        )
        fixture.source(
            "Check.kt",
            """
            package fixture

            import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
            import com.t8rin.imagetoolbox.core.filters.domain.model.copy

            fun checkGeneratedValues(): Boolean {
                val value = FilterValues.sample(
                    amount = 0.5f,
                    caption = null,
                    matrix = floatArrayOf(1f, 2f)
                )
                val equal = value.copy(matrix = floatArrayOf(1f, 2f))
                val changed = value.copy(amount = 0.7f, caption = "caption")
                return value == equal && value.hashCode() == equal.hashCode() &&
                    changed != value && changed.caption == "caption" &&
                    changed.matrix.contentEquals(value.matrix) &&
                    FilterValues.empty() === FilterValues.empty()
            }
            """
        )

        fixture.processSuccessfully()
        fixture.compile(fixture.generatedSources)

        URLClassLoader(arrayOf(fixture.classes.toURI().toURL()), javaClass.classLoader).use {
            val check = it.loadClass("fixture.CheckKt").getMethod("checkGeneratedValues")
            assertEquals(true, check.invoke(null))
        }
    }

    @Test
    fun `structured custom and empty contracts produce their own ui and codecs`() {
        val fixture = Fixture()
        fixture.source(
            "Filter.kt",
            contract(
                """
                interface Sample : Filter<Sample.Value> {
                    interface Value : ParameterValue { val amount: Float }
                }
                interface Custom : Filter<Custom.Value> {
                    interface Value : ParameterValue { val points: List<Float> }
                }
                interface Empty : Filter<Empty.Value> {
                    interface Value : EmptyValue
                }
                """
            )
        )
        fixture.source("SampleFilter.kt", implementation("Sample", "Structured"))
        fixture.source("CustomFilter.kt", implementation("Custom", "Custom"))
        fixture.source("EmptyFilter.kt", implementation("Empty", "Empty"))

        fixture.processSuccessfully()

        val files = fixture.generatedSources.map { it.name }.toSet()
        assertTrue(
            files.containsAll(
                listOf(
                    "SampleParams.kt", "CustomParams.kt", "FilterValues.kt",
                    "UiSampleFilter.kt", "UiCustomFilter.kt", "UiEmptyFilter.kt",
                    "SampleFilterCodec.kt", "CustomFilterCodec.kt", "FilterRegistrations.kt",
                    "UiFilterRegistrations.kt"
                )
            )
        )
        assertFalse("EmptyFilterCodec.kt" in files)
        fixture.generatedSources.filter {
            it.invariantSeparatorsPath.contains("/feature/filters/data/")
        }.forEach { source ->
            assertFalse(source.name, source.readLines().any {
                it.startsWith("import ") && ".presentation." in it
            })
        }
        // The Android UI is compiled by the app build; here compile the source contracts
        // and generated domain models independently of Android and Compose.
        fixture.compile(fixture.generatedSources.filter {
            it.invariantSeparatorsPath.contains("/core/filters/domain/")
        })
    }

    @Test
    fun `mutable value properties are rejected with the offending field name`() {
        val fixture = Fixture()
        fixture.source(
            "Filter.kt", contract(
                """
            interface Sample : Filter<Sample.Value> {
                interface Value : ParameterValue { var amount: Float }
            }
        """
            )
        )
        fixture.source("SampleFilter.kt", implementation("Sample", "Structured"))

        fixture.assertError("Filter parameter amount must be immutable")
    }

    @Test
    fun `missing schema is rejected at the filter declaration`() {
        val fixture = sampleFixture()
        fixture.source("SampleFilter.kt", implementation("Sample", null))

        fixture.assertError("The companion must implement Filter.Schema.Structured")
    }

    @Test
    fun `schema and constructor must use the same domain value`() {
        val fixture = sampleFixture()
        fixture.source(
            "SampleFilter.kt",
            implementation("Sample", "Structured")
                .replace("Schema.Structured<Filter.Sample.Value>", "Schema.Structured<String>")
        )

        fixture.assertError("The schema value type must match")
    }

    @Test
    fun `constructor value must belong to the implemented filter`() {
        val fixture = sampleFixture()
        fixture.source(
            "SampleFilter.kt",
            implementation("Sample", "Structured").replace("Filter.Sample.Value", "String")
        )

        fixture.assertError("The filter constructor must use its domain interface's Value")
    }

    @Test
    fun `unknown groups are rejected before registration is generated`() {
        val fixture = sampleFixture()
        fixture.source(
            "SampleFilter.kt",
            implementation("Sample", "Structured").replace("FilterGroups.COLOR", "\"unknown\"")
        )

        fixture.assertError("Unknown filter group: unknown")
        assertFalse(fixture.generatedSources.any { it.name == "FilterRegistrations.kt" })
    }

    @Test
    fun `empty schemas cannot discard declared parameters`() {
        val fixture = sampleFixture()
        fixture.source("SampleFilter.kt", implementation("Sample", "Empty"))

        fixture.assertError("Filter.Schema.Empty requires a Value without parameters")
    }

    @Test
    fun `misplaced implementations receive a package diagnostic`() {
        val fixture = sampleFixture()
        fixture.source(
            "SampleFilter.kt",
            implementation("Sample", "Structured").replace(
                "package com.t8rin.imagetoolbox.feature.filters.data.model",
                "package fixture"
            )
        )

        fixture.assertError("Filter implementations must be named NameFilter in")
    }

    private fun sampleFixture() = Fixture().apply {
        source(
            "Filter.kt", contract(
                """
            interface Sample : Filter<Sample.Value> {
                interface Value : ParameterValue { val amount: Float }
            }
        """
            )
        )
    }

    private fun contract(filters: String): String = """
        package com.t8rin.imagetoolbox.core.filters.domain.model

        import com.t8rin.imagetoolbox.core.ksp.annotations.GenerateFilterValues

        @GenerateFilterValues
        interface Filter<T> {
            val value: T
            interface ParameterValue
            interface EmptyValue : ParameterValue
            interface Schema<T> {
                val info: String
                interface Structured<T> : Schema<T>
                interface Custom<T> : Structured<T>
                interface Empty<T> : Schema<T>
                interface HiddenValue<T> : Schema<T>
            }
            $filters
        }
    """

    private fun implementation(name: String, schema: String?): String = """
        package com.t8rin.imagetoolbox.feature.filters.data.model

        import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
        import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
        import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups

        @FilterDefinition(group = FilterGroups.COLOR)
        class ${name}Filter(override val value: Filter.$name.Value) : Filter.$name {
            ${
        schema?.let {
            """
                companion object : Filter.Schema.$it<Filter.$name.Value> {
                    override val info = "$name"
                }
            """
        }.orEmpty()
    }
        }
    """

    private inner class Fixture {
        private val root = temporary.newFolder()
        private val sources = root.resolve("sources").apply { mkdirs() }
        private val generated = root.resolve("generated")
        val classes = root.resolve("classes")
        private val errors = mutableListOf<String>()
        private val libraries = listOf(
            File(Unit::class.java.protectionDomain.codeSource.location.toURI()),
            File(FilterDefinition::class.java.protectionDomain.codeSource.location.toURI())
        )

        val generatedSources
            get() = generated.walkTopDown().filter { it.extension == "kt" }.toList()

        fun source(name: String, text: String) {
            sources.resolve(name).writeText(text.trimIndent())
        }

        fun processSuccessfully() {
            val result = process()
            assertEquals(errors.joinToString("\n"), KotlinSymbolProcessing.ExitCode.OK, result)
            assertTrue(errors.joinToString("\n"), errors.isEmpty())
        }

        fun assertError(message: String) {
            assertEquals(KotlinSymbolProcessing.ExitCode.PROCESSING_ERROR, process())
            assertTrue(errors.joinToString("\n"), errors.any { message in it })
        }

        private fun process(): KotlinSymbolProcessing.ExitCode {
            val config = KSPJvmConfig.Builder().apply {
                moduleName = "filter-fixture"
                sourceRoots = listOf(sources)
                libraries = this@Fixture.libraries
                projectBaseDir = root
                outputBaseDir = generated
                cachesDir = root.resolve("cache")
                classOutputDir = generated.resolve("classes")
                kotlinOutputDir = generated.resolve("kotlin")
                javaOutputDir = generated.resolve("java")
                resourceOutputDir = generated.resolve("resources")
                jdkHome = File(System.getProperty("java.home"))
                jvmTarget = "17"
                languageVersion = KotlinVersion.CURRENT.let { "${it.major}.${it.minor}" }
                apiVersion = languageVersion
            }.build()
            val logger = object : KSPLogger {
                override fun logging(message: String, symbol: KSNode?) = Unit
                override fun info(message: String, symbol: KSNode?) = Unit
                override fun warn(message: String, symbol: KSNode?) = Unit
                override fun error(message: String, symbol: KSNode?) {
                    errors += message
                }

                override fun exception(e: Throwable) {
                    errors += e.stackTraceToString()
                }
            }
            return KotlinSymbolProcessing(
                config,
                listOf(FilterDefinitionProcessor()),
                logger
            ).execute()
        }

        fun compile(generated: List<File>) {
            val output = ByteArrayOutputStream()
            val args = listOf(
                "-no-stdlib", "-no-reflect", "-jvm-target", "17",
                "-classpath", libraries.joinToString(File.pathSeparator),
                "-d", classes.path
            ) + (sources.listFiles().orEmpty().toList() + generated).map(File::getPath)
            val result = K2JVMCompiler().exec(PrintStream(output), *args.toTypedArray())
            assertEquals(output.toString(), ExitCode.OK, result)
        }
    }
}
