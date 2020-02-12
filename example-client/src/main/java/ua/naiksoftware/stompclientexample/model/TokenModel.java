package ua.naiksoftware.stompclientexample.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenModel {
    private String token;
}
