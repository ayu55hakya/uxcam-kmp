package com.example.testapp

import androidx.compose.runtime.Composable

// No image picker wired for iOS yet — no-op so shared UI compiles and runs.
@Composable
actual fun rememberImagePicker(onResult: () -> Unit): () -> Unit = {}
