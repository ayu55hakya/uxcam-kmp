package com.example.starter

import androidx.compose.runtime.Composable

/**
 * Returns a function that, when invoked, opens the platform image picker (which sends the
 * user to another app). [onResult] fires when the user returns, picked or not.
 *
 * Used by the Session Controls screen to background the app so UXCam's short-break handling
 * fires. Android uses ActivityResultContracts.GetContent; iOS is a no-op stub.
 */
@Composable
expect fun rememberImagePicker(onResult: () -> Unit): () -> Unit
