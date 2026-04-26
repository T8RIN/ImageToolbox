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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.leftFrom
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.rightFrom
import com.t8rin.imagetoolbox.core.resources.icons.VisibilityOff
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.modifier.detectSwipes

@Composable
internal fun PageSwitcher(
    pageCount: Int,
    activePages: List<Int>? = null,
    modifier: Modifier = Modifier,
    content: @Composable (page: Int) -> Unit
) {
    var page by rememberSaveable {
        mutableIntStateOf(0)
    }
    val indices = remember(pageCount) {
        List(pageCount) { it }
    }

    Column(
        modifier = modifier
            .detectSwipes(
                key = indices,
                onSwipeLeft = {
                    page = indices.rightFrom(page)
                },
                onSwipeRight = {
                    page = indices.leftFrom(page)
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val targetPage = if (indices.isEmpty()) 0 else indices[page]

        Box(modifier = Modifier.weight(1f, false)) {
            content(targetPage)
        }

        if (pageCount > 1) {
            val isActive = activePages == null || targetPage in activePages

            Spacer(Modifier.height(6.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                EnhancedBadge(
                    contentColor = takeColorFromScheme {
                        if (isActive) {
                            onSecondary
                        } else {
                            outline
                        }
                    },
                    containerColor = takeColorFromScheme {
                        if (isActive) {
                            secondary
                        } else {
                            outlineVariant
                        }
                    }
                ) {
                    Text("${targetPage + 1} / $pageCount")
                }

                AnimatedVisibility(
                    visible = !isActive
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VisibilityOff,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}