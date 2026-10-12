package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoAccounts
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LikeabilityPurple
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PolarizationAmber
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.ReboundEmerald
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant

@Composable
fun MethodologyScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp)
            .testTag("methodology_screen")
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = "Metodoloji",
                tint = PopularityCyan,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = "Metodoloji & Etik İlkeler (v4)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Şeffaf, çok boyutlu ve itiraza açık kamuoyu algı mimarisi.",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Temel Felsefe
        MethodologySection(
            icon = Icons.Default.AutoAwesome,
            title = "1. Temel Felsefe: 'Nosedive'a Antitez",
            accentColor = PopularityCyan
        ) {
            Text(
                text = "Black Mirror'ın 'Nosedive' bölümünden ilham alırken, distopik insan puanlama tuzaklarını kökten reddediyoruz:",
                fontSize = 12.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            BulletItem("Bileşik 'insan puanı' yoktur: Popülerlik, sevilirlik ve kutuplaşma tamamen bağımsız üç boyuttur.")
            BulletItem("Skor kişinin değerini değil, tanımlı göstergeler altındaki dijital ve toplumsal algıyı gösterir.")
            BulletItem("Düşüşü değil toparlanmayı (Rebound) öne çıkarır; ceza sistemi değil, ilgi ölçümüdür.")
            BulletItem("Negatif listeler ('Araftakiler', 'Blacklist') tasarım gereği yasaktır.")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Matematiksel Formüller
        MethodologySection(
            icon = Icons.Default.Functions,
            title = "2. Hesaplama Modeli & Formüller",
            accentColor = LikeabilityPurple
        ) {
            Text(
                text = "A. Sevilirlik (0–100): Bayesçi Düzeltmeli Kullanıcı Oyları",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = LikeabilityPurple
            )
            Text(
                text = "Az oy alan profillerin tesadüfi 1 veya 5 yıldızlarla aşırı uçlara savrulmasını önlemek için Laplace/Bayes düzeltmesi kullanılır:",
                fontSize = 11.sp,
                color = Color.LightGray
            )
            FormulaBlock(
                "r̄_düzeltilmiş = (n · r̄ + m · C) / (n + m)\nL = 25 · (r̄_düzeltilmiş − 1)"
            )
            Text(
                text = "• n: Geçerli oy sayısı\n• r̄: Ham oylama ortalaması\n• C: Kategori başlangıç prior medyanı (soğuk başlangıç)\n• m: Düzeltme sabiti (25.0)",
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "B. Kutuplaşma (0–100): Dağılım Ölçümü (Ceza Değildir!)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PolarizationAmber
            )
            Text(
                text = "Kutuplaşma, bir kişinin 'kötü' olduğu anlamına gelmez. Toplumun o kişi hakkında ne ölçüde karşıt görüşlere ayrıldığını gösterir:",
                fontSize = 11.sp,
                color = Color.LightGray
            )
            FormulaBlock("D = 200 · min(p₊, p₋)")
            Text(
                text = "• p₊: 4-5 yıldız verenlerin oranı\n• p₋: 1-2 yıldız verenlerin oranı\n• Nötr oylar (3 yıldız) kutuplaşmanın tarafı sayılmaz.\nBöylece bir kişi hem yüksek sevilirlik hem yüksek kutuplaşmaya aynı anda sahip olabilir.",
                fontSize = 11.sp,
                color = Color(0xFFCBD5E1),
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "C. Popülerlik (0–100): Wikimedia Analytics",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PopularityCyan
            )
            Text(
                text = "Resmî Wikimedia REST API'sinden alınan teyitli aylık sayfa görüntülenmeleri, kategori içi logaritmik percentile ile 0-100 aralığına normalize edilir. Görüntülenme sevilirlik anlamına gelmez.",
                fontSize = 11.sp,
                color = Color.LightGray
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Devre Kesici & Anti-Linç
        MethodologySection(
            icon = Icons.Default.Shield,
            title = "3. Devre Kesici (Circuit Breaker) & Linç Koruması",
            accentColor = Color(0xFFFF5252)
        ) {
            BulletItem("±8 Puan Sınırı: 24 saatlik değişim ±8 puanı aşamaz.")
            BulletItem("48 Saat Soğuma: Ani oy dalgalanmaları ve bot hücumları soğuma havuzuna alınır ve kademeli olarak işlenir.")
            BulletItem("Suçlayıcı dil yasaktır: 'Skandalla çöktü' yerine 'Son 24 saatte olumsuz oy oranı yükseldi' gibi nötr teknik ifadeler kullanılır.")
            BulletItem("Cancel butonu yoktur: Yalnızca gerekçeli, standart 1-5 yıldız ve algı etiketleri kullanılır.")
            BulletItem("1 Oy / 90 Gün: Aynı kullanıcı bir profil için 90 günde bir kez oy güncelleyebilir.")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Hukuki Çerçeve & KVKK
        MethodologySection(
            icon = Icons.Default.Gavel,
            title = "4. Hukuki Çerçeve & KVKK Uyumu",
            accentColor = ReboundEmerald
        ) {
            BulletItem("18 Yaş Altı Yasağı: 18 yaşın altındaki bireyler kesinlikle skorlanmaz veya listelenmez.")
            BulletItem("Siyaset ve Din Hariçtir: Siyasiler, parti yöneticileri ve dini figürler sistem dışındadır.")
            BulletItem("Reklam/Sponsorluk Skor Etkileyemez: Puanlar para veya sponsorluk karşılığı asla değiştirilemez.")
            BulletItem("Doğrulanmamış İddialar Yasaktır: Yargı kararı veya teyitli kaynak olmadan sosyal medya dedikoduları skor girdisi olamaz.")
            BulletItem("Düzeltme & İtiraz Kanalı (Right of Reply): Sanatçı ve sporcu temsilcileri profillerine şeffaf düzeltme notu ekleyebilir.")
            BulletItem("Haftalık Hesaplama: Hata payını azaltmak ve spekülasyonu önlemek için skorlar haftalık döngüyle kilitlenir.")
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Tarama Verisi Mimarisi & Sıkıştırma
        MethodologySection(
            icon = Icons.Default.Archive,
            title = "5. Tarama Verileri, Sıkıştırma & 1000 Kişi Mimarisi",
            accentColor = PopularityCyan
        ) {
            Text(
                text = "Puanlama için yapılan tarama verilerini tutmak şart mıdır?",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "EVET, kesinlikle tutulmalıdır. Ancak ham veri değil, işlenmiş ve azami sıkıştırılmış kütük olarak saklanır:",
                fontSize = 11.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(6.dp))
            BulletItem("Denetim İzi & İtiraz Güvencesi: Bir skora itiraz edildiğinde o tarihteki gerçek Wikimedia görüntülenme sayısı ve oy histogramı ispatlanabilir olmalıdır.")
            BulletItem("Devre Kesici Güvencesi: Haftalık ve 24 saatlik ±8 puan sınırının aşılıp aşılmadığı önceki taranan snapshot ile doğrulanır.")
            BulletItem("Azami Sıkıştırma Oranı (~50 Bayt/Kayıt): Ham HTML veya istek logları yerine yalnızca hafta kodu, binlik görüntülenme ve hesaplanmış L, P, D, M değerleri saklanır.")
            BulletItem("Hafif Disk Ayak İzi: 1000 kişi için 1 yıllık (52 hafta) tüm geçmiş veri yalnızca ~2.6 MB tutar; cihazı ve belleği yormaz.")
            BulletItem("Otomatik Ekleme ve 1000 Kişi Tavanı: Katalog azami 1000 kişiyle sınırlıdır. Wikimedia REST API'den canlı eklenen veya taranan profiller bu tavanı aşamaz.")
        }
    }
}

@Composable
private fun MethodologySection(
    icon: ImageVector,
    title: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, SlateBorder, RoundedCornerShape(14.dp)),
        color = SlateCard
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = SlateBorder
            )
            content()
        }
    }
}

@Composable
private fun BulletItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = "•", color = PopularityCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(text = text, fontSize = 11.sp, color = Color(0xFFCBD5E1), lineHeight = 16.sp)
    }
}

@Composable
private fun FormulaBlock(formula: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(8.dp)),
        color = SlateCardVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
    ) {
        Text(
            text = formula,
            color = Color(0xFF38BDF8),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.padding(10.dp)
        )
    }
}
