package com.example.starter

import androidx.compose.runtime.Composable

/**
 * iOS stub: the sample does not wire a native image picker on iOS. Invoking the returned
 * lambda simply reports completion so the Session Controls screen behaves sensibly here.
 */
@Composable
actual fun rememberImagePicker(onResult: () -> Unit): () -> Unit = { onResult() }
