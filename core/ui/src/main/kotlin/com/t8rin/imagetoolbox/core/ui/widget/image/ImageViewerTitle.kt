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

package com.t8rin.imagetoolbox.core.ui.widget.image

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.utils.dateAdded
import com.t8rin.imagetoolbox.core.utils.lastModified
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ImageViewerTitle(
    uri: Uri?,
    name: String,
    modifier: Modifier = Modifier,
    dateMillis: Long? = null
) {
    val date by produceState(dateMillis, uri, dateMillis) {
        value = dateMillis
        value = dateMillis ?: withContext(Dispatchers.IO) {
            uri?.lastModified() ?: uri?.dateAdded()
        }
    }

    Column(modifier) {
        Text(
            text = name,
            color = White,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        date?.takeIf { it > 0 }?.let {
            Text(
                text = timestamp(
                    format = "d MMMM, yyyy • HH:mm",
                    date = it
                ),
                modifier = Modifier.padding(top = 2.dp),
                color = White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}