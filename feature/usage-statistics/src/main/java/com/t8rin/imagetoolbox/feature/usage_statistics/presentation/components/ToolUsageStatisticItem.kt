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

package com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import java.util.Calendar

@Composable
internal fun ToolUsageStatisticItem(
    item: UiToolUsageStatistic,
    progress: Float,
    shape: Shape,
    onClick: () -> Unit
) {
    PreferenceItemOverload(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        title = stringResource(item.screen.title),
        subtitle = stringResource(
            R.string.tool_usage_subtitle,
            item.openCount,
            item.lastOpenedTimestamp.asReadableDate()
        ),
        onClick = onClick,
        startIcon = {
            item.screen.icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null
                )
            }
        },
        badgeAlignment = Alignment.CenterVertically,
        badge = {
            EnhancedBadge(
                modifier = Modifier.padding(start = 8.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%"
                )
            }
        },
        placeBottomContentInside = true,
        bottomContent = {
            LinearWavyProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .padding(
                        top = if (progress >= 1f) 8.dp else 12.dp
                    )
                    .fillMaxWidth(),
                amplitude = {
                    if (it >= 0.05f) 0.8f else 0f
                }
            )
        }
    )
}

@Composable
internal fun Long?.asReadableDate(): String {
    val millis = this ?: return stringResource(R.string.no_data)

    val today = stringResource(R.string.header_today)
    val yesterday = stringResource(R.string.header_yesterday)

    val locale = LocalLocale.current.platformLocale

    val date = remember(millis) {
        Calendar.getInstance().apply {
            timeInMillis = millis
        }
    }

    val now = remember {
        Calendar.getInstance()
    }

    val yesterdayCalendar = remember {
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
    }

    fun Calendar.isSameDay(other: Calendar): Boolean {
        return get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
    }

    val time = remember(millis, locale) {
        SimpleDateFormat("HH:mm", locale).format(date.time)
    }

    return when {
        date.isSameDay(now) -> "$today $time"

        date.isSameDay(yesterdayCalendar) -> "$yesterday $time"

        date.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
            remember(millis, locale) {
                SimpleDateFormat("d MMMM, HH:mm", locale).format(date.time)
            }
        }

        else -> {
            remember(millis, locale) {
                SimpleDateFormat("d MMMM yyyy, HH:mm", locale).format(date.time)
            }
        }
    }
}