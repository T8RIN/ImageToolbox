package ru.tech.imageresizershrinker.core.ui.widget.text

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TitleItem(
    icon: ImageVector? = null,
    text: String,
    endContent: @Composable RowScope.() -> Unit,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.padding(16.dp),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(it, null)
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        endContent.let {
            Spacer(Modifier.width(8.dp))
            it()
        }
    }
}


@Composable
fun TitleItem(
    icon: ImageVector? = null,
    text: String,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.padding(16.dp),
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(it, null)
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}