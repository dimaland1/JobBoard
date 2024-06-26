package com.example.jobboard.API

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class JWTLOGIN (
    val nom_utilisateur : String,
    val token: String,
    val logo_url: String,
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

    @GET("job-offers/employer/{id}")
    fun getJobOffersByEmployer(@Path("id") id : Int): Call<List<Job>>

    @POST("job-offers/")
    fun addJobOffer(@Body job: AddJob): Call<Void>

    @DELETE("job-offers/{id}")
    fun deleteJobOffer(@Path("id") id : String) : Call<Void>

    @GET("apply/candidate/{id}")
    fun getApplications(@Path("id") id: String): Call<List<Job>>

    @POST("apply/")
    fun apply(
        @Body request : applyRequest
    ) : Call<Void>

    @GET("apply/job/{id}/")
    fun getJobApplications(@Path("id") id: String): Call<List<ApplicationsResponse>>

}