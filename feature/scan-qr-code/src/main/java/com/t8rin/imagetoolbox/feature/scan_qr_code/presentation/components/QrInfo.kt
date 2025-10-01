/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.domain.model.QrType

internal data class InfoEntry(
    val icon: ImageVector,
    val text: String,
    val canCopy: Boolean,
)

internal data class QrInfo(
    val title: String,
    val icon: ImageVector,
    val intent: Intent?,
    val data: List<InfoEntry>,
) {
    companion object {
        operator fun invoke(
            builder: QrBuilderScope.() -> Unit
        ): QrInfo = QrBuilderScopeImpl().apply(builder).build()
    }
}

internal interface QrBuilderScope {
    fun title(title: String): QrBuilderScope
    fun icon(icon: ImageVector): QrBuilderScope
    fun intent(intent: Intent?): QrBuilderScope
    fun entry(infoEntry: InfoEntry): QrBuilderScope

    fun build(): QrInfo
}

@Composable
internal fun qrInfoBuilder(
    qrType: QrType,
    builder: QrBuilderScope.() -> Unit
): QrInfo = remember(qrType) {
    derivedStateOf {
        QrInfo {
            if (qrType is QrType.Complex) intent(qrType.toIntent())
            builder()
        }
    }
}.value

private class QrBuilderScopeImpl : QrBuilderScope {
    private var title: String? = null
    private var icon: ImageVector? = null
    private var intent: Intent? = null
    private var data: List<InfoEntry> = emptyList()

    override fun title(title: String) = apply {
        this.title = title
    }

    override fun icon(icon: ImageVector) = apply {
        this.icon = icon
    }

    override fun intent(intent: Intent?) = apply {
        this.intent = intent
    }

    override fun entry(infoEntry: InfoEntry) = apply {
        data += infoEntry
    }

    override fun build(): QrInfo = QrInfo(
        title = requireNotNull(title),
        icon = requireNotNull(icon),
        intent = intent,
        data = data
    )
}