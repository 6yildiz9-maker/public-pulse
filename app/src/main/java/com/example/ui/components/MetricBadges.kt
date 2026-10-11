package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConfidenceLevel
import com.example.ui.theme.CircuitBreakerCoral
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.MomentumGreen
import com.example.ui.theme.MomentumRed
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.ReboundEmerald
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCardVariant
import java.util.Locale

@Composable
fun ScoreMetricPill(
    label: String,
    score: Double,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    scoreSuffix: String = ""
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Column {
                Text(
                    text = label,
                    fontSize = 9.sp,
                    color = color.copy(alpha = 0.9f),
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 10.sp
                )
                Text(
                    text = String.format(Locale.US, "%.1f%s", score, scoreSuffix),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MomentumChip(
    momentum: Double,
    modifier: Modifier = Modifier
) {
    val isPositive = momentum > 0.05
    val isNegative = momentum < -0.05
    val color = when {
        isPositive -> MomentumGreen
        isNegative -> MomentumRed
        else -> Color.Gray
    }
    val icon = when {
        isPositive -> Icons.Default.TrendingUp
        isNegative -> Icons.Default.TrendingDown
        else -> Icons.Default.TrendingFlat
    }
    val prefix = if (isPositive) "+" else ""

    Surface(
        modifier = modifier.clip(RoundedCornerShape(8.dp)),
        color = color.copy(alpha = 0.18f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Momentum",
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = String.format(Locale.US, "%s%.1f", prefix, momentum),
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ConfidenceBadgeView(
    level: ConfidenceLevel,
    modifier: Modifier = Modifier
) {
    val color = when (level) {
        ConfidenceLevel.YUKSEK -> ReboundEmerald
        ConfidenceLevel.ORTA -> PopularityCyan
        ConfidenceLevel.DUSUK -> PolarizationAmber
        ConfidenceLevel.YETERSIZ -> CircuitBreakerCoral
    }

    Surface(
        modifier = modifier.clip(RoundedCornerShape(6.dp)),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = level.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
    }
}

@Composable
fun CircuitBreakerBannerView(
    reason: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .testTag("circuit_breaker_banner"),
        color = CircuitBreakerCoral.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, CircuitBreakerCoral.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Devre Kesici",
                tint = CircuitBreakerCoral,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DEVRE KESİCİ DEVREDE (±8.0 Puan Sınırı)",
                    color = CircuitBreakerCoral,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = reason ?: "Son 24 saatteki ani oy artışı soğuma korumasına alındı. Puanlar kademeli işlenmektedir.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
fun ReboundBadgeView(
    story: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .testTag("rebound_badge_banner"),
        color = ReboundEmerald.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, ReboundEmerald.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = "Toparlanma Rozeti",
                tint = ReboundEmerald,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "REBOUND (TOPARLANMA) ROZETİ",
                    color = ReboundEmerald,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = story ?: "Zorlu dönemin ardından üretkenlik ve kamuoyu desteğiyle güçlü bir geri dönüş sergiledi.",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
fun StarDistributionBar(
    ratings: Map<Int, Int>,
    polarizationD: Double,
    modifier: Modifier = Modifier
) {
    val total = ratings.values.sum().coerceAtLeast(1)
    val pos = ((ratings[4] ?: 0) + (ratings[5] ?: 0)).toDouble() / total
    val neutral = (ratings[3] ?: 0).toDouble() / total
    val neg = ((ratings[1] ?: 0) + (ratings[2] ?: 0)).toDouble() / total

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SlateCardVariant, RoundedCornerShape(12.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Görüş Dağılımı & Kutuplaşma",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = String.format(Locale.US, "Kutuplaşma: %.1f/100", polarizationD),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PolarizationAmber
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Multi-segment progress bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF2D3748))
        ) {
            if (neg > 0.001) {
                Box(
                    modifier = Modifier
                        .weight(neg.toFloat().coerceAtLeast(0.01f))
                        .height(10.dp)
                        .background(CircuitBreakerCoral)
                )
            }
            if (neutral > 0.001) {
                Box(
                    modifier = Modifier
                        .weight(neutral.toFloat().coerceAtLeast(0.01f))
                        .height(10.dp)
                        .background(Color(0xFF64748B))
                )
            }
            if (pos > 0.001) {
                Box(
                    modifier = Modifier
                        .weight(pos.toFloat().coerceAtLeast(0.01f))
                        .height(10.dp)
                        .background(LikeabilityPurple)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CircuitBreakerCoral))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format(Locale.US, "Olumsuz (1-2★): %.0f%%", neg * 100),
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF64748B)))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format(Locale.US, "Nötr (3★): %.0f%%", neutral * 100),
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LikeabilityPurple))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format(Locale.US, "Olumlu (4-5★): %.0f%%", pos * 100),
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}
