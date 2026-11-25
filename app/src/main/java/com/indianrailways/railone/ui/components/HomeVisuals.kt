package com.indianrailways.railone.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.indianrailways.railone.R
import com.indianrailways.railone.ui.theme.RailOneBlue
import com.indianrailways.railone.ui.theme.RailOneOrange

enum class JourneyCardType {
    RESERVED,
    UNRESERVED,
    PLATFORM
}

/**
 * Official RailOne Journey Planner card:
 * Rounded illustrated image box on top + clean centered label directly underneath.
 */
@Composable
fun JourneyPlannerCard(
    title: String,
    type: JourneyCardType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
        ) {
            val imageRes = when (type) {
                JourneyCardType.RESERVED -> R.drawable.img_journey_reserved
                JourneyCardType.UNRESERVED -> R.drawable.img_journey_unreserved
                JourneyCardType.PLATFORM -> R.drawable.img_journey_platform
            }
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF334155)
        )
    }
}

/**
 * Authentic RailOne Wordmark with signature orange dot over the letter 'i'.
 */
@Composable
fun RailOneLogo(
    modifier: Modifier = Modifier,
    fontSizeSp: Float = 26f
) {
    val scale = fontSizeSp / 26f
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Ra",
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF3A4149),
            letterSpacing = (-0.5).sp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = (0.6f * scale).dp)
        ) {
            Box(
                modifier = Modifier
                    .size((5.2f * scale).dp)
                    .clip(CircleShape)
                    .background(RailOneOrange)
            )
            Spacer(modifier = Modifier.height((1.8f * scale).dp))
            Box(
                modifier = Modifier
                    .width((2.6f * scale).dp)
                    .height((13.5f * scale).dp)
                    .background(Color(0xFF3A4149), RoundedCornerShape(1.dp))
            )
        }
        Text(
            text = "l",
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF3A4149),
            letterSpacing = (-0.5).sp
        )
        Text(
            text = "One",
            fontSize = fontSizeSp.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3A4149),
            letterSpacing = (-0.5).sp
        )
    }
}

/**
 * Stylized blue train nose outline emblem shown on splash screen.
 */
@Composable
fun RailOneTrainOutlineLogo(
    modifier: Modifier = Modifier,
    strokeColor: Color = RailOneBlue
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.032f, cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Outer bullet-train nose contour
        val nose = Path().apply {
            moveTo(w * 0.14f, h * 0.36f)
            quadraticTo(w * 0.56f, h * 0.12f, w * 0.78f, h * 0.24f)
            quadraticTo(w * 0.90f, h * 0.33f, w * 0.88f, h * 0.64f)
            quadraticTo(w * 0.85f, h * 0.78f, w * 0.66f, h * 0.76f)
            lineTo(w * 0.26f, h * 0.72f)
            close()
        }
        drawPath(nose, color = strokeColor, style = stroke)

        // Windshield curve
        val glass = Path().apply {
            moveTo(w * 0.50f, h * 0.28f)
            quadraticTo(w * 0.72f, h * 0.42f, w * 0.68f, h * 0.67f)
        }
        drawPath(glass, color = strokeColor, style = stroke)

        // Speed streaks
        drawLine(
            color = strokeColor,
            start = Offset(w * 0.16f, h * 0.48f),
            end = Offset(w * 0.46f, h * 0.42f),
            strokeWidth = w * 0.026f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = strokeColor,
            start = Offset(w * 0.20f, h * 0.60f),
            end = Offset(w * 0.55f, h * 0.58f),
            strokeWidth = w * 0.026f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Left Top-Bar Language Switcher circular button ("A / अ") matching official screenshot.
 */
@Composable
fun RailOneLanguageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.White)
            .border(1.dp, Color(0xFFE5E7EB), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Subtle curved swap arcs
            val arcColor = Color(0xFF93C5FD)
            drawArc(
                color = arcColor,
                startAngle = 190f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(size.width * 0.22f, size.height * 0.20f),
                size = Size(size.width * 0.55f, size.height * 0.55f),
                style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = arcColor,
                startAngle = 15f,
                sweepAngle = 70f,
                useCenter = false,
                topLeft = Offset(size.width * 0.22f, size.height * 0.24f),
                size = Size(size.width * 0.55f, size.height * 0.55f),
                style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "A",
            color = RailOneBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(x = (-6).dp, y = (-5).dp)
        )
        Text(
            text = "अ",
            color = RailOneBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.offset(x = 6.dp, y = 6.dp)
        )
    }
}

/**
 * Right Top-Bar Notification Bell circular button with red unread counter badge.
 */
@Composable
fun RailOneNotificationButton(
    unreadCount: Int,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFE5E7EB), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = Color(0xFF334155),
                modifier = Modifier.size(22.dp)
            )
        }
        if (unreadCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-1).dp, y = 1.dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE51C23)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Crisp WAVES icon matching the dark slate "Go To WAVES" tile.
 */
@Composable
fun WavesLogoIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val stroke = Stroke(width = w * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        val path = Path().apply {
            moveTo(w * 0.14f, h * 0.25f)
            lineTo(w * 0.34f, h * 0.80f)
            lineTo(w * 0.50f, h * 0.16f)
            lineTo(w * 0.66f, h * 0.80f)
            lineTo(w * 0.86f, h * 0.25f)
        }
        drawPath(path, color = Color.White, style = stroke)
    }
}

/**
 * Scenic sunset railway bridge banner with X, Facebook, Instagram, and YouTube icons.
 */
@Composable
fun SocialMediaBannerCard(
    onPlatformClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(148.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Sunset sky & sea railway viaduct illustration
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Sunset sky gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E5799),
                            Color(0xFF6B8DD6),
                            Color(0xFFCE8CA5),
                            Color(0xFFF3B88B)
                        )
                    ),
                    size = Size(w, h * 0.68f)
                )

                // Ocean / water shimmer
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF8C7AA9),
                            Color(0xFF43506C)
                        )
                    ),
                    topLeft = Offset(0f, h * 0.68f),
                    size = Size(w, h * 0.32f)
                )

                // Viaduct bridge deck & pillars
                drawRect(
                    color = Color(0xFF2B2D42),
                    topLeft = Offset(0f, h * 0.67f),
                    size = Size(w, h * 0.08f)
                )
                for (i in 0..8) {
                    val px = w * (0.06f + i * 0.11f)
                    drawRect(
                        color = Color(0xFF1F2233),
                        topLeft = Offset(px, h * 0.74f),
                        size = Size(w * 0.025f, h * 0.26f)
                    )
                }

                // Distant express train silhouette on the bridge
                drawRoundRect(
                    color = Color(0xFF151926),
                    topLeft = Offset(w * 0.05f, h * 0.58f),
                    size = Size(w * 0.44f, h * 0.09f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Red locomotive accent
                drawRoundRect(
                    color = Color(0xFFB71C1C),
                    topLeft = Offset(w * 0.40f, h * 0.585f),
                    size = Size(w * 0.09f, h * 0.085f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
                // Headlight glow
                drawCircle(
                    color = Color(0xFFFFF59D),
                    radius = 4.dp.toPx(),
                    center = Offset(w * 0.485f, h * 0.63f)
                )
            }

            // Soft vignette for icon legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // X
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable { onPlatformClick("X (@RailMinIndia)") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("𝕏", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }

                    // Facebook
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1877F2))
                            .clickable { onPlatformClick("Facebook (Ministry of Railways)") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("f", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    // Instagram
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFEDA75), Color(0xFFFA7E1E), Color(0xFFD62976), Color(0xFF962FBF), Color(0xFF4F5BD5))
                                )
                            )
                            .clickable { onPlatformClick("Instagram (@railminindia)") },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            drawRoundRect(
                                color = Color.White,
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 5.5.dp.toPx(),
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 1.5.dp.toPx(),
                                center = Offset(size.width * 0.78f, size.height * 0.22f)
                            )
                        }
                    }

                    // YouTube
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFF0000))
                            .clickable { onPlatformClick("YouTube (Ministry of Railways)") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("▶", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
