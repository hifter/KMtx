package io.github.hifter.kmtx

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform