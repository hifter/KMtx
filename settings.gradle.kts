rootProject.name = "KMtx"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        // 阿里云镜像（插件）
        maven("https://maven.aliyun.com/repository/public") {
            name = "AliyunPublic"
//            mavenContent { releasesOnly() }
        }
        maven("https://maven.aliyun.com/repository/google") {
            name = "AliyunGoogle"
//            mavenContent { includeGroupAndSubgroups("com.google") }
        }
        maven("https://maven.aliyun.com/repository/gradle-plugin") {
            name = "AliyunGradlePlugin"
        }

        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        // 阿里云镜像（依赖）
//        maven("https://maven.aliyun.com/repository/public") {
//            name = "AliyunPublic"
//        }
        maven("https://maven.aliyun.com/repository/google") {
            name = "AliyunGoogle"
//            mavenContent {
//                includeGroupAndSubgroups("androidx")
//                includeGroupAndSubgroups("com.android")
//                includeGroupAndSubgroups("com.google")
//            }
        }
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":composeApp")