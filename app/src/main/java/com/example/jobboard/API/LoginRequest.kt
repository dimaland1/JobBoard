package com.example.jobboard.API

data class LoginRequest (
    val email: String,
    val password: String,
    val userType : String
)