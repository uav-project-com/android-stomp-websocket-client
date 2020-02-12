package ua.naiksoftware.stompclientexample.resource;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ua.naiksoftware.stompclientexample.model.TokenModel;
import ua.naiksoftware.stompclientexample.model.UserModel;

/**
 * Created by hieu19926@gmail.com on 07/02/2020.
 */
public interface AuthenticationController {
    @POST("/users/signin")
    Call<TokenModel> signUp(@Body UserModel user);
}
