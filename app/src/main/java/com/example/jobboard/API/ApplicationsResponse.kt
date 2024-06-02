package com.example.jobboard.API

data class ApplicationsResponse(
    val status: String,
    val name: String,
    val first_name: String,
    val dateCandidate: String,
    val logo_url: String
)

