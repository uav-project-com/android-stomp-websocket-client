package ua.naiksoftware.stompclientexample.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Naik on 23.02.17.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Command  implements Serializable {
    private String fromUser;
    private String toUser;
    String type; // command type
    String data;
}
