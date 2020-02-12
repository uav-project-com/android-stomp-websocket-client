package ua.naiksoftware.stompclientexample.repository;

import io.reactivex.Completable;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

import static ua.naiksoftware.stompclientexample.network.Constant.AUTH;

/**
 * Created by Naik on 24.02.17.
 */
public interface ExampleRepository {

    @POST("/users/hello")
    Completable sendRestEcho(@Header(AUTH) String token, @Query("msg") String message);
}
