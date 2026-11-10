package org.example.springstuff.migrations

import org.jetbrains.exposed.v1.core.InternalApi
import org.jetbrains.exposed.v1.plugin.core.migration.MigrationConfig
import org.jetbrains.exposed.v1.plugin.core.migration.MigrationGenerator
import org.jetbrains.exposed.v1.plugin.core.migration.MigrationLogger
import org.jetbrains.exposed.v1.plugin.core.migration.VersionFormat
import java.io.File

/**
 * Runs the Exposed migration generator in a forked JVM.
 *
 * Gradle 8.14 bundles kotlin-stdlib 2.0, which lacks `kotlin.time.Clock` (needed by Exposed's
 * MigrationGenerator). The Exposed plugin's worker always sees Gradle's stdlib, so the build runs the
 * generator here instead, on a classpath with an up-to-date stdlib. Configuration is passed as
 * `exposed.migrations.*` system properties by the `generateMigrations` task in build.gradle.kts.
 */
@OptIn(InternalApi::class)
fun main() {
    fun prop(name: String): String? = System.getProperty("exposed.migrations.$name")?.takeIf { it.isNotEmpty() }
    fun required(name: String): String = requireNotNull(prop(name)) { "Missing system property exposed.migrations.$name" }

    val debug = prop("debug").toBoolean()
    val config = MigrationConfig(
        tablesPackage = prop("tablesPackage").orEmpty(),
        tablesPackages = prop("tablesPackages")?.split(',')?.filter { it.isNotBlank() }.orEmpty(),
        classpathUrls = required("classpath").split(File.pathSeparatorChar)
            .filter { it.isNotEmpty() }
            .map { File(it).toURI().toURL() },
        fileDirectory = File(required("fileDirectory")),
        filePrefix = required("filePrefix"),
        fileVersionFormat = VersionFormat.valueOf(required("fileVersionFormat")),
        fileSeparator = required("fileSeparator"),
        useUpperCaseDescription = required("useUpperCaseDescription").toBoolean(),
        fileExtension = required("fileExtension"),
        fullFileName = prop("fullFileName"),
        databaseUrl = prop("databaseUrl"),
        databaseUser = prop("databaseUser"),
        databasePassword = System.getProperty("exposed.migrations.databasePassword"),
        testContainersImageName = prop("testContainersImageName"),
        debug = debug,
    )
    MigrationGenerator(config, MigrationLogger.stdout(debug)).generate()
}
