package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Puanlama ve tarama verilerinin saklandığı işlenmiş ve yüksek derecede sıkıştırılmış model.
 *
 * NEDEN TUTULUR?
 * 1. Şeffaflık & İtiraz Güvencesi: Herhangi bir skora itiraz edildiğinde, o haftaki Wikipedia
 *    sayfa görüntülenme sayısı, oy dağılımı (p+, p-) ve hesaplama girdileri inkâr edilemez
 *    bir denetim izi (audit trail) olarak sunulur.
 * 2. Momentum ve Devre Kesici Doğrulaması: 24 saatlik ve haftalık ±8 puanlık devre kesici
 *    sınırının aşılıp aşılmadığı önceki taranan snapshot ile doğrulanır.
 *
 * SIKIŞTIRMA ORANI:
 * - Ham HTML ve loglar yerine yalnızca işlenmiş kompakt sayısal göstergeler tutulur:
 * - 1 haftalık kayıt ~50 bayt.
 * - 1000 kişi için haftalık tarama: ~50 KB.
 * - 1000 kişi için 1 yıllık (52 hafta) tüm geçmiş: Yalnızca ~2.6 MB!
 */
@Entity(
    tableName = "compact_scan_history",
    indices = [Index(value = ["celebrityId", "epochWeek"])]
)
data class CompactScanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val celebrityId: String,
    val epochWeek: Int,                  // Örn: 202641 (2026 yılı 41. hafta)
    val viewsK: Int,                     // Görüntülenme (binlik tamsayı, örn: 142)
    val starDistributionCompact: String, // "c1,c2,c3,c4,c5" (örn: "3,6,14,88,420")
    val likeabilityL: Float,             // Hesaplanmış L (örn: 89.2)
    val popularityP: Float,              // Hesaplanmış P (örn: 84.5)
    val polarizationD: Float,            // Hesaplanmış D (örn: 4.8)
    val momentumM: Float,                // Hesaplanmış M (örn: 1.2)
    val circuitBreakerTriggered: Boolean,
    val scannedAt: Long = System.currentTimeMillis()
)
