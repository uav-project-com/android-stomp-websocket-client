package ua.naiksoftware.stompclientexample.network;

import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hieu19926@gmail.com on 07/02/2020.
 */
@Data
public class ApiClient {
    private static Retrofit retrofit = null;
    private static String baseUrl = "http://" + Constant.BASE_URL_SERVER + ":" + Constant.BASE_PORT;

    private ApiClient(){}

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();



        return retrofit;
    }
}
