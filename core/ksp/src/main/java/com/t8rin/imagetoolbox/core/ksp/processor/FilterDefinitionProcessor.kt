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

@file:Suppress("unused")

package com.t8rin.imagetoolbox.core.ksp.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated

internal class FilterDefinitionProcessor : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        object : SymbolProcessor {
            private val generator =
                FilterDefinitionGenerator(environment.codeGenerator, environment.logger)
            private var generated = false
            private var generatedValues = false

            override fun process(resolver: Resolver): List<KSAnnotated> {
                if (!generatedValues) generatedValues = generator.generateValueFactories(resolver)
                if (!generated) {
                    val definitions = generator.read(resolver)
                    if (definitions.isNotEmpty()) {
                        generator.generate(definitions)
                        generated = true
                    }
                }
                return emptyList()
            }
        }
}