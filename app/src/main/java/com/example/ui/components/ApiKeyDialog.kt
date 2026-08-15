package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.ui.theme.VibrantAccentPurple
import com.example.ui.theme.VibrantBorder
import com.example.ui.theme.VibrantBorderLight
import com.example.ui.theme.VibrantPrimary
import com.example.ui.theme.VibrantPrimaryContainer
import com.example.ui.theme.VibrantSecondaryContainer
import com.example.ui.theme.VibrantShadow
import com.example.ui.theme.VibrantSurface
import com.example.ui.theme.VibrantTertiaryContainer
import com.example.ui.theme.VibrantTertiaryText
import com.example.ui.theme.VibrantTextDark
import com.example.ui.theme.VibrantTextPrimary
import com.example.ui.theme.VibrantTextSecondary

@Composable
fun ApiKeyDialog(
    currentKey: String,
    onSaveKey: (String) -> Unit,
    testStatus: String?,
    isTesting: Boolean,
    onTestKey: () -> Unit,
    onDismiss: () -> Unit
) {
    var keyInput by remember { mutableStateOf(currentKey) }
    val shape = RoundedCornerShape(28.dp)

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("api_key_dialog")
        ) {
            // Shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 2.dp, y = 4.dp)
                    .background(VibrantShadow, shape)
            )

            // Dialog Surface
            Column(
                modifier = Modifier
                    .background(VibrantSurface, shape)
                    .border(1.5.dp, VibrantBorder, shape)
                    .clip(shape)
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(VibrantPrimary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔑", fontSize = 18.sp)
                        }
                        Text(
                            text = "Gemini Flash API Key",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = VibrantTextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(VibrantSecondaryContainer, CircleShape)
                            .clickable { onDismiss() }
                            .testTag("close_api_dialog_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = VibrantTextDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Insira sua Gemini API Key (ou configure no painel Secrets):",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = VibrantTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                NeoInputField(
                    value = keyInput,
                    onValueChange = {
                        keyInput = it
                        onSaveKey(it)
                    },
                    placeholder = "AIzaSy...",
                    testTag = "api_key_input"
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Modelo ativo: gemini-3.5-flash (otimizado para grego)",
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = VibrantTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Test Connection Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(VibrantPrimary, RoundedCornerShape(16.dp))
                        .clickable(enabled = !isTesting) { onTestKey() }
                        .testTag("test_api_key_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isTesting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text(text = "🧪", fontSize = 16.sp)
                        }
                        Text(
                            text = if (isTesting) "Testando conexão..." else "Testar Conexão Flash 3.5",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }

                if (!testStatus.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (testStatus.startsWith("✅")) Color(0xFFD1E7DD) else VibrantTertiaryContainer,
                                RoundedCornerShape(14.dp)
                            )
                            .border(
                                1.dp,
                                if (testStatus.startsWith("✅")) Color(0xFFBADBCC) else VibrantBorderLight,
                                RoundedCornerShape(14.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = testStatus,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = if (testStatus.startsWith("✅")) Color(0xFF0F5132) else VibrantTertiaryText
                        )
                    }
                }
            }
        }
    }
}
