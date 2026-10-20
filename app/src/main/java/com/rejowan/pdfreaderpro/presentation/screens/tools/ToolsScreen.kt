package com.rejowan.pdfreaderpro.presentation.screens.tools

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMerge
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Reorder
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.rejowan.pdfreaderpro.R
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToCompressTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToImageToPdfTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToLockTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToMergeTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToPageNumbersTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToPdfToImageTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToRemovePagesTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToReorderTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToRotateTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToSignTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToSplitTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToUnlockTool
import com.rejowan.pdfreaderpro.presentation.navigation.navigateToWatermarkTool
import kotlinx.coroutines.delay

// Accent colors (consistent with app design system)
private val AccentPurple = Color(0xFF9575CD)
private val AccentBlue = Color(0xFF64B5F6)
private val AccentTeal = Color(0xFF4DB6AC)
private val AccentAmber = Color(0xFFFFB74D)
private val AccentGreen = Color(0xFF81C784)

enum class ToolCategory(@StringRes val titleRes: Int, val accentColor: Color) {
    ORGANIZE(R.string.tool_category_organize, AccentPurple),
    EDIT(R.string.tool_category_edit, AccentBlue),
    SECURITY(R.string.tool_category_security, AccentAmber),
    CONVERT(R.string.tool_category_convert, AccentTeal)
}

data class PdfTool(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    val icon: ImageVector,
    val category: ToolCategory,
    val isEnabled: Boolean = false
)

private val pdfTools = listOf(
    PdfTool(
        "merge",
        R.string.tool_merge_pdfs,
        R.string.tool_merge_desc,
        Icons.AutoMirrored.Filled.CallMerge,
        ToolCategory.ORGANIZE,
        isEnabled = true
    ),
    PdfTool(
        "split",
        R.string.tool_split_pdf,
        R.string.tool_split_desc,
        Icons.AutoMirrored.Filled.CallSplit,
        ToolCategory.ORGANIZE,
        isEnabled = true
    ),
    PdfTool(
        "compress",
        R.string.tool_compress_pdf,
        R.string.tool_compress_desc,
        Icons.Default.Compress,
        ToolCategory.ORGANIZE,
        isEnabled = true
    ),
    PdfTool(
        "rotate",
        R.string.tool_rotate_pages,
        R.string.tool_rotate_desc,
        Icons.AutoMirrored.Filled.RotateRight,
        ToolCategory.ORGANIZE,
        isEnabled = true
    ),
    PdfTool(
        "reorder",
        R.string.tool_reorder_pages,
        R.string.tool_reorder_desc,
        Icons.Default.Reorder,
        ToolCategory.ORGANIZE,
        isEnabled = true
    ),
    PdfTool(
        "remove_pages",
        R.string.tool_remove_pages,
        R.string.tool_remove_pages_desc,
        Icons.Default.DeleteSweep,
        ToolCategory.EDIT,
        isEnabled = true
    ),
    PdfTool(
        "watermark",
        R.string.tool_watermark,
        R.string.tool_watermark_desc,
        Icons.Default.WaterDrop,
        ToolCategory.EDIT,
        isEnabled = true
    ),
    PdfTool(
        "page_numbers",
        R.string.tool_page_numbers,
        R.string.tool_page_numbers_desc,
        Icons.Default.FormatListNumbered,
        ToolCategory.EDIT,
        isEnabled = true
    ),
    PdfTool(
        "lock_pdf",
        R.string.tool_lock_pdf,
        R.string.tool_lock_desc,
        Icons.Default.Lock,
        ToolCategory.SECURITY,
        isEnabled = true
    ),
    PdfTool(
        "unlock_pdf",
        R.string.tool_unlock_pdf,
        R.string.tool_unlock_desc,
        Icons.Default.LockOpen,
        ToolCategory.SECURITY,
        isEnabled = true
    ),
    PdfTool(
        "sign_pdf",
        R.string.tool_sign_pdf,
        R.string.tool_sign_pdf_desc,
        Icons.Default.Draw,
        ToolCategory.SECURITY,
        isEnabled = true
    ),
    PdfTool(
        "img_to_pdf",
        R.string.tool_image_to_pdf,
        R.string.tool_image_to_pdf_desc,
        Icons.Default.Image,
        ToolCategory.CONVERT,
        isEnabled = true
    ),
    PdfTool(
        "pdf_to_img",
        R.string.tool_pdf_to_image,
        R.string.tool_pdf_to_image_desc,
        Icons.Default.Photo,
        ToolCategory.CONVERT,
        isEnabled = true
    ),
)

@Composable
fun ToolsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Signing needs the page in view, so this tool picks a file and hands it to
    // the reader with the signature editor already on.
    val signPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        val path = com.rejowan.pdfreaderpro.util.FileOperations.copyContentUriToCache(context, uri)
        if (path != null) navController.navigateToSignTool(path)
    }
    val pickPdfToSign = { signPickerLauncher.launch(arrayOf("application/pdf")) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        var animationIndex = 0

        ToolCategory.entries.forEach { category ->
            val toolsInCategory = pdfTools.filter { it.category == category }

            if (toolsInCategory.isNotEmpty()) {
                val headerDelay = animationIndex * 50
                animationIndex++

                // Full-width section header spanning both columns
                item(
                    span = { GridItemSpan(maxLineSpan) },
                    key = "header_${category.name}"
                ) {
                    SectionLabel(
                        text = stringResource(category.titleRes),
                        delay = headerDelay,
                        modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                    )
                }

                // Grid items for each tool in the category
                items(
                    items = toolsInCategory,
                    key = { it.id }
                ) { tool ->
                    val itemDelay = animationIndex * 50
                    animationIndex++

                    ToolItem(
                        tool = tool,
                        accentColor = category.accentColor,
                        animationDelay = itemDelay,
                        onClick = {
                            when (tool.id) {
                                "merge" -> navController.navigateToMergeTool()
                                "split" -> navController.navigateToSplitTool("")
                                "compress" -> navController.navigateToCompressTool("")
                                "rotate" -> navController.navigateToRotateTool("")
                                "reorder" -> navController.navigateToReorderTool("")
                                "lock_pdf" -> navController.navigateToLockTool("")
                                "unlock_pdf" -> navController.navigateToUnlockTool("")
                                "sign_pdf" -> pickPdfToSign()
                                "remove_pages" -> navController.navigateToRemovePagesTool("")
                                "watermark" -> navController.navigateToWatermarkTool("")
                                "page_numbers" -> navController.navigateToPageNumbersTool("")
                                "img_to_pdf" -> navController.navigateToImageToPdfTool()
                                "pdf_to_img" -> navController.navigateToPdfToImageTool("")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(
    text: String,
    delay: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        isVisible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(200),
        label = "section alpha"
    )

    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = MaterialTheme.typography.labelMedium.letterSpacing * 1.5f
        ),
        color = MaterialTheme.colorScheme.primary.copy(alpha = alpha),
        modifier = modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
private fun ToolItem(
    tool: PdfTool,
    accentColor: Color,
    animationDelay: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.95f,
        animationSpec = tween(250, easing = FastOutSlowInEasing),
        label = "tool scale"
    )

    val effectiveAccentColor = if (tool.isEnabled) accentColor else accentColor.copy(alpha = 0.4f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (tool.isEnabled) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = accentColor),
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon prominently at the top/center
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = effectiveAccentColor.copy(alpha = 0.12f)
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = stringResource(R.string.cd_decorative),
                    modifier = Modifier
                        .padding(12.dp)
                        .size(28.dp),
                    tint = effectiveAccentColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title directly below it (no subtitle/description)
            Text(
                text = stringResource(tool.nameRes),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = if (tool.isEnabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
            )

            // Coming soon indicator if disabled
            if (!tool.isEnabled) {
                Spacer(modifier = Modifier.height(6.dp))
                ComingSoonBadge()
            }
        }
    }
}

@Composable
private fun ComingSoonBadge() {
    Box(
        modifier = Modifier
            .background(
                color = AccentAmber.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = stringResource(R.string.coming_soon),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = AccentAmber
        )
    }
}
