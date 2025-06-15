pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://artifact.bytedance.com/repository/pangle")
        }
        maven {
            credentials {
                username ="software-inhouse"
                password ="apero@123"
            }
            url = uri("https://artifactory.apero.vn/artifactory/gradle-release/")

        }
    }
}

rootProject.name = "NinhDT_BTVN"
include(":app")
include(":aisevice")
