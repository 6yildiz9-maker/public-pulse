package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.PulseUiState
import com.example.ui.PulseViewModel
import com.example.ui.components.ScoreMetricPill
import com.example.ui.components.StarDistributionBar
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant
import java.util.Locale

@Composable
fun CompareScreen(
    uiState: PulseUiState,
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val celeb1 = uiState.compareCelebrity1 ?: uiState.celebrities.firstOrNull()
    val celeb2 = uiState.compareCelebrity2 ?: uiState.celebrities.getOrNull(1)

    var showMenu1 by remember { mutableStateOf(false) }
    var showMenu2 by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
            .testTag("compare_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CompareArrows,
                contentDescription = "Karşılaştır",
                tint = PopularityCyan,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "Çok Boyutlu Karşılaştırma",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tek puana indirgemeden iki kamuoyu figürünün göstergelerini kıyaslayın.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selectors Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Celebrity 1 Selector Box
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showMenu1 = true }
                        .border(1.dp, LikeabilityPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .testTag("compare_selector_1"),
                    color = SlateCard
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = celeb1?.name ?: "1. Kişi Seçin",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = LikeabilityPurple)
                    }
                }
                DropdownMenu(
                    expanded = showMenu1,
                    onDismissRequest = { showMenu1 = false },
                    modifier = Modifier.background(SlateCard)
                ) {
                    uiState.celebrities.forEach { celeb ->
                        DropdownMenuItem(
                            text = { Text(celeb.name, color = Color.White, fontSize = 13.sp) },
                            onClick = {
                                viewModel.selectCompareCelebrities(celeb, celeb2)
                                showMenu1 = false
                            }
                        )
                    }
                }
            }

            // Celebrity 2 Selector Box
            Box(modifier = Modifier.weight(1f)) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showMenu2 = true }
                        .border(1.dp, PopularityCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .testTag("compare_selector_2"),
                    color = SlateCard
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = celeb2?.name ?: "2. Kişi Seçin",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = PopularityCyan)
                    }
                }
                DropdownMenu(
                    expanded = showMenu2,
                    onDismissRequest = { showMenu2 = false },
                    modifier = Modifier.background(SlateCard)
                ) {
                    uiState.celebrities.forEach { celeb ->
                        DropdownMenuItem(
                            text = { Text(celeb.name, color = Color.White, fontSize = 13.sp) },
                            onClick = {
                                viewModel.selectCompareCelebrities(celeb1, celeb)
                                showMenu2 = false
                            }
                        )
                    }
                }
            }
        }

        if (celeb1 != null && celeb2 != null) {
            Spacer(modifier = Modifier.height(16.dp))

            // Comparison Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1
                CelebrityMiniHeader(celeb = celeb1, color = LikeabilityPurple, modifier = Modifier.weight(1f))
                // Card 2
                CelebrityMiniHeader(celeb = celeb2, color = PopularityCyan, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comparative Metrics Bars
            ComparisonMetricBar(
                title = "Sevilirlik (L)",
                val1 = celeb1.likeability,
                val2 = celeb2.likeability,
                name1 = celeb1.name,
                name2 = celeb2.name,
                color = LikeabilityPurple
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricBar(
                title = "Popülerlik (P)",
                val1 = celeb1.popularity,
                val2 = celeb2.popularity,
                name1 = celeb1.name,
                name2 = celeb2.name,
                color = PopularityCyan
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricBar(
                title = "Kutuplaşma (D)",
                val1 = celeb1.polarization,
                val2 = celeb2.polarization,
                name1 = celeb1.name,
                name2 = celeb2.name,
                color = PolarizationAmber,
                description = "Yüksek değer toplumun fikir ayrılığını gösterir (min p+, p-)."
            )

            Spacer(modifier = Modifier.height(12.dp))

            ComparisonMetricBar(
                title = "Momentum (M)",
                val1 = celeb1.momentum,
                val2 = celeb2.momentum,
                name1 = celeb1.name,
                name2 = celeb2.name,
                color = Color(0xFF10B981),
                isCenteredZero = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Oy Dağılımı Kıyaslaması",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${celeb1.name}:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = LikeabilityPurple
            )
            StarDistributionBar(
                ratings = celeb1.ratings,
                polarizationD = celeb1.polarization
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "${celeb2.name}:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PopularityCyan
            )
            StarDistributionBar(
                ratings = celeb2.ratings,
                polarizationD = celeb2.polarization
            )
        }
    }
}

@Composable
private fun CelebrityMiniHeader(
    celeb: Celebrity,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
        color = SlateCard
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(celeb.primaryColorHex).copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = celeb.avatarEmojiOrSymbol, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = celeb.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = celeb.profession,
                fontSize = 10.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ComparisonMetricBar(
    title: String,
    val1: Double,
    val2: Double,
    name1: String,
    name2: String,
    color: Color,
    description: String? = null,
    isCenteredZero: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(12.dp)),
        color = SlateCard
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = String.format(Locale.US, "%.1f vs %.1f", val1, val2),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (description != null) {
                Text(
                    text = description,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Two comparative rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name1,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    modifier = Modifier.width(90.dp)
                )
                LinearProgressIndicator(
                    progress = { (val1 / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = LikeabilityPurple,
                    trackColor = SlateCardVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format(Locale.US, "%.1f", val1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.width(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name2,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    modifier = Modifier.width(90.dp)
                )
                LinearProgressIndicator(
                    progress = { (val2 / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = PopularityCyan,
                    trackColor = SlateCardVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format(Locale.US, "%.1f", val2),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.width(36.dp)
                )
            }
        }
    }
}
