package tr.com.bilkent.wassapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WassAppResponse<T> {

    private int status;
    private String message;
    private T data;

    public WassAppResponse(String message) {
        this.message = message;
        this.status = 200;
    }

    public WassAppResponse(T data) {
        this("success");
        this.data = data;
    }

    public WassAppResponse(int status, String message) {
        this.message = message;
        this.status = status;
    }

}
