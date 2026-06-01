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

package com.t8rin.imagetoolbox.feature.help.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.ArrowForwardIos
import com.t8rin.imagetoolbox.core.resources.icons.HelpOutline
import com.t8rin.imagetoolbox.core.resources.icons.OpenInNew
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.help.data.HelpCategory
import com.t8rin.imagetoolbox.feature.help.data.HelpPage
import com.t8rin.imagetoolbox.feature.help.data.HelpTip
import com.t8rin.imagetoolbox.feature.help.presentation.screenLogic.HelpComponent

@Composable
fun HelpContent(
    component: HelpComponent
) {
    BackHandler {
        component.onGoBack()
    }

    val selectedCategory = component.selectedCategory
    val selectedTip = component.selectedTip

    when {
        selectedTip != null -> TutorialDetailScreen(
            tip = selectedTip,
            onGoBack = component.onGoBack,
            onNavigate = component.onNavigate
        )

        selectedCategory != null -> TutorialCategoryScreen(
            category = selectedCategory,
            tips = component.tipsFor(selectedCategory),
            onOpenTip = component::openTip,
            onGoBack = component.onGoBack
        )

        else -> HelpScreen(
            categories = component.categories,
            tips = component.tips,
            onOpenCategory = component::openCategory,
            onGoBack = component.onGoBack
        )
    }
}

@Composable
private fun HelpScreen(
    categories: List<HelpCategory>,
    tips: List<HelpTip>,
    onOpenCategory: (HelpCategory) -> Unit,
    onGoBack: () -> Unit
) {
    HelpScaffold(
        title = stringResource(R.string.help_tips),
        onGoBack = onGoBack
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            items(
                items = categories,
                key = { it.key }
            ) { category ->
                val categoryTips = tips.filter { it.category == category }
                PreferenceItem(
                    title = stringResource(category.title),
                    subtitle = stringResource(
                        R.string.help_category_subtitle_with_count,
                        stringResource(category.subtitle),
                        categoryTips.size
                    ),
                    startIcon = category.icon,
                    onClick = { onOpenCategory(category) }
                )
            }
        }
    }
}

@Composable
private fun TutorialCategoryScreen(
    category: HelpCategory,
    tips: List<HelpTip>,
    onOpenTip: (HelpTip) -> Unit,
    onGoBack: () -> Unit
) {
    HelpScaffold(
        title = stringResource(category.title),
        onGoBack = onGoBack
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            items(
                items = tips,
                key = { it.id }
            ) { tip ->
                PreferenceItem(
                    title = stringResource(tip.title),
                    subtitle = stringResource(tip.subtitle),
                    startIcon = tip.icon,
                    endIcon = Icons.Rounded.ArrowForwardIos,
                    onClick = { onOpenTip(tip) }
                )
            }
        }
    }
}

@Composable
private fun TutorialDetailScreen(
    tip: HelpTip,
    onGoBack: () -> Unit,
    onNavigate: (com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen) -> Unit
) {
    HelpScaffold(
        title = stringResource(tip.title),
        onGoBack = onGoBack,
        actions = {
            tip.deepLink?.let { screen ->
                EnhancedIconButton(
                    onClick = { onNavigate(screen) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.OpenInNew,
                        contentDescription = stringResource(R.string.open_tool)
                    )
                }
            }
        }
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            item {
                PreferenceItem(
                    title = stringResource(tip.title),
                    subtitle = stringResource(tip.subtitle),
                    startIcon = tip.icon,
                    autoShadowElevation = 0.dp,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                )
            }
            items(
                items = tip.pages,
                key = { it.title }
            ) { page ->
                TutorialPageItem(page)
            }
            tip.deepLink?.let { screen ->
                item {
                    EnhancedButton(
                        onClick = { onNavigate(screen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.OpenInNew,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.open_tool))
                    }
                }
            }
        }
    }
}

@Composable
private fun TutorialPageItem(
    page: HelpPage
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .container(
                shape = ShapeDefaults.default,
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.HelpOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(page.title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(page.description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        page.steps.forEachIndexed { index, stepId ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(stepId),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HelpScaffold(
    title: String,
    onGoBack: () -> Unit,
    actions: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EnhancedTopAppBar(
                type = EnhancedTopAppBarType.Large,
                title = {
                    Text(text = title)
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = onGoBack,
                        containerColor = Color.Transparent
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                },
                actions = {
                    actions()
                }
            )
        },
        contentWindowInsets = WindowInsets()
    ) { scaffoldPadding ->
        val bottomPadding = WindowInsets.navigationBars
            .union(WindowInsets.displayCutout)
            .asPaddingValues()
            .calculateBottomPadding()

        content(
            PaddingValues(
                start = 8.dp,
                top = scaffoldPadding.calculateTopPadding() + 8.dp,
                end = 8.dp,
                bottom = bottomPadding + 12.dp
            )
        )
    }
}
