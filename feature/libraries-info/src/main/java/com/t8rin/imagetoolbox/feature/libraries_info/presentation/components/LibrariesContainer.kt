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

package com.t8rin.imagetoolbox.feature.libraries_info.presentation.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Funding
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import com.mikepenz.aboutlibraries.ui.compose.LibraryColors
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.LibraryDimensions
import com.mikepenz.aboutlibraries.ui.compose.LibraryPadding
import com.mikepenz.aboutlibraries.ui.compose.LibraryShapes
import com.mikepenz.aboutlibraries.ui.compose.LibraryTextStyles
import com.mikepenz.aboutlibraries.ui.compose.m3.component.LibraryChip
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


@Composable
internal fun LibrariesContainer(
    libraries: Libs?,
    modifier: Modifier = Modifier,
    libraryModifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    showAuthor: Boolean = true,
    showDescription: Boolean = true,
    showVersion: Boolean = true,
    showLicenseBadges: Boolean = true,
    showFundingBadges: Boolean = true,
    typography: Typography = MaterialTheme.typography,
    colors: LibraryColors = LibraryDefaults.libraryColors(),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    textStyles: LibraryTextStyles = LibraryDefaults.libraryTextStyles(),
    shapes: LibraryShapes = LibraryDefaults.libraryShapes(),
    onLibraryClick: ((Library) -> Unit)? = null,
    onFundingClick: ((Funding) -> Unit)? = null,
    name: @Composable BoxScope.(name: String) -> Unit = {
        DefaultLibraryName(
            it,
            textStyles,
            colors,
            typography
        )
    },
    version: (@Composable BoxScope.(version: String) -> Unit)? = { version ->
        if (showVersion) DefaultLibraryVersion(
            version,
            textStyles,
            colors,
            typography,
            padding,
            dimensions,
            shapes
        )
    },
    author: (@Composable BoxScope.(authors: String) -> Unit)? = { author ->
        if (showAuthor && author.isNotBlank()) DefaultLibraryAuthor(
            author,
            textStyles,
            colors,
            typography
        )
    },
    description: (@Composable BoxScope.(description: String) -> Unit)? = { description ->
        if (showDescription) DefaultLibraryDescription(description, textStyles, colors, typography)
    },
    license: (@Composable FlowRowScope.(license: License) -> Unit)? = { license ->
        if (showLicenseBadges) DefaultLibraryLicense(
            license,
            textStyles,
            colors,
            padding,
            dimensions,
            shapes
        )
    },
    funding: (@Composable FlowRowScope.(funding: Funding) -> Unit)? = { funding ->
        if (showFundingBadges) DefaultLibraryFunding(
            funding,
            textStyles,
            colors,
            padding,
            dimensions,
            shapes,
            onFundingClick
        )
    },
    actions: (@Composable FlowRowScope.(library: Library) -> Unit)? = null,
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
) {
    val libs = libraries?.libraries ?: persistentListOf()

    LibrariesScaffold(
        libraries = libs,
        modifier = modifier,
        libraryModifier = libraryModifier.background(colors.libraryBackgroundColor),
        lazyListState = lazyListState,
        contentPadding = contentPadding,
        padding = padding,
        dimensions = dimensions,
        name = name,
        version = version,
        author = author,
        description = description,
        license = license,
        funding = funding,
        actions = actions,
        header = header,
        divider = divider,
        footer = footer,
        onLibraryClick = { library ->
            if (onLibraryClick != null) {
                onLibraryClick(library)
                true
            } else {
                false
            }
        },
    )
}

private val DefaultLibraryName: @Composable BoxScope.(name: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: Typography) -> Unit =
    { libraryName, textStyles, colors, typography ->
        Text(
            text = libraryName,
            style = textStyles.nameTextStyle ?: typography.titleLarge,
            color = colors.libraryContentColor,
            maxLines = textStyles.nameMaxLines,
            overflow = textStyles.nameOverflow,
        )
    }

private val DefaultLibraryVersion: @Composable BoxScope.(version: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: Typography, padding: LibraryPadding, dimensions: LibraryDimensions, shapes: LibraryShapes) -> Unit =
    { version, textStyles, colors, typography, padding, dimensions, shapes ->
        LibraryChip(
            modifier = Modifier.padding(padding.versionPadding.containerPadding),
            minHeight = dimensions.chipMinHeight,
            containerColor = colors.versionChipColors.containerColor,
            contentColor = colors.versionChipColors.contentColor,
            shape = shapes.chipShape,
        ) {
            Text(
                modifier = Modifier.padding(padding.versionPadding.contentPadding),
                text = version,
                style = textStyles.versionTextStyle ?: typography.bodyMedium,
                maxLines = textStyles.versionMaxLines,
                textAlign = TextAlign.Center,
                overflow = textStyles.defaultOverflow,
            )
        }
    }

private val DefaultLibraryAuthor: @Composable BoxScope.(author: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: Typography) -> Unit =
    { author, textStyles, colors, typography ->
        Text(
            text = author,
            style = textStyles.authorTextStyle ?: typography.bodyMedium,
            color = colors.libraryContentColor,
            maxLines = textStyles.authorMaxLines,
            overflow = textStyles.defaultOverflow,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .marquee()
        )
    }

private val DefaultLibraryDescription: @Composable BoxScope.(description: String, textStyles: LibraryTextStyles, colors: LibraryColors, typography: Typography) -> Unit =
    { description, textStyles, colors, typography ->
        Text(
            text = description,
            style = textStyles.descriptionTextStyle ?: typography.bodySmall,
            color = colors.libraryContentColor,
            maxLines = textStyles.descriptionMaxLines,
            overflow = textStyles.defaultOverflow,
        )
    }

private val DefaultLibraryLicense: @Composable FlowRowScope.(license: License, textStyles: LibraryTextStyles, colors: LibraryColors, padding: LibraryPadding, dimensions: LibraryDimensions, shapes: LibraryShapes) -> Unit =
    { license, textStyles, colors, padding, dimensions, shapes ->
        LibraryChip(
            modifier = Modifier.padding(padding.licensePadding.containerPadding),
            minHeight = dimensions.chipMinHeight,
            containerColor = colors.licenseChipColors.containerColor,
            contentColor = colors.licenseChipColors.contentColor,
            shape = shapes.chipShape,
        ) {
            Text(
                modifier = Modifier.padding(padding.licensePadding.contentPadding),
                maxLines = 1,
                text = license.name,
                style = textStyles.licensesTextStyle ?: LocalTextStyle.current,
                textAlign = TextAlign.Center,
                overflow = textStyles.defaultOverflow,
            )
        }
    }

private val DefaultLibraryFunding: @Composable FlowRowScope.(funding: Funding, textStyles: LibraryTextStyles, colors: LibraryColors, padding: LibraryPadding, dimensions: LibraryDimensions, shapes: LibraryShapes, onFundingClick: ((Funding) -> Unit)?) -> Unit =
    { funding, textStyles, colors, padding, dimensions, shapes, onFundingClick ->
        val uriHandler = LocalUriHandler.current
        LibraryChip(
            modifier = Modifier
                .padding(padding.fundingPadding.containerPadding)
                .pointerHoverIcon(PointerIcon.Hand),
            onClick = {
                if (onFundingClick != null) {
                    onFundingClick(funding)
                } else {
                    try {
                        uriHandler.openUri(funding.url)
                    } catch (t: Throwable) {
                        println("Failed to open funding url: ${funding.url} // ${t.message}")
                    }
                }
            },
            minHeight = dimensions.chipMinHeight,
            containerColor = colors.fundingChipColors.containerColor,
            contentColor = colors.fundingChipColors.contentColor,
            shape = shapes.chipShape,
        ) {
            Text(
                modifier = Modifier.padding(padding.fundingPadding.contentPadding),
                maxLines = 1,
                text = funding.platform,
                style = textStyles.fundingTextStyle ?: LocalTextStyle.current,
                textAlign = TextAlign.Center,
                overflow = textStyles.defaultOverflow,
            )
        }
    }

@Composable
private fun LibrariesScaffold(
    libraries: ImmutableList<Library>,
    modifier: Modifier = Modifier,
    libraryModifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    padding: LibraryPadding = LibraryDefaults.libraryPadding(),
    dimensions: LibraryDimensions = LibraryDefaults.libraryDimensions(),
    name: @Composable BoxScope.(name: String) -> Unit = {},
    version: (@Composable BoxScope.(version: String) -> Unit)? = null,
    author: (@Composable BoxScope.(authors: String) -> Unit)? = null,
    description: (@Composable BoxScope.(description: String) -> Unit)? = null,
    license: (@Composable FlowRowScope.(license: License) -> Unit)? = null,
    funding: (@Composable FlowRowScope.(funding: Funding) -> Unit)? = null,
    actions: (@Composable FlowRowScope.(library: Library) -> Unit)? = null,
    header: (LazyListScope.() -> Unit)? = null,
    divider: (@Composable LazyItemScope.() -> Unit)? = null,
    footer: (LazyListScope.() -> Unit)? = null,
    onLibraryClick: ((Library) -> Boolean)? = { false },
) {
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
        state = lazyListState,
        contentPadding = contentPadding
    ) {
        header?.invoke(this)
        itemsIndexed(libraries) { index, library ->
            val interactionSource = remember { MutableInteractionSource() }
            LibraryScaffoldLayout(
                modifier = libraryModifier
                    .container(
                        shape = shapeByInteraction(
                            shape = ShapeDefaults.byIndex(
                                index = index,
                                size = libraries.size,
                            ),
                            pressedShape = ShapeDefaults.pressed,
                            interactionSource = interactionSource
                        ),
                        resultPadding = 0.dp
                    )
                    .hapticsClickable(
                        indication = LocalIndication.current,
                        interactionSource = interactionSource
                    ) {
                        val license = library.licenses.firstOrNull()
                        val handled = onLibraryClick?.invoke(library) ?: false

                        if (!handled && !license?.url.isNullOrBlank()) {
                            license.url?.also {
                                try {
                                    uriHandler.openUri(it)
                                } catch (t: Throwable) {
                                    println("Failed to open url: $it // ${t.message}")
                                }
                            }
                        }
                    },
                libraryPadding = padding,
                name = { name(library.name) },
                version = {
                    val artifactVersion = library.artifactVersion
                    if (version != null && artifactVersion != null) {
                        version(artifactVersion)
                    }
                },
                author = {
                    val authors = library.author
                    if (author != null && authors.isNotBlank()) {
                        author(authors)
                    }
                },
                description = {
                    val desc = library.description
                    if (description != null && !desc.isNullOrBlank()) {
                        description(desc)
                    }
                },
                licenses = {
                    if (license != null && library.licenses.isNotEmpty()) {
                        library.licenses.forEach {
                            license(it)
                        }
                    }
                },
                actions = {
                    if (funding != null && library.funding.isNotEmpty()) {
                        library.funding.forEach {
                            funding(it)
                        }
                    }
                    if (actions != null) {
                        actions(library)
                    }
                }
            )

            if (divider != null && index < libraries.lastIndex) {
                divider.invoke(this)
            }
        }
        footer?.invoke(this)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LibraryScaffoldLayout(
    name: @Composable BoxScope.() -> Unit,
    version: @Composable BoxScope.() -> Unit,
    author: @Composable BoxScope.() -> Unit,
    description: @Composable BoxScope.() -> Unit,
    licenses: @Composable FlowRowScope.() -> Unit,
    actions: @Composable FlowRowScope.() -> Unit,
    modifier: Modifier = Modifier,
    libraryPadding: LibraryPadding = LibraryDefaults.libraryPadding(),
) {
    Column(
        modifier = modifier.padding(libraryPadding.contentPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(libraryPadding.namePadding)
                    .weight(1f),
                content = name
            )
            Box(content = version)
        }
        Spacer(Modifier.height(4.dp))
        Box(content = description)
        Spacer(Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            FlowRow(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                licenses()
                actions()
            }

            Box(
                modifier = Modifier.weight(1.2f),
                content = author
            )
        }
    }
}