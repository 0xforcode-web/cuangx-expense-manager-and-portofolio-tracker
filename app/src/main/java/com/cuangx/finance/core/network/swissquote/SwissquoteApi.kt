package com.cuangx.finance.core.network.swissquote

import retrofit2.http.GET
import retrofit2.http.Path

interface SwissquoteApi {

    @GET("public-quotes/bboquotes/instrument/{base}/{quote}")
    suspend fun getQuote(
        @Path("base") base: String,
        @Path("quote") quote: String
    ): List<SwissquoteResponse>
}
