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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.OpenInNew
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.help.data.HelpCategory
import com.t8rin.imagetoolbox.feature.help.data.HelpPage
import com.t8rin.imagetoolbox.feature.help.data.HelpTip
import com.t8rin.imagetoolbox.feature.help.presentation.screenLogic.HelpComponent

@Composable
fun HelpContent(
    component: HelpComponent
) {
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
            onOpenCategory = component::openCategory,
            onGoBack = component.onGoBack
        )
    }
}

@Composable
private fun HelpScreen(
    categories: List<HelpCategory>,
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            itemsIndexed(
                items = categories,
                key = { _, category -> category.key }
            ) { index, category ->
                PreferenceItem(
                    title = stringResource(category.title),
                    subtitle = stringResource(category.subtitle),
                    startIcon = category.icon,
                    shape = ShapeDefaults.byIndex(index, categories.size),
                    onClick = { onOpenCategory(category) },
                    modifier = Modifier.fillMaxWidth()
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            itemsIndexed(
                items = tips,
                key = { _, tip -> tip.id }
            ) { index, tip ->
                PreferenceItem(
                    title = stringResource(tip.title),
                    subtitle = stringResource(tip.subtitle),
                    startIcon = tip.icon,
                    shape = ShapeDefaults.byIndex(index, tips.size),
                    onClick = { onOpenTip(tip) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun TutorialDetailScreen(
    tip: HelpTip,
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    HelpScaffold(
        title = stringResource(tip.title),
        onGoBack = onGoBack
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
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
                            .padding(top = 12.dp)
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
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (page.steps.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                page.steps.forEachIndexed { index, stepId ->
                    StepItem(
                        stepNumber = index + 1,
                        text = stringResource(stepId),
                        shape = ShapeDefaults.byIndex(index, page.steps.size)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepItem(
    stepNumber: Int,
    text: String,
    shape: Shape,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .container(
                shape = shape,
                color = MaterialTheme.colorScheme.surfaceContainerLowest
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$stepNumber",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp)
        )
    }
}

@Composable
private fun HelpScaffold(
    title: String,
    onGoBack: () -> Unit,
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
                    TopAppBarEmoji()
                }
            )
        }
    ) { contentPadding ->
        content(contentPadding + PaddingValues(16.dp))
    }
}
