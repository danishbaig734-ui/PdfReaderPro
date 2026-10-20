package com.rejowan.pdfreaderpro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.pdfreaderpro.R
import java.util.Calendar

@Composable
private fun getGreetingMultiLine(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingRes = when {
        hour < 12 -> R.string.good_morning
        hour < 17 -> R.string.good_afternoon
        else -> R.string.good_evening
    }
    val rawGreeting = stringResource(greetingRes)
    return if (rawGreeting.contains(" ")) {
        val parts = rawGreeting.split(" ", limit = 2)
        val capitalizedSecond = parts[1].replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        "${parts[0]}\n$capitalizedSecond"
    } else {
        rawGreeting
    }
}

// Search bar height for collapse calculation
val SEARCH_BAR_HEIGHT = 54.dp

/**
 * Modern Pixel Player-inspired header for the Home screen:
 * - Large, bold, multi-line display greeting
 * - Muted subtitle directly below
 * - Analytics button aligned to the top-right
 * - Embedded search bar with generous padding
 *
 * @param collapseProgress 0f = fully expanded, 1f = fully collapsed
 * @param onSearchClick Called when search is clicked
 * @param onStatsClick Called when analytics/stats button is clicked
 * @param onSortClick Optional callback for sort (moved to tab row)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollapsingHomeHeader(
    collapseProgress: Float,
    onSearchClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSortClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val greetingMultiline = getGreetingMultiLine()

    // Clamp progress between 0 and 1
    val progress = collapseProgress.coerceIn(0f, 1f)

    // Search bar collapses: height shrinks and alpha fades
    val searchBarHeight = SEARCH_BAR_HEIGHT * (1f - progress)
    val searchBarAlpha = (1f - progress * 1.5f).coerceIn(0f, 1f)

    // Search icon in top bar appears when search bar is collapsed
    val searchIconAlpha = ((progress - 0.3f) * 2f).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Welcome Header with Pixel Player inspired generous spacing
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Large multi-line greeting + subtitle
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = greetingMultiline,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 34.sp,
                        lineHeight = 38.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.welcome_to_library),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }

            // Top right actions: search icon (when collapsed) and analytics button
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search icon - appears when search bar is collapsed
                if (searchIconAlpha > 0.05f) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.graphicsLayer { alpha = searchIconAlpha }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_pdf),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Analytics button aligned to the top-right
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                    tooltip = { PlainTooltip { Text(stringResource(R.string.library_stats_desc)) } },
                    state = rememberTooltipState()
                ) {
                    IconButton(
                        onClick = onStatsClick,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Analytics,
                            contentDescription = stringResource(R.string.library_stats_desc),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Search bar - collapses as user scrolls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(searchBarHeight)
                .graphicsLayer { alpha = searchBarAlpha }
        ) {
            if (progress < 0.95f) { // Don't render when fully collapsed
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(onClick = onSearchClick),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search_pdf),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.search_your_pdfs_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                        )
                    }
                }
            }
        }
    }
}
