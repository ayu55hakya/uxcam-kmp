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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.uxcam.kmp.UXCamKMP
import com.uxcam.kmp.uxcamOcclude

/**
 * Occlusion — mirrors OcclusionView/OcclusionActivity. Per-view occlusion uses the Compose
 * [uxcamOcclude] modifier (the cross-platform equivalent of the native occludeSensitiveView,
 * which takes a platform View), plus the screen-level and text-field APIs.
 */
@Composable
internal fun OcclusionScreen(onBack: () -> Unit) = DemoScaffold("Occlusion", "Occlusion", onBack) { report ->
    var password by remember { mutableStateOf("") }

    SectionHeader("Per-view occlusion (uxcamOcclude)")
    Text("Visible text (recorded normally)")
    Text(
        text = "Secret: 4111 1111 1111 1111",
        modifier = Modifier.uxcamOcclude("card_number"),
    )
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().uxcamOcclude("password_field"),
        value = password, onValueChange = { password = it },
        label = { Text("Password (occluded)") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
    )

    // Per-view occlusion against a real native View/UIView (occludeSensitiveView).
    NativeViewOcclusionSection(report)

    SectionHeader("Screen occlusion")
    DemoButton("Enable screen occlusion") {
        UXCamKMP.occludeSensitiveScreen(true); report("occludeSensitiveScreen(true)")
    }
    DemoButton("Disable screen occlusion") {
        UXCamKMP.occludeSensitiveScreen(false); report("occludeSensitiveScreen(false)")
    }
    DemoButton("Occlude without gesture") {
        UXCamKMP.occludeSensitiveScreen(true, withoutGesture = true)
        report("occludeSensitiveScreen(true, withoutGesture = true)")
    }

    SectionHeader("Text fields")
    DemoButton("Occlude all text fields") {
        UXCamKMP.occludeAllTextFields(true); report("occludeAllTextFields(true)")
    }
    DemoButton("Unocclude all text fields") {
        UXCamKMP.occludeAllTextFields(false); report("occludeAllTextFields(false)")
    }
}
