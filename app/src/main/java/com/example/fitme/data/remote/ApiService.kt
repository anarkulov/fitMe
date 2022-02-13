package com.example.fitme.data.remote

interface ApiService {

    /**  EXAMPLES REQUESTS  **/
/*
    @GET("/posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("/posts/{id}")
    suspend fun getPost(
        @Path(value = "id") id: Int
    ): Response<Post>

    @POST("/posts")
    suspend fun createPost(@Body dto: PostDTO): Response<Post>

    @PATCH("/posts")
    suspend fun updatePassword(@Body dto: PostDTO): Response<Post>

    *//*Email Sender Start*//*
    @PUT("/posts")
    suspend fun emailRegisterDetails(@Body detailsDTO: PostDTO): Response<Post>

    @DELETE("/posts/{id}")
    suspend fun deleteAddress(@Path(value = "id") id: Int): Response<Post>*/
}