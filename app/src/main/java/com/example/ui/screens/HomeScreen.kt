package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Celebrity
import com.example.data.model.CelebrityCategory
import com.example.ui.PulseUiState
import com.example.ui.PulseViewModel
import com.example.ui.ScoreSortOption
import com.example.ui.components.CelebrityCard
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: PulseUiState,
    viewModel: PulseViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Hero Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.public_pulse_banner),
                    contentDescription = "Public Pulse Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    ObsidianDark.copy(alpha = 0.8f),
                                    ObsidianDark
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = PopularityCyan.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ŞEFFAF KAMUOYU ALGISI",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PopularityCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = LikeabilityPurple.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "BİLEŞİK CEZA PUANI YOKTUR",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = LikeabilityPurple,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Public Pulse / Celebrity Score",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Black Mirror 'Nosedive'dan ilham alan; linç döngüsünü reddeden, çok boyutlu ve itiraza açık algı platformu.",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Search Bar
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = { Text("Kişi adı, unvan veya etiket ara...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Ara",
                            tint = PopularityCyan
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Temizle",
                                    tint = Color.Gray
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_text_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PopularityCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedContainerColor = SlateCard,
                        unfocusedContainerColor = SlateCard,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { viewModel.onCategorySelected(null) },
                        label = { Text("Tümü (100)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PopularityCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = SlateCard,
                            labelColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("category_all")
                    )
                }
                items(CelebrityCategory.values()) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) },
                        label = { Text(category.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PopularityCyan,
                            selectedLabelColor = Color.Black,
                            containerColor = SlateCard,
                            labelColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("category_${category.name.lowercase()}")
                    )
                }
            }
        }

        // Sort By Segment
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sıralama Ölçütü:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        text = "${uiState.filteredCelebrities.size} profil listeleniyor",
                        fontSize = 11.sp,
                        color = PopularityCyan
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(ScoreSortOption.values()) { option ->
                        val isSelected = uiState.sortOption == option
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.onSortOptionChanged(option) }
                                .border(
                                    1.dp,
                                    if (isSelected) LikeabilityPurple else SlateBorder,
                                    RoundedCornerShape(8.dp)
                                ),
                            color = if (isSelected) LikeabilityPurple.copy(alpha = 0.2f) else SlateCardVariant
                        ) {
                            Text(
                                text = option.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.LightGray,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Feed of Celebrities
        if (uiState.filteredCelebrities.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Arama kriterine uygun kişi bulunamadı.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(uiState.filteredCelebrities, key = { it.id }) { celebrity ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    CelebrityCard(
                        celebrity = celebrity,
                        onCardClick = { viewModel.selectCelebrityDetail(celebrity) },
                        onVoteClick = { viewModel.showVotingSheet(celebrity) },
                        onTransparencyClick = { viewModel.showTransparencyDialog(celebrity) }
                    )
                }
            }
        }
    }
}
