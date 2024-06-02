package com.example.jobboard.API

data class registerRequest (
    val name : String,
    val first_name : String,
    val nationality : String,
    val birth_date : String,
    val phone : String,
    val email: String,
    val city: String,
    val CV_link: String,
    val password: String,
    val address : String,
    val cp : String,
    val link : String,
    val companyName : String,
    val userType : String
)