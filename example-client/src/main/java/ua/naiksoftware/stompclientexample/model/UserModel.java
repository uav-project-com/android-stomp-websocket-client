package ua.naiksoftware.stompclientexample.model;

/**
 * Created by hieu19926@gmail.com on 07/02/2020.
 */
public class UserModel {
    public String getUsername() {
        return username;
    }

    public UserModel setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserModel setPassword(String password) {
        this.password = password;
        return this;
    }

    private String username;
    private String password;
}
