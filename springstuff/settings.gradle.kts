pluginManagement {
    // Makes the Exposed Gradle plugin (id: org.jetbrains.exposed.plugin) resolvable from the
    // local jb-exposed checkout in the plugins block below.
    includeBuild("../../jb-exposed")
}

rootProject.name = "springstuff"

// Links the local Exposed build (~/Workspace/jb-exposed): every org.jetbrains.exposed:* dependency
// is substituted with the corresponding project of that checkout, so springstuff always builds
// against the latest and greatest Exposed sources — including exposed-spring-boot4-data-starter,
// which is not published anywhere yet.
includeBuild("../../jb-exposed")
