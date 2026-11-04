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

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.Variance
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups

internal class FilterDefinitionGenerator(
    private val codegen: CodeGenerator,
    private val logger: KSPLogger
) {
    fun read(resolver: Resolver): List<FilterSpec> {
        return resolver
            .getSymbolsWithAnnotation("$PACKAGE.core.ksp.annotations.FilterDefinition")
            .filterIsInstance<KSClassDeclaration>()
            .mapNotNull { declaration ->
                val className = declaration.simpleName.asString()
                val source = declaration.containingFile ?: return@mapNotNull null
                val group = declaration.annotations.first {
                    it.annotationType.resolve().declaration.qualifiedName?.asString() == ANNOTATION
                }.arguments.single().value as String
                val hasUi = group != FilterGroups.HIDDEN
                val constructor = declaration.primaryConstructor?.parameters.orEmpty()
                val value = constructor.singleOrNull { it.name?.asString() == "value" }
                if (declaration.packageName.asString() != MODEL_PACKAGE || !className.endsWith("Filter")) {
                    logger.error(
                        "Filter implementations must be named NameFilter in $MODEL_PACKAGE",
                        declaration
                    )
                    return@mapNotNull null
                }
                if (value == null || constructor.any {
                        it.name?.asString() !in setOf(
                            "value",
                            "onError"
                        )
                    }) {
                    logger.error(
                        "A filter must declare a value parameter and optional onError callback",
                        declaration
                    )
                    return@mapNotNull null
                }
                if (hasUi && group !in groups) {
                    logger.error("Unknown filter group: $group", declaration)
                    return@mapNotNull null
                }
                val companion = declaration.declarations.filterIsInstance<KSClassDeclaration>()
                    .singleOrNull { it.isCompanionObject }
                val declaredType = value.type.resolve()
                val typeName = declaredType.renderType()
                val schemaTypes = companion?.superTypes.orEmpty()
                    .map { it.resolve() }
                    .mapNotNull { type ->
                        val name = type.declaration.qualifiedName?.asString()
                        if (name?.startsWith(SCHEMA_PREFIX) == true) name.removePrefix(SCHEMA_PREFIX) to type
                        else null
                    }.toMap()
                val empty = "Empty" in schemaTypes
                val customEditor = "Custom" in schemaTypes
                val expectedSchema = when {
                    !hasUi -> "HiddenValue"
                    empty -> "Empty"
                    customEditor -> "Custom"
                    else -> "Structured"
                }
                val schema = schemaTypes[expectedSchema]
                if (schema == null) {
                    logger.error(
                        "The companion must implement Filter.Schema.$expectedSchema",
                        declaration
                    )
                    return@mapNotNull null
                }
                if (schema.arguments.singleOrNull()?.type?.resolve()?.renderType() != typeName
                ) {
                    logger.error("The schema value type must match $typeName", declaration)
                    return@mapNotNull null
                }

                val parents =
                    declaration.superTypes.map { it.resolve().declaration.qualifiedName?.asString() }
                        .toList()
                val domain = parents.filterNotNull()
                    .firstOrNull { it.startsWith("$DOMAIN_PACKAGE.Filter.") }
                    ?.removePrefix("$DOMAIN_PACKAGE.")
                if (domain == null || typeName != "$DOMAIN_PACKAGE.$domain.Value") {
                    logger.error(
                        "The filter constructor must use its domain interface's Value",
                        declaration
                    )
                    return@mapNotNull null
                }
                val shader = "$SHADER_PACKAGE.ShaderEffectFilter" in parents
                val model = if (!empty) {
                    parameterModel(
                        domain.removePrefix("Filter.") + "Params",
                        declaredType,
                        className,
                        source
                    )
                        ?: return@mapNotNull null
                } else null
                if (empty && (declaredType.declaration as? KSClassDeclaration)?.getAllProperties()
                        ?.any() != false
                ) {
                    logger.error(
                        "Filter.Schema.Empty requires a Value without parameters",
                        declaration
                    )
                    return@mapNotNull null
                }
                if (shader && (model == null || model.fields.any {
                        it.kind !in setOf(
                            FieldKind.Number,
                            FieldKind.Integer,
                            FieldKind.Color
                        )
                    })) {
                    logger.error(
                        "The shader renderer needs its own numeric or color fields",
                        declaration
                    )
                    return@mapNotNull null
                }
                FilterSpec(
                    name = className,
                    group = group,
                    domain = domain,
                    onError = constructor.any { it.name?.asString() == "onError" },
                    customEditor = customEditor,
                    model = model,
                    source = source
                )
            }.sortedBy(FilterSpec::name).toList()
    }

    private fun parameterModel(
        name: String,
        value: KSType,
        owner: String,
        source: KSFile
    ): ParameterModel? {
        val declaration = value.declaration as? KSClassDeclaration ?: return null
        val valueType = value.renderType()
        val fields = parameterFields(declaration, valueType, owner) ?: return null
        if (fields.isEmpty()) {
            logger.error(
                "The filter parameter contract must declare at least one property",
                declaration
            )
            return null
        }
        return ParameterModel(name, valueType, owner, fields, source)
    }

    private fun parameterFields(
        declaration: KSClassDeclaration,
        valueType: String,
        owner: String
    ): List<Field>? {
        return declaration.getAllProperties()
            .sortedBy { it.simpleName.asString() }.toList()
            .map { property ->
                val name = property.simpleName.asString()
                val resolved = property.type.resolve().expandedType()
                val type = resolved.renderType()
                val kind = when {
                    resolved.isMarkedNullable -> FieldKind.Custom
                    else -> FieldKind.entries.firstOrNull { it.qualifiedType == type }
                        ?: FieldKind.Custom
                }
                if (property.isMutable) {
                    logger.error("Filter parameter $name must be immutable", property)
                    return null
                }
                Field(
                    name = name,
                    kind = kind,
                    type = type,
                    reference = "$valueType::$name",
                    owner = owner,
                    nullable = resolved.isMarkedNullable,
                    hasConcreteTypeArguments = resolved.arguments.any { it.variance != Variance.STAR }
                )
            }
    }

    private tailrec fun KSType.expandedType(): KSType {
        val alias = declaration as? KSTypeAlias ?: return this
        return alias.type.resolve().expandedType()
    }

    fun generateValueFactories(resolver: Resolver): Boolean {
        val root =
            resolver.getSymbolsWithAnnotation("$PACKAGE.core.ksp.annotations.GenerateFilterValues")
                .filterIsInstance<KSClassDeclaration>().singleOrNull() ?: return false
        val source = root.containingFile ?: return false
        val filters = root.declarations.filterIsInstance<KSClassDeclaration>()
            .mapNotNull { filter ->
                val value = filter.declarations.filterIsInstance<KSClassDeclaration>()
                    .singleOrNull { it.simpleName.asString() == "Value" } ?: return@mapNotNull null
                filter.simpleName.asString() to value
            }.sortedBy { it.first }.toList()
        val values = filters.mapNotNull { (name, declaration) ->
            val fields = parameterFields(declaration, "Filter.$name.Value", "FilterValues")
                ?: return@mapNotNull null
            name to fields
        }
        values.filter { it.second.isNotEmpty() }.forEach { (name, fields) ->
            write(Dependencies(false, source), PARAMS_PACKAGE, "${name}Params") {
                appendValueModel("${name}Params", "$DOMAIN_PACKAGE.Filter.$name.Value", fields)
            }
        }
        write(Dependencies(false, source), DOMAIN_PACKAGE, "FilterValues") {
            appendLine("object FilterValues {")
            values.forEachIndexed { index, (name, fields) ->
                if (index > 0) appendLine()
                val type = "Filter.$name.Value"
                val factory = factoryPrefix.replace(name) { it.value.lowercase() }
                if (fields.isEmpty()) {
                    appendLine("    fun $factory(): $type = ${name}Value")
                    appendLine()
                    appendLine("    private data object ${name}Value : $type")
                } else {
                    appendLine("    fun $factory(")
                    appendLine(fields.joinToString(",\n") { "        ${it.name}: ${it.type}" })
                    appendLine("    ): $type = $PARAMS_PACKAGE.${name}Params(")
                    appendLine(fields.joinToString(",\n") { "        ${it.name} = ${it.name}" })
                    appendLine("    )")
                }
            }
            appendLine("}")
            values.filter { it.second.isNotEmpty() }.forEach { (name, fields) ->
                val type = "Filter.$name.Value"
                val factory = factoryPrefix.replace(name) { it.value.lowercase() }
                appendLine()
                appendLine("fun $type.copy(")
                appendLine(fields.joinToString(",\n") { "    ${it.name}: ${it.type} = this.${it.name}" })
                appendLine("): $type = FilterValues.$factory(")
                appendLine(fields.joinToString(",\n") { "    ${it.name} = ${it.name}" })
                appendLine(")")
            }
        }
        return true
    }

    fun generate(specs: List<FilterSpec>) {
        val duplicates =
            specs.map(FilterSpec::name).duplicates() + specs.map(FilterSpec::domain).duplicates()
        if (duplicates.isNotEmpty()) {
            logger.error("Duplicate filter definitions: $duplicates")
            return
        }

        specs.mapNotNull(FilterSpec::model).forEach { model ->
            write(Dependencies(false, model.source), MODEL_PACKAGE, "${model.owner}Codec") {
                appendCodec(model)
            }
        }
        specs.forEach { spec ->
            spec.uiName?.let { name ->
                write(Dependencies(false, spec.source), UI_PACKAGE, name) {
                    appendUiFilter(spec, name)
                }
            }
        }
        write(
            Dependencies(true, *specs.map(FilterSpec::source).distinct().toTypedArray()),
            MODEL_PACKAGE,
            "FilterRegistrations"
        ) {
            appendMappings(specs)
        }
        write(
            Dependencies(true, *specs.map(FilterSpec::source).distinct().toTypedArray()),
            PRESENTATION_PACKAGE,
            "UiFilterRegistrations"
        ) {
            appendUiMappings(specs.filter { it.uiName != null })
        }
    }

    private fun List<String>.duplicates(): Set<String> =
        groupingBy { it }.eachCount().filterValues { it > 1 }.keys

    private fun write(
        dependencies: Dependencies,
        packageName: String,
        name: String,
        block: Appendable.() -> Unit
    ) {
        codegen.createNewFile(dependencies, packageName, name).bufferedWriter().use {
            it.appendLine("// Generated by KSP")
            it.appendLine("package $packageName")
            it.appendLine()
            it.block()
        }
    }

    private fun Appendable.appendArrayEquality(name: String, fields: List<Field>, indent: String) {
        appendLine("${indent}override fun equals(other: Any?): Boolean = other is $name &&")
        appendLine(fields.joinToString(" &&\n") {
            val comparison = when {
                it.isArray -> "${it.name}.contentEquals(other.${it.name})"
                it.type.removeSuffix("?") in setOf("kotlin.Float", "kotlin.Double") -> {
                    val access = if (it.nullable) "?." else "."
                    "${it.name}${access}toBits() == other.${it.name}${access}toBits()"
                }

                else -> "${it.name} == other.${it.name}"
            }
            "$indent    $comparison"
        })
        appendLine()
        appendLine("${indent}override fun hashCode(): Int {")
        appendLine("$indent    var result = 1")
        fields.forEach {
            val hash = if (it.isArray) "contentHashCode" else "hashCode"
            appendLine("$indent    result = 31 * result + ${it.name}.$hash()")
        }
        appendLine("$indent    return result")
        appendLine("$indent}")
    }

    private fun Appendable.appendValueModel(name: String, valueType: String, fields: List<Field>) {
        appendLine("data class $name(")
        appendLine(fields.joinToString(",\n") { "    override val ${it.name}: ${it.type}" })
        append(") : $valueType")
        if (fields.any { it.isArray }) {
            appendLine(" {")
            appendArrayEquality(name, fields, "    ")
            appendLine("}")
        } else appendLine()
    }

    private fun Appendable.appendCodec(model: ParameterModel) {
        appendLine("import $PARAMS_PACKAGE.${model.name}")
        appendLine("import $SERIALIZATION_PACKAGE.StoredParameterValues")
        appendLine("import $SERIALIZATION_PACKAGE.storedFieldOrder")
        appendLine("import $SERIALIZATION_PACKAGE.storedParameterValues")
        appendLine()
        appendLine("internal object ${model.owner}Codec {")
        val hasArray = model.fields.any { it.isArray }
        if (hasArray) {
            appendLine("    val Default get(): ${model.name} {")
            appendLine("        fieldOrder")
            appendLine("        return ${model.name}(")
        } else {
            appendLine("    val Default by lazy {")
            appendLine("        fieldOrder")
            appendLine("        ${model.name}(")
        }
        val indent = "        "
        appendLine(model.fields.joinToString(",\n") { "$indent    ${it.name} = ${it.source}.default" })
        appendLine("$indent)")
        appendLine("    }")
        appendLine()
        appendLine("    val fieldOrder by lazy {")
        appendLine("        ${model.owner}.parameters.orderedFields(")
        appendLine(model.fields.joinToString(",\n") { "            ${it.reference}" })
        appendLine("        )")
        appendLine("    }")
        appendLine()
        appendLine("    private val storedFieldOrder by lazy {")
        appendLine("        ${model.valueType.removeSuffix(".Value")}::class.storedFieldOrder()")
        appendLine("    }")
        appendLine()
        appendLine("    fun from(value: ${model.valueType}): ${model.name} {")
        appendLine("        fieldOrder")
        appendLine("        return value as? ${model.name} ?: ${model.name}(")
        appendLine(model.fields.joinToString(",\n") { "            ${it.name} = value.${it.name}" })
        appendLine("        )")
        appendLine("    }")
        appendLine()
        if (model.fields.any { it.hasConcreteTypeArguments }) {
            appendLine("    @Suppress(\"UNCHECKED_CAST\")")
        }
        appendLine("    fun readStoredValue(value: Any?): ${model.name}? {")
        appendLine("        val stored = storedParameterValues(value, storedFieldOrder.size) ?: return null")
        appendLine("        return ${model.name}(")
        appendLine(model.fields.joinToString(",\n") { field ->
            val storedValue = "stored[storedFieldOrder.indexOf(${field.reference})]"
            val decoded = if (field.nullable) {
                "$storedValue?.let { it as? ${field.type.removeSuffix("?")} ?: return null }"
            } else "$storedValue as? ${field.type} ?: return null"
            "            ${field.name} = if (${field.reference} !in storedFieldOrder) ${field.source}.default else $decoded"
        })
        appendLine("        )")
        appendLine("    }")
        appendLine()
        appendLine("    fun decode(value: String): ${model.name} {")
        appendLine("        val fields = StoredParameterValues(value, fieldOrder, storedFieldOrder)")
        appendLine("        return ${model.name}(")
        appendLine(model.fields.joinToString(",\n") { field ->
            val nullable = if (field.nullable) ", nullable = true" else ""
            "            ${field.name} = fields.read(${field.reference}, ${field.source}$nullable)"
        })
        appendLine("        )")
        appendLine("    }")
        appendLine("}")
    }

    private fun Appendable.appendUiFilter(spec: FilterSpec, name: String) {
        appendLine("import $MODEL_PACKAGE.${spec.name}")
        appendLine("import $DOMAIN_PACKAGE.Filter")
        if (spec.domain == "Filter.RawGmic") {
            appendLine("import androidx.compose.runtime.getValue")
            appendLine("import androidx.compose.runtime.mutableStateOf")
            appendLine("import androidx.compose.runtime.setValue")
            appendLine("import $PACKAGE.core.domain.model.ImageModel")
        }
        if (spec.hasGeneratedEditor) {
            appendLine("import androidx.compose.foundation.layout.Column")
            appendLine("import androidx.compose.foundation.layout.padding")
            appendLine("import androidx.compose.runtime.Composable")
            appendLine("import androidx.compose.ui.Modifier")
            appendLine("import androidx.compose.ui.unit.dp")
            appendLine("import $PACKAGE.feature.filters.presentation.components.FilterFieldItem")
            appendLine("import $PACKAGE.feature.filters.presentation.components.requireFieldEditors")

        }
        appendLine()
        if (spec.model == null) {
            appendLine("class $name : UiFilter<${spec.domainValueType}>(")
        } else {
            appendLine("class $name(")
            appendLine("    override val value: ${spec.valueType} = ${spec.defaultValue}")
            appendLine(") : UiFilter<${spec.domainValueType}>(")
        }
        appendLine("    schema = ${spec.name},")
        appendLine("""    serializationKey = "${spec.uiKey}",""")
        appendLine(if (spec.empty) "    value = ${spec.defaultValue}" else "    value = value")
        val interfaces = listOfNotNull(
            spec.domain,
            "FilterParameterEditor".takeIf { spec.hasGeneratedEditor }
        )
        append(")")
        interfaces.forEach { append(", $it") }
        appendLine(" {")
        val create =
            if (spec.empty) "$name()" else "$name(${spec.typedValue("value")} ?: ${spec.defaultValue})"
        if (spec.hasGeneratedEditor) {
            appendLine("    init {")
            appendLine("        ${spec.codecType}.fieldOrder")
            appendLine("        requireFieldEditors(${spec.name}.parameters)")
            appendLine("    }")
            appendLine()
        }
        appendLine("    override fun recreate(value: Any?): $name = $create")
        if (spec.domain == "Filter.RawGmic") {
            appendLine()
            appendLine("    override var auxiliaryImages: List<ImageModel> by mutableStateOf(emptyList())")
        }
        spec.fields.singleOrNull()?.takeIf { it.kind == FieldKind.Number }?.let {
            appendLine()
            appendLine("    override val singleNumber get() = value.${it.name}")
            appendLine("    override fun withSingleNumber(value: Float) = this.value.copy(${it.name} = value)")
        }
        if (spec.hasGeneratedEditor) appendEditor(spec)
        appendLine("}")
    }

    private fun Appendable.appendEditor(spec: FilterSpec) {
        appendLine()
        appendLine("    @Composable")
        appendLine("    override fun ParamsItem(onFilterChange: (Any) -> Unit, previewOnly: Boolean) {")
        appendLine("        Column(modifier = Modifier.padding(8.dp)) {")
        appendLine("            ${spec.codecType}.fieldOrder.forEach { property ->")
        appendLine("                when (property) {")
        spec.fields.forEach { field ->
            appendLine("                    ${field.reference} -> FilterFieldItem(")
            appendLine("                        property = ${field.reference},")
            appendLine("                        field = ${field.source},")
            appendLine("                        value = value.${field.name},")
            appendLine("                        filter = this@${spec.uiName},")
            appendLine("                        previewOnly = previewOnly")
            appendLine("                    ) { onFilterChange(value.copy(${field.name} = it)) }")
        }
        appendLine("                }")
        appendLine("            }")
        appendLine("        }")
        appendLine("    }")
    }

    private fun Appendable.appendMappings(specs: List<FilterSpec>) {
        appendLine("import $DOMAIN_PACKAGE.Filter")
        appendLine()
        val chunks = specs.chunked(64)
        appendLine("internal fun filterRegistrations(): List<FilterRegistration<*>> = buildList {")
        chunks.indices.forEach { appendLine("    addAll(filterRegistrations$it())") }
        appendLine("}")
        chunks.forEachIndexed { index, chunk ->
            appendLine()
            appendLine("private fun filterRegistrations$index(): List<FilterRegistration<*>> = listOf(")
            chunk.forEach { spec -> appendRegistration(spec) }
            appendLine(")")
        }
    }

    private fun Appendable.appendRegistration(spec: FilterSpec) {
        appendLine("    FilterRegistration<${spec.name}>(")
        appendLine("        type = ${spec.domain}::class,")
        appendLine("        implementation = ${spec.name}::class,")
        appendLine("""        transformationKey = "$MODEL_PACKAGE.${spec.name}",""")
        if (spec.uiName != null) appendLine("""        storedKey = "${spec.uiKey}",""")
        if (spec.model != null) {
            appendLine("""        paramsKey = "${spec.model.name}",""")
            appendLine("        decodeParams = ${spec.codecType}::decode,")
            appendLine("        readStoredParams = ${spec.codecType}::readStoredValue,")
            appendLine("        encodeParams = { ${spec.typedValue("it")}?.let(${spec.name}.parameters::encode) },")
            appendLine("        normalizeParams = { ${spec.typedValue("it")} },")
            appendLine("        cacheParams = { ${spec.typedValue("it")}?.let(${spec.name}.parameters::cacheKey) },")
        }
        val callback = "onError = { error -> owner?.pushError(error) }"
        val args = if (spec.onError) ", $callback" else ""
        val owner = if (spec.onError) "owner" else "_"
        val value = if (spec.empty) "_" else "value"
        appendLine("        createTransformation = { $value, $owner ->")
        if (!spec.empty) appendLine("            val typed = ${spec.typedValue("value")}")
        if (spec.empty) {
            appendLine("            ${spec.name}(value = ${spec.defaultValue}$args)")
        } else if (spec.uiName == null) {
            appendLine("            typed?.let { ${spec.name}(value = it$args) }")
        } else {
            appendLine("            ${spec.name}(value = typed ?: ${spec.defaultValue}$args)")
        }
        appendLine("        }")
        appendLine("    ),")
    }

    private fun Appendable.appendUiMappings(specs: List<FilterSpec>) {
        appendLine("import $DOMAIN_PACKAGE.Filter")
        appendLine("import $UI_PACKAGE.*")
        appendLine()
        val chunks = specs.chunked(64)
        appendLine("internal fun uiFilterRegistrations(): List<UiFilterRegistration> = buildList {")
        chunks.indices.forEach { appendLine("    addAll(uiFilterRegistrations$it())") }
        appendLine("}")
        chunks.forEachIndexed { index, chunk ->
            appendLine()
            appendLine("private fun uiFilterRegistrations$index(): List<UiFilterRegistration> = listOf(")
            chunk.forEach { spec ->
                appendLine("    UiFilterRegistration(")
                appendLine("        type = ${spec.domain}::class,")
                appendLine("""        group = "${spec.group}",""")
                appendLine("""        key = "${spec.uiKey}",""")
                val create = if (spec.empty) "${spec.uiName}()"
                else "${spec.uiName}(${spec.typedValue("value")} ?: ${spec.defaultValue})"
                val parameter = if (spec.empty) "_" else "value"
                appendLine("        create = { $parameter -> $create }")
                appendLine("    ),")
            }
            appendLine(")")
        }
    }

    internal data class FilterSpec(
        val name: String,
        val group: String,
        val domain: String,
        val onError: Boolean,
        val customEditor: Boolean,
        val model: ParameterModel?,
        val source: KSFile
    ) {
        val domainValueType get() = "$DOMAIN_PACKAGE.$domain.Value"
        val uiName get() = if (group == FilterGroups.HIDDEN) null else "Ui$name"
        val empty get() = model == null
        val valueType get() = "$PARAMS_PACKAGE.${requireNotNull(model).name}"
        val fields get() = model?.fields.orEmpty()
        val hasGeneratedEditor
            get() = group != FilterGroups.HIDDEN && !customEditor && fields.isNotEmpty()
        val codecType get() = "$MODEL_PACKAGE.${name}Codec"
        val defaultValue
            get() = if (empty) {
                val factory =
                    factoryPrefix.replace(domain.removePrefix("Filter.")) { it.value.lowercase() }
                "$DOMAIN_PACKAGE.FilterValues.$factory()"
            } else "$codecType.Default"

        fun typedValue(value: String): String =
            "($value as? $domainValueType)?.let($codecType::from)"

        val uiKey get() = "$UI_PACKAGE.$uiName"
    }

    internal data class ParameterModel(
        val name: String,
        val valueType: String,
        val owner: String,
        val fields: List<Field>,
        val source: KSFile
    )

    internal data class Field(
        val name: String,
        val kind: FieldKind,
        val type: String,
        val reference: String,
        val owner: String,
        val nullable: Boolean,
        val hasConcreteTypeArguments: Boolean
    ) {
        val source get() = "$owner.parameters.field($reference)"
        val isArray get() = type.removeSuffix("?") == "kotlin.FloatArray"
    }

    internal enum class FieldKind(val qualifiedType: String?) {
        Number("kotlin.Float"),
        Integer("kotlin.Int"),
        Color("$PACKAGE.core.domain.model.ColorModel"),
        Custom(null)
    }

    private companion object {
        private const val PACKAGE = "com.t8rin.imagetoolbox"
        private const val ANNOTATION = "$PACKAGE.core.ksp.annotations.FilterDefinition"
        private const val DOMAIN_PACKAGE = "$PACKAGE.core.filters.domain.model"
        private const val SCHEMA_PREFIX = "$DOMAIN_PACKAGE.Filter.Schema."
        private const val MODEL_PACKAGE = "$PACKAGE.feature.filters.data.model"
        private const val PARAMS_PACKAGE = "$DOMAIN_PACKAGE.params"
        private const val UI_PACKAGE = "$PACKAGE.core.filters.presentation.model"
        private const val PRESENTATION_PACKAGE = "$PACKAGE.feature.filters.presentation.model"
        private const val SERIALIZATION_PACKAGE =
            "$PACKAGE.feature.filters.data.utils.serialization"
        private const val SHADER_PACKAGE = "$PACKAGE.feature.filters.data.utils.gpu"
        private val factoryPrefix = Regex("^[A-Z]+(?=[A-Z][a-z]|[0-9]|$)|^[A-Z]")
        private val groups = setOf(
            FilterGroups.SIMPLE,
            FilterGroups.COLOR,
            FilterGroups.LUT,
            FilterGroups.LIGHT,
            FilterGroups.ENHANCEMENT,
            FilterGroups.ARTISTIC,
            FilterGroups.FRAMES,
            FilterGroups.EFFECTS,
            FilterGroups.BLUR,
            FilterGroups.PIXELATION,
            FilterGroups.DISTORTION,
            FilterGroups.GLITCH,
            FilterGroups.GEOMETRY,
            FilterGroups.DITHERING
        )
    }
}

internal fun KSType.renderType(): String {
    val declarationName = declaration.qualifiedName?.asString()
        ?: declaration.simpleName.asString()

    val argumentsRendered = arguments.takeIf { it.isNotEmpty() }
        ?.joinToString(", ") { argument ->
            val type = argument.type?.resolve()
            if (type == null || argument.variance == Variance.STAR) {
                "*"
            } else {
                val rendered = type.renderType()
                when (argument.variance) {
                    Variance.COVARIANT -> "out $rendered"
                    Variance.CONTRAVARIANT -> "in $rendered"
                    else -> rendered
                }
            }
        }
        ?.let { "<$it>" }
        ?: ""

    val nullable = if (isMarkedNullable) "?" else ""

    return declarationName + argumentsRendered + nullable
}
