package com.uxcam.kmp.gradle

import org.gradle.api.plugins.ExtensionAware
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
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
    }

    @Test
    fun `custom kmp version overrides default`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(PLUGIN_ID)

        val ext = project.extensions.getByType(UXCamExtension::class.java)
        ext.autoInstall.commonMain.uxcamKmpVersion.set("1.2.3")
        assertEquals("1.2.3", ext.autoInstall.commonMain.uxcamKmpVersion.get())
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
    fun `installs UXCam pod when not already present`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        val cocoapods = project.kotlinCocoapods()
        assertNull(cocoapods.pods.findByName("UXCam"))

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project)

        val pod = cocoapods.pods.getByName("UXCam")
        assertEquals(Versions.UXCAM_COCOA, pod.version)
        assertEquals("UXCam", pod.moduleName)
    }

    @Test
    fun `user set cocoa version is honored`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        project.extensions.getByType(UXCamExtension::class.java)
            .autoInstall.cocoapods.uxcamCocoaVersion.set("9.9.9")

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project)

        assertEquals("9.9.9", project.kotlinCocoapods().pods.getByName("UXCam").version)
    }

    @Test
    fun `does not overwrite an existing user declared UXCam pod`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        project.pluginManager.apply(PLUGIN_ID)
        project.pluginManager.apply("org.jetbrains.kotlin.native.cocoapods")

        project.kotlinCocoapods().pod("UXCam") { version = "custom" }

        project.plugins.getPlugin(UXCamPlugin::class.java).executeConfiguration(project)

        assertEquals("custom", project.kotlinCocoapods().pods.getByName("UXCam").version)
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
        val wrapperDeps = project.configurations
            .flatMap { it.dependencies }
            .filter { it.group == "com.uxcam.kmp" && it.name == "uxcam" }
        assertTrue(wrapperDeps.isEmpty())
    }

    private fun org.gradle.api.Project.kotlinCocoapods(): CocoapodsExtension {
        val kmp = extensions.getByType(KotlinMultiplatformExtension::class.java) as ExtensionAware
        return kmp.extensions.getByType(CocoapodsExtension::class.java)
    }
}
