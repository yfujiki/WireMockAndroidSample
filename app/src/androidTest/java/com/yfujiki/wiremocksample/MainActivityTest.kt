package com.yfujiki.wiremocksample

import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.apache.commons.io.IOUtils
import org.junit.*
import org.junit.runner.RunWith
import java.io.File
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {
    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var wireMockRule = WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort().dynamicHttpsPort())

    @Before
    fun setUp() {
        stubForMovies()
    }

    @After
    fun tearDown() {
    }

    private fun stubForMovies() {
        val json = readFileFromAssets("movies.json")

        WireMock.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/3/movie/now_playing")).willReturn(
                WireMock.aResponse().withStatus(200)
                    .withHeader("Content-Type", "Application/Json")
                    .withBody(json)
            )
        )
    }

    @Test
    fun openMainActivity() {
        Espresso.onView(withRecyclerView(R.id.recyclerView).atPosition(0)).check { view, noViewFoundException ->
            val titleTextView = view.findViewById<TextView>(R.id.textView)
            Assert.assertEquals("Our Dance of Revolution", titleTextView.text)
        }
    }

    private fun readFileFromAssets(fileName: String) : String {
        val file = File(this.javaClass.classLoader.getResource(fileName)!!.path)
        val reader = file.bufferedReader()
        return IOUtils.toString(reader)
    }
}
