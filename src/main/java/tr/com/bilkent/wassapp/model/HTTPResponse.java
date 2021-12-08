package tr.com.bilkent.wassapp.model;

import lombok.Data;

@Data
public class HTTPResponse<T> {

    private int status;
    private String message;
    private T data;

    public HTTPResponse(String message) {
        this.message = message;
        this.status = 200;
    }

    public HTTPResponse(T data) {
        this("success");
        this.data = data;
    }

    public HTTPResponse(int status, String message) {
        this.message = message;
        this.status = status;
    }

}
