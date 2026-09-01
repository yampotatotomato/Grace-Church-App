package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyVerse
import com.example.ui.theme.*

/**
 * Clean and minimal Scripture Reading Component that displays the Daily Verse.
 * Designed with modern Material 3 typography, serene contrast, and minimalist action affordances.
 */
@Composable
fun DailyScriptureCard(
    dailyVerse: DailyVerse,
    translation: String,
    isBookmarked: Boolean,
    onReadChapter: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showReflection: Boolean = true,
    reflectionText: String? = "Take a quiet moment to reflect on God's unwavering promises today. Meditate on how His grace sustains your path through every season.",
    onListen: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = Color.Black.copy(alpha = 0.05f),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .testTag("daily_scripture_reading_component"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header Row: Minimal badge + Date + Translation Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(ChurchGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.Sparkles,
                            contentDescription = null,
                            tint = ChurchGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "DAILY SCRIPTURE",
                        style = AppleTypographyStyles.referenceTag,
                        color = ChurchGold,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 11.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Translation Tag
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = translation,
                            style = AppleTypographyStyles.referenceTag,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Date display
                    Text(
                        text = dailyVerse.date,
                        style = AppleTypographyStyles.audioTimer,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Scripture Quote Layout with subtle accent bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Subtle vertical accent marker line
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(ChurchGold, ScriptureAccent)
                            )
                        )
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "“${dailyVerse.text}”",
                        style = AppleTypographyStyles.scriptureText(17.5f),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 27.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "— ${dailyVerse.reference}",
                            style = AppleTypographyStyles.referenceTag,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        if (dailyVerse.theme.isNotBlank()) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = ScriptureAccent.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = dailyVerse.theme,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ScriptureAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Expandable Reflection Section
            if (showReflection && reflectionText != null) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = CupertinoIcons.HeartFill,
                                contentDescription = null,
                                tint = DevotionAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PONDER & PRAY",
                                style = AppleTypographyStyles.referenceTag,
                                color = DevotionAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = reflectionText,
                            style = AppleTypographyStyles.devotionalProse(14f),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 21.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Toolbar (Minimal & Clean)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Primary Action: Read Chapter Pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RoyalNavy,
                    modifier = Modifier
                        .clickable(onClick = onReadChapter)
                        .testTag("daily_scripture_read_chapter_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Read Chapter",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ChurchGold,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Action Icons (Bookmark, Share/Copy, Reflection toggle, Audio)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Reflection Toggle Button
                    if (showReflection && reflectionText != null) {
                        IconButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("daily_scripture_toggle_reflection")
                        ) {
                            Icon(
                                imageVector = if (isExpanded) CupertinoIcons.HeartFill else CupertinoIcons.Heart,
                                contentDescription = "Toggle Reflection",
                                tint = if (isExpanded) DevotionAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Audio Recitation Button
                    IconButton(
                        onClick = {
                            isListening = !isListening
                            onListen?.invoke()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("daily_scripture_listen_button")
                    ) {
                        Icon(
                            imageVector = if (isListening) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Listen to verse",
                            tint = if (isListening) ChurchGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Bookmark Button
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("daily_scripture_bookmark_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) CupertinoIcons.BookmarkFill else CupertinoIcons.Bookmark,
                            contentDescription = "Bookmark Daily Verse",
                            tint = if (isBookmarked) ScriptureAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Share/Copy Button
                    IconButton(
                        onClick = {
                            if (onShare != null) {
                                onShare()
                            } else {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "“${dailyVerse.text}” — ${dailyVerse.reference} ($translation)"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Daily Scripture"))
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("daily_scripture_share_button")
                    ) {
                        Icon(
                            imageVector = CupertinoIcons.Share,
                            contentDescription = "Share Daily Verse",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
