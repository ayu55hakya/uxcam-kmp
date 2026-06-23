package com.example.testapp

import androidx.compose.runtime.Composable

/**
 * Returns a function that, when invoked, opens the platform image picker (which sends
 * the user to another app). [onResult] fires when the user returns, picked or not.
 *
 * Android uses ActivityResultContracts.GetContent for images; iOS is a no-op stub.
 */
@Composable
expect fun rememberImagePicker(onResult: () -> Unit): () -> Unit
