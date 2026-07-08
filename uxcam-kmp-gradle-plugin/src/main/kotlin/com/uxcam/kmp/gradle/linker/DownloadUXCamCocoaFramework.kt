package com.uxcam.kmp.gradle.linker

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URI
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream

/**
 * Deliver step of deliver-and-link: downloads `UXCam.xcframework.zip` for the pinned SDK version,
 * verifies its SHA-256, and unzips it into a shared cache directory. Idempotent — a completion
 * marker lets Gradle's up-to-date check skip the work once the framework is in place, so the
 * download happens at most once per (version, machine).
 *
 * Network robustness: connect/read timeouts so a stalled connection fails the build instead of
 * hanging it, retries with backoff for transient failures, an immediate (non-retried) failure for
 * 404s, and a fast, actionable failure when Gradle runs `--offline` with a cold cache.
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

    /**
     * Gradle's `--offline` flag, captured at registration. With a cold cache the task fails fast
     * with a pointer to `linker.frameworkPath` instead of touching (or hanging on) the network.
     * `@Internal`: none of the tuning knobs below affect the produced output, so they must not
     * invalidate the up-to-date check.
     */
    @get:Internal
    abstract val offline: Property<Boolean>

    /** TCP connect timeout — a dead endpoint fails after this instead of hanging the build. */
    @get:Internal
    abstract val connectTimeoutMillis: Property<Int>

    /** Per-read stall timeout during the transfer. */
    @get:Internal
    abstract val readTimeoutMillis: Property<Int>

    /** Total attempts for transient failures (resets, timeouts, 5xx). 404s are never retried. */
    @get:Internal
    abstract val maxAttempts: Property<Int>

    init {
        offline.convention(false)
        connectTimeoutMillis.convention(30_000)
        readTimeoutMillis.convention(30_000)
        maxAttempts.convention(3)
    }

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

        if (offline.get()) {
            throw GradleException(
                "UXCam: ${framework.name} is not cached at ${dest.absolutePath} and Gradle is " +
                        "running in offline mode — cannot download ${zipUrl.get()}.\n" +
                        "Run once without --offline, or point " +
                        "uxcamKmp { linker { frameworkPath.set(\"...\") } } at a local UXCam.xcframework."
            )
        }

        dest.parentFile.mkdirs()
        // Download and extract next to dest, then atomically rename into place, so a
        // concurrent build in another daemon never observes (or clobbers) a partial tree.
        val zipFile = File.createTempFile("uxcam-cocoa-", ".zip", dest.parentFile)
        val tmpDir = Files.createTempDirectory(dest.parentFile.toPath(), "${dest.name}-").toFile()
        try {
            val actual = downloadWithRetries(zipFile)
            if (actual != expected) {
                throw GradleException(
                    "UXCam Cocoa framework checksum mismatch for ${zipUrl.get()}.\n" +
                            "  expected: $expected\n  actual:   $actual\n" +
                            "If you overrode the Cocoa version, set the matching checksum or point " +
                            "uxcamKmp { linker { frameworkPath.set(\"...\") } } at a local UXCam.xcframework."
                )
            }

            zipFile.inputStream().buffered().use { unzip(it, tmpDir) }
            if (!File(tmpDir, framework.name).isDirectory) {
                throw GradleException(
                    "Expected ${framework.name} in the downloaded archive but it was not found under " +
                            "${dest.absolutePath}. The archive layout may have changed."
                )
            }
            File(tmpDir, marker.name).writeText(zipUrl.get())

            dest.deleteRecursively()
            try {
                Files.move(tmpDir.toPath(), dest.toPath(), StandardCopyOption.ATOMIC_MOVE)
            } catch (e: IOException) {
                // Lost the race to a concurrent build — accept its result if it's complete.
                if (!(marker.exists() && framework.isDirectory)) throw e
            }
            logger.lifecycle("Resolved native UXCam Cocoa framework → ${framework.absolutePath}")
        } finally {
            zipFile.delete()
            tmpDir.deleteRecursively()
        }
    }

    /**
     * Downloads [zipUrl] into [target], retrying transient [IOException]s with linear backoff.
     * Each attempt truncates [target] and hashes the fresh stream, so a partial transfer from a
     * failed attempt can never leak into the checksum. Returns the SHA-256 hex of the download.
     */
    private fun downloadWithRetries(target: File): String {
        val url = URI(zipUrl.get()).toURL()
        val attempts = maxAttempts.get().coerceAtLeast(1)
        var lastFailure: IOException? = null
        for (attempt in 1..attempts) {
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val connection = url.openConnection().apply {
                    connectTimeout = connectTimeoutMillis.get()
                    readTimeout = readTimeoutMillis.get()
                }
                DigestInputStream(connection.getInputStream(), digest).use { input ->
                    target.outputStream().use { input.copyTo(it) }
                }
                return digest.digest().joinToString("") { "%02x".format(it) }
            } catch (e: FileNotFoundException) {
                // 404 — deterministic, retrying can't help. Usually an overridden cocoaVersion
                // whose tag ships no UXCam.xcframework.zip.
                throw GradleException(
                    "UXCam: $url does not exist (HTTP 404). If you overrode " +
                            "uxcamKmp.linker.cocoaVersion, check that version publishes an " +
                            "UXCam.xcframework.zip, or point linker.frameworkPath at a local copy.",
                    e,
                )
            } catch (e: IOException) {
                lastFailure = e
                if (attempt < attempts) {
                    logger.warn(
                        "UXCam: downloading $url failed (attempt $attempt/$attempts): " +
                                "${e.message} — retrying in ${attempt}s."
                    )
                    Thread.sleep(attempt * 1000L)
                }
            }
        }
        throw GradleException(
            "UXCam: downloading $url failed after $attempts attempts: ${lastFailure?.message}",
            lastFailure,
        )
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
