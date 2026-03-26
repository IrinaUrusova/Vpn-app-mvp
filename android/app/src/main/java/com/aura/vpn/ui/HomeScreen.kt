package com.aura.vpn.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    apiStatus: String,
    configPreview: String,
    loading: Boolean,
    onConnectToggle: () -> Unit,
    onFetchConfig: () -> Unit,
    onReissue: () -> Unit,
) {
    val connected = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1026))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "AURA VPN",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFFEAF3FF),
            fontWeight = FontWeight.Bold,
        )

        Text(
            modifier = Modifier.padding(top = 12.dp, bottom = 10.dp),
            text = if (connected.value) "Статус: Подключено" else "Статус: Отключено",
            color = if (connected.value) Color(0xFF6FEAF2) else Color(0xFFA9B6D3),
        )

        Text(
            text = "API: $apiStatus",
            color = Color(0xFFEADFA8),
            modifier = Modifier.padding(bottom = 24.dp),
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                connected.value = !connected.value
                onConnectToggle()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        ) {
            Text(
                text = if (connected.value) "Отключить" else "Подключить",
                modifier = Modifier
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF8A5CFF), Color(0xFF6FEAF2)),
                        ),
                    )
                    .padding(vertical = 12.dp, horizontal = 24.dp),
                color = Color.White,
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = onFetchConfig,
            enabled = !loading,
        ) {
            Text(if (loading) "Загрузка..." else "Обновить конфиг")
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            onClick = onReissue,
            enabled = !loading,
        ) {
            Text("Починить подключение")
        }

        Text(
            text = configPreview,
            color = Color(0xFFA9B6D3),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
