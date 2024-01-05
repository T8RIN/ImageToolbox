package ru.tech.imageresizershrinker.coreui.widget.controls

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

@Composable
fun OOMWarning(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                    alpha = 0.7f
                ),
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier
                .padding(8.dp)
                .border(
                    width = LocalSettingsState.current.borderWidth,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(0.4f),
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.image_size_warning),
                fontSize = 12.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 14.sp,
                color = LocalContentColor.current.copy(alpha = 0.5f)
            )
        }
    }
}