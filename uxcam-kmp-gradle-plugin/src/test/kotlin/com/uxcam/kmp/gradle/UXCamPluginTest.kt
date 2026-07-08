package com.uxcam.kmp.gradle

import com.uxcam.kmp.gradle.linker.DownloadUXCamCocoaFramework
import com.uxcam.kmp.gradle.linker.FrameworkArchitectures
import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val PLUGIN_ID = "com.uxcam.kmp.gradle"

class UXCamPluginTest {

    @Test
    fun `plugin and uxcamKmp extension are applied`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(PLUGIN_ID)

        assertTrue(project.plugins.hasPlugin(UXCamPlugin::class.java))
        assertNotNull(project.extensions.getByName(UXCAM_EXTENSION_NAME))
    }

    @Test
    fun `default versions are set`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(PLUGIN_ID)

        val ext = project.extensions.getByType(UXCamExtension::class.java)
        assertEquals(Versions.UXCAM_KMP, ext.autoInstall.commonMain.uxcamKmpVersion.get())
        assertEquals(Versions.UXCAM_COCOA, ext.autoInstall.cocoapods.uxcamCocoaVersion.get())
        assertEquals(Versions.UXCAM_COCOA, ext.linker.cocoaVersion.get())
        assertEquals(Versions.UXCAM_COCOA_SHA256, ext.linker.cocoaSha256.get())
    }

    @Test
    fun `installUXCamForKmp adds wrapper dependency to commonMain`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)

        val ext = project.extensions.getByType(UXCamExtension::class.java)
        project.installUXCamForKmp(ext.autoInstall.commonMain)

        val wrapperDeps = project.configurations
            .flatMap { it.dependencies }
            .filter { it.group == "com.uxcam.kmp" && it.name == "uxcam" }
        assertTrue(wrapperDeps.isNotEmpty(), "expected com.uxcam.kmp:uxcam to be installed")

        val commonMainConfig = project.configurations.find { it.name.contains("commonMain", ignoreCase = true) }
        assertNotNull(commonMainConfig)
        assertTrue(commonMainConfig.dependencies.contains(wrapperDeps.first()))
    }

    @Test
    fun `non-compose consumer does not get the compose artifact`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)

        val ext = project.extensions.getByType(UXCamExtension::class.java)
        project.installUXCamForKmp(ext.autoInstall.commonMain)

        val composeDeps = project.configurations
            .flatMap { it.dependencies }
            .filter { it.group == "com.uxcam.kmp" && it.name == "uxcam-compose" }
        assertTrue(composeDeps.isEmpty(), "uxcam-compose must only be installed for Compose consumers")
    }

    @Test
    fun `installs UXCam pod when not already present`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val cocoapods = project.kotlinCocoapods()
        assertNull(cocoapods.pods.findByName("UXCam"))

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        val pod = cocoapods.pods.getByName("UXCam")
        assertEquals(Versions.UXCAM_COCOA, pod.version)
        assertEquals("UXCam", pod.moduleName)
    }

    @Test
    fun `sets ios deployment target when consumer has not`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        assertEquals(Versions.MIN_IOS_DEPLOYMENT_TARGET, project.kotlinCocoapods().ios.deploymentTarget)
    }

    @Test
    fun `does not lower a higher consumer ios deployment target`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")
        project.kotlinCocoapods().ios.deploymentTarget = "15.0"

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        assertEquals("15.0", project.kotlinCocoapods().ios.deploymentTarget)
    }

    @Test
    fun `autoInstall disabled installs nothing`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")
        project.extensions.getByType(UXCamExtension::class.java).autoInstall.enabled.set(false)

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        assertNull(project.kotlinCocoapods().pods.findByName("UXCam"))
        val wrapperDeps = project.configurations.flatMap { it.dependencies }
            .filter { it.group == "com.uxcam.kmp" && it.name == "uxcam" }
        assertTrue(wrapperDeps.isEmpty())
    }

    // ---- deliver-and-link (non-CocoaPods) path ----

    @Test
    fun `registers download task and links non-cocoapods apple targets`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)

        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kotlin.iosArm64().binaries.framework { baseName = "shared" }

        // No Kotlin CocoaPods plugin applied → deliver-and-link branch.
        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        val download = project.tasks.findByName("downloadUXCamCocoaFramework")
        assertNotNull(download, "expected the download task to be registered for non-cocoapods consumers")
        assertTrue(download is DownloadUXCamCocoaFramework)
    }

    @Test
    fun `non-mac host skips the deliver-and-link path entirely`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)

        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val target = kotlin.iosArm64()
        target.binaries.framework { baseName = "shared" }

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = false)

        // Linker opts, the download task, and the static merge only feed macOS-hosted link
        // tasks — off-Mac none of it should be configured (no xcode-select warnings, no
        // framework download on Linux/Windows CI).
        assertNull(project.tasks.findByName("downloadUXCamCocoaFramework"))
        target.binaries.withType(Framework::class.java).forEach { fw ->
            assertTrue(
                project.tasks.getByName(fw.linkTaskName).dependsOn.none {
                    it.toString().contains("downloadUXCamCocoaFramework")
                },
            )
        }
    }

    @Test
    fun `dynamic consumer framework is wired to the UXCam download`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)

        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val target = kotlin.iosArm64()
        target.binaries.framework { baseName = "shared" } // dynamic by default (isStatic = false)

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        target.binaries.withType(Framework::class.java).forEach { fw ->
            val linkTask = project.tasks.findByName(fw.linkTaskName)
            assertNotNull(linkTask, "expected link task ${fw.linkTaskName}")
            assertTrue(
                linkTask.dependsOn.any { it.toString().contains("downloadUXCamCocoaFramework") },
                "dynamic framework link task '${fw.linkTaskName}' must depend on the UXCam download",
            )
        }
    }

    @Test
    fun `static consumer framework is wired to the UXCam download for the post-link merge`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)

        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val target = kotlin.iosArm64()
        target.binaries.framework {
            baseName = "shared"
            isStatic = true
        }

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        // The archive-producing link ignores linker options, so the static framework instead gets
        // the native SDK merged in after the link — which needs the download to have run first.
        target.binaries.withType(Framework::class.java).forEach { fw ->
            val linkTask = project.tasks.findByName(fw.linkTaskName)
            assertNotNull(linkTask, "expected link task ${fw.linkTaskName}")
            assertTrue(
                linkTask.dependsOn.any { it.toString().contains("downloadUXCamCocoaFramework") },
                "static framework link task '${fw.linkTaskName}' must depend on the UXCam download",
            )
        }
    }

    @Test
    fun `static merge opt-out leaves the link task unwired`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)

        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        val target = kotlin.iosArm64()
        target.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
        project.extensions.getByType(UXCamExtension::class.java)
            .linker.mergeStaticFrameworks.set(false)

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        // With the merge disabled the native SDK is the consumer's responsibility at app link.
        target.binaries.withType(Framework::class.java).forEach { fw ->
            val linkTask = project.tasks.findByName(fw.linkTaskName)
            assertNotNull(linkTask, "expected link task ${fw.linkTaskName}")
            assertTrue(
                linkTask.dependsOn.none { it.toString().contains("downloadUXCamCocoaFramework") },
                "opted-out static framework link task '${fw.linkTaskName}' must not depend on the UXCam download",
            )
        }
    }

    @Test
    fun `frameworkPath override skips the download task`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)

        val kotlin = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
        kotlin.iosArm64().binaries.framework { baseName = "shared" }
        project.extensions.getByType(UXCamExtension::class.java)
            .linker.frameworkPath.set("/tmp/UXCam.xcframework")

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project, hostIsMac = true)

        assertNull(
            project.tasks.findByName("downloadUXCamCocoaFramework"),
            "frameworkPath override should skip the download task",
        )
    }

    @Test
    fun `arch slice mapping covers ios targets and rejects others`() {
        assertEquals("ios-arm64", FrameworkArchitectures.sliceFor("iosArm64"))
        assertEquals("ios-arm64_x86_64-simulator", FrameworkArchitectures.sliceFor("iosSimulatorArm64"))
        assertEquals("ios-arm64_x86_64-simulator", FrameworkArchitectures.sliceFor("iosX64"))
        assertNull(FrameworkArchitectures.sliceFor("macosArm64"))
        assertNull(FrameworkArchitectures.sliceFor("linuxX64"))

        assertEquals("arm64", FrameworkArchitectures.archFor("iosArm64"))
        assertEquals("arm64", FrameworkArchitectures.archFor("iosSimulatorArm64"))
        assertEquals("x86_64", FrameworkArchitectures.archFor("iosX64"))
        assertNull(FrameworkArchitectures.archFor("macosArm64"))
    }

    @Test
    fun `compareVersions orders by numeric component`() {
        assertTrue(compareVersions("2.2.21", "2.2.0") > 0)
        assertTrue(compareVersions("2.2.0", "2.2.21") < 0)
        assertEquals(0, compareVersions("2.2.21", "2.2.21"))
        assertEquals(0, compareVersions("2.2.21-RC2", "2.2.21"))
        assertTrue(compareVersions("2.10.0", "2.9.0") > 0)
    }

    private fun org.gradle.api.Project.kotlinCocoapods(): CocoapodsExtension {
        val kmp = extensions.getByType(KotlinMultiplatformExtension::class.java) as ExtensionAware
        return kmp.extensions.getByType(CocoapodsExtension::class.java)
    }
}
