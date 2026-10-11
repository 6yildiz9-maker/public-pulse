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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.Celebrity
import com.example.data.model.PublicMetricsCalculator
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.ReboundEmerald
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VotingBottomSheet(
    celebrity: Celebrity,
    alreadyVoted: Boolean,
    onDismiss: () -> Unit,
    onSubmitVote: (knows: String, rating: Int, tags: List<String>) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var knowsOption by remember { mutableStateOf("Evet") }
    var selectedRating by remember { mutableIntStateOf(5) }
    val selectedTags = remember { mutableStateListOf<String>() }

    val availableTags = listOf(
        "Yetenekli", "Samimi", "Mütevazı", "Yenilikçi", "Disiplinli",
        "Aşırı Medyatik", "Vizyoner", "Tartışmalı", "Doğal", "Lider"
    )

    // Calculate live Bayesian simulation
    val simulatedRatings = celebrity.ratings.toMutableMap()
    val currentCount = simulatedRatings[selectedRating] ?: 0
    simulatedRatings[selectedRating] = currentCount + 1
    val simulatedMean = PublicMetricsCalculator.calculateBayesianMean(
        ratings = simulatedRatings,
        priorC = celebrity.category.priorMean,
        smoothingM = celebrity.category.smoothingM
    )
    val simulatedLikeability = PublicMetricsCalculator.calculateLikeability(simulatedMean)
    val simulatedDelta = simulatedLikeability - celebrity.likeability

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SlateCard,
        modifier = Modifier.testTag("voting_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Kamuoyu Değerlendirmesi",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LikeabilityPurple
                    )
                    Text(
                        text = celebrity.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat", tint = Color.Gray)
                }
            }

            if (alreadyVoted) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = SlateCardVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Oylandı",
                            tint = PopularityCyan,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "Bu Kişi İçin Oyunuz Kayıtlı",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Anti-spam ve bot koruması uyarınca her profil için 90 günde bir kez oy kullanılabilir. Mevcut oyunuz Bayesçi puan havuzunda aktiftir.",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))

                // 1. SORU: TANIMA SORUSU
                Text(
                    text = "1. Bu kişiyi ne kadar tanıyorsunuz?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Şartname gereği, bilgiye dayalı olmayan oylamalar filtrelenir.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(6.dp))

                val options = listOf(
                    "Evet" to "Evet, yakından takip ediyorum",
                    "Kısmen" to "Kısmen, medyadan biliyorum",
                    "Hayır" to "Hayır, tanımıyorum"
                )

                options.forEach { (key, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { knowsOption = key }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = knowsOption == key,
                            onClick = { knowsOption = key },
                            colors = RadioButtonDefaults.colors(selectedColor = LikeabilityPurple)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            color = if (knowsOption == key) Color.White else Color.Gray,
                            fontWeight = if (knowsOption == key) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                }

                if (knowsOption == "Hayır") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFF1E293B)
                    ) {
                        Text(
                            text = "ℹ️ Tanımadığınız kişileri oylamak Bayesçi hesaplama güvenilirliğini düşüreceği için puanlama adımı devre dışıdır.",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. SORU: 1-5 YILDIZ PUAN
                    Text(
                        text = "2. Genel Kamuoyu Algınız (1-5 Yıldız)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (star in 1..5) {
                            val isSelected = star <= selectedRating
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedRating = star }
                                    .padding(4.dp)
                                    .testTag("star_rating_$star")
                            ) {
                                Icon(
                                    imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "$star Yıldız",
                                    tint = if (isSelected) PolarizationAmber else Color.Gray,
                                    modifier = Modifier.size(36.dp)
                                )
                                Text(
                                    text = "$star★",
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    val starDescription = when (selectedRating) {
                        1 -> "1★: Oldukça Olumsuz Görüş (Kutuplaşma p₋ payı)"
                        2 -> "2★: Olumsuz Görüş (Kutuplaşma p₋ payı)"
                        3 -> "3★: Nötr / Kararsız (Kutuplaşma dışı bağımsız oy)"
                        4 -> "4★: Olumlu Görüş (Kutuplaşma p₊ payı)"
                        5 -> "5★: Oldukça Olumlu / Takdir Edilen (Kutuplaşma p₊ payı)"
                        else -> ""
                    }

                    Text(
                        text = starDescription,
                        fontSize = 11.sp,
                        color = PolarizationAmber,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. SORU: ALGI ETİKETLERİ
                    Text(
                        text = "3. Öne Çıkan Nitelikler (Opsiyonel)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        availableTags.forEach { tag ->
                            val isChosen = selectedTags.contains(tag)
                            FilterChip(
                                selected = isChosen,
                                onClick = {
                                    if (isChosen) selectedTags.remove(tag) else selectedTags.add(tag)
                                },
                                label = { Text(tag, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = LikeabilityPurple.copy(alpha = 0.25f),
                                    selectedLabelColor = Color.White,
                                    containerColor = SlateCardVariant,
                                    labelColor = Color.LightGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // CANLI BAYESÇİ SİMÜLASYONU
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        color = SlateCardVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Matematiksel Etki Önizlemesi",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PopularityCyan
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Mevcut Sevilirlik: ${String.format(Locale.US, "%.1f", celebrity.likeability)}",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Oyunuz Sonrası: ${String.format(Locale.US, "%.1f (%+.2f)", simulatedLikeability, simulatedDelta)}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (simulatedDelta >= 0) ReboundEmerald else Color(0xFFFF5252)
                                )
                            }
                            Text(
                                text = "Bayesçi yumuşatma (m=${celebrity.category.smoothingM.toInt()}) tekil manipülatif oyları soğurur.",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            onSubmitVote(knowsOption, selectedRating, selectedTags.toList())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_vote_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = LikeabilityPurple),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Oyu Şeffaf Kütüğe Kaydet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
