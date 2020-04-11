package ua.naiksoftware.stompclientexample.resource.implement;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stompclientexample.callback.RestfulCallback;
import ua.naiksoftware.stompclientexample.model.TokenModel;
import ua.naiksoftware.stompclientexample.model.UserModel;
import ua.naiksoftware.stompclientexample.resource.AuthenticationController;

/**
 * Created by hieu19926@gmail.com on 07/02/2020.
 */
public class AuthenticationControllerIpml {
    public AuthenticationController getController() {
        return controller;
    }

    public void setController(AuthenticationController controller) {
        this.controller = controller;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RestfulCallback getCallback() {
        return callback;
    }

    public void setCallback(RestfulCallback callback) {
        this.callback = callback;
    }

    private AuthenticationController controller;
    private String token;
    private RestfulCallback callback;
    public AuthenticationControllerIpml(AuthenticationController controller) {
        this.controller = controller;
    }

    public void login(UserModel user) {
        Call<TokenModel> call = this.controller.signUp(user);
        call.enqueue(new Callback<TokenModel>() {
            @Override
            public void onResponse(Call<TokenModel> call, Response<TokenModel> response) {
                token = Objects.requireNonNull(response.body()).getToken();
                Log.e("body", "" + token);
                Log.e("status", "" + response.code());
                callback.passCallbackData(token);
            }

            @Override
            public void onFailure(Call<TokenModel> call, Throwable t) {
                Log.e("ERROR", "Cannot connect: " + t.getMessage());
                Log.e("ERROR_LINK", "request:\n " + new Gson().toJson(call.request()));
            }
        });
    }
}
