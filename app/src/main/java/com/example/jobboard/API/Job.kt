package com.example.jobboard.API

data class Job(
    val id: Int,
    val employer_id: Int,
    val title: String,
    val description: String,
    val target_job: String,
    val period: String,
    val remuneration: Int,
    val location: String,
    val created_at: String,
    val updated_at: String,
    val status: String
)