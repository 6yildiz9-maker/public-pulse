package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.Celebrity
import com.example.ui.theme.PopularityCyan
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardVariant

@Composable
fun ObjectionDialog(
    celebrity: Celebrity,
    onDismiss: () -> Unit,
    onSubmitObjection: (representative: String, subject: String, statement: String) -> Unit
) {
    var repName by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Biyografi & Veri Düzeltme Talebi") }
    var statement by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .testTag("objection_dialog"),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = "İtiraz",
                            tint = PopularityCyan
                        )
                        Column {
                            Text(
                                text = "Düzeltme & İtiraz Bildirimi",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${celebrity.name} Profili İçin",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat", tint = Color.Gray)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SlateBorder)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    color = SlateCardVariant
                ) {
                    Text(
                        text = "KVKK ve 5651 sayılı kanun ilkeleri uyarınca, sanatçı/sporcu temsilcileri veya yetkililer veri düzeltme, tekzip veya beyan talebinde bulunabilir. Onaylanan itirazlar kamuoyuna açık şeffaf kütük notu olarak yayınlanır.",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = repName,
                    onValueChange = { repName = it },
                    label = { Text("Başvuran Adı / Temsilci Unvanı", fontSize = 12.sp) },
                    placeholder = { Text("Örn: Basın Temsilcisi / Hukuk Bürosu") },
                    modifier = Modifier.fillMaxWidth().testTag("rep_name_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PopularityCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Konu Başlığı", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("objection_subject_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PopularityCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = statement,
                    onValueChange = { statement = it },
                    label = { Text("Açıklama / Düzeltme Metni", fontSize = 12.sp) },
                    placeholder = { Text("Lütfen doğrulanabilir kaynak veya temsilci beyanını yazınız...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp).testTag("objection_statement_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PopularityCyan,
                        unfocusedBorderColor = SlateBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (statement.isNotBlank()) {
                            onSubmitObjection(repName, subject, statement)
                        }
                    },
                    enabled = statement.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("submit_objection_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PopularityCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "İtirazı Şeffaflık Kütüğüne İşle",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}
