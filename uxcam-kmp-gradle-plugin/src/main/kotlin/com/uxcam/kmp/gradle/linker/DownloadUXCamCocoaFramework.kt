package com.uxcam.kmp.gradle.linker

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URI
import java.security.MessageDigest
import java.util.zip.ZipInputStream

/**
 * Deliver step of deliver-and-link: downloads `UXCam.xcframework.zip` for the pinned SDK version,
 * verifies its SHA-256, and unzips it into a shared cache directory. Idempotent — a completion
 * marker lets Gradle's up-to-date check skip the work once the framework is in place, so the
 * download happens at most once per (version, machine).
 *
 * Configuration-cache safe: holds only [String]/[File] inputs, never a `Project` reference.
 */
abstract class DownloadUXCamCocoaFramework : DefaultTask() {

    @get:Input
    abstract val zipUrl: Property<String>

    @get:Input
    abstract val sha256: Property<String>

    /** Directory the `UXCam.xcframework` is extracted into (parent of the .xcframework). */
    @get:OutputDirectory
    abstract val destinationDir: DirectoryProperty

    @TaskAction
    fun resolve() {
        val dest = destinationDir.get().asFile
        val expected = sha256.get().lowercase()
        val marker = File(dest, ".uxcam-$expected.complete")
        val framework = File(dest, "${UXCamCocoaArtifact.FRAMEWORK}.xcframework")
        if (marker.exists() && framework.isDirectory) {
            logger.info("UXCam Cocoa framework already present at ${framework.absolutePath}.")
            return
        }

        dest.deleteRecursively()
        dest.mkdirs()

        val zipBytes = URI(zipUrl.get()).toURL().openStream().use { it.readBytes() }

        val actual = MessageDigest.getInstance("SHA-256").digest(zipBytes)
            .joinToString("") { "%02x".format(it) }
        if (actual != expected) {
            throw GradleException(
                "UXCam Cocoa framework checksum mismatch for ${zipUrl.get()}.\n" +
                    "  expected: $expected\n  actual:   $actual\n" +
                    "If you overrode the Cocoa version, set the matching checksum or point " +
                    "uxcamKmp { linker { frameworkPath.set(\"...\") } } at a local UXCam.xcframework."
            )
        }

        unzip(zipBytes.inputStream(), dest)
        if (!framework.isDirectory) {
            throw GradleException(
                "Expected ${framework.name} in the downloaded archive but it was not found under " +
                    "${dest.absolutePath}. The archive layout may have changed."
            )
        }
        marker.writeText(zipUrl.get())
        logger.lifecycle("Resolved native UXCam Cocoa framework → ${framework.absolutePath}")
    }

    private fun unzip(input: java.io.InputStream, dest: File) {
        val destPath = dest.canonicalPath
        ZipInputStream(input).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                val out = File(dest, entry.name)
                // Guard against zip-slip: every entry must stay within dest.
                if (!out.canonicalPath.startsWith(destPath + File.separator)) {
                    throw GradleException("Refusing to extract entry outside target dir: ${entry.name}")
                }
                if (entry.isDirectory) {
                    out.mkdirs()
                } else {
                    out.parentFile.mkdirs()
                    out.outputStream().use { zip.copyTo(it) }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
    }
}

internal object UXCamCocoaArtifact {
    const val FRAMEWORK = "UXCam"
}
