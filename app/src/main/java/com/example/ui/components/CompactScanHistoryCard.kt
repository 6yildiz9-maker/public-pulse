package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.data.local.entity.CompactScanEntity
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.ReboundEmerald
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant
import java.util.Locale

@Composable
fun CompactScanHistoryCard(
    scanHistory: List<CompactScanEntity>,
    onRescanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
        color = SlateCard
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
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Sıkıştırılmış Kütük",
                        tint = ReboundEmerald,
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "Haftalık Sıkıştırılmış Denetim İzi",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Kompakt Format (~50 byte/kayıt, SQLite)",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                OutlinedButton(
                    onClick = onRescanClick,
                    modifier = Modifier
                        .height(32.dp)
                        .testTag("rescan_wikipedia_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PopularityCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PopularityCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Canlı Tara", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                color = SlateCardVariant
            ) {
                Text(
                    text = "Tarama verileri şeffaflık ve itiraz denetimi için saklanır. Ham veri yerine sadece işlenmiş göstergeler saklandığından 1000 kişi için 1 yıllık kayıt yalnızca ~2.6 MB disk alanı tüketir.",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1),
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (scanHistory.isEmpty()) {
                Text(
                    text = "Bu profil için henüz arşivlenmiş tarama kaydı bulunmuyor. 'Canlı Tara' butonuna basarak ilk gerçek Wikimedia kaydını alabilirsiniz.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = "Son Tarama Snapshot'ları (En son ${scanHistory.size} hafta):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(6.dp))

                scanHistory.take(5).forEach { scan ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        color = Color(0xFF0F141F),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, SlateBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val weekStr = scan.epochWeek.toString()
                                val formattedWeek = if (weekStr.length == 6) "${weekStr.take(4)} H${weekStr.takeLast(2)}" else weekStr
                                Text(
                                    text = formattedWeek,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Wiki: ${scan.viewsK}k görüntülenme",
                                    fontSize = 10.sp,
                                    color = PopularityCyan
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = String.format(Locale.US, "L: %.1f", scan.likeabilityL),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LikeabilityPurple
                                    )
                                    Text(
                                        text = String.format(Locale.US, "P: %.1f", scan.popularityP),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PopularityCyan
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = String.format(Locale.US, "D: %.1f", scan.polarizationD),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PolarizationAmber
                                    )
                                    Text(
                                        text = String.format(Locale.US, "%+0.1f", scan.momentumM),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (scan.momentumM >= 0) ReboundEmerald else Color(0xFFFF5252)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
