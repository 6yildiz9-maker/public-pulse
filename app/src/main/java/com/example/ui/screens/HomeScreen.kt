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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.data.model.CelebrityCategory
import com.example.ui.PulseUiState
import com.example.ui.PulseViewModel
import com.example.ui.ScoreSortOption
import com.example.ui.components.CelebrityCard
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.ReboundEmerald
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
                                text = "GERÇEK WIKIMEDIA VERİSİ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = PopularityCyan,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Surface(
                            color = ReboundEmerald.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ÜST SINIR: 1000 KİŞİ",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ReboundEmerald,
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
                        text = "Wikimedia REST API ile canlı beslenen, sıkıştırılmış denetim izine sahip şeffaf kamuoyu algı indeksi.",
                        fontSize = 11.sp,
                        color = Color.LightGray,
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // Live Real-Data Control & Capacity Bar
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = SlateCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
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
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = PopularityCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Katalog: ${uiState.storageStats.celebrityCount} / ${uiState.storageStats.maxCelebrityLimit} Kişi",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Text(
                            text = "Kütük: ${uiState.storageStats.totalScanSnapshots} Kayıt (~${(uiState.storageStats.estimatedStorageBytes / 1024L).coerceAtLeast(1)} KB)",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    if (uiState.isScanning) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val (cur, tot) = uiState.scanProgress ?: (0 to 1)
                        LinearProgressIndicator(
                            progress = { if (tot > 0) cur.toFloat() / tot.toFloat() else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = PopularityCyan,
                            trackColor = SlateCardVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Wikimedia taranıyor: $cur / $tot profil işlendi...",
                            fontSize = 10.sp,
                            color = PopularityCyan
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.triggerWikipediaScanAll() },
                            enabled = !uiState.isScanning,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("scan_all_btn"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = PopularityCyan),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PopularityCyan.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tümünü Canlı Tara", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.setShowAddCelebrityDialog(true) },
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("open_add_celebrity_dialog_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = PopularityCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Wiki'den Ekle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    if (uiState.storageStats.celebrityCount < 1000) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.expandCatalogToTarget(1000) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("expand_to_1000_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = LikeabilityPurple),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Taramayı Genişlet: 1000 Kişiye Tamamla",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
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
                        label = { Text("Tümü (${uiState.celebrities.size})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
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
