package com.jaynewstrom.json.sample.retrofit;

import com.fasterxml.jackson.core.JsonFactory;
import com.jaynewstrom.json.retrofit.JsonConverterFactory;
import com.jaynewstrom.json.sample.RealJsonDeserializerFactory;
import com.jaynewstrom.json.sample.RealJsonSerializerFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public final class JsonConverterFactoryTest {

    interface Service {
        @POST("test") Call<Unknown> callBasicResponseNotSupported();

        @POST("test") Call<Void> callBasicRequestNotSupported(@Body Unknown notSupported);

        @POST("test") Call<Basic> callBasicResponse();

        @POST("test") Call<Void> callBasicRequest(@Body Basic basic);

        @POST("test") Call<List<Basic>> callBasicResponseWithList();

        @POST("test") Call<Void> callBasicRequestWithList(@Body List<Basic> list);

        @POST("test") Call<AutoBasic> callAutoBasicResponse();

        @POST("test") Call<Void> callAutoBasicRequest(@Body AutoBasic basic);
    }

    private static final class Unknown {

    }

    @Rule public final MockWebServer mockWebServer = new MockWebServer();

    private Retrofit retrofit;

    @Before
    public void setUp() {
        retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(converterFactory())
                .build();
    }

    private Service service() {
        return retrofit.create(Service.class);
    }

    private Converter.Factory converterFactory() {
        return JsonConverterFactory.create(new JsonFactory(), new RealJsonSerializerFactory(), new RealJsonDeserializerFactory());
    }

    @Test public void testResponseModelNotSupported() throws InterruptedException, IOException {
        try {
            service().callBasicResponseNotSupported();
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageContaining("Unable to create converter for class " +
                    "com.jaynewstrom.json.sample.retrofit.JsonConverterFactoryTest$Unknown");
        }
    }

    @Test public void testRequestModelNotSupported() throws InterruptedException, IOException {
        try {
            service().callBasicRequestNotSupported(new Unknown()).execute();
            fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessageContaining("Unable to create @Body converter for class " +
                    "com.jaynewstrom.json.sample.retrofit.JsonConverterFactoryTest$Unknown");
        }
    }

    @Test public void testResponseConverter() throws InterruptedException, IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{\"foo\":\"bar\"}"));
        Call<Basic> call = service().callBasicResponse();
        Response<Basic> response = call.execute();
        Basic responseBody = response.body();
        assertThat(responseBody.foo).isEqualTo("bar");
    }

    @Test public void testRequestConverter() throws InterruptedException, IOException {
        mockWebServer.enqueue(new MockResponse());
        Basic requestBody = new Basic("bar");
        Call<Void> call = service().callBasicRequest(requestBody);
        call.execute();
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"foo\":\"bar\"}");
    }

    @Test public void testResponseConverterWithList() throws InterruptedException, IOException {
        mockWebServer.enqueue(new MockResponse().setBody("[{\"foo\":\"bar\"}]"));
        Call<List<Basic>> call = service().callBasicResponseWithList();
        Response<List<Basic>> response = call.execute();
        List<Basic> responseBody = response.body();
        assertThat(responseBody).hasSize(1);
        assertThat(responseBody.get(0).foo).isEqualTo("bar");
    }

    @Test public void testRequestConverterWithList() throws InterruptedException, IOException {
        mockWebServer.enqueue(new MockResponse());
        Basic requestBody = new Basic("bar");
        Call<Void> call = service().callBasicRequestWithList(Collections.singletonList(requestBody));
        call.execute();
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
        assertThat(request.getBody().readUtf8()).isEqualTo("[{\"foo\":\"bar\"}]");
    }

    @Test public void testResponseConverterUsingAutoValue() throws InterruptedException, IOException {
        mockWebServer.enqueue(new MockResponse().setBody("{\"foo\":\"bar\"}"));
        Call<AutoBasic> call = service().callAutoBasicResponse();
        Response<AutoBasic> response = call.execute();
        AutoBasic responseBody = response.body();
        assertThat(responseBody.getFoo()).isEqualTo("bar");
    }

    @Test public void testRequestConverterUsingAutoValue() throws InterruptedException, IOException {
        mockWebServer.enqueue(new MockResponse());
        AutoBasic requestBody = new AutoValue_AutoBasic("bar");
        Call<Void> call = service().callAutoBasicRequest(requestBody);
        call.execute();
        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/json; charset=UTF-8");
        assertThat(request.getBody().readUtf8()).isEqualTo("{\"foo\":\"bar\"}");
    }
}
