package com.example.template

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.uxcam.kmp.UXCamKMP

/** Manual screen naming, auto-tagging config, and the ignore list — mirrors ScreenTaggingView. */
@Composable
internal fun ScreenTaggingScreen(onBack: () -> Unit) =
    DemoScaffold("Screen Tagging", "Screen Tagging", onBack) { report ->
        var screenName by remember { mutableStateOf("") }
        var ignoreName by remember { mutableStateOf("") }

        SectionHeader("Tagging")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = screenName, onValueChange = { screenName = it },
            label = { Text("Screen name") }, singleLine = true,
        )
        DemoButton("Tag screen") {
            val name = screenName.ifBlank { "CustomScreen" }
            UXCamKMP.tagScreenName(name); report("tagScreenName(\"$name\")")
        }
        DemoButton("Enable auto screen naming") {
            UXCamKMP.setAutomaticScreenNameTagging(true); report("setAutomaticScreenNameTagging(true)")
        }
        DemoButton("Disable auto screen naming") {
            UXCamKMP.setAutomaticScreenNameTagging(false); report("setAutomaticScreenNameTagging(false)")
        }
        DemoButton("Enable improved screen capture") {
            UXCamKMP.setImprovedScreenCaptureEnabled(true); report("setImprovedScreenCaptureEnabled(true)")
        }
        DemoButton("Disable improved screen capture") {
            UXCamKMP.setImprovedScreenCaptureEnabled(false); report("setImprovedScreenCaptureEnabled(false)")
        }

        SectionHeader("Ignore list")
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = ignoreName, onValueChange = { ignoreName = it },
            label = { Text("Screen name to ignore") }, singleLine = true,
        )
        DemoButton("Add to ignore list") {
            val name = ignoreName.ifBlank { "Settings" }
            UXCamKMP.addScreenNameToIgnore(name); report("addScreenNameToIgnore(\"$name\")")
        }
        DemoButton("Add multiple to ignore") {
            UXCamKMP.addScreenNamesToIgnore(listOf("Login", "Payment"))
            report("addScreenNamesToIgnore([Login, Payment])")
        }
        DemoButton("Remove from ignore list") {
            val name = ignoreName.ifBlank { "Settings" }
            UXCamKMP.removeScreenNameToIgnore(name); report("removeScreenNameToIgnore(\"$name\")")
        }
        DemoButton("Remove multiple from ignore") {
            UXCamKMP.removeScreenNamesToIgnore(listOf("Login", "Payment"))
            report("removeScreenNamesToIgnore([Login, Payment])")
        }
        DemoButton("Remove all ignored") {
            UXCamKMP.removeAllScreenNamesToIgnore(); report("removeAllScreenNamesToIgnore()")
        }
        DemoButton("List ignored screens") {
            report("screenNamesBeingIgnored() = ${UXCamKMP.screenNamesBeingIgnored()}")
        }
    }
