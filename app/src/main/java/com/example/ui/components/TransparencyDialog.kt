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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Celebrity
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant
import java.util.Locale

@Composable
fun TransparencyDialog(
    celebrity: Celebrity,
    onDismiss: () -> Unit
) {
    val details = celebrity.calculationDetails

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .testTag("transparency_dialog"),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Şeffaflık & Metodoloji Kartı",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PopularityCyan
                        )
                        Text(
                            text = "${celebrity.name} Skoru Neye Göre?",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_transparency_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color.Gray
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = SlateBorder
                )

                // 1. SEVİLİRLİK (L) FORMÜLÜ
                SectionHeader(
                    title = "1. Sevilirlik (L): ${String.format(Locale.US, "%.1f", details.likeabilityL)} / 100",
                    color = LikeabilityPurple
                )
                Text(
                    text = "Az oyda manipülasyonu önlemek için ham ortalama yerine Bayesçi yumuşatma (Bayesian Smoothing) uygulanır:",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                FormulaCard(
                    formulaText = "r̄_düzeltilmiş = (n · r̄ + m · C) / (n + m)\nL = 25 · (r̄_düzeltilmiş − 1)"
                )
                BreakdownRow("Geçerli Oy Sayısı (n):", "${details.n} oy")
                BreakdownRow("Ham Oy Ortalaması (r̄):", String.format(Locale.US, "%.2f / 5.00", details.rawAverage))
                BreakdownRow("Kategori Prior Ortalaması (C):", String.format(Locale.US, "%.2f", details.categoryPriorC))
                BreakdownRow("Yumuşatma Sabiti (m):", String.format(Locale.US, "%.1f", details.smoothingM))
                BreakdownRow("Bayesçi Düzeltilmiş (r̄_düzeltilmiş):", String.format(Locale.US, "%.2f", details.bayesianMean))
                BreakdownRow("Nihai Sevilirlik (0-100):", String.format(Locale.US, "%.1f puan", details.likeabilityL))

                Spacer(modifier = Modifier.height(16.dp))

                // 2. KUTUPLAŞMA (D) FORMÜLÜ
                SectionHeader(
                    title = "2. Kutuplaşma (D): ${String.format(Locale.US, "%.1f", details.polarizationD)} / 100",
                    color = PolarizationAmber
                )
                Text(
                    text = "Kutuplaşma bir 'ceza puanı' değildir! Toplumdaki görüş ayrılığının simetrik dağılımıdır:",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                FormulaCard(
                    formulaText = "D = 200 · min(p₊, p₋)\np₊: 4-5★ oranı, p₋: 1-2★ oranı (3★ nötrdür)"
                )
                BreakdownRow("Olumlu Oy Oranı (p₊):", String.format(Locale.US, "%%%.1f", details.pPositive * 100))
                BreakdownRow("Olumsuz Oy Oranı (p₋):", String.format(Locale.US, "%%%.1f", details.pNegative * 100))
                BreakdownRow("Dağılım Katsayısı (D):", String.format(Locale.US, "%.1f / 100", details.polarizationD))

                Spacer(modifier = Modifier.height(16.dp))

                // 3. POPÜLERLİK (P) FORMÜLÜ
                SectionHeader(
                    title = "3. Popülerlik (P): ${String.format(Locale.US, "%.1f", details.popularityP)} / 100",
                    color = PopularityCyan
                )
                Text(
                    text = "Wikimedia Resmî Analytics API'sinden alınan teyitli sayfa görüntülenmeleri ve kategori içi logaritmik ölçeklendirme:",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                BreakdownRow("Wikipedia Sayfa Başlığı:", celebrity.wikiTitle)
                BreakdownRow("Aylık Görüntülenme:", "${celebrity.wikiMonthlyViews} ziyaret")
                BreakdownRow("Kategori Tavan Beklentisi:", "${celebrity.category.maxExpectedViews} ziyaret")
                BreakdownRow("Nihai Popülerlik Skoru:", String.format(Locale.US, "%.1f / 100", details.popularityP))

                Spacer(modifier = Modifier.height(16.dp))

                // 4. DEVRE KESİCİ & GÜVENLİK
                SectionHeader(
                    title = "4. Güvenlik, Devre Kesici & Güncelleme",
                    color = Color.White
                )
                BreakdownRow("Güven Düzeyi:", details.confidence.label)
                BreakdownRow("Devre Kesici Durumu:", if (celebrity.isCircuitBreakerActive) "AKTİF (±8 puan sınırı)" else "Normal Akış")
                BreakdownRow("Hesaplama Periyodu:", "Haftalık resmi indeksleme (Hata payını azaltır)")
                BreakdownRow("Oy Güvenliği:", "Kişi başına 1 oy / 90 günde bir yenileme hakkı")

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("understand_transparency_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PopularityCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Anladım, Şeffaflığı Onayla",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun FormulaCard(formulaText: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = SlateCardVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Text(
            text = formulaText,
            color = Color(0xFF38BDF8),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
private fun BreakdownRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}
