package com.yfujiki.wiremocksample

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import retrofit2.Retrofit
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.commons.io.IOUtils
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.AssertionError
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TMDBServiceTest {
    private var service: TMDBService? = null

    @Rule @JvmField
    var wireMockRule = WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort())

    @Before
    fun setUp() {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("http://127.0.0.1:${wireMockRule.port()}")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        service = retrofit.create<TMDBService>(TMDBService::class.java)
    }


    @After
    fun tearDown() {

    }

    @Test
    fun testNowPlaying() {
        val json = readFileFromAssets("movies.json")

        stubFor(get(urlPathEqualTo("/3/movie/now_playing")).willReturn(
            aResponse().withStatus(200)
                .withHeader("Content-Type","Application/Json")
                .withBody(json)
        ))

        val latch = CountDownLatch(1)
        val failure = AtomicReference<AssertionError>()

        service!!.nowplaying(1).subscribe{ moviesPage ->
            try {
                assertEquals(2, moviesPage.page)
                assertEquals(55, moviesPage.totalPages)
                assertEquals(1089, moviesPage.totalResults)
                assertEquals(20, moviesPage.resultsCount)
            } catch (assertionError: AssertionError) {
                failure.set(assertionError)
            } finally {
                latch.countDown()
            }
        }

        latch.await(3, TimeUnit.SECONDS)

        if (failure.get() != null) {
            throw failure.get()
        }
    }

    private fun readFileFromAssets(fileName: String) : String {
        val file = File(this.javaClass.classLoader.getResource(fileName)!!.path)
        val reader = file.bufferedReader()
        return IOUtils.toString(reader)
    }
}
