package ua.naiksoftware.stompclientexample.model;

import java.io.Serializable;

/**
 * Created by Naik on 23.02.17.
 */
public class Command  implements Serializable {
    private String fromUser;
    private String toUser;

    public String getFromUser() {
        return fromUser;
    }

    public Command setFromUser(String fromUser) {
        this.fromUser = fromUser;
        return this;
    }

    public String getToUser() {
        return toUser;
    }

    public Command setToUser(String toUser) {
        this.toUser = toUser;
        return this;
    }

    public String getType() {
        return type;
    }

    public Command setType(String type) {
        this.type = type;
        return this;
    }

    public String getData() {
        return data;
    }

    public Command setData(String data) {
        this.data = data;
        return this;
    }

    String type; // command type
    String data;
}
