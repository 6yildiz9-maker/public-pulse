package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Celebrity
import com.example.ui.PulseUiState
import com.example.ui.PulseViewModel
import com.example.ui.RankingTab
import com.example.ui.components.CelebrityCard
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard

@Composable
fun RankingsScreen(
    uiState: PulseUiState,
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab = uiState.selectedRankingTab

    val sortedList: List<Celebrity> = when (currentTab) {
        RankingTab.TOP_LIKED -> uiState.celebrities.sortedByDescending { it.likeability }
        RankingTab.TOP_POPULAR -> uiState.celebrities.sortedByDescending { it.popularity }
        RankingTab.TOP_RISING -> uiState.celebrities.sortedByDescending { it.momentum }
        RankingTab.TOP_POLARIZED -> uiState.celebrities.sortedByDescending { it.polarization }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
    ) {
        // Top Header
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Haftalık İndeks Listeleri",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Her liste kendi bağımsız ölçütüne göre düzenlenir. Sabit negatif veya linç listeleri sistem dışıdır.",
                fontSize = 11.sp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Non-accusatory anti-lynch principle banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = SlateCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Bilgi",
                        tint = PopularityCyan,
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = "İlke: Düşüşü değil toparlanmayı öne çıkarıyoruz. Skorlar ceza sistemi değil, toplumun ve dijital verilerin çok boyutlu algısıdır.",
                        fontSize = 10.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = currentTab.ordinal,
            containerColor = SlateCard,
            contentColor = Color.White,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[currentTab.ordinal]),
                    color = when (currentTab) {
                        RankingTab.TOP_LIKED -> LikeabilityPurple
                        RankingTab.TOP_POPULAR -> PopularityCyan
                        RankingTab.TOP_RISING -> Color(0xFF10B981)
                        RankingTab.TOP_POLARIZED -> PolarizationAmber
                    }
                )
            }
        ) {
            RankingTab.values().forEach { tab ->
                val isSelected = currentTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.onRankingTabSelected(tab) },
                    text = {
                        Text(
                            text = tab.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("ranking_tab_${tab.name.lowercase()}")
                )
            }
        }

        // Subtitle explaining the metric
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF0F1520)
        ) {
            Text(
                text = currentTab.subtitle,
                fontSize = 11.sp,
                color = PopularityCyan,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("rankings_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            itemsIndexed(sortedList, key = { _, item -> item.id }) { index, celebrity ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    CelebrityCard(
                        celebrity = celebrity,
                        onCardClick = { viewModel.selectCelebrityDetail(celebrity) },
                        onVoteClick = { viewModel.showVotingSheet(celebrity) },
                        onTransparencyClick = { viewModel.showTransparencyDialog(celebrity) },
                        rankingNumber = index + 1
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
