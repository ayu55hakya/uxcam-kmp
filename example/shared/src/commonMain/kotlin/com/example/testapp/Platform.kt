package com.example.testapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform