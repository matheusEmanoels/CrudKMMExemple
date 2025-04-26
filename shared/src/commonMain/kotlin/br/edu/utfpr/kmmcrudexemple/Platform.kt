package br.edu.utfpr.kmmcrudexemple

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform