package com.example.jobboard.API

data class applyRequest (
    val job_id: String,
    val name: String,
    val first_name : String,
    val nationality: String,
    val email: String,
    val dateNaissance: String
)