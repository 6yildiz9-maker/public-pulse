package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Celebrity
import com.example.ui.theme.CircuitBreakerCoral
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.ReboundEmerald
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CelebrityCard(
    celebrity: Celebrity,
    onCardClick: () -> Unit,
    onVoteClick: () -> Unit,
    onTransparencyClick: () -> Unit,
    modifier: Modifier = Modifier,
    rankingNumber: Int? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .testTag("celebrity_card_${celebrity.id}"),
        colors = CardDefaults.cardColors(containerColor = SlateCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Avatar, Name, Category, Momentum & Ranking
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (rankingNumber != null) {
                    Text(
                        text = "#$rankingNumber",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = when (rankingNumber) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> Color.Gray
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                // Avatar Icon with dynamic gradient
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(celebrity.primaryColorHex),
                                    Color(celebrity.primaryColorHex).copy(alpha = 0.5f)
                                )
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = celebrity.avatarEmojiOrSymbol,
                        fontSize = 22.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = celebrity.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        ConfidenceBadgeView(level = celebrity.confidenceLevel)
                    }
                    Text(
                        text = celebrity.profession,
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                MomentumChip(momentum = celebrity.momentum)
            }

            // Circuit Breaker or Rebound Notices if active
            if (celebrity.isCircuitBreakerActive) {
                Spacer(modifier = Modifier.height(8.dp))
                CircuitBreakerBannerView(reason = celebrity.circuitBreakerReason)
            } else if (celebrity.hasReboundBadge) {
                Spacer(modifier = Modifier.height(8.dp))
                ReboundBadgeView(story = celebrity.reboundStory)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // THE 3 DISTINCT SCORES (NO SINGLE COMPOSITE SCORE)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ScoreMetricPill(
                    label = "SEVİLİRLİK (L)",
                    score = celebrity.likeability,
                    color = LikeabilityPurple,
                    icon = Icons.Default.Favorite,
                    modifier = Modifier.weight(1f)
                )
                ScoreMetricPill(
                    label = "POPÜLERLİK (P)",
                    score = celebrity.popularity,
                    color = PopularityCyan,
                    icon = Icons.Default.Visibility,
                    modifier = Modifier.weight(1f)
                )
                ScoreMetricPill(
                    label = "KUTUPLAŞMA (D)",
                    score = celebrity.polarization,
                    color = PolarizationAmber,
                    icon = Icons.Default.Tune,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Perception Tags
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                celebrity.tags.take(3).forEach { tag ->
                    Surface(
                        color = SlateCardVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "#$tag",
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: "Bu Skor Neye Göre?" and "Oy Ver"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onTransparencyClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("transparency_button_${celebrity.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PopularityCyan
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PopularityCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Şeffaflık",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Neye Göre?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onVoteClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .testTag("vote_button_${celebrity.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LikeabilityPurple
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HowToVote,
                        contentDescription = "Oy Ver",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Oy Ver",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
