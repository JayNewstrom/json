package com.jaynewstrom.json.sample.retrofit

import com.fasterxml.jackson.core.JsonFactory
import com.jaynewstrom.json.retrofit.JsonConverterFactory
import com.jaynewstrom.json.sample.RealJsonDeserializerFactory
import com.jaynewstrom.json.sample.RealJsonSerializerFactory
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.fest.assertions.api.Assertions.assertThat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

class JsonConverterFactoryTest {
    @get:Rule val mockWebServer = MockWebServer()

    private lateinit var retrofit: Retrofit

    private interface Service {
        @POST("test") fun callBasicResponseNotSupported(): Call<Unknown>

        @POST("test") fun callBasicRequestNotSupported(@Body notSupported: Unknown): Call<Void>

        @POST("test") fun callBasicResponse(): Call<Basic>

        @POST("test") fun callBasicRequest(@Body basic: Basic): Call<Void>

        @POST("test") fun callBasicResponseWithList(): Call<List<Basic>>

        @POST("test") fun callBasicResponseWithNestedList(): Call<List<List<Basic>>>

        @POST("test") fun callBasicRequestWithList(@Body list: List<Basic>): Call<Void>

        @JvmSuppressWildcards @POST("test") fun callBasicRequestWithNestedList(@Body list: List<List<Basic>>): Call<Void>
    }

    private class Unknown

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(converterFactory())
            .build()
    }

    private fun service(): Service {
        return retrofit.create(Service::class.java)
    }

    private fun converterFactory(): Converter.Factory {
        return JsonConverterFactory.create(JsonFactory(), RealJsonSerializerFactory(), RealJsonDeserializerFactory())
    }

    @Test
    fun testResponseModelNotSupported() {
        try {
            service().callBasicResponseNotSupported()
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageContaining("Unable to create converter for class " + "com.jaynewstrom.json.sample.retrofit.JsonConverterFactoryTest\$Unknown")
        }
    }

    @Test
    fun testRequestModelNotSupported() {
        try {
            service().callBasicRequestNotSupported(Unknown()).execute()
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageContaining("Unable to create @Body converter for class " + "com.jaynewstrom.json.sample.retrofit.JsonConverterFactoryTest\$Unknown")
        }
    }

    @Test
    fun testResponseConverter() {
        mockWebServer.enqueue(MockResponse().setBody("{\"foo\":\"bar\"}"))
        val call = service().callBasicResponse()
        val response = call.execute()
        val responseBody = response.body()
        assertThat(responseBody!!.foo).isEqualTo("bar")
    }

    @Test
    fun testRequestConverter() {
        mockWebServer.enqueue(MockResponse())
        val requestBody = Basic("bar")
        val call = service().callBasicRequest(requestBody)
        call.execute()
        val request = mockWebServer.takeRequest()
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.body.readUtf8()).isEqualTo("{\"foo\":\"bar\"}")
    }

    @Test
    fun testResponseConverterWithList() {
        mockWebServer.enqueue(MockResponse().setBody("[{\"foo\":\"bar\"}]"))
        val call = service().callBasicResponseWithList()
        val response = call.execute()
        val responseBody = response.body()
        assertThat(responseBody).hasSize(1)
        assertThat(responseBody!![0].foo).isEqualTo("bar")
    }

    @Test
    fun testResponseConverterWithNestedList() {
        mockWebServer.enqueue(MockResponse().setBody("[[{\"foo\":\"bar\"}]]"))
        val call = service().callBasicResponseWithNestedList()
        val response = call.execute()
        val responseBody = response.body()
        assertThat(responseBody).hasSize(1)
        assertThat(responseBody!![0]).hasSize(1)
        assertThat(responseBody[0][0].foo).isEqualTo("bar")
    }

    @Test
    fun testRequestConverterWithList() {
        mockWebServer.enqueue(MockResponse())
        val requestBody = Basic("bar")
        val call = service().callBasicRequestWithList(listOf(requestBody))
        call.execute()
        val request = mockWebServer.takeRequest()
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.body.readUtf8()).isEqualTo("[{\"foo\":\"bar\"}]")
    }

    @Test
    fun testRequestConverterWithNestedList() {
        mockWebServer.enqueue(MockResponse())
        val requestBody = Basic("bar")
        val call = service().callBasicRequestWithNestedList(listOf(listOf(requestBody)))
        call.execute()
        val request = mockWebServer.takeRequest()
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8")
        assertThat(request.body.readUtf8()).isEqualTo("[[{\"foo\":\"bar\"}]]")
    }
}
