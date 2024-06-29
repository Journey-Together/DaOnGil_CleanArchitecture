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
        maven("https://repository.map.naver.com/archive/maven")
        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }
        maven { url = java.net.URI("https://jitpack.io") }
        maven { url = java.net.URI("https://maven.google.com") }
    }
}

rootProject.name = "DaOnGil_CleanArchitecture"
include(":app")
include(":domain")
include(":presentation")
include(":data")
