package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Celebrity
import com.example.ui.PulseViewModel
import com.example.ui.components.CircuitBreakerBannerView
import com.example.ui.components.ConfidenceBadgeView
import com.example.ui.components.MomentumChip
import com.example.ui.components.ReboundBadgeView
import com.example.ui.components.ScoreMetricPill
import com.example.ui.components.StarDistributionBar
import com.example.ui.theme.CircuitBreakerCoral
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.ReboundEmerald
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    celebrity: Celebrity,
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.selectCelebrityDetail(null)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = ObsidianDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = celebrity.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectCelebrityDetail(null) },
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.showTransparencyDialog(celebrity) },
                        modifier = Modifier.testTag("detail_transparency_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Şeffaflık",
                            tint = PopularityCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateCard)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showVotingSheet(celebrity) },
                containerColor = LikeabilityPurple,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("detail_vote_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.HowToVote, contentDescription = "Oy Ver")
                    Text("Oy Ver", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 72.dp)
        ) {
            // Profile Header Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp)),
                color = SlateCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(celebrity.primaryColorHex),
                                            Color(celebrity.primaryColorHex).copy(alpha = 0.5f)
                                        )
                                    )
                                )
                                .border(2.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = celebrity.avatarEmojiOrSymbol,
                                fontSize = 32.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = celebrity.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                ConfidenceBadgeView(level = celebrity.confidenceLevel)
                            }
                            Text(
                                text = celebrity.profession,
                                fontSize = 12.sp,
                                color = PopularityCyan,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Kategori: ${celebrity.category.title}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        MomentumChip(momentum = celebrity.momentum)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = celebrity.bio,
                        fontSize = 13.sp,
                        color = Color.LightGray,
                        lineHeight = 18.sp
                    )

                    if (celebrity.recentActivity.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            color = SlateCardVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Güncel",
                                    tint = PopularityCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Son Gündem: ${celebrity.recentActivity}",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        celebrity.tags.forEach { tag ->
                            Surface(
                                color = SlateCardVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "#$tag",
                                    fontSize = 11.sp,
                                    color = Color(0xFFBAE6FD),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Circuit Breaker or Rebound
            if (celebrity.isCircuitBreakerActive) {
                CircuitBreakerBannerView(reason = celebrity.circuitBreakerReason)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (celebrity.hasReboundBadge) {
                ReboundBadgeView(story = celebrity.reboundStory)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 3 DISTINCT METRIC CARDS (NO SINGLE COMPOSITE SCORE)
            Text(
                text = "3 Bağımsız Gösterge (Bileşik Puan Yoktur)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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

            Spacer(modifier = Modifier.height(14.dp))

            // STAR DISTRIBUTION & POLARIZATION VISUALIZER
            StarDistributionBar(
                ratings = celebrity.ratings,
                polarizationD = celebrity.polarization
            )

            Spacer(modifier = Modifier.height(14.dp))

            // WIKIPEDIA ANALYTICS
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = SlateCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Wikipedia",
                                tint = PopularityCyan
                            )
                            Text(
                                text = "Wikimedia Resmî Analytics",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Surface(
                            color = PopularityCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ÜCRETSİZ / ŞEFFAF API",
                                fontSize = 9.sp,
                                color = PopularityCyan,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sayfa Başlığı: ${celebrity.wikiTitle}",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = "Son 30 Gün Ziyareti: ${celebrity.wikiMonthlyViews} görüntülenme",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Kategori Ağırlıklı Popülerlik Skoru: ${String.format(Locale.US, "%.1f", celebrity.popularity)} / 100",
                        fontSize = 12.sp,
                        color = PopularityCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TRANSPARENCY CTA
            Button(
                onClick = { viewModel.showTransparencyDialog(celebrity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("detail_view_transparency_button"),
                colors = ButtonDefaults.buttonColors(containerColor = SlateCardVariant),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PopularityCyan.copy(alpha = 0.4f))
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Şeffaflık",
                    tint = PopularityCyan
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Bu Skor Neye Göre? (Tüm Formülü İncele)",
                    color = PopularityCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // OBJECTION AND CORRECTION SECTION
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = SlateCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = "İtiraz",
                                tint = Color.Gray
                            )
                            Text(
                                text = "Düzeltme & İtiraz Kanalı (KVKK / Tekzip)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    if (celebrity.objectionNote != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            color = SlateCardVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PopularityCyan.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Temsilci Açıklaması (${celebrity.objectionNote.submittedDate})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PopularityCyan
                                    )
                                    Text(
                                        text = celebrity.objectionNote.status,
                                        fontSize = 10.sp,
                                        color = ReboundEmerald,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = celebrity.objectionNote.statement,
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Bu profil hakkında kayıtlı bir itiraz veya düzeltme notu bulunmamaktadır. Kişi veya temsilcisi şeffaflık kütüğüne resmi not düşebilir.",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { viewModel.showObjectionDialog(celebrity) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("detail_file_objection_button"),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Gray.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "Resmi Düzeltme / İtiraz Bildir",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
