package com.example.starter

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

@Composable
actual fun rememberImagePicker(onResult: () -> Unit): () -> Unit {
    // StartActivityForResult + Intent.createChooser(ACTION_GET_CONTENT) launches a separate
    // chooser activity that reliably backgrounds the app, so UXCam's short-break handling fires.
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        // Fires whether the user picked an image or backed out — either way they've returned.
        onResult()
    }
    return {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        launcher.launch(Intent.createChooser(intent, "Select Picture"))
    }
}
