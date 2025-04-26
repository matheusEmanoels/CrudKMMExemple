package br.edu.utfpr.kmmcrudexemple.shared.data

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String
)