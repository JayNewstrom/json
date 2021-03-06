package com.jaynewstrom.jsonDelight.sample.retrofit

import com.fasterxml.jackson.core.JsonFactory
import com.jaynewstrom.jsonDelight.retrofit.JsonConverterFactory
import com.jaynewstrom.jsonDelight.runtime.JsonDeserializerFactory
import com.jaynewstrom.jsonDelight.runtime.JsonSerializerFactory
import okhttp3.ResponseBody
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
import java.lang.reflect.Type

class JsonConverterFactoryTest {
    @get:Rule val mockWebServer = MockWebServer()

    private lateinit var retrofit: Retrofit

    private interface Service {
        @POST("test") fun callFallThrough(): Call<Another>

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

    private object Another

    private object AnotherConverterFactory : Converter.Factory() {
        override fun responseBodyConverter(type: Type, annotations: Array<Annotation>, retrofit: Retrofit): Converter<ResponseBody, *>? {
            if (type == Another::class.java) {
                return AnotherConverter
            }
            return super.responseBodyConverter(type, annotations, retrofit)
        }
    }

    private object AnotherConverter : Converter<ResponseBody, Another> {
        override fun convert(value: ResponseBody): Another {
            return Another
        }
    }

    @Before
    fun setUp() {
        retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(converterFactory())
            .addConverterFactory(AnotherConverterFactory)
            .build()
    }

    private fun service(): Service {
        return retrofit.create(Service::class.java)
    }

    private fun converterFactory(): Converter.Factory {
        return JsonConverterFactory.create(JsonFactory(), JsonSerializerFactory(), JsonDeserializerFactory())
    }

    @Test
    fun testConverterFactoryFallsBackIfAnotherConverterHandlesTheType() {
        mockWebServer.enqueue(MockResponse().setBody("Found"))
        val response = service().callFallThrough().execute()
        assertThat(response.body()!!).isEqualTo(Another)
    }

    @Test
    fun testResponseModelNotSupported() {
        try {
            service().callBasicResponseNotSupported()
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageContaining("Unable to create converter for class " + "com.jaynewstrom.jsonDelight.sample.retrofit.JsonConverterFactoryTest\$Unknown")
        }
    }

    @Test
    fun testRequestModelNotSupported() {
        try {
            service().callBasicRequestNotSupported(Unknown()).execute()
            fail()
        } catch (e: IllegalArgumentException) {
            assertThat(e).hasMessageContaining("Unable to create @Body converter for class " + "com.jaynewstrom.jsonDelight.sample.retrofit.JsonConverterFactoryTest\$Unknown")
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
