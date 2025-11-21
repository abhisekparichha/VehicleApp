pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "drive-safe-obd"

include(
    ":app",
    ":core",
    ":data",
    ":obd",
    ":ui",
    ":features:dashboard",
    ":features:diagnostics",
    ":features:logs",
    ":features:performance"
)
