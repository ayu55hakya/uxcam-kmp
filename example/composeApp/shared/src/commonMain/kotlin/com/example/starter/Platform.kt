package com.example.starter

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
