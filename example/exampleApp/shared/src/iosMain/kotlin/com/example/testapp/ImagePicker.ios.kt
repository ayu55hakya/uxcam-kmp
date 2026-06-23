package com.example.testapp

import androidx.compose.runtime.Composable

/**
 * iOS stub: the sample does not wire a native image picker on iOS. Invoking the returned
 * lambda simply reports completion so screens that background the app on Android still
 * behave sensibly here.
 */
@Composable
actual fun rememberImagePicker(onResult: () -> Unit): () -> Unit = { onResult() }
