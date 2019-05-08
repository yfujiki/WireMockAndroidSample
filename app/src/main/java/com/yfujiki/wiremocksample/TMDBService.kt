package com.yfujiki.wiremocksample

import com.yfujiki.wiremocksample.Movie
import com.yfujiki.wiremocksample.MoviesPage
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface TMDBService {
    @GET("3/movie/now_playing")
    fun nowplaying(@Query("page") page: Int,
                 @Query("api_key") apiKey: String = "<YOUR_API_KEY>"
    ): Flowable<MoviesPage>
}