package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CelebrityCategory
import com.example.data.remote.WikipediaSearchResult
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant

@Composable
fun AddCelebrityFromWikiDialog(
    currentCount: Int,
    maxLimit: Int,
    searchResults: List<WikipediaSearchResult>,
    isSearching: Boolean,
    onSearch: (String) -> Unit,
    onAdd: (wikiTitle: String, category: CelebrityCategory) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CelebrityCategory.OYUNCU) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .testTag("add_celebrity_dialog"),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = PopularityCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Wikipedia'dan Kişi Ekle",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Katalog Kapasitesi: $currentCount / $maxLimit Kişi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PopularityCyan
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat", tint = Color.Gray)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = SlateBorder)

                // Category selector chips
                Text(
                    text = "1. Kategori Seçin:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CelebrityCategory.values().forEach { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.title, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PopularityCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = SlateCardVariant,
                                labelColor = Color.LightGray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                Text(
                    text = "2. Kişi Adını Arayın:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        onSearch(it)
                    },
                    placeholder = { Text("Örn: Cem Yılmaz, İlkin Aydın...", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Ara", tint = PopularityCyan)
                    },
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = PopularityCyan
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wiki_search_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PopularityCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Results list
                Text(
                    text = "Wikipedia Arama Sonuçları:",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateCardVariant)
                        .padding(4.dp)
                ) {
                    if (searchResults.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (searchQuery.isBlank()) "Aramak istediğiniz ismi yazın." else "Sonuç bulunamadı.",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        LazyColumn {
                            items(searchResults) { result ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    color = Color(0xFF131924),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = result.title,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            if (result.snippet.isNotBlank()) {
                                                Text(
                                                    text = result.snippet,
                                                    fontSize = 10.sp,
                                                    color = Color.Gray,
                                                    maxLines = 2
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(
                                            onClick = { onAdd(result.title, selectedCategory) },
                                            modifier = Modifier
                                                .height(34.dp)
                                                .testTag("add_wiki_btn_${result.title}"),
                                            colors = ButtonDefaults.buttonColors(containerColor = PopularityCyan),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Ekle",
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("Ekle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ℹ️ Şartname kuralı: 18 yaş altı bireyler, siyasiler ve dini figürler sistem dışıdır. Ekleme anında Wikimedia REST API'sinden aylık gerçek sayfa görüntülenme verisi otomatik çekilir.",
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 14.sp
                )
            }
        }
    }
}
