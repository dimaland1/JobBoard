package com.example.jobboard.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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


data class LoginRequest (
    val email: String,
    val password: String,
    val userType : String
)

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

data class JWTLOGIN (
    val nom_utilisateur : String,
    val token: String
)

data class applyRequest (
    val job_id: String,
    val name: String,
    val first_name : String,
    val nationality: String,
    val email: String,
    val dateNaissance: String
)

data class Apply (
    val id: Int,
    val job_id: Int,
    val name: String,
    val first_name : String,
)

interface ApiInterface {
    @GET("job-offers")
    fun getJobOffers(): Call<List<Job>>

    @GET("job-offers/{city}/{jobTitle}")
    fun getJobOffersBy(
        @Path("city") city: String,
        @Path("jobTitle") jobTitle: String
    ): Call<List<Job>>

    @GET("job-offers/{id}")
    fun getJobOfferById(@Path("id") id : String): Call<Job>

    @POST("auth/login")
    fun login(@Body request : LoginRequest): Call<JWTLOGIN>

    @POST("auth/register")
    fun register(@Body request : registerRequest): Call<JWTLOGIN>

    @POST("/apply/")
    fun apply(
        @Body request : applyRequest
    ) : Call<Apply>

    @GET("job-offers/employer/{id}")
    fun getJobOffersByEmployer(@Path("id") id : Int): Call<List<Job>>

    // retourn un status code 201
    @POST("job-offers/")
    fun addJobOffer(@Body job: AddJob): Call<Job>
}