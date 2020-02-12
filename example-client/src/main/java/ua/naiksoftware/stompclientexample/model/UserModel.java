package ua.naiksoftware.stompclientexample.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Created by hieu19926@gmail.com on 07/02/2020.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    private String username;
    private String password;
}
