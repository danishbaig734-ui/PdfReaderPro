package com.rejowan.pdfreaderpro.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeHeader(
    onStatsClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSortClick: (() -> Unit)? = null
) {
    val greeting = getGreetingMultiLine()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            ) {
                Text(
                    text = greeting,
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

            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = {
                    PlainTooltip {
                        Text(stringResource(R.string.library_statistics))
                    }
                },
                state = rememberTooltipState()
            ) {
                IconButton(
                    onClick = onStatsClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Analytics,
                        contentDescription = stringResource(R.string.library_statistics),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
