package com.example.jobboard.API

data class AddJob(
    val employer_id: Int,
    val title: String,
    val description: String,
    val target_job: String,
    val period: String,
    val remuneration: Int,
    val location: String,
    val status: String
)