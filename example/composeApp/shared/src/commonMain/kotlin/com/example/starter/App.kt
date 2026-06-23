package com.example.starter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * The whole app UI. A minimal greeting screen: shows which platform it's running on
 * and a counter button. This is the starting point — build the real app on top of it.
 */
@Composable
fun App() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            var count by remember { mutableStateOf(0) }

            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            ) {
                Text(
                    text = "Hello, Compose Multiplatform!",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "Running on ${getPlatform().name}",
                    style = MaterialTheme.typography.bodyLarge,
                )
                Button(onClick = { count++ }) {
                    Text("Clicked $count times")
                }
            }
        }
    }
}
