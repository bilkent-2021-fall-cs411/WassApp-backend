package tr.com.bilkent.wassapp.model.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class SendMessagePayload {

    @NotBlank
    private String body;

    @NotNull
    private String receiver;
}
