/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import android.content.Context
import android.text.Html
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.ContentCopy
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.resources.icons.Link
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberRetainedLazyListState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale

@Composable
internal fun GmicCommandsInfoSheet(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit
) {
    val context = LocalContext.current.applicationContext
    var catalog by remember { mutableStateOf<GmicCatalog?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable(isSearching) { mutableStateOf("") }

    LaunchedEffect(visible, catalog) {
        if (visible && catalog == null) {
            withContext(Dispatchers.Default) {
                runCatching { context.readGmicCatalog() }
            }.onSuccess {
                catalog = it
            }.onFailure {
                error = it.message.orEmpty()
            }
        }
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        confirmButton = {},
        enableBottomContentWeight = false,
        title = {
            AnimatedContent(targetState = isSearching) { searching ->
                if (searching) {
                    val focusManager = LocalFocusManager.current
                    BackHandler {
                        query = ""
                        isSearching = false
                    }
                    ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                        RoundedTextField(
                            value = query,
                            onValueChange = { query = it },
                            hint = { Text(stringResource(R.string.search_here)) },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search,
                                autoCorrectEnabled = null
                            ),
                            startIcon = {
                                EnhancedIconButton(
                                    onClick = {
                                        query = ""
                                        isSearching = false
                                    },
                                    modifier = Modifier.padding(start = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowBack,
                                        contentDescription = stringResource(R.string.exit),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            },
                            endIcon = {
                                val scope = rememberCoroutineScope()

                                AnimatedVisibility(
                                    visible = query.isNotEmpty(),
                                    enter = fadeIn() + scaleIn(),
                                    exit = fadeOut() + scaleOut()
                                ) {
                                    EnhancedIconButton(
                                        onClick = {
                                            scope.launch {
                                                query = ""
                                                delay(300)
                                                focusManager.clearFocus()
                                            }
                                        },
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = stringResource(R.string.close),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            },
                            shape = ShapeDefaults.circle
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TitleItem(
                            text = stringResource(R.string.gmic_commands_title),
                            subtitle = catalog?.let {
                                stringResource(
                                    R.string.gmic_commands_count,
                                    it.version,
                                    it.filters.size
                                )
                            },
                            icon = Icons.Rounded.Info
                        )
                        Spacer(Modifier.weight(1f))
                        EnhancedIconButton(
                            onClick = { isSearching = true },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = stringResource(R.string.search_here)
                            )
                        }
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = { onDismiss(false) }
                        ) {
                            AutoSizeText(stringResource(R.string.close))
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }
    ) {
        when {
            error != null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.smth_went_wrong, error.orEmpty()),
                    modifier = Modifier.padding(24.dp)
                )
            }

            catalog == null -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EnhancedLoadingIndicator()
            }

            else -> GmicCommandsList(
                catalog = catalog!!,
                query = query
            )
        }
    }
}

@Composable
private fun GmicCommandsList(
    catalog: GmicCatalog,
    query: String
) {
    val filteredFilters = remember(catalog, query) {
        val normalizedQuery = query.trim().lowercase(Locale.ROOT)
        if (normalizedQuery.isEmpty()) catalog.filters
        else catalog.filters.filter { normalizedQuery in it.searchText }
    }

    AnimatedContent(
        targetState = filteredFilters.isNotEmpty(),
        modifier = Modifier.fillMaxWidth()
    ) { isNotEmpty ->
        if (isNotEmpty) {
            LazyColumn(
                state = rememberRetainedLazyListState("GMIC"),
                modifier = Modifier.animateContentSizeNoClip(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                itemsIndexed(
                    items = filteredFilters,
                    key = { _, filter -> "${filter.category}/${filter.command}/${filter.name}" }
                ) { index, filter ->
                    ExpandableItem(
                        shape = ShapeDefaults.byIndex(index, filteredFilters.size),
                        modifier = Modifier.animateItem(),
                        visibleContent = {
                            TitleItem(
                                text = filter.name,
                                subtitle = "${filter.category} · ${filter.command}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp),
                                endContent = if (filter.parameters.isNotEmpty()) {
                                    {
                                        Badge(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        ) {
                                            Text(filter.parameters.size.toString())
                                        }
                                    }
                                } else null
                            )
                        },
                        expandableContent = {
                            GmicCommandDetails(filter)
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.nothing_found_by_search),
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(
                        start = 24.dp,
                        end = 24.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                )
                Icon(
                    imageVector = Icons.Outlined.SearchOff,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(2f)
                        .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                        .fillMaxSize()
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GmicCommandDetails(filter: GmicCatalogFilter) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val infoItemsCount = if (filter.description.isEmpty()) 1 else 2
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            PreferenceItemOverload(
                title = filter.command,
                onClick = { Clipboard.copy(filter.command) },
                endIcon = {
                    Icon(
                        imageVector = Icons.Rounded.ContentCopy,
                        contentDescription = null
                    )
                },
                titleFontStyle = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                ),
                containerColor = MaterialTheme.colorScheme.surface,
                shape = ShapeDefaults.byIndex(0, infoItemsCount),
                modifier = Modifier.fillMaxWidth()
            )
            if (filter.description.isNotEmpty()) {
                PreferenceItemOverload(
                    title = stringResource(R.string.description),
                    subtitle = filter.description,
                    shape = ShapeDefaults.byIndex(1, infoItemsCount),
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (filter.parameters.isNotEmpty()) {
            TitleItem(
                text = stringResource(R.string.gmic_parameters),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                filter.parameters.forEachIndexed { index, parameter ->
                    GmicParameterDetails(
                        parameter = parameter,
                        shape = ShapeDefaults.byIndex(index, filter.parameters.size)
                    )
                }
            }
        }
    }
}

@Composable
private fun GmicParameterDetails(
    parameter: GmicCatalogParameter,
    shape: Shape
) {
    val uriHandler = LocalUriHandler.current
    val details = buildList {
        add(parameter.type.toReadableName())
        parameter.defaultValue?.let {
            add(stringResource(R.string.gmic_default_value, it))
        }
        if (parameter.min != null || parameter.max != null) {
            add(
                stringResource(
                    R.string.gmic_value_range,
                    parameter.min.orEmpty(),
                    parameter.max.orEmpty()
                )
            )
        }
        if (parameter.choices.isNotEmpty()) {
            add(stringResource(R.string.gmic_choices, parameter.choices))
        }
    }.joinToString(separator = " · ")

    PreferenceItemOverload(
        title = parameter.name.ifEmpty { parameter.type.toReadableName() },
        subtitle = parameter.url ?: details,
        onClick = parameter.url?.let { url ->
            { uriHandler.openUri(url) }
        },
        endIcon = parameter.url?.let {
            {
                Icon(
                    imageVector = Icons.Rounded.Link,
                    contentDescription = null
                )
            }
        },
        subtitleFontStyle = if (parameter.url != null) {
            MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        } else {
            PreferenceItemDefaults.SubtitleFontStyle
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = shape,
        modifier = Modifier.fillMaxWidth()
    )
}

private data class GmicCatalog(
    val version: String,
    val filters: List<GmicCatalogFilter>
)

private data class GmicCatalogFilter(
    val category: String,
    val name: String,
    val command: String,
    val description: String,
    val parameters: List<GmicCatalogParameter>,
    val searchText: String
)

private data class GmicCatalogParameter(
    val type: String,
    val name: String,
    val defaultValue: String?,
    val min: String?,
    val max: String?,
    val choices: String,
    val url: String?
)

private fun Context.readGmicCatalog(): GmicCatalog {
    val root = assets.open(GMIC_COMMANDS_ASSET).bufferedReader().use {
        JSONObject(it.readText())
    }
    val filters = buildList {
        val categories = root.getJSONArray("categories")
        for (categoryIndex in 0 until categories.length()) {
            val category = categories.getJSONObject(categoryIndex)
            val categoryName = category.getString("name")
            val categoryFilters = category.getJSONArray("filters")

            for (filterIndex in 0 until categoryFilters.length()) {
                val filter = categoryFilters.getJSONObject(filterIndex)
                val name = filter.getString("name")
                val command = filter.getString("command")
                if (command == "_none_" || command == "gui_download_all_data") continue

                val parametersJson = filter.getJSONArray("parameters")
                val parameters = buildList {
                    for (parameterIndex in 0 until parametersJson.length()) {
                        val parameter = parametersJson.getJSONObject(parameterIndex)
                        val type = parameter.getString("type")
                        if (type != "note" && type != "separator") {
                            add(parameter.toGmicParameter(type))
                        }
                    }
                }
                val description = (0 until parametersJson.length())
                    .asSequence()
                    .map(parametersJson::getJSONObject)
                    .firstOrNull {
                        it.optString("type") == "note" &&
                                it.optString("text").contains("Description:", ignoreCase = true)
                    }
                    ?.optString("text")
                    ?.toPlainText()
                    ?.substringAfter(":")
                    ?.trim()
                    .orEmpty()
                val searchText = buildString {
                    append(categoryName)
                    append(' ')
                    append(name)
                    append(' ')
                    append(command)
                    parameters.forEach {
                        append(' ')
                        append(it.name)
                        append(' ')
                        append(it.type)
                    }
                }.lowercase(Locale.ROOT)

                add(
                    GmicCatalogFilter(
                        category = categoryName,
                        name = name,
                        command = command,
                        description = description,
                        parameters = parameters,
                        searchText = searchText
                    )
                )
            }
        }
    }

    return GmicCatalog(
        version = root.getString("gmic_version"),
        filters = filters
    )
}

private fun JSONObject.toGmicParameter(type: String): GmicCatalogParameter {
    val choices = optJSONObject("choices")?.let { choices ->
        choices.keys()
            .asSequence()
            .sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }
            .joinToString { key -> "$key: ${choices.getString(key)}" }
    }.orEmpty()

    return GmicCatalogParameter(
        type = type,
        name = optString("name"),
        defaultValue = optNullableString("default")
            ?: optNullableString("position")
            ?: optNullableString("value"),
        min = optNullableString("min"),
        max = optNullableString("max"),
        choices = choices,
        url = optNullableString("url")
    )
}

private fun JSONObject.optNullableString(name: String): String? =
    if (has(name)) optString(name).takeIf(String::isNotEmpty) else null

private fun String.toPlainText(): String = Html.fromHtml(
    this,
    Html.FROM_HTML_MODE_LEGACY
).toString().replace(Regex("\\s+"), " ").trim()

private fun String.toReadableName(): String = replaceFirstChar(Char::uppercase)

private const val GMIC_COMMANDS_ASSET = "update402.json"
